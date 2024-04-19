package amber1093.respite_bench.packethandler;

import amber1093.respite_bench.RespiteBenchClient;
import amber1093.respite_bench.packet.ConfigUpdatePacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.PlayPacketHandler;

@FunctionalInterface
public interface ConfigUpdatePacketS2CHandler
extends PlayPacketHandler<ConfigUpdatePacket> {
	public static void applyConfigSettings(ConfigUpdatePacket packet) {
		//update configoverride
		RespiteBenchClient.configoverride = packet.getConfig();
	}
}
