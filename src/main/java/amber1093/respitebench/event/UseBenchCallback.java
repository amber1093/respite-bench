package amber1093.respitebench.event;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface UseBenchCallback {

	Event<UseBenchCallback> EVENT = EventFactory.createArrayBacked(
		UseBenchCallback.class,
		(listeners) -> (canSpawn) -> {
			List<UUID> uuidList = new ArrayList<>();
			for (UseBenchCallback listener : listeners) {
				uuidList.addAll(listener.useBenchEvent(canSpawn));
			}
			return uuidList;
		}
	);

    List<UUID> useBenchEvent(boolean canSpawn);
}
