package amber1093.respite_bench.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;

public interface UseBenchCallback {

	Event<UseBenchCallback> EVENT = EventFactory.createArrayBacked(
		UseBenchCallback.class,
		(listeners) -> (canSpawn) -> {
			for (UseBenchCallback listener : listeners) {
				ActionResult result = listener.setCanSpawn(canSpawn);
				if(result != ActionResult.PASS) {
					return result;
				}
			}
			return ActionResult.PASS;
		}
	);

    ActionResult setCanSpawn(boolean canSpawn);
}
