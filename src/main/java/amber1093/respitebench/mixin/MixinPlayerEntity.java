package amber1093.respitebench.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import amber1093.respitebench.block.BenchBlock;
import amber1093.respitebench.block.MobRespawnerBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

/**
 * <p>Mixins to {@link PlayerEntity#findRespawnPosition} and {@link PlayerEntity#dropInventory}.</p>
 * Adds logic required to make {@link BenchBlock} work as a spawn point and make {@link MobRespawnerBlock} respawn mobs upon death
 */
@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity {

	protected MixinPlayerEntity(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	/**
	 * <p>Injects to {@link PlayerEntity#findRespawnPosition} which allows {@link BenchBlock} to be used as a spawn point and reset all {@link MobRespawnerBlock}</p>
	 */
	@Inject(at = @At("HEAD"), method = "findRespawnPosition", cancellable = true)
	private static Optional<Vec3d> onFindRespawnPosition(ServerWorld world, BlockPos pos, float angle, boolean forced, boolean alive, CallbackInfoReturnable<Optional<Vec3d>> callbackInfoReturnable) {

		BenchBlock.mobRespawnerUpdate();

		if (world.getBlockState(pos).getBlock() instanceof BenchBlock) {
			Optional<Vec3d> optional = BenchBlock.findRespawnPosition(EntityType.PLAYER, world, pos);
			callbackInfoReturnable.setReturnValue(optional);
		}
		return null;
	}

	/**
	 * <p>Injects to {@link PlayerEntity#dropInventory} which makes flasks refill when {@link GameRules.KEEP_INVENTORY} is true.</p>
	 */
	@Inject(at = @At("HEAD"), method = "dropInventory")
	public void refillFlasksOnRespawn(CallbackInfo callbackInfo) {
		//TODO (config) check if spawn point block is a bench
		if (this.getWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY) == true) {
			BenchBlock.refillFlasks(this.getInventory());
		}
	}

	@Shadow
	public PlayerInventory getInventory() {
		throw new UnsupportedOperationException("Unimplemented method 'getInventory'");
	}
}
