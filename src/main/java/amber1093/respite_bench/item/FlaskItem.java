package amber1093.respite_bench.item;

import amber1093.respite_bench.RespiteBench;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;

public class FlaskItem extends Item {

	public FlaskItem(Settings settings) {
		super(settings);
	}

	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		PlayerEntity playerEntity = user instanceof PlayerEntity ? (PlayerEntity)user : null;

		if (!world.isClient()) {
			new StatusEffectInstance(StatusEffects.INSTANT_HEALTH)
					.getEffectType()
					.applyInstantEffect(playerEntity, user, playerEntity, 1, 1);
			user.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 100));
		}
		
		if (playerEntity != null) {
			playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
			if (!playerEntity.getAbilities().creativeMode) {
				stack.decrement(1);
			}
		}
		
		if (playerEntity == null || !playerEntity.getAbilities().creativeMode) {
			if (stack.isEmpty()) {
				return new ItemStack(RespiteBench.EMPTY_FLASK);
			}
			if (playerEntity != null) {
				playerEntity.getInventory().insertStack(new ItemStack(RespiteBench.EMPTY_FLASK));
			}
		}
		
		return stack;
	}

	@Override
	public SoundEvent getDrinkSound() {
		return SoundEvents.ENTITY_GENERIC_DRINK;
	}

	@Override
	public SoundEvent getEatSound() {
		return SoundEvents.ENTITY_GENERIC_DRINK;
	}
}
