package valoeghese.epic.gen;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptException;

import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import valoeghese.epic.abstraction.ScriptManager;

public class BiomeGenProperties extends ScriptManager {
	private static final DecimalFormat FORMAT = new DecimalFormat("###.###");

	public BiomeGenProperties() {
		INSTANCE = this;
		File biomeGen = getScript("biomeGen");

		try {
			createIfNotExists(biomeGen, pr -> {
				pr.println("// Epic Fantasy biome generation properties.");
				pr.println("// set generation properties using \"function setGenerationProperties(depth, projection, scale, smoothness)\"");
				pr.println(setGenPropertiesJs("minecraft:forest", 0.3f, 0.5f, 0.1f, 4));
				pr.println(setGenPropertiesJs("minecraft:plains", 0.15f, 0.2f, 0.075f, 4));
			});

			ScriptContext context = new ScriptContext();
			context.addFunctionDefinition("setGenerationProperties", BiomeGenProperties.class, "addBiome", 5);
			context.runScript(biomeGen);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		} catch (ScriptException e) {
			throw new RuntimeException("Error running script!", e);
		}
	}

	private static String setGenPropertiesJs(String id, double depth, double projection, double scale, int smoothness) {
		return new StringBuilder("setGenerationProperties(")
				.append('"').append(id).append('"').append(", ")
				.append(FORMAT.format(depth)).append(", ")
				.append(FORMAT.format(projection)).append(", ")
				.append(FORMAT.format(scale)).append(", ")
				.append(FORMAT.format(smoothness)).append(");")
				.toString();
	}

	public static void addBiome(String id, float depth, float projection, float scale, int smoothness) {
		BiomeGenProperties.INSTANCE.properties.put(
				BuiltinRegistries.BIOME.get(new ResourceLocation(id)),
				new BiomeEntry(depth, projection, scale, smoothness));
	}

	private final Map<Biome, BiomeEntry> properties = new HashMap<>();

	private static BiomeGenProperties INSTANCE;

	public static BiomeEntry getGenerationProperties(Biome biome) {
		return BiomeGenProperties.INSTANCE.properties.computeIfAbsent(biome, b -> new BiomeEntry(b.getDepth(), 0.0f, b.getScale(), 5));
	}

	public static class BiomeEntry {
		private BiomeEntry(float depth, float projection, float scale, int smoothness) {
			this.depth = depth;
			this.projection = projection;
			this.scale = scale;
			this.smoothness = smoothness;
		}

		public final float depth;
		public final float projection;
		public final float scale;
		public final int smoothness;
	}
}
