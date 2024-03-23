package amber1093.respite_bench.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;

public interface UseBenchCallback {

	Event<UseBenchCallback> EVENT = EventFactory.createArrayBacked(
			UseBenchCallback.class,
			(listeners) -> (spawnDelay) -> {
				for (UseBenchCallback listener : listeners) {
					ActionResult result = listener.setSpawnDelay(spawnDelay);
					if(result != ActionResult.PASS) {
						return result;
					}
				}
				return ActionResult.PASS;
			}
	);

    ActionResult setSpawnDelay(int spawnDelay);
}
