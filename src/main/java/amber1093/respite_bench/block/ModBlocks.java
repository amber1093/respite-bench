package amber1093.respite_bench.block;

import amber1093.respite_bench.RespiteBench;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {

    public static final Block MOB_RESPAWNER = registerBlock("mob_respawner", new Block(FabricBlockSettings.copyOf(Blocks.BEDROCK).nonOpaque()));
    public static final Block BENCH = registerBlock("bench", new Block(FabricBlockSettings.copyOf(Blocks.BEDROCK).nonOpaque()));

    private static Block registerBlock (String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(RespiteBench.MOD_ID, name), block);
    } 

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, new Identifier(RespiteBench.MOD_ID, name), new BlockItem(block, new FabricItemSettings()));
    }

    
    public static void registerModBlocks() {
        RespiteBench.LOGGER.info("Registering mod blocks for " + RespiteBench.MOD_ID);
    }
}
