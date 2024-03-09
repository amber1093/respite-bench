package amber1093.respite_bench;

import amber1093.respite_bench.block.ModBlocks;
import amber1093.respite_bench.entity.BenchEntity;
import amber1093.respite_bench.entity.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.minecraft.client.render.entity.EntityRenderer;

public class RespiteBenchClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.MOB_RESPAWNER, RenderLayer.getTranslucent());
        EntityRendererRegistry.register(ModEntities.BENCH_ENTITY, new EntityRendererFactory<BenchEntity>() {

            @Override
            public EntityRenderer<BenchEntity> create(Context var1) {
                return new EntityRenderer<BenchEntity>(var1) {
                    @Override
                    public Identifier getTexture(BenchEntity var1) {
                        return new Identifier(RespiteBench.MOD_ID, "bench_entity");
                    }
                };
            }
        });
    }
}
