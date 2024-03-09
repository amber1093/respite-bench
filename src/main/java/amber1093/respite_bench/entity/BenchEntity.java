package amber1093.respite_bench.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.DisplayEntity.TextDisplayEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;


public class BenchEntity extends TextDisplayEntity {

    public boolean allowKill = false;

    public BenchEntity(EntityType<? extends TextDisplayEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void tick() {
        if (!this.getWorld().isClient()) {
            if (this.hasPassengers() || !allowKill) {
                PlayerEntity player = (PlayerEntity) getFirstPassenger();
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 10, 255));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 10, 255));
            }
            else {
                this.discard();
                return;
            }
        }
    }
}
