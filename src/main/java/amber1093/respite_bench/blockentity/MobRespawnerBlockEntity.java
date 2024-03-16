package amber1093.respite_bench.blockentity;

import amber1093.respite_bench.RespiteBench;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class MobRespawnerBlockEntity extends BlockEntity {
    public MobRespawnerBlockEntity(BlockPos pos, BlockState state) {
        super(RespiteBench.MOB_RESPAWER_BLOCK_ENTITY_TYPE, pos, state);
    }
}
