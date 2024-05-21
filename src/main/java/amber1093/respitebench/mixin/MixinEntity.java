package amber1093.respitebench.mixin;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import amber1093.respitebench.event.DiscardConnectedEntityCallback;
import amber1093.respitebench.event.EntityDeathCallback;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

/** 
 * <p>Mixins to {@link Entity} constructor and {@link Entity#remove}.</p>
 * <p>Injects an event registry method for {@link DiscardConnectedEntityCallback} in the entity constructor.</p>
 * <p>Injects an event invoker for {@link EntityDeathCallback} in the entity remove method.</p>
 */
@Mixin(Entity.class)
public abstract class MixinEntity {

	@Inject(at = @At("RETURN"), method = "<init>*")
	public void registerConnectedEntityEvent(CallbackInfo callbackInfo) {
		DiscardConnectedEntityCallback.EVENT.register((uuidList) -> {
			if (!this.getWorld().isClient() && this.getUuid() != null && uuidList.contains(this.getUuid())) {
				this.discard();
			}
		});
	}

	@Inject(at = @At("HEAD"), method = "remove")
	private void invokeEntityDeathCallback(CallbackInfo callbackInfo) {
		if (this.getUuid() != null) {
			EntityDeathCallback.EVENT.invoker().removeEntityUuid(this.getUuid());
		}
	}


	@Shadow
	public UUID getUuid() {
		throw new UnsupportedOperationException("Unimplemented method 'getUuid'");
	}

	@Shadow
	public void discard() {
		throw new UnsupportedOperationException("Unimplemented method 'discard'");
	}

	@Shadow
	public World getWorld() {
		throw new UnsupportedOperationException("Unimplemented method 'getWorld'");
	}
}
