package amber1093.respitebench;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import amber1093.respitebench.block.BenchBlock;
import amber1093.respitebench.block.MobRespawnerBlock;
import amber1093.respitebench.blockentity.MobRespawnerBlockEntity;
import amber1093.respitebench.config.ConfigMenu;
import amber1093.respitebench.entity.BenchEntity;
import amber1093.respitebench.item.FlaskItem;
import amber1093.respitebench.packet.ConfigUpdatePacket;
import amber1093.respitebench.packet.MobRespawnerUpdateC2SPacket;
import amber1093.respitebench.packethandler.ConfigUpdatePacketC2SHandler;
import amber1093.respitebench.packethandler.MobRespawnerUpdatePacketHandler;

public class RespiteBench implements ModInitializer {
	public static final String MOD_ID = "respitebench";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Nullable
	public static ConfigMenu serverconfig = null;

	//#region Items
	public static final FoodComponent FLASK_FOOD_COMPONENT = new FoodComponent.Builder().alwaysEdible().build();
	public static final Item FLASK = registerItem("flask", new FlaskItem(new FabricItemSettings().food(FLASK_FOOD_COMPONENT)));
	public static final Item EMPTY_FLASK = registerItem("empty_flask", new Item(new FabricItemSettings()));
	public static final Item FLASK_SHARD = registerItem("flask_shard", new Item(new FabricItemSettings()));

	private static Item registerItem(String name, Item item) {
		return Registry.register(Registries.ITEM, new Identifier(MOD_ID, name), item);
	}
	//#endregion

	//#region ItemGroups
	public static final ItemGroup RESPITE_BENCH_GROUP = Registry.register(
			Registries.ITEM_GROUP,
			new Identifier(MOD_ID, "respitebench"),
			FabricItemGroup.builder()
				.displayName(Text.translatable("itemgroup.respitebench"))
				.icon(() -> new ItemStack(RespiteBench.FLASK))
				.entries((displayContext, entries) -> {
					entries.add(RespiteBench.FLASK);
					entries.add(RespiteBench.EMPTY_FLASK);
					entries.add(RespiteBench.FLASK_SHARD);
					entries.add(RespiteBench.MOB_RESPAWNER);
					entries.add(RespiteBench.BENCH);
				})
				.build()
	);
	//#endregion

	//#region Blocks
	public static final Block MOB_RESPAWNER = registerBlock("mob_respawner", new MobRespawnerBlock(FabricBlockSettings.copyOf(Blocks.SPAWNER).strength(-1.0F, 3600000.0F)));
	public static final Block BENCH = registerBlock("bench", new BenchBlock(FabricBlockSettings.copyOf(Blocks.BEDROCK).nonOpaque()));

	private static Block registerBlock (String name, Block block) {
		registerBlockItem(name, block);
		return Registry.register(Registries.BLOCK, new Identifier(MOD_ID, name), block);
	} 

	private static Item registerBlockItem(String name, Block block) {
		return Registry.register(Registries.ITEM, new Identifier(MOD_ID, name), new BlockItem(block, new FabricItemSettings()));
	}
	//#endregion

	//#region BlockEntity
	/* //* BenchBlockEntity is currently unused
	public static final BlockEntityType<BenchBlockEntity> BENCH_BLOCK_ENTITY = Registry.register(
			Registries.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "bench_block_entity_type"), 
			FabricBlockEntityTypeBuilder.create(BenchBlockEntity::new, RespiteBench.BENCH).build());
	*/
	public static final BlockEntityType<MobRespawnerBlockEntity> MOB_RESPAWER_BLOCK_ENTITY_TYPE = Registry.register(
			Registries.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "mob_respawner_block_entity_type"),
			FabricBlockEntityTypeBuilder.create(MobRespawnerBlockEntity::new, RespiteBench.MOB_RESPAWNER).build());
	//#endregion
	
	//#region Entity
	public static final EntityType<BenchEntity> BENCH_ENTITY = Registry.register(Registries.ENTITY_TYPE, new Identifier(MOD_ID, "bench_entity"),
			FabricEntityTypeBuilder.create(SpawnGroup.MISC, BenchEntity::new).disableSummon().dimensions(EntityDimensions.fixed(0, 1f)).fireImmune().build());
	//#endregion

	//#region Networking
	public static final Identifier MOB_RESPAWNER_UPDATE_PACKET_ID = new Identifier(MOD_ID, "mob_respawner_update");
	public static final Identifier CONFIG_UPDATE_PACKET_ID = new Identifier(MOD_ID, "config_update");
	//#endregion

	@Override
	public void onInitialize() {
		//register packet for MobRespawnerScreen
		ServerPlayNetworking.registerGlobalReceiver(
			MobRespawnerUpdateC2SPacket.TYPE,
			(packet, player, responseSender) -> {
				MobRespawnerUpdatePacketHandler.updateMobRespawnerSettings(packet, player);
			}
		);

		//register packet for updating config
		ServerPlayNetworking.registerGlobalReceiver(
			ConfigUpdatePacket.TYPE, 
			(packet, player, responseSender) -> {
				ConfigUpdatePacketC2SHandler.updateConfigSettings(packet, player);
			}
		);

		//player join server event
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			if (serverconfig != null) {
				ServerPlayNetworking.send(handler.getPlayer(), new ConfigUpdatePacket(serverconfig));
			}
		});
	}
}