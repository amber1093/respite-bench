package amber1093.respitebench.block;

import org.jetbrains.annotations.Nullable;

import amber1093.respitebench.RespiteBench;
import amber1093.respitebench.blockentity.MobRespawnerBlockEntity;
import amber1093.respitebench.logic.MobRespawnerLogic;
import amber1093.respitebench.screen.MobRespawnerScreen;
import net.minecraft.block.BlockState;
import net.minecraft.block.SpawnerBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
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
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return MobRespawnerBlock.checkType(type, RespiteBench.MOB_RESPAWER_BLOCK_ENTITY_TYPE, world.isClient ? MobRespawnerBlockEntity::clientTick : MobRespawnerBlockEntity::serverTick);
    }

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient) {
			if (player.isCreative() && !(player.getInventory().getMainHandStack().getItem() instanceof SpawnEggItem)) {

				BlockEntity blockEntity = world.getBlockEntity(pos);
				if (blockEntity != null && blockEntity instanceof MobRespawnerBlockEntity) {
					MobRespawnerBlockEntity mobRespawnerBlockEntity = (MobRespawnerBlockEntity)blockEntity;

					NbtCompound nbt = mobRespawnerBlockEntity.getLogic().writeNbt(new NbtCompound());
					MinecraftClient.getInstance().setScreen(
						new MobRespawnerScreen(
							Text.translatable("screen.respitebench.mob_respawner"),
							pos,
							nbt.getInt(MobRespawnerLogic.MAX_CONNECTED_ENTITIES_KEY),
							nbt.getInt(MobRespawnerLogic.SPAWN_COUNT_KEY),
							nbt.getInt(MobRespawnerLogic.REQUIRED_PLAYER_RANGE_KEY),
							nbt.getInt(MobRespawnerLogic.SPAWN_RANGE_KEY),
							nbt.getBoolean(MobRespawnerLogic.ENABLED_KEY),
							nbt.getBoolean(MobRespawnerLogic.ONE_OFF_KEY)
						)
					);
					return ActionResult.PASS;
				}
			}
		}
		return ActionResult.PASS;
	}
}
