package amber1093.respite_bench.item;

import amber1093.respite_bench.RespiteBench;
import amber1093.respite_bench.RespiteBenchClient;
import net.minecraft.entity.LivingEntity;
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
		PlayerEntity player = user instanceof PlayerEntity ? (PlayerEntity)user : null;

		if (player != null) {
			player.incrementStat(Stats.USED.getOrCreateStat(this));
			if (!player.getAbilities().creativeMode) {
				stack.decrement(1);
				if (stack.isEmpty()) {
					return new ItemStack(RespiteBench.EMPTY_FLASK);
				}
				else {
					player.getInventory().insertStack(new ItemStack(RespiteBench.EMPTY_FLASK));
				}

				player.heal(RespiteBenchClient.getFlaskHealAmount());
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
		return RespiteBenchClient.getFlaskUseTime();
	}
}
