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

		if (playerEntity != null) {
			playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
			if (!playerEntity.getAbilities().creativeMode) {
				stack.decrement(1);
				if (stack.isEmpty()) {
					return new ItemStack(RespiteBench.EMPTY_FLASK);
				}
				else {
					playerEntity.getInventory().insertStack(new ItemStack(RespiteBench.EMPTY_FLASK));
				}
			}

			if (!world.isClient()) {
				new StatusEffectInstance(StatusEffects.INSTANT_HEALTH)
						.getEffectType()
						.applyInstantEffect(playerEntity, user, playerEntity, 1, 1);
				user.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 100));
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

	@Override
	public int getMaxUseTime(ItemStack stack) {
		return 15;
	}
}
