package amber1093.respite_bench.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import amber1093.respite_bench.RespiteBench;
import amber1093.respite_bench.block.BenchBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/**
 * <p>Mixins to {@link PlayerEntity#findRespawnPosition}.</p>
 * Used to add player respawning behaviour for {@link BenchBlock}.
 */
@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity {

	@Inject(at = @At("HEAD"), method = "findRespawnPosition", cancellable = true)
	private static Optional<Vec3d> onFindRespawnPosition(ServerWorld world, BlockPos pos, float angle, boolean forced, boolean alive, CallbackInfoReturnable<Optional<Vec3d>> callbackInfoReturnable) {
		RespiteBench.LOGGER.info("onFindRespawnPosition executed"); //DEBUG
		BlockState blockState = world.getBlockState(pos);
		Block block = blockState.getBlock();
		if (block instanceof BenchBlock) {
			RespiteBench.LOGGER.info("onFindRespawnPosition: block is BenchBlock"); //DEBUG
			Optional<Vec3d> optional = BenchBlock.findRespawnPosition(EntityType.PLAYER, world, pos);
			callbackInfoReturnable.setReturnValue(optional);
		}
		return null;
	}	
}
