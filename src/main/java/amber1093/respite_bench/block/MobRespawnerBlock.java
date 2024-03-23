package amber1093.respite_bench.block;

import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import amber1093.respite_bench.RespiteBench;
import amber1093.respite_bench.blockentity.MobRespawnerBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.SpawnerBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

//TODO make better texture
public class MobRespawnerBlock extends SpawnerBlock {

	public MobRespawnerBlockEntity blockEntity = null;

	public MobRespawnerBlock(Settings settings) {
		super(settings);
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		blockEntity = new MobRespawnerBlockEntity(pos, state);
		return blockEntity;
	}

	@Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return MobRespawnerBlock.checkType(type, RespiteBench.MOB_RESPAWER_BLOCK_ENTITY_TYPE, world.isClient ? MobRespawnerBlockEntity::clientTick : MobRespawnerBlockEntity::serverTick);
    }

	//TODO: vanilla spawner cannot read NBT from spawn eggs. make this work for Mob Respawner
	/*
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (!world.isClient())  {
			if (player.isCreative()) {
				if (!player.isSneaking()) {
					if (player.getMainHandStack().getItem() instanceof SpawnEggItem) {
						player.sendMessage(Text.literal("DING DING DING!!!!"));
					}
					else if (blockEntity != null) {
						player.sendMessage(Text.literal("MobRespawnerBlockEntity.setSpawnDelay(0)"));
						blockEntity.logic.setSpawnDelay(0);
						blockEntity.markDirty();
					}
				}
				else {

				}
			}
		}
		return ActionResult.PASS;
	}
	*/

	//TODO add mob name when NBT is present
	@Override
	public void appendTooltip(ItemStack stack, BlockView world, List<Text> tooltip, TooltipContext options) {

		tooltip.add(ScreenTexts.EMPTY);
		tooltip.add(Text.translatable("block.minecraft.spawner.desc1").formatted(Formatting.GRAY));
		tooltip.add(ScreenTexts.space().append(Text.translatable("block.minecraft.spawner.desc2")).formatted(Formatting.BLUE));

		tooltip.add(ScreenTexts.EMPTY);
		tooltip.add(Text.translatable("block.respite_bench.mob_respawner.tooltip.desc3").formatted(Formatting.GRAY));
		tooltip.add(Text.translatable("block.respite_bench.mob_respawner.tooltip.desc4").formatted(Formatting.BLUE));
		tooltip.add(Text.translatable("block.respite_bench.mob_respawner.tooltip.desc5").formatted(Formatting.BLUE));

		Optional<Text> optional = getEntityNameForTooltip(stack);
        if (optional.isPresent()) {
			tooltip.add(ScreenTexts.EMPTY);
            tooltip.add(optional.get());
        }
	}

	private Optional<Text> getEntityNameForTooltip(ItemStack stack) {
        NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(stack);
        if (nbtCompound != null && nbtCompound.contains("SpawnData", NbtElement.COMPOUND_TYPE)) {
			String string = nbtCompound.getCompound("SpawnData").getCompound("entity").getString("id");
			Identifier identifier = Identifier.tryParse(string);
			if (identifier != null) {
				return Registries.ENTITY_TYPE.getOrEmpty(identifier).map(entityType -> Text.translatable(entityType.getTranslationKey()).formatted(Formatting.GRAY));
			}
        }
        return Optional.empty();
    }
}
