/*
 * Decompiled with CFR 0.2.1 (FabricMC 53fa44c9).
 */
package amber1093.respite_bench.logic;

import com.mojang.logging.LogUtils;

import java.util.Optional;
import java.util.function.Function;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.collection.Weighted;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LightType;
import net.minecraft.world.MobSpawnerEntry;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

/** <p>Mostly a copy paste of {@link net.minecraft.world.MobSpawnerLogic}, 
 * modified to only spawn mobs when called by the event {@link amber1093.respite_bench.event.UseBenchCallback}
 * which is called by {@link amber1093.respite_bench.block.BenchBlock}.</p>
 * 
 * <p>spawnRange and spawn position behaviour change: if spawn count is 1, spawn position will not be randomized.
 * 
 * <p>Added: {int maxAliveEntities}, {DataPool#Entity aliveEntities}</p>
 * <p>Removed: minSpawnDelay, maxSpawnDelay, maxNearbyEntities</p>
 * <p>{int spawnDelay} changed to {boolean canSpawn}</p>
 * <p>spawnCount default value changed from 4 to 1</p>
 * <p>spawnRange default value changed from 4 to 2 </p>
 */
public abstract class MobRespawnerLogic {
    public static final String SPAWN_DATA_KEY = "SpawnData";
    private static final Logger LOGGER = LogUtils.getLogger();
    @Nullable
    private MobSpawnerEntry spawnEntry;
    @Nullable
    private Entity renderedEntity;
	private DataPool<MobSpawnerEntry> spawnPotentials = DataPool.<MobSpawnerEntry>empty();

	private DataPool<Entity> aliveEntities = DataPool.<Entity>empty();
	private int maxAliveEntities = 1;

    private double rotation = 0;
	private double particleRotationX = 0;
	private double particleRotationY = 0;

    public boolean canSpawn = false;
    private int spawnCount = 1;
    private int spawnRange = 2;
    private int maxNearbyEntities = 99;
    private int requiredPlayerRange = 16;

    public void setEntityId(EntityType<?> type, @Nullable World world, Random random, BlockPos pos) {
        this.getSpawnEntry(world, random, pos).getNbt().putString("id", Registries.ENTITY_TYPE.getId(type).toString());
    }

    private boolean isPlayerInRange(World world, BlockPos pos) {
        return world.isPlayerInRange(
				(double)pos.getX() + 0.5,
				(double)pos.getY() + 0.5,
				(double)pos.getZ() + 0.5,
				this.requiredPlayerRange);
    }

    public void clientTick(World world, BlockPos pos) {
		if (this.isPlayerInRange(world, pos) && this.renderedEntity != null) {
            if (!this.canSpawn) {
				++this.rotation;
            }
			else {
				this.particleRotationX += 10;
				this.particleRotationY += 2;
				double particleRotationXRadian = Math.toRadians(this.particleRotationX % 360);
				double particleRotationYRadian = Math.toRadians(this.particleRotationY % 360);

				double particlePosX = (double)pos.getX() + 0.5d; 
				double particlePosY = (double)pos.getY() + 0.5d;
				double particlePosZ = (double)pos.getZ() + 0.5d;

				world.addParticle(
						ParticleTypes.SOUL_FIRE_FLAME,
						particlePosX + (0.4 * Math.cos(particleRotationXRadian)),
						particlePosY + (0.4 * Math.cos(particleRotationYRadian)),
						particlePosZ + (0.4 * Math.sin(particleRotationXRadian)),
						0, 0, 0);
			}
        }
    }

