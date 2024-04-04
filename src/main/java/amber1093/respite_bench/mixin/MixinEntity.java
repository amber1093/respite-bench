package amber1093.respite_bench.mixin;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import amber1093.respite_bench.event.DiscardConnectedEntityCallback;
import amber1093.respite_bench.event.EntityDeathCallback;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

/** 
 * <p>Mixins to {@link Entity} constructor and {@link Entity#remove}.</p>
 * <p>Injects an event registry method for {@link DiscardConnectedEntityCallback} in the entity constructor.</p>
 * <p>Injects an event invoker for {@link EntityDeathCallback} in the entity remove method.</p>
 */
@Mixin(Entity.class)
public abstract class MixinEntity {

	@Inject(at = { @At("RETURN") }, method = "<init>*")
	public void registerConnectedEntityEvent(CallbackInfo callbackInfo) {
		DiscardConnectedEntityCallback.EVENT.register((uuidList) -> {
			if (!this.getWorld().isClient()) {
				if (uuidList.contains(this.getUuid())) {
					this.discard();
				}
			}
		});
	}

	@Inject(at = @At("HEAD"), method = "remove")
	private void invokeEntityDeathCallback(CallbackInfo callbackInfo) {
		UUID uuid = this.getUuid();
		EntityDeathCallback.EVENT.invoker().removeEntityUuid(uuid);
	}


	@Shadow
	public UUID getUuid() {
		return null;
	}

	@Shadow
	public void discard() {
		return;
	}

	@Shadow
	public World getWorld() {
		return null;
	}
}
