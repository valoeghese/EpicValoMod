package valoeghese.epic.gen;

import net.minecraft.client.gui.screens.worldselection.WorldPreset;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

public class GenClient {
	public static void addPreset() {
		epicFantasy = new SelfAddingWorldPreset("epic_fantasy") {
			@Override
			protected ChunkGenerator generator(long seed) {
				return new EpicFantasyChunkGenerator(
						new EpicFantasyBiomeSource(seed),
						seed,
						() -> NoiseGeneratorSettings.OVERWORLD);
			}
		};
	}

	static abstract class SelfAddingWorldPreset extends WorldPreset {
		SelfAddingWorldPreset(String string) {
			super(string);
			WorldPreset.PRESETS.add(this);
		}
	}

	public static WorldPreset epicFantasy;
}
