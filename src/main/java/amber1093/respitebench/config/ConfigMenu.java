package amber1093.respitebench.config;

import amber1093.respitebench.RespiteBench;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Config(name = RespiteBench.MOD_ID)
@Environment(EnvType.CLIENT)
public class ConfigMenu implements ConfigData {

	@ConfigEntry.Gui.Excluded
	public static final int FLASK_HEAL_AMOUNT_DEFAULT = 12;
	@ConfigEntry.Gui.Excluded
	public static final int FLASK_USE_TIME_DEFAULT = 15;
	@ConfigEntry.Gui.Excluded
	public static final float BENCH_DISTANCE_REQUIRED = 0;

	@ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
	public FlaskConfig flask = new FlaskConfig();
	@ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
	public BenchConfig bench = new BenchConfig();
	@ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
	public MobRespawnerConfig mobrespawner = new MobRespawnerConfig();

	public static class FlaskConfig {
		@ConfigEntry.Gui.Tooltip
		public int healAmount = FLASK_HEAL_AMOUNT_DEFAULT;
		@ConfigEntry.Gui.Tooltip
		public int useTime = FLASK_USE_TIME_DEFAULT;
	}

	public static class BenchConfig {
		@ConfigEntry.Gui.Tooltip
		public boolean restInstantly = false;
		@ConfigEntry.Gui.Tooltip
		public boolean clearPotionEffects = false;
		@ConfigEntry.Gui.Tooltip
		public boolean setSpawnPoint = true;
		@ConfigEntry.Gui.Tooltip
		public float distanceRequired = BENCH_DISTANCE_REQUIRED;
	}

	public static class MobRespawnerConfig {
		@ConfigEntry.Gui.Tooltip
		public boolean ignoreSpawnRules = true;
	}

	@Override
	public void validatePostLoad() throws ValidationException {
		if (this.flask.healAmount < 0 || this.flask.healAmount >= Integer.MAX_VALUE) {
			this.flask.healAmount = FLASK_HEAL_AMOUNT_DEFAULT;
		}

		if (this.flask.useTime < 0 || this.flask.useTime >= Integer.MAX_VALUE) {
			this.flask.useTime = FLASK_USE_TIME_DEFAULT;
		}

		if (this.bench.distanceRequired < 0 || this.bench.distanceRequired >= Float.MAX_VALUE) {
			this.bench.distanceRequired = BENCH_DISTANCE_REQUIRED;
		}
	}
}
