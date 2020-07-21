package valoeghese.epic.gen;

import java.io.File;

import net.minecraft.world.level.biome.Biome;
import valoeghese.epic.abstraction.ScriptManager;

public class BiomeGenProperties extends ScriptManager {
	public BiomeGenProperties() {
		File biomeGen = getScript("biomeGen");

		/*try {
			createIfNotExists(biomeGen, pr -> {
				pr.println("// Epic Fantasy biome generation properties.");
			});
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}*/

		INSTANCE = this;
	}

	public static BiomeGenProperties INSTANCE;

	public static class BiomeEntry {
		public float depth;
		public float projection;
		public float scale;
		public int smoothness;
		public Biome biome;
	}
}
