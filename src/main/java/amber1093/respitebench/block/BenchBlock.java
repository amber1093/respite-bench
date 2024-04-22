package amber1093.respitebench.block;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import amber1093.respitebench.RespiteBench;
import amber1093.respitebench.RespiteBenchClient;
import amber1093.respitebench.entity.BenchEntity;
import amber1093.respitebench.event.DiscardConnectedEntityCallback;
import amber1093.respitebench.event.UseBenchCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;
import net.minecraft.world.World;

//TODO better texture
//TODO add wood variants
//TODO BenchEntity is killed no matter which BenchBlock is broken


public class BenchBlock extends HorizontalFacingBlock {

	//* BenchBlockEntity is currently unused
	//private BenchBlockEntity benchBlockEntity;
	
	private static final VoxelShape SHAPE_NORTH = createShape(Direction.NORTH);
	private static final VoxelShape SHAPE_EAST = createShape(Direction.EAST);
	private static final VoxelShape SHAPE_WEST = createShape(Direction.WEST);
	private static final VoxelShape SHAPE_SOUTH = createShape(Direction.SOUTH);

	public BenchBlock(Settings settings) {
		super(settings);
	}

	protected static VoxelShape createShape(Direction direction) {
		VoxelShape shape = VoxelShapes.empty();
		switch (direction) {
			case NORTH:
				shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.75, 0, 0.1875, 0.875, 0.3125, 0.3125));
				shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.75, 0, 0.8125, 0.875, 1, 0.9375));
				shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0, 0.8125, 0.25, 1, 0.9375));
				shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0, 0.1875, 0.25, 0.3125, 0.3125));
				shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0.3125, 0.125, 0.25, 0.375, 0.8125));
				shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.75, 0.3125, 0.125, 0.875, 0.375, 0.8125));
				shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.375, 0.125, 1, 0.4375, 0.375));
				shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.375, 0.4375, 1, 0.4375, 0.6875));
				shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.4375, 0.75, 1, 0.6875, 0.8125));
				shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.75, 0.75, 1, 1, 0.8125));
				break;
			case EAST:
				shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.6875, 0, 0.75, 0.8125, 0.3125, 0.875));
				shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.0625, 0, 0.75, 0.1875, 1, 0.875));
				shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.0625, 0, 0.125, 0.1875, 1, 0.25));
				shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.6875, 0, 0.125, 0.8125, 0.3125, 0.25));
				shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0.3125, 0.125, 0.875, 0.375, 0.25));
				shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0.3125, 0.75, 0.875, 0.375, 0.875));
				shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.625, 0.375, 0, 0.875, 0.4375, 1));
				shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.3125, 0.375, 0, 0.5625, 0.4375, 1));
				shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0.4375, 0, 0.25, 0.6875, 1));
				shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0.75, 0, 0.25, 1, 1));
				break;
			case SOUTH:
				shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.75, 0, 0.6875, 0.875, 0.3125, 0.8125));
				shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.75, 0, 0.0625, 0.875, 1, 0.1875));
				shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0, 0.0625, 0.25, 1, 0.1875));
				shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0, 0.6875, 0.25, 0.3125, 0.8125));
				shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0.3125, 0.1875, 0.25, 0.375, 0.875));
				shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.75, 0.3125, 0.1875, 0.875, 0.375, 0.875));
				shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.375, 0.625, 1, 0.4375, 0.875));
				shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.375, 0.3125, 1, 0.4375, 0.5625));
				shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.4375, 0.1875, 1, 0.6875, 0.25));
				shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.75, 0.1875, 1, 1, 0.25));
				break;
			case WEST:
				shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0, 0.75, 0.3125, 0.3125, 0.875));
				shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.8125, 0, 0.75, 0.9375, 1, 0.875));
				shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.8125, 0, 0.125, 0.9375, 1, 0.25));
				shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0, 0.125, 0.3125, 0.3125, 0.25));
				shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0.3125, 0.125, 0.8125, 0.375, 0.25));
				shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0.3125, 0.75, 0.8125, 0.375, 0.875));
				shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0.375, 0, 0.375, 0.4375, 1));
				shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4375, 0.375, 0, 0.6875, 0.4375, 1));
				shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.75, 0.4375, 0, 0.8125, 0.6875, 1));
				shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.75, 0.75, 0, 0.8125, 1, 1));
				break;
			case DOWN:
				break;
			case UP:
				break;
			default:
				break;
		}
		return shape;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(Properties.HORIZONTAL_FACING);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		return super.getPlacementState(context).with(Properties.HORIZONTAL_FACING, context.getHorizontalPlayerFacing().getOpposite());
	}

	public VoxelShape getShape(BlockState state) {
		Direction direction = state.get(FACING);
		switch (direction) {
			case NORTH:
				return SHAPE_NORTH;
			case EAST:
				return SHAPE_EAST;
			case WEST:
				return SHAPE_WEST;
			case SOUTH:
				return SHAPE_SOUTH;
			case DOWN:
			case UP:
			default:
				RespiteBench.LOGGER.warn("BenchBlock.getShape: The direction " + direction.toString() + " is invalid!");
				return SHAPE_NORTH;
		}
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return this.getShape(state);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return this.getShape(state);
	}

	//TODO run this when player respawns
	//TODO add particle effects (and maybe sound effect)
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		rest(world, pos, player, RespiteBench.BENCH_ENTITY);
		return ActionResult.SUCCESS;
	}

	public static void rest(World world, BlockPos pos, PlayerEntity player, EntityType<? extends BenchEntity> entityToRide) {

		if (!world.isClient()) {
			//spawn and teleport BenchEntity
			if (RespiteBenchClient.getBenchRestInstantly() == false) {
				sit(world, pos, player, entityToRide);
			}

			//set spawn point
			if (RespiteBenchClient.getBenchSetSpawnPoint()) {
				((ServerPlayerEntity)player).setSpawnPoint(world.getRegistryKey(), pos, player.getYaw(), false, true);
			}

			//heal and clear status from player
			player.heal(player.getMaxHealth());

			if (RespiteBenchClient.getBenchClearPotionEffects()) {
				player.clearStatusEffects();
			}

			//replace all EmptyFlask with Flask
			refillFlasks(player.getInventory());

			//allow all mob respawners to spawn mobs once
			List<UUID> uuidList = UseBenchCallback.EVENT.invoker().useBenchEvent(true);

			//use the list from UseBenchCallback and discard all matching entities
			DiscardConnectedEntityCallback.EVENT.invoker().discardConnectedEntities(uuidList);
		}
	}

	public static void sit(World world, BlockPos pos, PlayerEntity player, EntityType<? extends BenchEntity> entityToRide) {
		BenchEntity benchEntity = entityToRide.create(world);
		benchEntity.setInvulnerable(true);
		benchEntity.setPosition(pos.getX() + 0.5f, pos.getY() - 0.55f, pos.getZ() + 0.5f);
		world.spawnEntity(benchEntity);
		player.startRiding(benchEntity);
	}

	public static void refillFlasks(PlayerInventory playerInventory) {
		while (playerInventory.getSlotWithStack(new ItemStack(RespiteBench.EMPTY_FLASK)) != -1) {
			int flaskSlot = playerInventory.getSlotWithStack(new ItemStack(RespiteBench.EMPTY_FLASK));
			int flaskAmount = playerInventory.getStack(flaskSlot).getCount();
			
			playerInventory.getStack(flaskSlot).setCount(0);
			playerInventory.insertStack(new ItemStack(RespiteBench.FLASK, flaskAmount));
		}
	}

	//no need to copy paste all that code :)
	public static Optional<Vec3d> findRespawnPosition(EntityType<?> entity, CollisionView world, BlockPos pos) {
		return RespawnAnchorBlock.findRespawnPosition(entity, world, pos);
	}

	/* //* BenchBlockEntity is currently unused
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		benchBlockEntity = new BenchBlockEntity(pos, state);
		return benchBlockEntity;
	}
	*/

}
