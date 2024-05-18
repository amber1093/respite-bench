package amber1093.respitebench.logic;

import com.mojang.logging.LogUtils;

import amber1093.respitebench.RespiteBenchClient;
import amber1093.respitebench.packet.MobRespawnerUpdateC2SPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.collection.Weighted;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LightType;
import net.minecraft.world.MobSpawnerEntry;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

//! TODO investigate for possible chunkloading related bugs
	//? known bug: triggered when a connected entity is killed while mob respawner is unloaded
	//* known bug, unknown reason: sometimes respawner keeps spawning mobs with no regard for maxConnectedEntities

/** <p>Mostly a copy paste of {@link net.minecraft.world.MobSpawnerLogic}, 
 * modified to only spawn mobs when called by the event {@link amber1093.respitebench.event.UseBenchCallback}
 * 
 * <p>Entities spawned by this block will receive the {@code boolean PersistenceRequired} tag with the value {@code true}.
 * This tag is used to prevent chunk loading related bugs.</p>
 * 
 * <p>List of changes compared to the vanilla spawner:</p>
 * <p>Added: {@code int maxConnectedEntities}, {@code List<UUID> connectedEntitiesUuid}</p>
 * <p>Removed: minSpawnDelay, maxSpawnDelay, maxNearbyEntities</p>
 * <p>{@code int spawnDelay} changed to {@code boolean canSpawn}</p>
 * <p>spawnCount default value changed from 4 to 1</p>
 * <p>spawnRange default value changed from 4 to 2 </p>
 */
public abstract class MobRespawnerLogic {
    public static final String SPAWN_DATA_KEY = "SpawnData";
	public static final String CAN_SPAWN_KEY = "CanSpawn";
	public static final String ACTIVE_KEY = "Active";
	public static final String ONE_OFF_KEY = "OneOff";
	public static final String SPAWN_POTENTIALS_KEY = "SpawnPotentials";
	public static final String MAX_CONNECTED_ENTITIES_KEY = "MaxConnectedEntities";
	public static final String SPAWN_COUNT_KEY = "SpawnCount";
	public static final String REQUIRED_PLAYER_RANGE_KEY = "RequiredPlayerRange";
	public static final String SPAWN_RANGE_KEY = "SpawnRange";
	public static final String CONNECTED_ENTITIES_UUID_KEY = "ConnectedEntitiesUuid";

    private static final Logger LOGGER = LogUtils.getLogger();
    @Nullable
    private Entity renderedEntity;
    @Nullable
    private MobSpawnerEntry spawnEntry;
	private DataPool<MobSpawnerEntry> spawnPotentials = DataPool.<MobSpawnerEntry>empty();

	private List<UUID> connectedEntitiesUuid = new ArrayList<>();
	public int maxConnectedEntities = 1;

    public boolean canSpawn = false;
	public boolean active = true;
	public boolean oneOff = false;
    public int spawnCount = 1;
    public int spawnRange = 2;
    public int requiredPlayerRange = 16;

	//just for particle visuals, not stored in nbt
	private double rotation = 0;
	private double particleRotationX = 0;
	private double particleRotationY = 0;

    private boolean isPlayerInRange(World world, BlockPos pos) {
        return world.isPlayerInRange(
				(double)pos.getX() + 0.5,
				(double)pos.getY() + 0.5,
				(double)pos.getZ() + 0.5,
				this.requiredPlayerRange);
    }

    public void clientTick(World world, BlockPos pos) {
		if (this.isPlayerInRange(world, pos) && this.renderedEntity != null) {

			ParticleEffect particleType = ParticleTypes.SOUL_FIRE_FLAME;
            if (!this.canSpawn) {
				++this.rotation;
            }
			else {
				particleType = ParticleTypes.SMOKE;
			}

			this.particleRotationX += 10;
			this.particleRotationY += 2;
			double particleRotationXRadian = Math.toRadians(this.particleRotationX % 360);
			double particleRotationYRadian = Math.toRadians(this.particleRotationY % 360);

			double particlePosX = (double)pos.getX() + 0.5d; 
			double particlePosY = (double)pos.getY() + 0.5d;
			double particlePosZ = (double)pos.getZ() + 0.5d;

			world.addParticle(
					particleType,
					particlePosX + (0.4 * Math.cos(particleRotationXRadian)),
					particlePosY + (0.4 * Math.cos(particleRotationYRadian)),
					particlePosZ + (0.4 * Math.sin(particleRotationXRadian)),
					0, 0, 0);
        }
    }

