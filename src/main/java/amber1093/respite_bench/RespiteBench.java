package amber1093.respite_bench;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import amber1093.respite_bench.block.BenchBlock;
import amber1093.respite_bench.block.MobRespawnerBlock;
import amber1093.respite_bench.blockentity.BenchBlockEntity;
import amber1093.respite_bench.blockentity.MobRespawnerBlockEntity;
import amber1093.respite_bench.entity.BenchEntity;
import amber1093.respite_bench.item.FlaskItem;

//TODO downgrade fabricloader version to 0.14.21
public class RespiteBench implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MOD_ID = "respite_bench";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	//#region Items
	public static final FoodComponent FLASK_FOOD_COMPONENT = new FoodComponent.Builder().alwaysEdible().build();
	public static final Item FLASK = registerItem("flask", new FlaskItem(new FabricItemSettings().food(FLASK_FOOD_COMPONENT)));
	public static final Item EMPTY_FLASK = registerItem("empty_flask", new Item(new FabricItemSettings()));
	public static final Item FLASK_SHARD = registerItem("flask_shard", new Item(new FabricItemSettings()));

	private static Item registerItem(String name, Item item) {
		return Registry.register(Registries.ITEM, new Identifier(RespiteBench.MOD_ID, name), item);
	}
	//#endregion

	//#region ItemGroups
	public static final ItemGroup RESPITE_BENCH_GROUP = Registry.register(
			Registries.ITEM_GROUP,
			new Identifier(RespiteBench.MOD_ID, "respite_bench"),
			FabricItemGroup.builder()
				.displayName(Text.translatable("itemgroup.respite_bench"))
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
	public static final Block MOB_RESPAWNER = registerBlock("mob_respawner", new MobRespawnerBlock(FabricBlockSettings.copyOf(Blocks.SPAWNER)));
	public static final Block BENCH = registerBlock("bench", new BenchBlock(FabricBlockSettings.copyOf(Blocks.BEDROCK).nonOpaque()));

	private static Block registerBlock (String name, Block block) {
		registerBlockItem(name, block);
		return Registry.register(Registries.BLOCK, new Identifier(RespiteBench.MOD_ID, name), block);
	} 

	private static Item registerBlockItem(String name, Block block) {
		return Registry.register(Registries.ITEM, new Identifier(RespiteBench.MOD_ID, name), new BlockItem(block, new FabricItemSettings()));
	}
	//#endregion

	//#region BlockEntity
	public static final BlockEntityType<BenchBlockEntity> BENCH_BLOCK_ENTITY = Registry.register(
			Registries.BLOCK_ENTITY_TYPE, new Identifier(RespiteBench.MOD_ID, "bench_block_entity_type"), 
			FabricBlockEntityTypeBuilder.create(BenchBlockEntity::new, RespiteBench.BENCH).build());

	public static final BlockEntityType<MobRespawnerBlockEntity> MOB_RESPAWER_BLOCK_ENTITY_TYPE = Registry.register(
			Registries.BLOCK_ENTITY_TYPE, new Identifier(RespiteBench.MOD_ID, "mob_respawner_block_entity_type"),
			FabricBlockEntityTypeBuilder.create(MobRespawnerBlockEntity::new, RespiteBench.MOB_RESPAWNER).build());
	//#endregion
	
	//#region Entity
	public static final EntityType<BenchEntity> BENCH_ENTITY = Registry.register(Registries.ENTITY_TYPE, new Identifier(RespiteBench.MOD_ID, "bench_entity"),
			FabricEntityTypeBuilder.create(SpawnGroup.MISC, BenchEntity::new).disableSummon().fireImmune().build());
	//#endregion

	@Override
	public void onInitialize() {

	}
}