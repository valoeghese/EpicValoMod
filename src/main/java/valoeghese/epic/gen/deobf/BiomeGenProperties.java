package valoeghese.epic.gen.deobf;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptException;

import net.fabricmc.fabric.api.biomes.v1.OverworldBiomes;
import net.fabricmc.fabric.api.biomes.v1.OverworldClimate;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.Features;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderBaseConfiguration;
import valoeghese.epic.abstraction.Logger;
import valoeghese.epic.abstraction.ScriptManager;
import valoeghese.epic.abstraction.core.Game;
import valoeghese.epic.abstraction.world.gen.BiomeGen;
import valoeghese.epic.gen.Gen;

public class BiomeGenProperties extends ScriptManager {
	public static final DecimalFormat FORMAT = new DecimalFormat("###.###");
	private static boolean addedBiomes = false;

	public BiomeGenProperties() {
		INSTANCE = this;
		Logger.info("Gen", "Loading Biome Gen Script.");
		File biomeGen = getScript("biomeGen");
		//pr.println(setGenPropertiesJs("minecraft:forest", 0.3f, 0.5f, 0.09f, 0.1f, 4));
		try {
			createIfNotExists(biomeGen, pr -> {
				pr.println("// Epic Fantasy biome generation properties.");

				pr.println(addBiomeJs(new JSBiome("epic_fantasy:rolling_plains", "plains")
						.vanillaShape(0.1f, 0.25f)
						.climate(0.8f, 0.4f)
						.addLakes(true)
						.vanillaReplaceGen("minecraft:plains", 0.4f)
						.decorations(new Decorations.PresetCombo()
								.setGroundFoliage(DecorationCategory.PLAINS))));

				pr.println(addBiomeJs(new JSBiome("epic_fantasy:grove", "forest")
						.vanillaShape(0.2f, 0.025f)
						.climate(0.6f, 0.5f)
						.addLakes(true)
						.vanillaBaseGen(OverworldClimate.TEMPERATE, 0.25f)
						.decorations(new Decorations.PresetCombo()
								.setGroundFoliage(DecorationCategory.FOREST)
								.setTreeFoliage(DecorationCategory.FOREST_SPARSE_TREES))));

				pr.println(setGenPropertiesJs("epic_fantasy:rolling_plains", new GenerationProperties.Builder()
						.depthScale(0.25f, 0.075f)
						.periodFactor(1.5f) // we're using projection (depth modulation), so stretching out the main scale might make vanilla's scale less pronounced
						.projection(1.15f, 0.105f)
						.interpolation(5)));

				pr.println();
				pr.println("// Vanilla Biomes");

				pr.println(setGenPropertiesJs("minecraft:plains", new GenerationProperties.Builder()
						.depthScale(0.1f, 0.02f)
						.periodFactor(1.5f)
						.interpolation(5)));

				pr.println(setGenPropertiesJs("minecraft:sunflower_plains", new GenerationProperties.Builder()
						.depthScale(0.125f, 0.02f)
						.periodFactor(1.5f)
						.interpolation(5)));

				GenerationProperties.Builder mtnsGen = new GenerationProperties.Builder()
						.depthScale(1.4f, 0.4f)
						.periodFactor(2.3f)
						.hillinessFactor(2.0f)
						.interpolation(8)
						.projection(1.6f, 0.05f);

				pr.println(setGenPropertiesJs("minecraft:river", new GenerationProperties.Builder()
						.depthScale(-0.7f, 0.0f)
						.periodFactor(1.5f)
						.interpolation(6)));

				pr.println(setGenPropertiesJs("minecraft:mountains", mtnsGen));
				pr.println(setGenPropertiesJs("minecraft:mountain_edge", mtnsGen
						.depthScale(0.5f, 0.2f)
						.hillinessFactor(1.5f)
						.projection(0.0f, 1.0f)));
				pr.println(setGenPropertiesJs("minecraft:wooded_mountains", mtnsGen
						.depthScale(1.4f, 0.25f)
						.hillinessFactor(1.75f)));
				pr.println(setGenPropertiesJs("minecraft:gravelly_mountains", mtnsGen));
				pr.println(setGenPropertiesJs("minecraft:stone_shore", mtnsGen
						.depthScale(0.0f, 0.18f)
						.hillinessFactor(1.0f)
						.projection(0.0f, 1.0f)));

				pr.println(setGenPropertiesJs("minecraft:taiga", new GenerationProperties.Builder()
						.depthScale(0.1f, 0.2f)
						.periodFactor(1.6f)
						.interpolation(5)
						.projection(0.4f, 0.09f)));

				pr.println(setGenPropertiesJs("minecraft:taiga_hills", new GenerationProperties.Builder()
						.depthScale(0.45f, 0.3f)
						.periodFactor(1.6f)
						.interpolation(5)
						.projection(0.4f, 0.09f)));

				pr.println(setGenPropertiesJs("minecraft:desert_hills", new GenerationProperties.Builder()
						.depthScale(0.45f, 0.3f)
						.periodFactor(2.3f)
						.interpolation(6)
						.projection(0.9f, 0.2f)));

				pr.println(setGenPropertiesJs("minecraft:desert", new GenerationProperties.Builder()
						.depthScale(0.15f, 0.0f)
						.periodFactor(2.3f)
						.interpolation(6)));
			}, true);

			ScriptContext context = new ScriptContext();
			context.addFunctionDefinition("setGenerationProperties", BiomeGenProperties.class, "setGenerationProperties", 2);
			context.addFunctionDefinition("addBiome", BiomeGenProperties.class, "addBiome", 1);
			context.addClassDefinition("Biome", JSBiome.class);
			context.addClassDefinition("Decorations", Decorations.class);
			context.addClassDefinition("DecorationCategory", DecorationCategory.class);
			context.addClassDefinition("GenerationProperties", GenerationProperties.Builder.class);
			context.addClassDefinition("Climate", OverworldClimate.class);
			context.runScript(biomeGen);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		} catch (ScriptException e) {
			throw new RuntimeException("Error running script!", e);
		}

		addedBiomes = true;
	}