    public void serverTick(ServerWorld world, BlockPos pos) {

        if (!this.isPlayerInRange(world, pos) || !this.canSpawn || !this.active) {
            return;
        }

		int currentConnectedEntityAmount = getConnectedEntityAmount();
		if (currentConnectedEntityAmount >= maxConnectedEntities) {
			this.updateSpawns(world, pos, false);
			return;
		}

        boolean spawnSuccessful = false;
        Random random = world.getRandom();

		//if PersistenceRequired is not present, add it
		if (this.getSpawnEntry(world, random, pos).getNbt().getCompound("EntityTag").getBoolean("PersistenceRequired") == false) {
			setEntityNbt(null, world, random, pos);
		}
        MobSpawnerEntry mobSpawnerEntry = this.getSpawnEntry(world, random, pos);

		//mob spawning logic
        for (int i = 0; i < this.spawnCount; ++i) {

			//setup
            MobSpawnerEntry.CustomSpawnRules customSpawnRules;
            NbtCompound nbtCompound = mobSpawnerEntry.getNbt();
            Optional<EntityType<?>> optional = EntityType.fromNbt(nbtCompound);
            if (optional.isEmpty()) {
                this.updateSpawns(world, pos, false);
                return;
            }

			//spawn position
			NbtList nbtList = nbtCompound.getList("Pos", NbtElement.DOUBLE_TYPE);
			int nbtListSize = nbtList.size();
			double d, e, f;
			d = ( nbtListSize >= 1 ? nbtList.getDouble(0) : (double)pos.getX() + (random.nextDouble() - random.nextDouble()) * (double)this.spawnRange + 0.5 );
			e = ( nbtListSize >= 2 ? nbtList.getDouble(1) : (double)(pos.getY() + random.nextInt(3) - 1) );
			f = ( nbtListSize >= 3 ? nbtList.getDouble(2) : (double)pos.getZ() + (random.nextDouble() - random.nextDouble()) * (double)this.spawnRange + 0.5 );

			//check if mob can spawn in given position
            if (!world.isSpaceEmpty(optional.get().createSimpleBoundingBox(d, e, f))) continue;

			//check for spawn rules, peaceful mode, light levels, etc.
			BlockPos blockPos = BlockPos.ofFloored(d, e, f);
			if (RespiteBenchClient.getMobRespawnerIgnoreSpawnRules() == false) {	//TODO doesnt work if light level is above 12 for some reason
				if (!mobSpawnerEntry.getCustomSpawnRules().isPresent()
						? !SpawnRestriction.canSpawn(optional.get(), world, SpawnReason.SPAWNER, blockPos, world.getRandom())
						: !optional.get().getSpawnGroup().isPeaceful() && world.getDifficulty() == Difficulty.PEACEFUL
								|| !(customSpawnRules = mobSpawnerEntry.getCustomSpawnRules().get()).blockLightLimit().contains(world.getLightLevel(LightType.BLOCK, blockPos))
								|| !customSpawnRules.skyLightLimit().contains(world.getLightLevel(LightType.SKY, blockPos))) {
					continue;
				}
			}

			//load entity
            Entity entity2 = EntityType.loadEntityWithPassengers(nbtCompound, world, entity -> {
                entity.refreshPositionAndAngles(d, e, f, entity.getYaw(), entity.getPitch());
                return entity;
            });

			//cancel if entity doesnt exist
            if (entity2 == null) {
                this.updateSpawns(world, pos, false);
                return;
            }

			//prepare mob spawn
            entity2.refreshPositionAndAngles(entity2.getX(), entity2.getY(), entity2.getZ(), random.nextFloat() * 360.0f, 0.0f);
            if (entity2 instanceof MobEntity) {
                MobEntity mobEntity = (MobEntity)entity2;
                if (mobSpawnerEntry.getCustomSpawnRules().isEmpty() && !mobEntity.canSpawn(world, SpawnReason.SPAWNER) || !mobEntity.canSpawn(world)) { 
					continue;
				}

                if (mobSpawnerEntry.getNbt().getSize() == 1 && mobSpawnerEntry.getNbt().contains("id", NbtElement.STRING_TYPE)) {
                    ((MobEntity)entity2).initialize(world, world.getLocalDifficulty(entity2.getBlockPos()), SpawnReason.SPAWNER, null, null);
                }
            }

			//spawn mob
            if (!world.spawnNewEntityAndPassengers(entity2)) {
                this.updateSpawns(world, pos, false);
                return;
            }

			//get uuid
			this.connectedEntitiesUuid.add(entity2.getUuid());

			//notify server
            world.syncWorldEvent(WorldEvents.SPAWNER_SPAWNS_MOB, pos, 0);
            world.emitGameEvent(entity2, GameEvent.ENTITY_PLACE, blockPos);

			//particles
            if (entity2 instanceof MobEntity) {
                ((MobEntity)entity2).playSpawnEffects();
            }

            spawnSuccessful = true;

			//stop if max entity limit is reached
			currentConnectedEntityAmount += 1;
			if (currentConnectedEntityAmount >= maxConnectedEntities) {
				this.updateSpawns(world, pos, false);
				break;
			}
        }

        if (spawnSuccessful) {
            this.updateSpawns(world, pos, true);
        }
    }

