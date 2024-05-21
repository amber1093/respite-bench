package amber1093.respitebench.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class TrappedBenchBlock extends BenchBlock {

	public static final BooleanProperty POWERED = BooleanProperty.of("powered");

	public TrappedBenchBlock(Settings settings) {
		super(settings);
		setDefaultState(getDefaultState().with(POWERED, false));
	}

	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return (state.get(POWERED).booleanValue() ? 15 : 0);
	}

	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return this.getWeakRedstonePower(state, world, pos, direction);
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(POWERED);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		pulseRedstone(world, state, pos);
		return super.onUse(state, world, pos, player, hand, hit);
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		world.setBlockState(pos, state.with(POWERED, false), 1);
	}

	public static void pulseRedstone(World world, BlockState state, BlockPos pos) {
		world.setBlockState(pos, state.with(POWERED, true), 1);
		world.scheduleBlockTick(pos, state.getBlock(), 2);
	}
}
