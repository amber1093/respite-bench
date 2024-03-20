package amber1093.respite_bench;

import amber1093.respite_bench.blockentityrenderer.MobRespawnerBlockEntityRenderer;
import amber1093.respite_bench.entity.BenchEntity;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.minecraft.client.render.entity.EntityRenderer;

@SuppressWarnings("deprecation")
public class RespiteBenchClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		BlockRenderLayerMap.INSTANCE.putBlock(RespiteBench.MOB_RESPAWNER, RenderLayer.getCutout());
		EntityRendererRegistry.register(RespiteBench.BENCH_ENTITY, new EntityRendererFactory<BenchEntity>() {
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
		BlockEntityRendererRegistry.register(RespiteBench.MOB_RESPAWER_BLOCK_ENTITY_TYPE, MobRespawnerBlockEntityRenderer::new);
			
	}
}
