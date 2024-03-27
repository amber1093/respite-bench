package amber1093.respite_bench.mixin;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import amber1093.respite_bench.RespiteBench;
import amber1093.respite_bench.event.EntityDeathCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

@Mixin(value = {LivingEntity.class})
public abstract class MixinLivingEntity extends Entity {

	public MixinLivingEntity(EntityType<?> type, World world) {
		super(type, world);
		//TODO Auto-generated constructor stub
	}

	/** 
	 * <p>Mixins to {@link LivingEntity}</p>
	 * <p>Inserts an event invoker for entity death
	 * cuz idk how to use the vanilla {@link net.minecraft.world.event.GameEvent.ENTITY_DIE}</p>
	 */
	@Inject(at = @At("HEAD"), method = "onDeath")
	private void onOnDeath(CallbackInfo ci) {
		if (!super.isRemoved() && !this.dead) {
			UUID uuid = this.getUuid();
			EntityDeathCallback.EVENT.invoker().removeEntityUuid(uuid);
			RespiteBench.LOGGER.info(String.valueOf(uuid)); //DEBUG
		}
	}

	@Shadow
	private boolean dead = false;

}
