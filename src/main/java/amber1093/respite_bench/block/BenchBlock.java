package amber1093.respite_bench.block;

import amber1093.respite_bench.blockentity.BenchBlockEntity;
import amber1093.respite_bench.entity.BenchEntity;
import amber1093.respite_bench.entity.ModEntities;
import amber1093.respite_bench.item.ModItems;
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
import net.minecraft.text.Text;
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

    public BenchBlock(Settings settings) {
        super(settings);
    }

    @Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(Properties.HORIZONTAL_FACING);
	}

    @Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		return super.getPlacementState(context).with(Properties.HORIZONTAL_FACING, context.getHorizontalPlayerFacing().getOpposite());
	}

    //TODO make lighting work on different rotations
    public VoxelShape getShape(BlockState state) {
        VoxelShape shape = VoxelShapes.empty();
        Direction direction = state.get(FACING);

        switch (direction) {
            case NORTH:
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.0625, 0.4375, 0.75, 0.9375, 0.625, 0.8125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.0625, 0.6875, 0.75, 0.9375, 0.8125, 0.8125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.0625, 0.3125, 0.25, 0.9375, 0.375, 0.4375));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.0625, 0.3125, 0.5, 0.9375, 0.375, 0.6875));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0.25, 0.25, 0.3125, 0.3125, 0.75));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0.375, 0.8125, 0.3125, 0.875, 0.875));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0.015625, 0.63125, 0.3125, 0.203125, 0.69375));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.6875, 0.25, 0.25, 0.8125, 0.3125, 0.75));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.6875, 0.375, 0.8125, 0.8125, 0.875, 0.875));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.6875, 0.015625, 0.63125, 0.8125, 0.203125, 0.69375));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0, 0.625, 0.3125, 0.25, 0.75));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0, 0.3125, 0.3125, 0.25, 0.4375));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.6875, 0, 0.625, 0.8125, 0.25, 0.75));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.6875, 0, 0.3125, 0.8125, 0.25, 0.4375));
                break;
            case EAST:
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0.4375, 0.0625, 0.25, 0.625, 0.9375));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0.6875, 0.0625, 0.25, 0.8125, 0.9375));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5625, 0.3125, 0.0625, 0.75, 0.375, 0.9375));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.3125, 0.3125, 0.0625, 0.5, 0.375, 0.9375));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.25, 0.25, 0.1875, 0.75, 0.3125, 0.3125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0.375, 0.1875, 0.1875, 0.875, 0.3125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.30625, 0.015625, 0.1875, 0.36875, 0.203125, 0.3125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.25, 0.25, 0.6875, 0.75, 0.3125, 0.8125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0.375, 0.6875, 0.1875, 0.875, 0.8125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.30625, 0.015625, 0.6875, 0.36875, 0.203125, 0.8125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.25, 0, 0.1875, 0.375, 0.25, 0.3125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5625, 0, 0.1875, 0.6875, 0.25, 0.3125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.25, 0, 0.6875, 0.375, 0.25, 0.8125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5625, 0, 0.6875, 0.6875, 0.25, 0.8125));
                break;
            case SOUTH:
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.0625, 0.4375, 0.1875, 0.9375, 0.625, 0.25));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.0625, 0.6875, 0.1875, 0.9375, 0.8125, 0.25));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.0625, 0.3125, 0.5625, 0.9375, 0.375, 0.75));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.0625, 0.3125, 0.3125, 0.9375, 0.375, 0.5));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0.25, 0.25, 0.3125, 0.3125, 0.75));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0.375, 0.125, 0.3125, 0.875, 0.1875));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0.015625, 0.30625, 0.3125, 0.203125, 0.36875));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.6875, 0.25, 0.25, 0.8125, 0.3125, 0.75));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.6875, 0.375, 0.125, 0.8125, 0.875, 0.1875));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.6875, 0.015625, 0.30625, 0.8125, 0.203125, 0.36875));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0, 0.25, 0.3125, 0.25, 0.375));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0, 0.5625, 0.3125, 0.25, 0.6875));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.6875, 0, 0.25, 0.8125, 0.25, 0.375));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.6875, 0, 0.5625, 0.8125, 0.25, 0.6875));
                break;
            case WEST:
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.75, 0.4375, 0.0625, 0.8125, 0.625, 0.9375));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.75, 0.6875, 0.0625, 0.8125, 0.8125, 0.9375));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.25, 0.3125, 0.0625, 0.4375, 0.375, 0.9375));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5, 0.3125, 0.0625, 0.6875, 0.375, 0.9375));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.25, 0.25, 0.1875, 0.75, 0.3125, 0.3125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.8125, 0.375, 0.1875, 0.875, 0.875, 0.3125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.63125, 0.015625, 0.1875, 0.69375, 0.203125, 0.3125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.25, 0.25, 0.6875, 0.75, 0.3125, 0.8125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.8125, 0.375, 0.6875, 0.875, 0.875, 0.8125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.63125, 0.015625, 0.6875, 0.69375, 0.203125, 0.8125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.625, 0, 0.1875, 0.75, 0.25, 0.3125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.3125, 0, 0.1875, 0.4375, 0.25, 0.3125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.625, 0, 0.6875, 0.75, 0.25, 0.8125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.3125, 0, 0.6875, 0.4375, 0.25, 0.8125));
                break;
            default:
                break;
        }

        return shape;
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
            BenchEntity benchEntity = ModEntities.BENCH_ENTITY.create(world);
            benchEntity.setInvulnerable(true);
            benchEntity.setPosition(pos.getX() + 0.5f, pos.getY() + 1.0f, pos.getZ() + 0.5f);
            world.spawnEntity(benchEntity);
            player.startRiding(benchEntity);
            benchEntity.allowKill = true;
            player.heal(player.getMaxHealth());
            player.clearStatusEffects();
            do {
                int flaskSlot = player.getInventory().getSlotWithStack(new ItemStack(ModItems.EMPTY_FLASK));
                int flaskAmount = player.getInventory().getStack(flaskSlot).getCount();
                player.getInventory().getStack(flaskSlot).setCount(0);
                player.getInventory().insertStack(new ItemStack(ModItems.FLASK, flaskAmount));

                player.sendMessage(Text.literal("flaskSlot" + String.valueOf(flaskSlot)));
                player.sendMessage(Text.literal("flaskAmount" + String.valueOf(flaskAmount)));
            }
            while (player.getInventory().getSlotWithStack(new ItemStack(ModItems.EMPTY_FLASK)) != -1);
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
