package valoeghese.epic.metallurgy;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import valoeghese.epic.abstraction.core.Game;
import valoeghese.epic.abstraction.core.Game.Ore;
import valoeghese.epic.abstraction.world.BiomeGen;
import valoeghese.epic.abstraction.world.BlockType;

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
		// Other useful stuff
		silicaSand = Game.addBlock("silica_sand", BlockType.FALLING.create(FabricBlockSettings.copy(Blocks.SAND)),
				new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS));

		//   Copper
		// ===========

		copperIngot = Game.addItem("copper_ingot", new Item(new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS)));

		// native copper
		nativeCopper = Game.addBlock("copper_ore", BlockType.BASIC.create(FabricBlockSettings.copyOf(Blocks.IRON_ORE)
				.requiresTool()
				.breakByTool(FabricToolTags.PICKAXES, mohrHardness(2.5f, 3))), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS));

		// important copper ore
		chalcocite = Game.addBlock("chalcocite", BlockType.BASIC.create(FabricBlockSettings.copyOf(Blocks.IRON_ORE)
				.requiresTool()
				.breakByTool(FabricToolTags.PICKAXES, mohrHardness(2.5f, 3))), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS));

		// green pigment
		malachite = Game.addBlock("malachite", BlockType.BASIC.create(FabricBlockSettings.copyOf(Blocks.IRON_ORE)
				.requiresTool()
				.breakByTool(FabricToolTags.PICKAXES, mohrHardness(3.5f, 4))), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS));

		// cyan pigment
		azurite = Game.addBlock("azurite", BlockType.BASIC.create(FabricBlockSettings.copyOf(Blocks.IRON_ORE)
				.requiresTool()
				.breakByTool(FabricToolTags.PICKAXES, mohrHardness(3.5f, 4))), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS));

		cuprite = Game.addBlock("cuprite", BlockType.BASIC.create(FabricBlockSettings.copyOf(Blocks.IRON_ORE)
				.requiresTool()
				.breakByTool(FabricToolTags.PICKAXES, mohrHardness(3.5f, 4))), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS));

		bornite = Game.addBlock("bornite", BlockType.BASIC.create(FabricBlockSettings.copyOf(Blocks.IRON_ORE)
				.requiresTool()
				.breakByTool(FabricToolTags.PICKAXES, mohrHardness(3))), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS));

		// need silica sand to extract
		chalcopyrite = Game.addBlock("chalcopyrite", BlockType.BASIC.create(FabricBlockSettings.copyOf(Blocks.IRON_ORE)
				.requiresTool()
				.breakByTool(FabricToolTags.PICKAXES, mohrHardness(3.5f, 4))), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS));

		chalcopyriteChunks = Game.addItem("chalcopyrite_chunks", new Item(new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS)));
		chalcopyriteChunksSilica = Game.addItem("cupric_silica_mix", new Item(new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS)));

		//    Iron
		// ===========
		// with vanilla iron = native iron (kamacite, has low nickel content alloyed).

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

	public static void addBiomes() {
		silicaSandBeach = new BiomeGen(new BiomeGen.Properties("silica_sand_beach", Biome.BiomeCategory.BEACH)
				.shape(Biomes.BEACH.getDepth(), Biomes.BEACH.getScale())
				);
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

		Game.addOverworldOre(new Ore(4, 2)
				.addState(64, 1.0f, nativeCopper));
	}

	public static Item chalcopyriteChunks;
	public static Item chalcopyriteChunksSilica;
	public static Item copperIngot;

	public static Block azurite;
	public static Block bornite;
	public static Block chalcocite;
	public static Block chalcopyrite;
	public static Block cuprite;
	public static Block malachite;
	public static Block nativeCopper;
	public static Block silicaSand;

	public static BiomeGen silicaSandBeach;
}
