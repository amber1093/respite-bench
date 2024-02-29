package amber1093.respite_bench.item;

import amber1093.respite_bench.RespiteBench;
import amber1093.respite_bench.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {

    public static final ItemGroup RESPITE_BENCH_GROUP = Registry.register(
            Registries.ITEM_GROUP,
            new Identifier(RespiteBench.MOD_ID, "respite_bench"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.respite_bench"))
                .icon(() -> new ItemStack(ModItems.FLASK))
                    .entries((displayContext, entries) -> {
                        entries.add(ModItems.FLASK);
                        entries.add(ModItems.EMPTY_FLASK);
                        entries.add(ModBlocks.MOB_RESPAWNER);
                        entries.add(ModBlocks.BENCH);
                    }).build()
    );

    public static void registerItemGroups() {
        RespiteBench.LOGGER.info("Registering item groups for " + RespiteBench.MOD_ID);
    }
}
