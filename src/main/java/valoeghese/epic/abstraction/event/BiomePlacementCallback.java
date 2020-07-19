package valoeghese.epic.abstraction.event;

import java.util.concurrent.atomic.AtomicReference;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.level.biome.Biome;

@FunctionalInterface
public interface BiomePlacementCallback {
	Event<BiomePlacementCallback> OVERWORLD = EventFactory.createArrayBacked(BiomePlacementCallback.class, listeners -> (biome, genX, genZ) -> {
		Biome vanilla = biome.get();

		for (BiomePlacementCallback callback : listeners) {
			biome.set(vanilla);

			if (callback.onBiomePlace(biome, genX, genZ)) {
				return true;
			}
		}

		return false;
	});
	/**
	 * Called on biome placement. Change the biome reference parameter and return true to replace the biome.
	 * @param biome the biome that is to generate. Will be equal to the biome that is otherwise to generate at the beginning of the event.
	 * @param genX the generation x of the biome. Equal to {@code x >> 2}.
	 * @param genZ the generation z of the biome. Equal to {@code z >> 2}.
	 * @return true if the biome should be replaced by the biome stored in the {@code biome} parameter, false to continue event processing.
	 */
	boolean onBiomePlace(AtomicReference<Biome> biome, int genX, int genZ);
}
