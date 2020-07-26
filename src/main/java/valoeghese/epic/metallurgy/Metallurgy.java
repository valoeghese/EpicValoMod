package valoeghese.epic.metallurgy;

import java.util.Random;

import net.fabricmc.fabric.api.biomes.v1.FabricBiomes;
import net.fabricmc.fabric.api.biomes.v1.OverworldBiomes;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderBaseConfiguration;
import valoeghese.epic.abstraction.core.Game;
import valoeghese.epic.abstraction.core.Game.Ore;
import valoeghese.epic.abstraction.event.BiomePlacementCallback;
import valoeghese.epic.abstraction.world.BlockType;
import valoeghese.epic.abstraction.world.Fluid;
import valoeghese.epic.abstraction.world.gen.BiomeGen;
import valoeghese.epic.util.OpenSimplexNoise;

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

		silicaSandstone = Game.addBlock("silica_sandstone", BlockType.BASIC.create(FabricBlockSettings.copy(Blocks.SANDSTONE)),
				new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS));

		//   Copper
		// ===========

		copperIngot = Game.addItem("copper_ingot", new Item(new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS)));

		blockCopper = Game.addBlock("copper_block", BlockType.BASIC.create(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK)
				.requiresTool()
				.breakByTool(FabricToolTags.PICKAXES, mohrHardness(2.5f, 3))), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS));

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

		// + pyrite 
		// + magnetite
		// + hematite
		// + ?

		//   Electrum (natural alloy of gold and silver)
		// ============

		//    Zinc
		// ===========
		zincIngot = Game.addItem("zinc_ingot", new Item(new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS)));

		nativeZinc = Game.addBlock("zinc_ore", BlockType.BASIC.create(FabricBlockSettings.copyOf(Blocks.IRON_ORE)
				.requiresTool()
				.breakByTool(FabricToolTags.PICKAXES, mohrHardness(2.5f))), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS));

		blockZinc = Game.addBlock("zinc_block", BlockType.BASIC.create(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK)
				.requiresTool()
				.breakByTool(FabricToolTags.PICKAXES, mohrHardness(2.5f))), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS));

		sphalerite = Game.addBlock("sphalerite", BlockType.BASIC.create(FabricBlockSettings.copyOf(Blocks.IRON_ORE)
				.requiresTool()
				.breakByTool(FabricToolTags.PICKAXES, mohrHardness(3.5f, 4))), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS));

		smithsonite = Game.addBlock("smithsonite", BlockType.BASIC.create(FabricBlockSettings.copyOf(Blocks.IRON_ORE)
				.requiresTool()
				.breakByTool(FabricToolTags.PICKAXES, mohrHardness(4.5f))), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS));

		//  Vanadium - hardened steel
		// ===========

		//  Chromium - stainless steel
		// ===========

		//  Titanium
		// ===========

		//    Tin
		// ===========

		tinIngot = Game.addItem("tin_ingot", new Item(new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS)));

		blockTin = Game.addBlock("tin_block", BlockType.BASIC.create(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK)
				.requiresTool()
				.breakByTool(FabricToolTags.PICKAXES, 1)), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS));

		cassiterite = Game.addBlock("cassiterite", BlockType.BASIC.create(FabricBlockSettings.copyOf(Blocks.IRON_ORE)
				.requiresTool()
				.breakByTool(FabricToolTags.PICKAXES, mohrHardness(6, 7))), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS));

		stannite = Game.addBlock("stannite", BlockType.BASIC.create(FabricBlockSettings.copyOf(Blocks.IRON_ORE)
				.requiresTool()
				.breakByTool(FabricToolTags.PICKAXES, mohrHardness(4))), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS));

		//    Lead
		// ===========

		blockLead = Game.addBlock("lead_block", BlockType.BASIC.create(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK)
				.requiresTool()
				.breakByTool(FabricToolTags.PICKAXES, mohrHardness(1.5f))), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS));

		nativeLead = Game.addBlock("lead_ore", BlockType.BASIC.create(FabricBlockSettings.copyOf(Blocks.IRON_ORE)
				.requiresTool()
				.breakByTool(FabricToolTags.PICKAXES, mohrHardness(1.5f))), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS));

		// also a silver ore
		galena = Game.addBlock("galena", BlockType.BASIC.create(FabricBlockSettings.copyOf(Blocks.IRON_ORE)
				.requiresTool()
				.breakByTool(FabricToolTags.PICKAXES, mohrHardness(2.5f, 2.75f))), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS));

		//   Silver
		// ===========

		//  Aluminum and Cryolite (bauxite (also gallium) in swamps, and very rare other sources (native?))
		// ===========

		//  Manganese - enhancing steel (corrosion resistance)
		// ===========

		// Mercury
		// TODO make it spawn liquid mercury on break.
		mercuryDeposit = Game.addBlock("mercury_ore", BlockType.BASIC.create(FabricBlockSettings.copyOf(Blocks.IRON_ORE)
				.requiresTool()
				.breakByTool(FabricToolTags.PICKAXES, 1)), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS));

		mercury = Game.addFluid(new Mercury(), Mercury.Block::new);

		// Alloys

		blockBrass = Game.addBlock("brass_block", BlockType.BASIC.create(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK)
				.requiresTool()
				.breakByTool(FabricToolTags.PICKAXES, 1)), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS));

		blockBronze = Game.addBlock("bronze_block", BlockType.BASIC.create(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK)
				.requiresTool()
				.breakByTool(FabricToolTags.PICKAXES, 2)), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS));

		// Yes, it very very rarely can form naturally. I'm gonna make this really rare.
		// Also a meme reference to that comment on the forge allomancy mod
		nativeBrass = Game.addBlock("brass_ore", BlockType.BASIC.create(FabricBlockSettings.copyOf(Blocks.IRON_ORE)
				.requiresTool()
				.breakByTool(FabricToolTags.PICKAXES, 1)), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS));

	}

	public static void addBiomes() {
		silicaSandBeach = Game.addBiome(new BiomeGen(new BiomeGen.Properties("silica_sand_beach", Biome.BiomeCategory.BEACH)
				.shape(Biomes.BEACH.getDepth(), Biomes.BEACH.getScale())
				.surfaceBlocks(new SurfaceBuilderBaseConfiguration(
						silicaSand.defaultBlockState(),
						silicaSand.defaultBlockState(),
						Blocks.GRAVEL.defaultBlockState()))
				)
				.addDefaultFeatures(true, true));

		silicaSandDesert = Game.addBiome(new BiomeGen(new BiomeGen.Properties("silica_sand_desert", Biome.BiomeCategory.BEACH)
				.shape(Biomes.DESERT.getDepth(), Biomes.DESERT.getScale())
				.surfaceBlocks(new SurfaceBuilderBaseConfiguration(
						silicaSand.defaultBlockState(),
						silicaSandstone.defaultBlockState(),
						Blocks.GRAVEL.defaultBlockState()))
				)
				.addDefaultFeatures(false, true));

		FabricBiomes.addSpawnBiome(silicaSandBeach);
		FabricBiomes.addSpawnBiome(silicaSandDesert);

		OverworldBiomes.addBiomeVariant(Biomes.DESERT, silicaSandDesert, 0.25);
		OverworldBiomes.addShoreBiome(silicaSandDesert, silicaSandBeach, 1.0);

		OpenSimplexNoise noise = new OpenSimplexNoise(new Random(0));

		BiomePlacementCallback.OVERWORLD.register((biome, x, z) -> {
			if (biome.get() == Biomes.BEACH) {
				if (noise.sample(0.01 * x, 0.01 * z) > 0.3) {
					biome.set(silicaSandBeach);
					return true;
				}
			}

			return false;
		});
	}

	public static void addOreGen() {
		// Tin (generates with granite); Copper
		Game.addOverworldOre("copper_tin_ore_placer", new Game.ComplexOre()
				.addOre(new Ore(12, 20)
						.addState(96, 1.0f, azurite)
						.addState(64, 1.0f, bornite)
						.addState(64, 0.5f, chalcocite)
						.addState(96, 1.5f, chalcopyrite)
						.addState(96, 1.0f, cuprite)
						.addState(128, 1.0f, malachite), 0.5f, 0.6f)
				.addOre(new Ore(10, 14, Blocks.GRANITE.defaultBlockState())
						.addState(64, 0.25f, stannite)
						.addState(44, 1.0f, cassiterite), -0.5f, 0.6f));

		Game.addOverworldOre(new Ore(4, 2)
				.addState(64, 1.0f, nativeCopper));

		// Zinc
		Game.addOverworldOre(new Ore(12, 22)
				.addState(72, 1.0f, smithsonite)
				.addState(72, 1.0f, sphalerite));

		Game.addOverworldOre(new Ore(2, 1)
				.addState(48, 1.0f, nativeZinc));
	}

	public static Item chalcopyriteChunks;
	public static Item chalcopyriteChunksSilica;
	public static Item copperIngot;
	public static Item tinIngot;
	public static Item zincIngot;

	public static Block azurite;
	public static Block bornite;
	public static Block cassiterite;
	public static Block chalcocite;
	public static Block chalcopyrite;
	public static Block cuprite;
	public static Block galena;
	public static Block malachite;
	public static Block mercuryDeposit;
	public static Block nativeBrass;
	public static Block nativeCopper;
	public static Block nativeLead;
	public static Block nativeZinc;
	public static Block smithsonite;
	public static Block sphalerite;
	public static Block stannite;

	public static Block blockBrass;
	public static Block blockBronze;
	public static Block blockCopper;
	public static Block blockLead;
	public static Block blockTin;
	public static Block blockZinc;
	public static Block silicaSand;
	public static Block silicaSandstone;

	public static Fluid mercury;

	public static BiomeGen silicaSandBeach;
	public static BiomeGen silicaSandDesert;
}
