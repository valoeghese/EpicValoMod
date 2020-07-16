package valoeghese.epic.abstraction.game;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import valoeghese.epic.Setup;

public final class Game {
	public static void addRawBlock(String name, Block block) {
		Registry.register(Registry.BLOCK, new ResourceLocation(Setup.MODID, name), block);
	}

	public static void addBlock(String name, Block block, Item.Properties properties) {
		addRawBlock(name, block);
		Registry.register(Registry.ITEM, new ResourceLocation(Setup.MODID, name), new BlockItem(block, properties));
	}

	public static void addItem(String name, Item item) {
		Registry.register(Registry.ITEM, new ResourceLocation(Setup.MODID, name), item);
	}

	public static void addFeature(String name, Feature<? extends FeatureConfiguration> feature) {
		Registry.register(Registry.FEATURE, new ResourceLocation(Setup.MODID, name), feature);
	}
}
