package amber1093.respite_bench.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import amber1093.respite_bench.RespiteBench;
import amber1093.respite_bench.blockentity.MobRespawnerBlockEntity;

/** 
 * <p>Mixins to {@link SpawnEggItem#useOnBlock}</p>
 * <p>Copy-pasted functionality of {@link MobSpawnerBlockEntity} interaction with {@link SpawnEggItem},
 * modified for {@link MobRespawnerBlockEntity}.</p>
 */
@Mixin(SpawnEggItem.class)
public abstract class MixinSpawnEggItem {

	@Inject(at = @At("HEAD"), method = "useOnBlock", cancellable = true)
	private ActionResult useOnMobRespawner(ItemUsageContext context, CallbackInfoReturnable<ActionResult> callbackInfoReturnable) {
		if (context.getPlayer().isCreative()) {
			World world = context.getWorld();
			Random random = world.getRandom();
			BlockPos blockPos = context.getBlockPos();
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			BlockState blockState = world.getBlockState(blockPos);
			if ((blockState.isOf(RespiteBench.MOB_RESPAWNER)) && (blockEntity instanceof MobRespawnerBlockEntity)) {
				ItemStack itemStack = context.getStack();
				NbtCompound spawnEggNbt = itemStack.getNbt();
				MobRespawnerBlockEntity mobRespawnerBlockEntity = (MobRespawnerBlockEntity) blockEntity;

				//run vanilla code first
				EntityType<?> entityType = this.getEntityType(spawnEggNbt);
				mobRespawnerBlockEntity.setEntityType(entityType, random);

				//if nbt is present, copy full nbt data
				if (spawnEggNbt != null && spawnEggNbt.contains("EntityTag", NbtElement.COMPOUND_TYPE)) {

					mobRespawnerBlockEntity.setEntityNbt(spawnEggNbt, random);
					mobRespawnerBlockEntity.resetRenderedEntity();
				}

				blockEntity.markDirty();
				world.updateListeners(blockPos, blockState, blockState, Block.NOTIFY_ALL);
				world.emitGameEvent((Entity)context.getPlayer(), GameEvent.BLOCK_CHANGE, blockPos);
				callbackInfoReturnable.setReturnValue(ActionResult.SUCCESS);
			}
		}
		return ActionResult.CONSUME;
	}

	@Shadow
	public EntityType<?> getEntityType(NbtCompound nbt) {
		return null;
	}
}