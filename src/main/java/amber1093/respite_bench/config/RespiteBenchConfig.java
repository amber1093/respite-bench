package amber1093.respite_bench.config;

import amber1093.respite_bench.RespiteBench;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Config(name = RespiteBench.MOD_ID)
@Environment(EnvType.CLIENT)
public class RespiteBenchConfig implements ConfigData {

	@ConfigEntry.Gui.Excluded
	private static final int FLASK_HEAL_AMOUNT_DEFAULT = 12;
	@ConfigEntry.Gui.Excluded
	private static final int FLASK_USE_TIME_DEFAULT = 15;


	@ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
	public FlaskConfig flask = new FlaskConfig();

	@ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
	public BenchConfig bench = new BenchConfig();


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
	}


	@Override
	public void validatePostLoad() throws ValidationException {
		if (this.flask.healAmount < 0 || this.flask.healAmount >= Integer.MAX_VALUE) {
			this.flask.healAmount = FLASK_HEAL_AMOUNT_DEFAULT;
		}

		if (this.flask.useTime < 0 || this.flask.useTime >= Integer.MAX_VALUE) {
			this.flask.useTime = FLASK_USE_TIME_DEFAULT;
		}
	}
}
