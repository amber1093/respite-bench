package amber1093.respite_bench.config;

import amber1093.respite_bench.RespiteBench;
import amber1093.respite_bench.packet.RespiteBenchConfigUpdatePacket;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.event.ConfigSerializeEvent.Save;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.ActionResult;

public class RespiteBenchConfigSave implements Save<RespiteBenchConfig> {

	//TODO any player can change config regardless of permissions
	//TODO the actual config of all players gets overriden
	@Override
	public ActionResult onSave(ConfigHolder<RespiteBenchConfig> arg0, RespiteBenchConfig config) {
		//first save the config normally
		AutoConfig.getConfigHolder(RespiteBenchConfig.class).setConfig(config);

		//then send C2S packet to which then sends a S2C to update config of all players (checking if the sender is server host cannot be done yet)
		if (ClientPlayNetworking.canSend(RespiteBenchConfigUpdatePacket.TYPE)) {
			ClientPlayNetworking.send(new RespiteBenchConfigUpdatePacket(config));
			RespiteBench.LOGGER.info("save button press event: C2S packet sent"); //DEBUG
			return ActionResult.PASS;
		}
		RespiteBench.LOGGER.info("save button press event: no packet sent"); //DEBUG
		return ActionResult.PASS;
	}

}
