package amber1093.respite_bench.blockentity;

import amber1093.respite_bench.RespiteBench;
import amber1093.respite_bench.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {

    public static final BlockEntityType<BenchBlockEntity> BENCH_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE, new Identifier(RespiteBench.MOD_ID, "bench_entity_type"), 
            FabricBlockEntityTypeBuilder.create(BenchBlockEntity::new, ModBlocks.BENCH).build());

    public static void registerModBlockEntities() {
        RespiteBench.LOGGER.info("Registering mod block entities for " + RespiteBench.MOD_ID);
    }
}