    private void updateSpawns(World world, BlockPos pos, boolean canSpawn) {
        Random random = world.random;
        this.canSpawn = canSpawn;
        this.spawnPotentials.getOrEmpty(random).ifPresent(spawnPotential -> this.setSpawnEntry((MobSpawnerEntry)spawnPotential.getData()));
        this.sendStatus(world, pos, 1);
    }

	public void setCanSpawn(World world, BlockPos pos, boolean canSpawn) {
		this.canSpawn = canSpawn;
		this.sendStatus(world, pos, 1);
	}

    public void readNbt(NbtCompound nbt) {

		if (nbt.contains(ACTIVE_KEY)) {
			this.active = nbt.getBoolean(ACTIVE_KEY);
		}

		if (nbt.contains(ONE_OFF_KEY)) {
			this.oneOff = nbt.getBoolean(ONE_OFF_KEY);
		}

		if (nbt.contains(CAN_SPAWN_KEY)) {
			this.canSpawn = nbt.getBoolean(CAN_SPAWN_KEY);
		}

        if (nbt.contains(SPAWN_DATA_KEY, NbtElement.COMPOUND_TYPE)) {
			// vanilla code that i dont understand
			MobSpawnerEntry mobSpawnerEntry = MobSpawnerEntry.CODEC.parse(NbtOps.INSTANCE, nbt.getCompound(SPAWN_DATA_KEY)).resultOrPartial(string -> LOGGER.warn("Invalid SpawnData: {}", string)).orElseGet(MobSpawnerEntry::new);
			this.setSpawnEntry(mobSpawnerEntry);
        }

		// vanilla code that i dont understand
        if (nbt.contains(SPAWN_POTENTIALS_KEY, NbtElement.LIST_TYPE)) {
            NbtList nbtList = nbt.getList(SPAWN_POTENTIALS_KEY, NbtElement.COMPOUND_TYPE);
            this.spawnPotentials = MobSpawnerEntry.DATA_POOL_CODEC.parse(NbtOps.INSTANCE, nbtList).resultOrPartial(error -> LOGGER.warn("Invalid SpawnPotentials list: {}", error)).orElseGet(DataPool::<MobSpawnerEntry>empty);
        } else {
            this.spawnPotentials = DataPool.of(this.spawnEntry != null ? this.spawnEntry : new MobSpawnerEntry());
        }

        if (nbt.contains(SPAWN_COUNT_KEY, NbtElement.NUMBER_TYPE)) {
            this.spawnCount = nbt.getShort(SPAWN_COUNT_KEY);
        }
        if (nbt.contains(REQUIRED_PLAYER_RANGE_KEY, NbtElement.NUMBER_TYPE)) {
            this.requiredPlayerRange = nbt.getShort(REQUIRED_PLAYER_RANGE_KEY);
        }
        if (nbt.contains(SPAWN_RANGE_KEY, NbtElement.NUMBER_TYPE)) {
            this.spawnRange = nbt.getShort(SPAWN_RANGE_KEY);
        }

		if (nbt.contains(CONNECTED_ENTITIES_UUID_KEY, NbtElement.LIST_TYPE)) {
			this.connectedEntitiesUuid.clear();
			NbtList nbtList = nbt.getList(CONNECTED_ENTITIES_UUID_KEY, NbtElement.COMPOUND_TYPE);
			
			for (int i = 0; i < nbtList.size(); i++) {
				NbtCompound nbtCompound = nbtList.getCompound(i);
				this.connectedEntitiesUuid.add(nbtCompound.getUuid(String.valueOf(i)));
			}
		}

		if (nbt.contains(MAX_CONNECTED_ENTITIES_KEY, NbtElement.NUMBER_TYPE)) {
			this.maxConnectedEntities = nbt.getShort(MAX_CONNECTED_ENTITIES_KEY);
		}

        this.renderedEntity = null;
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
		nbt.putBoolean(ACTIVE_KEY, this.active);
		nbt.putBoolean(ONE_OFF_KEY, this.oneOff);
        nbt.putBoolean(CAN_SPAWN_KEY, this.canSpawn);
        nbt.putShort(SPAWN_COUNT_KEY, (short)this.spawnCount);
        nbt.putShort(REQUIRED_PLAYER_RANGE_KEY, (short)this.requiredPlayerRange);
        nbt.putShort(SPAWN_RANGE_KEY, (short)this.spawnRange);
        if (this.spawnEntry != null) {
			nbt.put(SPAWN_DATA_KEY, MobSpawnerEntry.CODEC.encodeStart(NbtOps.INSTANCE, this.spawnEntry).result().orElseThrow(() -> new IllegalStateException("Invalid SpawnData")));
        }
		nbt.put(SPAWN_POTENTIALS_KEY, MobSpawnerEntry.DATA_POOL_CODEC.encodeStart(NbtOps.INSTANCE, this.spawnPotentials).result().orElseThrow());

		if (this.getConnectedEntityAmount() > 0) {
			NbtList nbtList = new NbtList();
			for (int i = 0; i < this.getConnectedEntityAmount(); i++) {
				NbtCompound nbtCompound = new NbtCompound();
				nbtCompound.putUuid(String.valueOf(i), this.connectedEntitiesUuid.get(i));
				nbtList.add(nbtCompound);
			}
			nbt.put(CONNECTED_ENTITIES_UUID_KEY, nbtList);
		}
		nbt.putShort(MAX_CONNECTED_ENTITIES_KEY, (short)this.maxConnectedEntities);

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

	public void setEntityId(EntityType<?> type, @Nullable World world, Random random, BlockPos pos) {
        this.getSpawnEntry(world, random, pos).getNbt().putString("id", Registries.ENTITY_TYPE.getId(type).toString());
    }

	public void setEntityNbt(@Nullable NbtCompound nbt, @Nullable World world, Random random, BlockPos pos) {
		if (nbt != null) {
			this.getSpawnEntry(world, random, pos).getNbt().copyFrom(nbt.getCompound("EntityTag")).copyFrom(getPersistentTag());
		}
		else {
			this.getSpawnEntry(world, random, pos).getNbt().copyFrom(getPersistentTag());
		}
	}

    private MobSpawnerEntry getSpawnEntry(@Nullable World world, Random random, BlockPos pos) {
		if (this.spawnEntry == null) {
			this.setSpawnEntry(this.spawnPotentials.getOrEmpty(random).map(Weighted.Present::getData).orElseGet(MobSpawnerEntry::new));
        }
        return this.spawnEntry;
    }

	public void setSpawnEntry(MobSpawnerEntry spawnEntry) {
		this.spawnEntry = spawnEntry;
	}

	public static NbtCompound getPersistentTag() {
		NbtCompound extraNbt = new NbtCompound(); 
		extraNbt.putBoolean("PersistenceRequired", true);
		return extraNbt;
	}

	public boolean removeEntityUuid(UUID uuidToRemove) {
		boolean success = this.connectedEntitiesUuid.remove(uuidToRemove);
		if (success && !this.canSpawn && this.oneOff && this.active && this.getConnectedEntitiesUuid().isEmpty()) {
			this.active = false;
		}
		return success;
	}

	public void updateSettings(MobRespawnerUpdateC2SPacket packet) {

		if (maxConnectedEntities >= 0) {
			this.maxConnectedEntities = packet.maxConnectedEntities();
		}

		if (spawnCount >= 0) {
			this.spawnCount = packet.spawnCount();
		}

		if (requiredPlayerRange >= 0) {
			this.requiredPlayerRange = packet.requiredPlayerRange();
		}

		if (spawnRange >= 0) {
			this.spawnRange = packet.spawnRange();
		}

		this.active = packet.active();
		this.oneOff = packet.oneOff();
	}

    public abstract void sendStatus(World var1, BlockPos var2, int var3);

    public double getRotation() {
        return this.rotation;
    }

	public List<UUID> getConnectedEntitiesUuid() {
		return this.connectedEntitiesUuid;
	}

	public int getConnectedEntityAmount() {
		return this.connectedEntitiesUuid.size();
	}

	public void resetRenderedEntity() {
		this.renderedEntity = null;
	}
}

