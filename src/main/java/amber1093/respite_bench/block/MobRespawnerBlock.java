package amber1093.respite_bench.block;

import java.util.List;

import amber1093.respite_bench.blockentity.MobRespawnerBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.SpawnerBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class MobRespawnerBlock extends SpawnerBlock {

	public MobRespawnerBlock(Settings settings) {
		super(settings);
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new MobRespawnerBlockEntity(pos, state);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (!world.isClient())  {
			if (player.isCreative()) {
				if (!player.isSneaking()) {
					if (player.getMainHandStack().getItem() instanceof SpawnEggItem) {
						player.sendMessage(Text.literal("DING DING DING!!!!"));
					}
				}
				else {

				}
			}
		}
		return ActionResult.PASS;
	}

	@Override
	public void appendTooltip(ItemStack stack, BlockView world, List<Text> tooltip, TooltipContext options) {

		tooltip.add(ScreenTexts.EMPTY);
		tooltip.add(Text.translatable("block.minecraft.spawner.desc1").formatted(Formatting.GRAY));
		tooltip.add(ScreenTexts.space().append(Text.translatable("block.minecraft.spawner.desc2")).formatted(Formatting.BLUE));

		tooltip.add(ScreenTexts.EMPTY);
		tooltip.add(Text.translatable("block.respite_bench.mob_respawner.tooltip.desc3").formatted(Formatting.GRAY));
		tooltip.add(Text.translatable("block.respite_bench.mob_respawner.tooltip.desc4").formatted(Formatting.BLUE));
		tooltip.add(Text.translatable("block.respite_bench.mob_respawner.tooltip.desc5").formatted(Formatting.BLUE));
	}
}
