package amber1093.respite_bench.event;

import java.util.UUID;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface EntityDeathCallback {

	Event<EntityDeathCallback> EVENT = EventFactory.createArrayBacked(
		EntityDeathCallback.class,
		(listeners) -> (uuidToRemove) -> {
			boolean result = false;
			for (EntityDeathCallback listener : listeners) {
				result = listener.removeEntityUuid(uuidToRemove);
				if (result == true) {
					break;
				}
			}
			return result;
		}
	);

	boolean removeEntityUuid(UUID uuidToRemove);
}
