package amber1093.respitebench.blockentity;

import amber1093.respitebench.RespiteBench;
import amber1093.respitebench.event.EntityDeathCallback;
import amber1093.respitebench.event.UseBenchCallback;
import amber1093.respitebench.logic.MobRespawnerLogic;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.MobSpawnerEntry;
import net.minecraft.world.World;

public class MobRespawnerBlockEntity extends BlockEntity {
	
	public MobRespawnerBlockEntity(BlockPos pos, BlockState state) {
		super(RespiteBench.MOB_RESPAWER_BLOCK_ENTITY_TYPE, pos, state);

		UseBenchCallback.EVENT.register((canSpawn) -> {
			this.logic.setCanSpawn(this.world, this.pos, canSpawn);
			this.markDirty();
			return this.logic.getConnectedEntitiesUuid();
		});

		EntityDeathCallback.EVENT.register((uuidToRemove) -> {
			boolean result = this.logic.removeEntityUuid(uuidToRemove);
			if (result == true) {
				this.markDirty();
			}
			return result;
		});

	}

	public final MobRespawnerLogic logic = new MobRespawnerLogic() {

		@Override
        public void sendStatus(World world, BlockPos pos, int status) {
            world.addSyncedBlockEvent(pos, RespiteBench.MOB_RESPAWNER, status, 0);
        }

        @Override
        public void setSpawnEntry(MobSpawnerEntry spawnEntry) {
            super.setSpawnEntry(spawnEntry);
            if (world != null) {
                BlockState blockState = world.getBlockState(pos);
                world.updateListeners(pos, blockState, blockState, Block.NO_REDRAW);
            }
        }
	};

    @Override
    public void readNbt(NbtCompound nbt) {
        this.logic.readNbt(nbt);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
		this.logic.writeNbt(nbt);
    }

    public static void clientTick(World world, BlockPos pos, BlockState state, MobRespawnerBlockEntity blockEntity) {
        blockEntity.logic.clientTick(world, pos);
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, MobRespawnerBlockEntity blockEntity) {
        blockEntity.logic.serverTick((ServerWorld)world, pos);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbtCompound = this.createNbt();
        nbtCompound.remove(MobRespawnerLogic.SPAWN_POTENTIALS_KEY);
        return nbtCompound;
    }

    @Override
    public boolean onSyncedBlockEvent(int type, int data) {
        if (this.logic.handleStatus(this.world, type)) {
            return true;
        }
        return super.onSyncedBlockEvent(type, data);
    }

    @Override
    public boolean copyItemDataRequiresOperator() {
        return true;
    }

	public void resetRenderedEntity() {
		this.logic.resetRenderedEntity();
	}

    public void setEntityType(EntityType<?> entityType, Random random) {
        this.logic.setEntityId(entityType, this.world, random, this.pos);
    }

	public void setEntityNbt(NbtCompound nbtCompound, Random random) {
		this.logic.setEntityNbt(nbtCompound, this.world, random, this.pos);
	}

    public MobRespawnerLogic getLogic() {
        return this.logic;
    }

	public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
	
	public void updateSettings(int maxConnectedEntities, int spawnCount, int requiredPlayerRange, int spawnRange) {
		this.logic.updateSettings(maxConnectedEntities, spawnCount, requiredPlayerRange, spawnRange);
	}
}