    public void serverTick(ServerWorld world, BlockPos pos) {
        if (!this.isPlayerInRange(world, pos)) {
            return;
        }
        if (!this.canSpawn) {
            ++this.rotation;
            return;
        }

		//begin spawning mobs
        boolean bl = false;
        Random random = world.getRandom();
        MobSpawnerEntry mobSpawnerEntry = this.getSpawnEntry(world, random, pos);
        for (int i = 0; i < this.spawnCount; ++i) {

			//setup
            MobSpawnerEntry.CustomSpawnRules customSpawnRules;
            NbtCompound nbtCompound = mobSpawnerEntry.getNbt();
            Optional<EntityType<?>> optional = EntityType.fromNbt(nbtCompound);
            if (optional.isEmpty()) {
                this.updateSpawns(world, pos);
                return;
            }

			//TODO add a message when it fails to summon
			//spawn position
			NbtList nbtList = nbtCompound.getList("Pos", NbtElement.DOUBLE_TYPE);
			int nbtListSize = nbtList.size();
			double d, e, f;
			d = ( nbtListSize >= 1 ? nbtList.getDouble(0) : (double)pos.getX() + (random.nextDouble() - random.nextDouble()) * (double)this.spawnRange + 0.5 );
			e = ( nbtListSize >= 2 ? nbtList.getDouble(1) : (double)(pos.getY() + random.nextInt(3) - 1) );
			f = ( nbtListSize >= 3 ? nbtList.getDouble(2) : (double)pos.getZ() + (random.nextDouble() - random.nextDouble()) * (double)this.spawnRange + 0.5 );

			//idk what happens down here
            if (!world.isSpaceEmpty(optional.get().createSimpleBoundingBox(d, e, f))) continue;
            BlockPos blockPos = BlockPos.ofFloored(d, e, f);
            if (!mobSpawnerEntry.getCustomSpawnRules().isPresent()
					? !SpawnRestriction.canSpawn(optional.get(), world, SpawnReason.SPAWNER, blockPos, world.getRandom())
					: !optional.get().getSpawnGroup().isPeaceful() && world.getDifficulty() == Difficulty.PEACEFUL
							|| !(customSpawnRules = mobSpawnerEntry.getCustomSpawnRules().get()).blockLightLimit().contains(world.getLightLevel(LightType.BLOCK, blockPos))
							|| !customSpawnRules.skyLightLimit().contains(world.getLightLevel(LightType.SKY, blockPos))) continue;
            Entity entity2 = EntityType.loadEntityWithPassengers(nbtCompound, world, entity -> {
                entity.refreshPositionAndAngles(d, e, f, entity.getYaw(), entity.getPitch());
                return entity;
            });
            if (entity2 == null) {
                this.updateSpawns(world, pos);
                return;
            }
            int k = world.getNonSpectatingEntities(entity2.getClass(), new Box(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).expand(this.spawnRange)).size();
            if (k >= this.maxNearbyEntities) {
                this.updateSpawns(world, pos);
                return;
            }
            entity2.refreshPositionAndAngles(entity2.getX(), entity2.getY(), entity2.getZ(), random.nextFloat() * 360.0f, 0.0f);
            if (entity2 instanceof MobEntity) {
                MobEntity mobEntity = (MobEntity)entity2;
                if (mobSpawnerEntry.getCustomSpawnRules().isEmpty() && !mobEntity.canSpawn(world, SpawnReason.SPAWNER) || !mobEntity.canSpawn(world)) continue;
                if (mobSpawnerEntry.getNbt().getSize() == 1 && mobSpawnerEntry.getNbt().contains("id", NbtElement.STRING_TYPE)) {
                    ((MobEntity)entity2).initialize(world, world.getLocalDifficulty(entity2.getBlockPos()), SpawnReason.SPAWNER, null, null);
                }
            }
            if (!world.spawnNewEntityAndPassengers(entity2)) {
                this.updateSpawns(world, pos);
                return;
            }
            world.syncWorldEvent(WorldEvents.SPAWNER_SPAWNS_MOB, pos, 0);
            world.emitGameEvent(entity2, GameEvent.ENTITY_PLACE, blockPos);
            if (entity2 instanceof MobEntity) {
                ((MobEntity)entity2).playSpawnEffects();
            }
            bl = true;
        }
        if (bl) {
            this.updateSpawns(world, pos);
        }
    }

    private void updateSpawns(World world, BlockPos pos) {
        Random random = world.random;
        this.canSpawn = false;
        this.spawnPotentials.getOrEmpty(random).ifPresent(spawnPotential -> this.setSpawnEntry(world, pos, (MobSpawnerEntry)spawnPotential.getData()));
        this.sendStatus(world, pos, 1);
    }

