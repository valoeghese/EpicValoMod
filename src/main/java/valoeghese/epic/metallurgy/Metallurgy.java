package valoeghese.epic.metallurgy;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import valoeghese.epic.abstraction.core.Game;
import valoeghese.epic.abstraction.core.Game.Ore;

public class Metallurgy {
	private static int mohrHardness(float hardnessLow, float hardnessHigh) {
		return mohrHardness((hardnessLow + hardnessHigh) / 2.0f);
	}

	private static int mohrHardness(float hardness) {
		if (hardness < 3.0f) {
			return 0;
		} else if (hardness < 6.0f) {
			return 1;
		} else if (hardness < 9.0f) {
			return 2;
		} else {
			return 3;
		}
	}

	public static void addMetals() {
		//   Copper
		// ===========

		copper_ingot = Game.addItem("copper_ingot", new Item(new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS)));

		// important copper ore
		chalcocite = Game.addBlock("chalcocite", new Block(FabricBlockSettings.copyOf(Blocks.IRON_ORE)
				.requiresTool()
				.breakByTool(FabricToolTags.PICKAXES, mohrHardness(2.5f, 3))), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS));

		// green pigment
		malachite = Game.addBlock("malachite", new Block(FabricBlockSettings.copyOf(Blocks.IRON_ORE)
				.requiresTool()
				.breakByTool(FabricToolTags.PICKAXES, mohrHardness(3.5f, 4))), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS));

		// cyan pigment
		azurite = Game.addBlock("azurite", new Block(FabricBlockSettings.copyOf(Blocks.IRON_ORE)
				.requiresTool()
				.breakByTool(FabricToolTags.PICKAXES, mohrHardness(3.5f, 4))), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS));

		cuprite = Game.addBlock("cuprite", new Block(FabricBlockSettings.copyOf(Blocks.IRON_ORE)
				.requiresTool()
				.breakByTool(FabricToolTags.PICKAXES, mohrHardness(3.5f, 4))), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS));

		bornite = Game.addBlock("bornite", new Block(FabricBlockSettings.copyOf(Blocks.IRON_ORE)
				.requiresTool()
				.breakByTool(FabricToolTags.PICKAXES, mohrHardness(3))), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS));

		// need silica sand to extract
		chalcopyrite = Game.addBlock("chalcopyrite", new Block(FabricBlockSettings.copyOf(Blocks.IRON_ORE)
				.requiresTool()
				.breakByTool(FabricToolTags.PICKAXES, mohrHardness(3.5f, 4))), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS));

		//    Iron
		// ===========
		// with vanilla -> native iron

		//   Electrum (natural alloy of gold and silver)
		// ============

		//    Zinc
		// ===========

		//  Vanadium - hardened steel
		// ===========

		//  Chromium - stainless steel
		// ===========

		//  Titanium
		// ===========

		//    Tin
		// ===========

		//    Lead
		// ===========

		//   Silver
		// ===========

		//  Aluminum and Cryolite (bauxite (also gallium) in swamps, and very rare other sources (native?))
		// ===========

		//  Manganese - enhancing steel (corrosion resistance)
		// ===========
	}

	public static void alterTools() {
		Game.forEachItem(item -> {
			if (FabricToolTags.PICKAXES.contains(item)) {

			}
		});
	}

	public static void addOreGen() {
		// copper
		Game.addOverworldOre(new Ore(12, 20)
				.addState(96, 1.0f, azurite)
				.addState(64, 1.0f, bornite)
				.addState(64, 0.5f, chalcocite)
				.addState(96, 1.5f, chalcopyrite)
				.addState(96, 1.0f, cuprite)
				.addState(128, 1.0f, malachite));
	}

	public static Item copper_ingot;

	public static Block azurite;
	public static Block bornite;
	public static Block chalcocite;
	public static Block chalcopyrite;
	public static Block cuprite;
	public static Block malachite;
}
