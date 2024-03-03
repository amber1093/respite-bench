package amber1093.respite_bench.item;

//import net.minecraft.entity.effect.StatusEffectInstance;
//import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;

public class ModFoodComponents {

    public static final FoodComponent FLASK = new FoodComponent.Builder()
            .alwaysEdible()
        //  .statusEffect(new StatusEffectInstance(StatusEffects.INSTANT_HEALTH), 1)
        //  .statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 100), 1)
            .build();
    
}
