package valoeghese.epic.abstraction.core;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.BiomeCategory;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration.Predicates;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import valoeghese.epic.Setup;
import valoeghese.epic.abstraction.Logger;
import valoeghese.epic.abstraction.world.BiomeGen;
import valoeghese.epic.abstraction.world.ComplexOreGenerator;
import valoeghese.epic.abstraction.world.Generator;
import valoeghese.epic.abstraction.world.GeneratorType;
import valoeghese.epic.abstraction.world.SpecialOre;

public final class Game {
	public static Block addRawBlock(String name, Block block) {
		return Registry.register(Registry.BLOCK, new ResourceLocation(Setup.MODID, name), block);
	}

	public static Block addBlock(String name, Block block, Item.Properties itemProperties) {
		addRawBlock(name, block);
		addItem(name, new BlockItem(block, itemProperties));
		return block;
	}

	public static Item addItem(String name, Item item) {
		return Registry.register(Registry.ITEM, new ResourceLocation(Setup.MODID, name), item);
	}

	public static <T extends FeatureConfiguration> Feature<T> addFeature(String name, Feature<T> feature) {
		return Registry.register(Registry.FEATURE, new ResourceLocation(Setup.MODID, name), feature);
	}

	public static GeneratorType addGenerator(String name, Generator generator) {
		GeneratorType result = new GeneratorType(generator);
		addFeature(name, result.feature);
		return result;
	}

	public static void addGenFeature(Decoration decorationStep, ConfiguredFeature<?, ?> feature, Predicate<Biome> biomePredicate) {
		BuiltinRegistries.BIOME.forEach(b -> {
			if (biomePredicate.test(b)) {
				b.addFeature(decorationStep, feature);
			}
		});

		RegistryEntryAddedCallback.event(BuiltinRegistries.BIOME).register((id, location, b) -> {
			if (biomePredicate.test(b)) {
				b.addFeature(decorationStep, feature);
			}
		});
	}

	private static List<ConfiguredFeature<?, ?>> resolveOre(RuleTest target, Ore ore) {
		float total = 0;

		for (float f : ore.weights) {
			total += f;
		}

		List<ConfiguredFeature<?, ?>> list = new ArrayList<>();

		for (int i = 0; i < ore.states.size(); ++i) {
			int cnt = (int) (ore.count * ore.weights.getFloat(i) / total);

			if (cnt < 1) {
				Logger.warn("Abstraction", "BlockState " + ore.states.get(i) + " received an ore count of 0!");
			}

			if (ore.specialBunny == null) {
				list.add(Feature.ORE.configured(new OreConfiguration(target, ore.states.get(i), ore.size))
						.range(ore.ranges.getInt(i)).squared().count(ore.count));
			} else {
				list.add(SpecialOre.FEATURE.configured(new OreConfiguration(new SpecialOre.CodecHacks(ore.specialBunny), ore.states.get(i), ore.size))
						.range(ore.ranges.getInt(i)).squared().count(ore.count));
			}
		}

		return list;
	}

	public static void addOverworldOre(Ore ore) {
		Predicate<Biome> overworld = b -> {
			BiomeCategory bc = b.getBiomeCategory();
			return bc != BiomeCategory.NETHER && bc != BiomeCategory.THEEND;
		};

		for (ConfiguredFeature<?, ?> feature : resolveOre(Predicates.NATURAL_STONE, ore)) {
			addGenFeature(Decoration.UNDERGROUND_ORES, feature, overworld);
		}
	}

	public static void addOverworldOre(String featureName, ComplexOre ore) {
		Predicate<Biome> overworld = b -> {
			BiomeCategory bc = b.getBiomeCategory();
			return bc != BiomeCategory.NETHER && bc != BiomeCategory.THEEND;
		};

		ComplexOreGenerator generator = new ComplexOreGenerator();

		for (OreTuple tuple : ore.ores) {
			for (ConfiguredFeature<?, ?> oreFeature : resolveOre(Predicates.NATURAL_STONE, tuple.ore)) {
				generator.addFeature(oreFeature, tuple.target, tuple.distance);
			}
		}

		GeneratorType type = addGenerator(featureName, generator);
		addGenFeature(Decoration.UNDERGROUND_ORES, type.featureConfigured, overworld);
	}

	public static BiomeGen addBiome(BiomeGen biome) {
		return Registry.register(BuiltinRegistries.BIOME, biome.location, biome);
	}

	public static class ComplexOre {
		public ComplexOre() {
		}

		private List<OreTuple> ores = new ArrayList<>();

		public ComplexOre addOre(Ore ore, double target, double distance) {
			this.ores.add(new OreTuple(ore, target, distance));
			return this;
		}
	}

	private static class OreTuple {
		OreTuple(Ore ore, double target, double distance) {
			this.ore = ore;
			this.target = target;
			this.distance = distance;
		}

		private final Ore ore;
		private final double target;
		private final double distance;
	}

	public static class Ore {
		public Ore(int size, int count) {
			this.size = size;
			this.count = count;
			this.specialBunny = null;
		}

		public Ore(int size, int count, BlockState specialBunny) {
			this.size = size;
			this.count = count;
			this.specialBunny = specialBunny;
		}

		private final int size;
		private final int count;
		private final BlockState specialBunny;
		private final List<BlockState> states = new ArrayList<>();
		private final IntList ranges = new IntArrayList();
		private final FloatList weights = new FloatArrayList();

		/**
		 * Adds the specific configured state to the ore.
		 * @return the ore the state is to be added to.
		 */
		public Ore addState(int range, float weight, Block block) {
			return this.addState(range, weight, block.defaultBlockState());
		}

		/**
		 * Adds the specific configured state to the ore.
		 * @return the ore the state is to be added to.
		 */
		public Ore addState(int range, float weight, BlockState state) {
			this.states.add(state);
			this.weights.add(weight);
			this.ranges.add(range);
			return this;
		}
	}

	public static void forEachItem(Consumer<Item> func) {
		Registry.ITEM.forEach(func);
		RegistryEntryAddedCallback.event(Registry.ITEM).register((id, location, item) -> func.accept(item));
	}
}
