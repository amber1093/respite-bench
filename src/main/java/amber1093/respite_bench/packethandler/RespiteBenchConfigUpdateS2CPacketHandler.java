package amber1093.respite_bench.packethandler;

import amber1093.respite_bench.RespiteBenchClient;
import amber1093.respite_bench.packet.RespiteBenchConfigUpdatePacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.PlayPacketHandler;

@FunctionalInterface
public interface RespiteBenchConfigUpdateS2CPacketHandler
extends PlayPacketHandler<RespiteBenchConfigUpdatePacket> {
	public static void applyConfigSettings(RespiteBenchConfigUpdatePacket packet) {
		//update configoverride
		RespiteBenchClient.configoverride = packet.getConfig();
	}
}
