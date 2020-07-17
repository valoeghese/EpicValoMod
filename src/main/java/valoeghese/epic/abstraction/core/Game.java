package valoeghese.epic.abstraction.core;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
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
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration.Predicates;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import valoeghese.epic.Setup;
import valoeghese.epic.abstraction.world.Generator;

public final class Game {
	public static Block addRawBlock(String name, Block block) {
		return Registry.register(Registry.BLOCK, new ResourceLocation(Setup.MODID, name), block);
	}

	public static Block addBlock(String name, Block block, Item.Properties properties) {
		addRawBlock(name, block);
		Registry.register(Registry.ITEM, new ResourceLocation(Setup.MODID, name), new BlockItem(block, properties));
		return block;
	}

	public static Item addItem(String name, Item item) {
		return Registry.register(Registry.ITEM, new ResourceLocation(Setup.MODID, name), item);
	}

	public static <T extends FeatureConfiguration> Feature<T> addFeature(String name, Feature<T> feature) {
		return Registry.register(Registry.FEATURE, new ResourceLocation(Setup.MODID, name), feature);
	}

	public static Generator addGenerator(String name, Generator generator) {
		addFeature(name, new Generator.GeneratorFeature(generator));
		return generator;
	}

	public static void addOre(RuleTest target, BlockState state, int size, int range, int count, Predicate<Biome> biomePredicate) {
		BuiltinRegistries.BIOME.forEach(b -> {
			if (biomePredicate.test(b)) {
				b.addFeature(
						Decoration.UNDERGROUND_ORES,
						Feature.ORE.configured(new OreConfiguration(target, state, size))
						.range(range).squared().count(count)
						);
			}
		});
	}

	private static void addOverworldOre(int size, int count, float[] weights, int[] ranges, BlockState[] blocks) {
		float total = 0;

		for (float f : weights) {
			total += f;
		}

		Predicate<Biome> overworld = b -> {
			BiomeCategory bc = b.getBiomeCategory();
			return bc != BiomeCategory.NETHER && bc != BiomeCategory.THEEND;
		};

		for (int i = 0; i < blocks.length; ++i) {
			Game.addOre(Predicates.NATURAL_STONE, blocks[i], size, ranges[i], (int) (count * weights[i] / total), overworld);
		}
	}

	public static void addOverworldOre(Ore ore) {
		addOverworldOre(ore.size, ore.count, ore.weights.toFloatArray(), ore.ranges.toIntArray(), ore.states.toArray(new BlockState[0]));
	}

	public static class Ore {
		public Ore(int size, int count) {
			this.size = size;
			this.count = count;
		}

		private final int size;
		private final int count;
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
}