	private static String setGenPropertiesJs(String id, GenerationProperties.Builder builder) {
		return new StringBuilder("setGenerationProperties(")
				.append('"').append(id).append('"').append(", ")
				.append(builder.toJS("")).append(");")
				.toString();
	}

	private static String addBiomeJs(JSBiome biome) {
		StringBuilder result = new StringBuilder("addBiome(new Biome(\"").append(biome.properties.name).append("\", \"").append(biome.properties.category.getName()).append("\")\n")
				.append("    .vanillaShape(").append(FORMAT.format(biome.properties.getTemperature())).append(", ").append(FORMAT.format(biome.properties.getRainfall())).append(")\n")
				.append("    .climate(").append(FORMAT.format(biome.properties.getTemperature())).append(", ").append(FORMAT.format(biome.properties.getRainfall())).append(")\n")
				.append("    .addLakes(").append(biome.lakes).append(")\n")
				.append("    .topBlock(\"").append(Registry.BLOCK.getKey(biome.topBlock).toString()).append("\")\n")
				.append("    .underBlock(\"").append(Registry.BLOCK.getKey(biome.underBlock).toString()).append("\")\n")
				.append("    .underwaterBlock(\"").append(Registry.BLOCK.getKey(biome.underwaterBlock).toString()).append("\")\n");

		if (biome.climate != null) {
			result.append("    .vanillaBaseGen(Climate.").append(biome.climate.name()).append(", ").append(FORMAT.format(biome.genWeight)).append(")\n");
		}

		if (biome.replaceBiome != null) {
			result.append("    .vanillaReplaceGen(\"").append(biome.replaceBiome).append("\", ").append(FORMAT.format(biome.replaceChance)).append(")\n");
		}

		return result.append("    .decorations(").append(biome.decorations.toJS("    ")).append("));").toString();
	}

	public static void setupForChunkGen() {
		INSTANCE.properties = new HashMap<>(INSTANCE.scriptedProperties);

		synchronized (INSTANCE) {
			RUNTIME_INSTANCE = INSTANCE;
		}
	}

	public static void setGenerationProperties(String id, GenerationProperties.Builder builder) {
		INSTANCE.scriptedProperties.put(
				BuiltinRegistries.BIOME.get(new ResourceLocation(id)),
				builder.build());
	}

	private Map<Biome, GenerationProperties> properties;
	private final Map<Biome, GenerationProperties> scriptedProperties = new HashMap<>();

	private static BiomeGenProperties INSTANCE;
	private static BiomeGenProperties RUNTIME_INSTANCE;

	public static GenerationProperties getGenerationProperties(Biome biome) {
		synchronized (INSTANCE) {
			return RUNTIME_INSTANCE.properties.computeIfAbsent(biome, b -> new GenerationProperties.Builder()
					.depthScale(b.getDepth(), b.getScale())
					.build());
		}
	}

