package valoeghese.epic.abstraction.core;

import java.util.function.Predicate;

import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
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

	public static void addOre(RuleTest target, BlockState state, int size, int range, int count, Predicate<Biome> predicate) {
		BuiltinRegistries.BIOME.forEach(b -> {
			if (predicate.test(b)) {
				b.addFeature(
						Decoration.UNDERGROUND_ORES,
						Feature.ORE.configured(new OreConfiguration(target, state, size))
						.range(range).squared().count(count)
						);
			}
		});
	}
}
