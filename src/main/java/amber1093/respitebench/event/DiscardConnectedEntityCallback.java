package amber1093.respitebench.event;

import java.util.List;
import java.util.UUID;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * <p>Used to discard entities connected to mob respawners.</p>
 * <p>Called whenever a player respawns, uses a bench or disables a mob respawner.</p>
 * 
 * <p>Requires a {@code List<UUID>} provided by {@link UseBenchCallback} or directly by a {@link MobRespawnerLogic}
 */
public interface DiscardConnectedEntityCallback {

	Event<DiscardConnectedEntityCallback> EVENT = EventFactory.createArrayBacked(
		DiscardConnectedEntityCallback.class,
		(listeners) -> (uuidList) -> {
			for (DiscardConnectedEntityCallback listener : listeners) {
				listener.discardConnectedEntities(uuidList);
			}
		}
	);

	void discardConnectedEntities(List<UUID> uuidList);
}
