package amber1093.respite_bench.block;

import amber1093.respite_bench.RespiteBench;
import amber1093.respite_bench.blockentity.BenchBlockEntity;
import amber1093.respite_bench.entity.BenchEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BenchBlock extends HorizontalFacingBlock implements BlockEntityProvider {
	private BenchBlockEntity blockEntity;

	public VoxelShape shape_north = createShape(Direction.NORTH);
	public VoxelShape shape_east = createShape(Direction.EAST);
	public VoxelShape shape_west = createShape(Direction.WEST);
	public VoxelShape shape_south = createShape(Direction.SOUTH);

	public BenchBlock(Settings settings) {
		super(settings);
	}

	protected VoxelShape createShape(Direction direction) {
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
			return shape_north;
		case EAST:
			return shape_east;
		case WEST:
			return shape_west;
		case SOUTH:
			return shape_south;

		case DOWN:
		case UP:
		default:
			RespiteBench.LOGGER.warn("BenchBlock.getShape: The direction " + direction.toString() + " is invalid!");
			return shape_north;
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

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (!world.isClient()) {

			//spawn and teleport BenchEntity
			BenchEntity benchEntity = RespiteBench.BENCH_ENTITY.create(world);
			benchEntity.setInvulnerable(true);
			benchEntity.setPosition(pos.getX() + 0.5f, pos.getY() + 1.0f, pos.getZ() + 0.5f);
			world.spawnEntity(benchEntity);
			player.startRiding(benchEntity);
			benchEntity.allowKill = true;

			//heal and clear status from player
			player.heal(player.getMaxHealth());
			player.clearStatusEffects();

			//replace all EmptyFlask with Flask
			while (player.getInventory().getSlotWithStack(new ItemStack(RespiteBench.EMPTY_FLASK)) != -1) {
				int flaskSlot = player.getInventory().getSlotWithStack(new ItemStack(RespiteBench.EMPTY_FLASK));
				int flaskAmount = player.getInventory().getStack(flaskSlot).getCount();
				player.getInventory().getStack(flaskSlot).setCount(0);
				player.getInventory().insertStack(new ItemStack(RespiteBench.FLASK, flaskAmount));
			}
			return ActionResult.SUCCESS;
		}
		return ActionResult.PASS;
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
	blockEntity = new BenchBlockEntity(pos, state);
	return blockEntity;
	}
}
