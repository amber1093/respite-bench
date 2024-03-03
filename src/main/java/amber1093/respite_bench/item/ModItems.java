package amber1093.respite_bench.item;

import amber1093.respite_bench.RespiteBench;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item FLASK = registerItem("flask", new FlaskItem(new FabricItemSettings().food(ModFoodComponents.FLASK)));
    public static final Item EMPTY_FLASK = registerItem("empty_flask", new Item(new FabricItemSettings()));
    public static final Item FLASK_SHARD = registerItem("flask_shard", new Item(new FabricItemSettings()));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(RespiteBench.MOD_ID, name), item);
    }

    public static void registerModItems() {
        RespiteBench.LOGGER.info("Registering items for " + RespiteBench.MOD_ID);
    }

}
