package amber1093.respite_bench.packethandler;

import amber1093.respite_bench.RespiteBench;
import amber1093.respite_bench.packet.RespiteBenchConfigUpdatePacket;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.PlayPacketHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

@FunctionalInterface
public interface RespiteBenchConfigUpdateC2SPacketHandler
extends PlayPacketHandler<RespiteBenchConfigUpdatePacket> {
	public static void updateConfigSettings(RespiteBenchConfigUpdatePacket packet, ServerPlayerEntity player) {
		MinecraftServer server = player.getServer();
		if (server != null && (player.hasPermissionLevel(4) || server.isSingleplayer())) {

			//update serverconfig
			RespiteBench.serverconfig = packet.getConfig();

			//send config data to all players
			RespiteBenchConfigUpdatePacket newPacket = new RespiteBenchConfigUpdatePacket(packet.getConfig());
			sendToAllPlayers(newPacket, server);

		}
	}

	static void sendToAllPlayers(RespiteBenchConfigUpdatePacket packet, MinecraftServer server) {
		for (ServerPlayerEntity serverPlayerEntity : PlayerLookup.all(server)) {
			ServerPlayNetworking.send(serverPlayerEntity, packet);
		}
	}
}