    public void readNbt(@Nullable World world, BlockPos pos, NbtCompound nbt) {

		this.canSpawn = nbt.getBoolean("CanSpawn");
		boolean spawnPotentialsExists = nbt.contains("SpawnPotentials", NbtElement.LIST_TYPE);
        boolean spawnDataExists = nbt.contains(SPAWN_DATA_KEY, NbtElement.COMPOUND_TYPE);

        if (spawnDataExists) {
            MobSpawnerEntry mobSpawnerEntry = MobSpawnerEntry.CODEC.parse(NbtOps.INSTANCE, nbt.getCompound(SPAWN_DATA_KEY)).resultOrPartial(string -> LOGGER.warn("Invalid SpawnData: {}", string)).orElseGet(MobSpawnerEntry::new);
            this.setSpawnEntry(world, pos, mobSpawnerEntry);
        }

        if (spawnPotentialsExists) {
            NbtList nbtList = nbt.getList("SpawnPotentials", NbtElement.COMPOUND_TYPE);
            this.spawnPotentials = MobSpawnerEntry.DATA_POOL_CODEC.parse(NbtOps.INSTANCE, nbtList).resultOrPartial(error -> LOGGER.warn("Invalid SpawnPotentials list: {}", error)).orElseGet(DataPool::<MobSpawnerEntry>empty);
        } else {
            this.spawnPotentials = DataPool.of(this.spawnEntry != null ? this.spawnEntry : new MobSpawnerEntry());
        }

        if (nbt.contains("SpawnCount", NbtElement.NUMBER_TYPE)) {
            this.spawnCount = nbt.getShort("SpawnCount");
        }
        if (nbt.contains("MaxNearbyEntities", NbtElement.NUMBER_TYPE)) {
            this.maxNearbyEntities = nbt.getShort("MaxNearbyEntities");
            this.requiredPlayerRange = nbt.getShort("RequiredPlayerRange");
        }
        if (nbt.contains("SpawnRange", NbtElement.NUMBER_TYPE)) {
            this.spawnRange = nbt.getShort("SpawnRange");
        }
        this.renderedEntity = null;
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putBoolean("CanSpawn", this.canSpawn);
        nbt.putShort("SpawnCount", (short)this.spawnCount);
        nbt.putShort("MaxNearbyEntities", (short)this.maxNearbyEntities);
        nbt.putShort("RequiredPlayerRange", (short)this.requiredPlayerRange);
        nbt.putShort("SpawnRange", (short)this.spawnRange);
        if (this.spawnEntry != null) {
            nbt.put(SPAWN_DATA_KEY, MobSpawnerEntry.CODEC.encodeStart(NbtOps.INSTANCE, this.spawnEntry).result().orElseThrow(() -> new IllegalStateException("Invalid SpawnData")));
        }
        nbt.put("SpawnPotentials", MobSpawnerEntry.DATA_POOL_CODEC.encodeStart(NbtOps.INSTANCE, this.spawnPotentials).result().orElseThrow());
        return nbt;
    }

    @Nullable
    public Entity getRenderedEntity(World world, Random random, BlockPos pos) {
        if (this.renderedEntity == null) {
            NbtCompound nbtCompound = this.getSpawnEntry(world, random, pos).getNbt();
            if (!nbtCompound.contains("id", NbtElement.STRING_TYPE)) {
                return null;
            }
            this.renderedEntity = EntityType.loadEntityWithPassengers(nbtCompound, world, Function.identity());
            if (nbtCompound.getSize() != 1 || this.renderedEntity instanceof MobEntity) {
                // empty if block
            }
        }
        return this.renderedEntity;
    }

    public boolean handleStatus(World world, int status) {
        if (status == 1) {
            if (world.isClient) {
                this.canSpawn = false;
            }
            return true;
        }
        return false;
    }

    protected void setSpawnEntry(@Nullable World world, BlockPos pos, MobSpawnerEntry spawnEntry) {
        this.spawnEntry = spawnEntry;
    }

    private MobSpawnerEntry getSpawnEntry(@Nullable World world, Random random, BlockPos pos) {
        if (this.spawnEntry != null) {
            return this.spawnEntry;
        }
        this.setSpawnEntry(world, pos, this.spawnPotentials.getOrEmpty(random).map(Weighted.Present::getData).orElseGet(MobSpawnerEntry::new));
        return this.spawnEntry;
    }

    public abstract void sendStatus(World var1, BlockPos var2, int var3);

    public double getRotation() {
        return this.rotation;
    }

	public void setCanSpawn(boolean canSpawn) {
		this.canSpawn = canSpawn;
	}
}