	public static void addBiome(JSBiome jsBiome) {
		if (!addedBiomes) {
			jsBiome.properties.surfaceBlocks(new SurfaceBuilderBaseConfiguration(
					jsBiome.topBlock.defaultBlockState(),
					jsBiome.underBlock.defaultBlockState(),
					jsBiome.underwaterBlock.defaultBlockState()));
			BiomeGen biome = new BiomeGen(jsBiome.properties);
			biome.addDefaultFeatures(jsBiome.lakes, false);
			jsBiome.decorations.addDecorations(biome);

			if (jsBiome.climate != null) {
				OverworldBiomes.addContinentalBiome(biome, jsBiome.climate, jsBiome.genWeight);
			}

			if (jsBiome.replaceBiome != null) {
				OverworldBiomes.addBiomeVariant(BuiltinRegistries.BIOME.get(new ResourceLocation(jsBiome.replaceBiome)), biome, jsBiome.replaceChance);
			}

			Game.addBiome(biome);
		}
	}

	public static class JSBiome {
		public JSBiome(String name, String category) {
			this.properties = new BiomeGen.Properties(name, Biome.BiomeCategory.byName(category));
		}

		private final BiomeGen.Properties properties;
		private Block topBlock = Blocks.GRASS_BLOCK;
		private Block underBlock = Blocks.DIRT;
		private Block underwaterBlock = Blocks.GRAVEL;
		private boolean lakes = false;
		private Decorations decorations;
		private OverworldClimate climate = null;
		private double genWeight;
		private String replaceBiome;
		private double replaceChance;

		public JSBiome vanillaShape(float depth, float scale) {
			this.properties.shape(depth, scale);
			return this;
		}

		public JSBiome vanillaReplaceGen(String biome, double chance) {
			this.replaceBiome = biome;
			this.replaceChance = chance;
			return this;
		}

		public JSBiome vanillaBaseGen(OverworldClimate climate, double weight) {
			this.climate = climate;
			this.genWeight = weight;
			return this;
		}

		public JSBiome topBlock(String id) {
			this.topBlock = Registry.BLOCK.get(new ResourceLocation(id));
			return this;
		}

		public JSBiome underBlock(String id) {
			this.underBlock = Registry.BLOCK.get(new ResourceLocation(id));
			return this;
		}

		public JSBiome underwaterBlock(String id) {
			this.underwaterBlock = Registry.BLOCK.get(new ResourceLocation(id));
			return this;
		}

		public JSBiome climate(float temperature, float rainfall) {
			this.properties.climate(temperature, rainfall);
			return this;
		}

		public JSBiome addLakes(boolean add) {
			this.lakes = add;
			return this;
		}

		public JSBiome decorations(Decorations decorations) {
			this.decorations = decorations;
			return this;
		}
	}

	public static abstract class Decorations {
		protected abstract void addDecorations(Biome biome);
		protected abstract String toJS(String defaultIndent);

		public static class PresetCombo extends Decorations {
			private DecorationCategory groundFoliage = DecorationCategory.FOREST;
			private DecorationCategory treeFoliage = DecorationCategory.PLAINS;
			private DecorationCategory spawns = DecorationCategory.PLAINS;

			public PresetCombo setGroundFoliage(DecorationCategory type) {
				this.groundFoliage = type;
				return this;
			}

			public PresetCombo setTreeFoliage(DecorationCategory type) {
				this.treeFoliage = type;
				return this;
			}

			public PresetCombo setSpawns(DecorationCategory type) {
				this.spawns = type;
				return this;
			}

