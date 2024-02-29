package amber1093.respite_bench.item;

import amber1093.respite_bench.RespiteBench;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item FLASK = registerItem("flask", new Item(new FabricItemSettings()));
    public static final Item EMPTY_FLASK = registerItem("empty_flask", new Item(new FabricItemSettings()));

    private static void addItemsToFoodAndDrinkItemGroup(FabricItemGroupEntries entries) {
        entries.add(FLASK);
        entries.add(EMPTY_FLASK);
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(RespiteBench.MOD_ID, name), item);
    }

    public static void registerModItems() {
        RespiteBench.LOGGER.info("Registering items for " + RespiteBench.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(ModItems::addItemsToFoodAndDrinkItemGroup);
    }

}
