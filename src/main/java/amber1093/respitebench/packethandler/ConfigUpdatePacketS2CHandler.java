package amber1093.respitebench.packethandler;

import amber1093.respitebench.RespiteBenchClient;
import amber1093.respitebench.packet.ConfigUpdatePacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.PlayPacketHandler;

@FunctionalInterface
public interface ConfigUpdatePacketS2CHandler
extends PlayPacketHandler<ConfigUpdatePacket> {
	public static void applyConfigSettings(ConfigUpdatePacket packet) {
		//update configoverride
		RespiteBenchClient.configoverride = packet.getConfig();
	}
}