			@Override
			protected void addDecorations(Biome biome) {
				switch (this.groundFoliage) {
				case DESERT:
					BiomeDefaultFeatures.addDefaultGrass(biome);
					BiomeDefaultFeatures.addDesertVegetation(biome);
					BiomeDefaultFeatures.addDefaultMushrooms(biome);
					BiomeDefaultFeatures.addDesertExtraVegetation(biome);
					BiomeDefaultFeatures.addDesertExtraDecoration(biome);
					break;
				case FOREST:
				case FOREST_SPARSE_TREES:
				default:
					BiomeDefaultFeatures.addDefaultFlowers(biome);
					BiomeDefaultFeatures.addDefaultGrass(biome);
					BiomeDefaultFeatures.addDefaultMushrooms(biome);
					BiomeDefaultFeatures.addDefaultExtraVegetation(biome);
					break;
				case JUNGLE:
					BiomeDefaultFeatures.addWarmFlowers(biome);
					BiomeDefaultFeatures.addJungleExtraVegetation(biome);
					BiomeDefaultFeatures.addDefaultMushrooms(biome);
					BiomeDefaultFeatures.addDefaultExtraVegetation(biome);
					BiomeDefaultFeatures.addJungleExtraVegetation(biome);
					break;
				case MESA:
					BiomeDefaultFeatures.addBadlandGrass(biome);
					BiomeDefaultFeatures.addDefaultMushrooms(biome);
					BiomeDefaultFeatures.addBadlandExtraVegetation(biome);
					break;
				case PLAINS:
					BiomeDefaultFeatures.addPlainGrass(biome);
					biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.FLOWER_PLAIN_DECORATED);
					biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_GRASS_PLAIN);
					BiomeDefaultFeatures.addDefaultMushrooms(biome);
					BiomeDefaultFeatures.addDefaultExtraVegetation(biome);
					break;
				case SAVANNA:
					BiomeDefaultFeatures.addSavannaGrass(biome);
					BiomeDefaultFeatures.addWarmFlowers(biome);
					BiomeDefaultFeatures.addSavannaExtraGrass(biome);
					break;
				case TAIGA:
					BiomeDefaultFeatures.addFerns(biome);
					BiomeDefaultFeatures.addDefaultFlowers(biome);
					BiomeDefaultFeatures.addTaigaGrass(biome);
					BiomeDefaultFeatures.addDefaultMushrooms(biome);
					BiomeDefaultFeatures.addDefaultExtraVegetation(biome);
					break;
				}

				switch (this.treeFoliage) {
				case FOREST_SPARSE_TREES:
					biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Gen.groveTrees);
					break;
				case FOREST:
					BiomeDefaultFeatures.addOtherBirchTrees(biome);
					break;
				case JUNGLE:
					BiomeDefaultFeatures.addLightBambooVegetation(biome);
					BiomeDefaultFeatures.addJungleTrees(biome);
					break;
				case DESERT:
				case MESA:
					break;
				case PLAINS:
				default:
					biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PLAIN_VEGETATION);
					break;
				case SAVANNA:
					BiomeDefaultFeatures.addSavannaTrees(biome);
					break;
				case TAIGA:
					BiomeDefaultFeatures.addTaigaTrees(biome);
					break;
				}

				switch (this.spawns) {
				case DESERT:
					BiomeDefaultFeatures.desertSpawns(biome);
					break;
				case FOREST:
				case FOREST_SPARSE_TREES:
					BiomeDefaultFeatures.farmAnimals(biome);
					BiomeDefaultFeatures.commonSpawns(biome);
					biome.addSpawn(MobCategory.CREATURE, new Biome.SpawnerData(EntityType.WOLF, 5, 4, 4));
					break;
				case JUNGLE:
					BiomeDefaultFeatures.baseJungleSpawns(biome);
					break;
				case MESA:
					BiomeDefaultFeatures.commonSpawns(biome);
					break;
				case PLAINS:
				default:
					BiomeDefaultFeatures.plainsSpawns(biome);
					break;
				case SAVANNA:
					BiomeDefaultFeatures.farmAnimals(biome);
					biome.addSpawn(MobCategory.CREATURE, new Biome.SpawnerData(EntityType.HORSE, 1, 2, 6));
					biome.addSpawn(MobCategory.CREATURE, new Biome.SpawnerData(EntityType.DONKEY, 1, 1, 1));
					BiomeDefaultFeatures.commonSpawns(biome);
					break;
				case TAIGA:
					BiomeDefaultFeatures.farmAnimals(biome);
					biome.addSpawn(MobCategory.CREATURE, new Biome.SpawnerData(EntityType.WOLF, 8, 4, 4));
					biome.addSpawn(MobCategory.CREATURE, new Biome.SpawnerData(EntityType.RABBIT, 4, 2, 3));
					biome.addSpawn(MobCategory.CREATURE, new Biome.SpawnerData(EntityType.FOX, 8, 2, 4));
					BiomeDefaultFeatures.commonSpawns(biome);
					break;
				}
			}

			@Override
			protected String toJS(String defaultIndent) {
				return new StringBuilder("new Decorations.PresetCombo()\n")
						.append(defaultIndent).append("    .setGroundFoliage(DecorationCategory.").append(this.groundFoliage.toString()).append(")\n")
						.append(defaultIndent).append("    .setTreeFoliage(DecorationCategory.").append(this.treeFoliage.toString()).append(")\n")
						.append(defaultIndent).append("    .setSpawns(DecorationCategory.").append(this.spawns.toString()).append(")")
						.toString();
			}
		}
	}

	public static enum DecorationCategory {
		FOREST,
		PLAINS,
		SAVANNA,
		TAIGA,
		JUNGLE,
		MESA,
		DESERT,
		FOREST_SPARSE_TREES
	}
}
