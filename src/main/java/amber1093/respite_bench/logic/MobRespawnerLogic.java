package amber1093.respite_bench.logic;

import org.jetbrains.annotations.Nullable;

import amber1093.respite_bench.RespiteBench;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.MobSpawnerEntry;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.World;

public abstract class MobRespawnerLogic extends MobSpawnerLogic {

	/*
	private int minSpawnDelay = 0;
	private int maxSpawnDelay = 0;
	private int spawnCount = 1;
	private int maxNearbyEntities = 999999;
	private int requiredPlayerRange = 999999;
	private int spawnRange = 1;
	*/

	

	@Override
		public void sendStatus(World world, BlockPos pos, int status) {
			world.addSyncedBlockEvent(pos, RespiteBench.MOB_RESPAWNER, status, 0);
		}

	@Override
	public void setSpawnEntry(@Nullable World world, BlockPos pos, MobSpawnerEntry spawnEntry) {
		super.setSpawnEntry(world, pos, spawnEntry);
		if (world != null) {
			BlockState blockState = world.getBlockState(pos);
			world.updateListeners(pos, blockState, blockState, Block.NO_REDRAW);
		}
	}
}
