package amber1093.respitebench.config;

import amber1093.respitebench.packet.ConfigUpdatePacket;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.event.ConfigSerializeEvent.Save;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.ActionResult;

public class ConfigSave implements Save<ConfigMenu> {

	//TODO any player can change config regardless of permissions
	@Override
	public ActionResult onSave(ConfigHolder<ConfigMenu> arg0, ConfigMenu config) {
		//first save the config normally
		AutoConfig.getConfigHolder(ConfigMenu.class).setConfig(config);

		//then send C2S packet to which then sends a S2C to update config of all players (checking if the sender is server host cannot be done yet)
		if (ClientPlayNetworking.canSend(ConfigUpdatePacket.TYPE)) {
			ClientPlayNetworking.send(new ConfigUpdatePacket(config));
			return ActionResult.PASS;
		}
		return ActionResult.PASS;
	}

}
