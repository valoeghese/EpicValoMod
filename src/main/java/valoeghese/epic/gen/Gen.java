package valoeghese.epic.gen;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import valoeghese.epic.Setup;
import valoeghese.epic.gen.deobf.BiomeGenProperties;

public class Gen {
	public static void loadGenScripts() {
		new BiomeGenProperties();
		Registry.register(Registry.CHUNK_GENERATOR, new ResourceLocation(Setup.MODID, "epic_fantasy"), EpicFantasyChunkGenerator.CODEC);
	}
}
