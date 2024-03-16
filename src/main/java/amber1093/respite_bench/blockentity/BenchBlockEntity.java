package amber1093.respite_bench.blockentity;

import amber1093.respite_bench.RespiteBench;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class BenchBlockEntity extends BlockEntity {
	public BenchBlockEntity(BlockPos pos, BlockState state) {
		super(RespiteBench.BENCH_BLOCK_ENTITY, pos, state);
	}
}
