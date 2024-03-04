package amber1093.respite_bench.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.Properties;
import net.minecraft.state.StateManager;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class BenchBlock extends HorizontalFacingBlock {

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

    public BlockRotation getBlockRotation(BlockState state) {
        Direction direction = state.get(FACING);
        switch (direction) {
            case NORTH:
                return BlockRotation.NONE;
            case EAST:
                return BlockRotation.CLOCKWISE_90;
            case SOUTH:
                return BlockRotation.CLOCKWISE_180;
            case WEST:
                return BlockRotation.COUNTERCLOCKWISE_90;
            default:
                break;
        }
        return null;
    }

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
}
