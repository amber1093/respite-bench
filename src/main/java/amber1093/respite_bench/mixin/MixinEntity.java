package amber1093.respite_bench.mixin;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


import amber1093.respite_bench.event.EntityDeathCallback;
import net.minecraft.entity.Entity;

/** 
 * <p>Mixins to {@link Entity}</p>
 * <p>Inserts an event invoker for entity death
 * cuz idk how to use the vanilla {@link net.minecraft.world.event.GameEvent.ENTITY_DIE}</p>
 */
@Mixin(Entity.class)
public abstract class MixinEntity {

	@Inject(at = @At("HEAD"), method = "remove")
	private void onRemove(CallbackInfo callbackInfo) {
		UUID uuid = this.getUuid();
		EntityDeathCallback.EVENT.invoker().removeEntityUuid(uuid);
	}

	@Shadow
	public UUID getUuid() {
		return null;
	}
}
