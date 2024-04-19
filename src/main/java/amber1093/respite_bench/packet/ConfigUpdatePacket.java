package amber1093.respite_bench.packet;

import amber1093.respite_bench.RespiteBench;
import amber1093.respite_bench.config.ConfigMenu;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;

public record ConfigUpdatePacket(
	int flask_healAmount,
	int flask_useTime,
	boolean bench_restInstantly,
	boolean bench_clearPotionEffects,
	boolean bench_setSpawnPoint,
	boolean mobrespawner_ignoreSpawnRules
) implements FabricPacket {

	public static final PacketType<ConfigUpdatePacket> TYPE = (
		PacketType.create(
			RespiteBench.CONFIG_UPDATE_PACKET_ID,
			ConfigUpdatePacket::new
		)
	);

	public ConfigUpdatePacket(PacketByteBuf buf) {
		this(
			buf.readInt(),
			buf.readInt(),
			buf.readBoolean(),
			buf.readBoolean(),
			buf.readBoolean(),
			buf.readBoolean()
		);
	}

	public ConfigUpdatePacket(ConfigMenu config) {
		this(
			config.flask.healAmount,
			config.flask.useTime,
			config.bench.restInstantly,
			config.bench.clearPotionEffects,
			config.bench.setSpawnPoint,
			config.mobrespawner.ignoreSpawnRules
		);
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeInt(this.flask_healAmount());
		buf.writeInt(this.flask_useTime());
		buf.writeBoolean(this.bench_restInstantly());
		buf.writeBoolean(this.bench_clearPotionEffects());
		buf.writeBoolean(this.bench_setSpawnPoint());
		buf.writeBoolean(this.mobrespawner_ignoreSpawnRules());
	}

	public ConfigMenu getConfig() {
		ConfigMenu config =  new ConfigMenu();
		config.flask.healAmount = this.flask_healAmount();
		config.flask.useTime = this.flask_useTime();
		config.bench.restInstantly = this.bench_restInstantly();
		config.bench.clearPotionEffects = this.bench_clearPotionEffects();
		config.bench.setSpawnPoint = this.bench_setSpawnPoint();
		config.mobrespawner.ignoreSpawnRules = this.mobrespawner_ignoreSpawnRules();
		return config;
	}

	@Override
	public PacketType<ConfigUpdatePacket> getType() {
		return TYPE;
	}
}
