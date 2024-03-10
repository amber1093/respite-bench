package amber1093.respite_bench.entity;

import amber1093.respite_bench.RespiteBench;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    
    public static final EntityType<BenchEntity> BENCH_ENTITY = Registry.register(Registries.ENTITY_TYPE, new Identifier(RespiteBench.MOD_ID, "bench_entity"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, BenchEntity::new).disableSummon().fireImmune().build());

    public static void registerModEntities() {
        RespiteBench.LOGGER.info("Registering mod entities for " + RespiteBench.MOD_ID);
    }
}
