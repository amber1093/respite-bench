package amber1093.respitebench.logic;

import com.mojang.logging.LogUtils;

import amber1093.respitebench.RespiteBenchClient;
import amber1093.respitebench.blockentity.MobRespawnerBlockEntity;
import amber1093.respitebench.event.DiscardConnectedEntityCallback;
import amber1093.respitebench.packet.MobRespawnerUpdateC2SPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import net.minecraft.block.BlockState;
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
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.slf4j.Logger;

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
	public static final String SPAWN_AMOUNT_LEFT_KEY = "SpawnAmountLeft";
	public static final String ENABLED_KEY = "Enabled";
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

    public int spawnAmountLeft = 0;
	public boolean enabled = true;
	public boolean oneOff = false;
    public int spawnCount = 1;
    public int spawnRange = 2;
    public int requiredPlayerRange = 16;

	//just for particle visuals, not stored in nbt
	private static double rotation = 0;
	private static boolean canRotate = false;
	private static Vector3d particlePos = new Vector3d();
	private static Vector2d particleRotationPos = new Vector2d();
	private static Vector2d particleRotationRadian = new Vector2d();

    private static boolean isPlayerInRange(World world, BlockPos pos, int requiredPlayerRange) {
        return world.isPlayerInRange(
				(double)pos.getX() + 0.5,
				(double)pos.getY() + 0.5,
				(double)pos.getZ() + 0.5,
				requiredPlayerRange);
    }

    public static void clientTick(World world, BlockPos pos, BlockState state, MobRespawnerBlockEntity blockEntity) {
		if (canRotate) {
			++rotation;
			canRotate = false;

			particleRotationPos.x += 10;
			particleRotationPos.y += 2;
			particleRotationRadian.x = Math.toRadians(particleRotationPos.x % 360);
			particleRotationRadian.y = Math.toRadians(particleRotationPos.y % 360);

			particlePos.x = (0.4 * Math.cos(particleRotationRadian.x)); 
			particlePos.y = (0.4 * Math.cos(particleRotationRadian.y));
			particlePos.z = (0.4 * Math.sin(particleRotationRadian.x));
		}

		if (isPlayerInRange(world, pos, blockEntity.logic.requiredPlayerRange) && blockEntity.logic.renderedEntity != null) {
			ParticleEffect particleType = ParticleTypes.SOUL_FIRE_FLAME;
            if (blockEntity.logic.spawnAmountLeft > 0) {
				particleType = ParticleTypes.SMOKE;
			}

			world.addParticle(
					particleType,
					particlePos.x + (double)pos.getX() + 0.5d, 
					particlePos.y + (double)pos.getY() + 0.5d, 
					particlePos.z + (double)pos.getZ() + 0.5d, 
					0, 0, 0);
        }
    }

    public void serverTick(ServerWorld world, BlockPos pos) {

		canRotate = true;

        if (this.spawnAmountLeft <= 0 || !this.enabled || !isPlayerInRange(world, pos, this.requiredPlayerRange)) {
            return;
        }

		if (this.getConnectedEntityAmount() >= this.maxConnectedEntities) {
			this.updateSpawns(world, pos, 0);
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
                this.updateSpawns(world, pos, 0);
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
			if (RespiteBenchClient.getMobRespawnerIgnoreSpawnRules() == false) {
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
                this.updateSpawns(world, pos, 0);
                return;
            }

			//prepare mob spawn
            entity2.refreshPositionAndAngles(entity2.getX(), entity2.getY(), entity2.getZ(), random.nextFloat() * 360.0f, 0.0f);
            if (entity2 instanceof MobEntity) {
                MobEntity mobEntity = (MobEntity)entity2;

				//second set of spawn rule checks
				if (RespiteBenchClient.getMobRespawnerIgnoreSpawnRules() == false) {
					if (mobSpawnerEntry.getCustomSpawnRules().isEmpty() && !mobEntity.canSpawn(world, SpawnReason.SPAWNER) || !mobEntity.canSpawn(world)) { 
						continue;
					}
				}

				//initialize mob
                if (mobSpawnerEntry.getNbt().getSize() == 1 && mobSpawnerEntry.getNbt().contains("id", NbtElement.STRING_TYPE)) {
                    ((MobEntity)entity2).initialize(world, world.getLocalDifficulty(entity2.getBlockPos()), SpawnReason.SPAWNER, null, null);
                }
            }

			//spawn mob
            if (!world.spawnNewEntityAndPassengers(entity2)) {
                this.updateSpawns(world, pos, 0);
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
			if (--this.spawnAmountLeft <= 0) {
				this.updateSpawns(world, pos, 0);
				break;
			}
        }

        if (spawnSuccessful) {
            this.updateSpawns(world, pos, this.spawnAmountLeft);
        }
    }

    private void updateSpawns(World world, BlockPos pos, int spawnAmountLeft) {
        Random random = world.random;
        this.spawnAmountLeft = spawnAmountLeft;
        this.spawnPotentials.getOrEmpty(random).ifPresent(spawnPotential -> this.setSpawnEntry((MobSpawnerEntry)spawnPotential.getData()));
        this.sendStatus(world, pos, 1);
    }

	public void setCanSpawn(World world, BlockPos pos, boolean canSpawn) {
		this.spawnAmountLeft = ((canSpawn || this.enabled) ? this.maxConnectedEntities : 0);
		this.sendStatus(world, pos, 1);
	}

    public void readNbt(NbtCompound nbt) {

		if (nbt.contains(ENABLED_KEY)) {
			this.enabled = nbt.getBoolean(ENABLED_KEY);
		}

		if (nbt.contains(ONE_OFF_KEY)) {
			this.oneOff = nbt.getBoolean(ONE_OFF_KEY);
		}

		if (nbt.contains(SPAWN_AMOUNT_LEFT_KEY)) {
			this.spawnAmountLeft = nbt.getInt(SPAWN_AMOUNT_LEFT_KEY);
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
		nbt.putBoolean(ENABLED_KEY, this.enabled);
		nbt.putBoolean(ONE_OFF_KEY, this.oneOff);
        nbt.putInt(SPAWN_AMOUNT_LEFT_KEY, this.spawnAmountLeft);
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
                this.spawnAmountLeft = 0;
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
		boolean success = this.getConnectedEntitiesUuid().remove(uuidToRemove);
		if (success && this.spawnAmountLeft <= 0 && this.oneOff && this.enabled && this.getConnectedEntitiesUuid().isEmpty()) {
			this.enabled = false;
		}
		return success;
	}

	public void updateSettings(MobRespawnerUpdateC2SPacket packet) {

		if (this.maxConnectedEntities >= 0) {
			this.maxConnectedEntities = packet.maxConnectedEntities();
		}

		if (this.spawnCount >= 0) {
			this.spawnCount = packet.spawnCount();
		}

		if (this.requiredPlayerRange >= 0) {
			this.requiredPlayerRange = packet.requiredPlayerRange();
		}

		if (this.spawnRange >= 0) {
			this.spawnRange = packet.spawnRange();
		}

		if (this.enabled == true && packet.enabled() == false) {
			DiscardConnectedEntityCallback.EVENT.invoker().discardConnectedEntities(getConnectedEntitiesUuid());
		}

		this.enabled = packet.enabled();
		this.oneOff = packet.oneOff();
	}

    public abstract void sendStatus(World var1, BlockPos var2, int var3);

    public double getRotation() {
        return rotation;
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

