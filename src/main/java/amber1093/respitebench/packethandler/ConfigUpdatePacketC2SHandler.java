package amber1093.respitebench.packethandler;

import amber1093.respitebench.RespiteBench;
import amber1093.respitebench.packet.ConfigUpdatePacket;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.PlayPacketHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

@FunctionalInterface
public interface ConfigUpdatePacketC2SHandler
extends PlayPacketHandler<ConfigUpdatePacket> {
	public static void updateConfigSettings(ConfigUpdatePacket packet, ServerPlayerEntity player) {
		MinecraftServer server = player.getServer();
		if (server != null && (player.hasPermissionLevel(4) || server.isSingleplayer())) {

			//update serverconfig
			RespiteBench.serverconfig = packet.getConfig();

			//send config data to all players
			ConfigUpdatePacket newPacket = new ConfigUpdatePacket(packet.getConfig());
			sendToAllPlayers(newPacket, server);

		}
	}

	static void sendToAllPlayers(ConfigUpdatePacket packet, MinecraftServer server) {
		for (ServerPlayerEntity serverPlayerEntity : PlayerLookup.all(server)) {
			ServerPlayNetworking.send(serverPlayerEntity, packet);
		}
	}
}
