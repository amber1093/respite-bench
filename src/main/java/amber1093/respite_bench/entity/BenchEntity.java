package amber1093.respite_bench.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.DisplayEntity.TextDisplayEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class BenchEntity extends TextDisplayEntity {

	public BenchEntity(EntityType<? extends TextDisplayEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	public void tick() {
		if (!this.getWorld().isClient()) {
			if (this.hasPassengers()) {
				Entity passenger = this.getFirstPassenger();
				if (passenger != null) {
					PlayerEntity player = (PlayerEntity)passenger;
					//TODO make this a config
					player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 10, 255, true, false));
					player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 10, 255, true, false));
				}
				else {
					this.discard();
					return;
				}
			}
			else {
				this.discard();
				return;
			}
		}
	}
}
