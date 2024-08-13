package amber1093.respitebench;

import org.jetbrains.annotations.Nullable;

import amber1093.respitebench.blockentityrenderer.MobRespawnerBlockEntityRenderer;
import amber1093.respitebench.config.ConfigMenu;
import amber1093.respitebench.config.ConfigSave;
import amber1093.respitebench.entity.BenchEntity;
import amber1093.respitebench.packet.ConfigUpdatePacket;
import amber1093.respitebench.packethandler.ConfigUpdatePacketS2CHandler;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData.ValidationException;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.impl.client.rendering.BlockEntityRendererRegistryImpl;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.minecraft.client.render.entity.EntityRenderer;

public class RespiteBenchClient implements ClientModInitializer {

	public static ConfigMenu config;
	@Nullable
	public static ConfigMenu configoverride = null;

	@Override
	public void onInitializeClient() {


		//register S2C config update handler
		ClientPlayNetworking.registerGlobalReceiver(
			ConfigUpdatePacket.TYPE,
			(packet, player, responseSender) -> {
				ConfigUpdatePacketS2CHandler.applyConfigSettings(packet);
			}
		);

		//register and read config
		AutoConfig.register(ConfigMenu.class, Toml4jConfigSerializer::new);
		readConfig();

		//save button press event
		AutoConfig.getConfigHolder(ConfigMenu.class).registerSaveListener(new ConfigSave());

		//disconnect from server event
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			RespiteBenchClient.configoverride = null;
		});

		// other nonsense
		BlockRenderLayerMap.INSTANCE.putBlock(RespiteBench.MOB_RESPAWNER, RenderLayer.getCutout());
		BlockEntityRendererRegistryImpl.register(RespiteBench.MOB_RESPAWER_BLOCK_ENTITY_TYPE, MobRespawnerBlockEntityRenderer::new);
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
	}

	private static void readConfig() {
		config = AutoConfig.getConfigHolder(ConfigMenu.class).getConfig();
		try {
			config.validatePostLoad();
		} catch (ValidationException e) {
			e.printStackTrace();
		}
	}

	public static int getFlaskHealAmount() {
		return (configoverride == null ? config.flask.healAmount : configoverride.flask.healAmount);
	}

	public static int getFlaskUseTime() {
		return (configoverride == null ? config.flask.useTime : configoverride.flask.useTime);
	}

	public static boolean getBenchRestInstantly() {
		return (configoverride == null ? config.bench.restInstantly : configoverride.bench.restInstantly);
	}
	
	public static boolean getBenchClearPotionEffects() {
		return (configoverride == null ? config.bench.clearPotionEffects : configoverride.bench.clearPotionEffects);
	}

	public static boolean getBenchSetSpawnPoint() {
		return (configoverride == null ? config.bench.setSpawnPoint : configoverride.bench.setSpawnPoint);
	}

	public static float getBenchDistanceRequired() {
		return (configoverride == null ? config.bench.distanceRequired : configoverride.bench.distanceRequired);
	}

	public static boolean getMobRespawnerIgnoreSpawnRules() {
		return (configoverride == null ? config.mobrespawner.ignoreSpawnRules : configoverride.mobrespawner.ignoreSpawnRules);
	}
}
