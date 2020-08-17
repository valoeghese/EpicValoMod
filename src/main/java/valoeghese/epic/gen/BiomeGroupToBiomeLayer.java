package valoeghese.epic.gen;

import net.minecraft.data.BuiltinRegistries;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.C0Transformer;
import valoeghese.epic.abstraction.Logger;
import valoeghese.epic.metallurgy.Metallurgy;

public enum BiomeGroupToBiomeLayer implements C0Transformer {
	INSTANCE;

	@Override
	public int apply(Context context, int prev) {
		return BuiltinRegistries.BIOME.getId(applyForBiome(context, prev));
	}

	private Biome applyForBiome(Context context, int prev) {
		switch (prev) {
		case EpicFantasyBiomeLayer.DESERT:
			if (context.nextRandom(5) == 0) return Biomes.BADLANDS_PLATEAU;

			switch (context.nextRandom(5)) {
			case 0:
				return Metallurgy.silicaSandDesert;
			case 1:
				return Biomes.DESERT_HILLS;
			case 2:
				return Biomes.GRAVELLY_MOUNTAINS;
			default:
				return Biomes.DESERT;
			}
		case EpicFantasyBiomeLayer.FOREST:
			switch (context.nextRandom(6)) {
			case 0:
				return Biomes.BIRCH_FOREST;
			case 1:
				return Biomes.DARK_FOREST;
			case 2:
				return Gen.grove;
			case 3:
				return Biomes.TALL_BIRCH_FOREST;
			default:
				return Biomes.FOREST;
			}
		case EpicFantasyBiomeLayer.GRASSLAND:
			switch (context.nextRandom(5)) {
			case 0:
				return Biomes.FOREST;
			case 1:
				return Gen.rollingPlains;
			case 2:
				return Gen.grove;
			case 3:
				return Biomes.MOUNTAINS;
			default:
				return Biomes.PLAINS;
			}
		case EpicFantasyBiomeLayer.ICE_FLATS:
			switch (context.nextRandom(4)) {
			case 0:
				return Biomes.SNOWY_MOUNTAINS;
			case 1:
				return Biomes.SNOWY_TAIGA;
			default:
				return Biomes.SNOWY_TUNDRA;
			}
		case EpicFantasyBiomeLayer.JUNGLE:
			switch (context.nextRandom(6)) {
			case 0:
			case 1:
				return Biomes.BAMBOO_JUNGLE;
			case 2:
				return Biomes.JUNGLE_EDGE; // as clearing
			default:
				return Biomes.JUNGLE;
			}
		case EpicFantasyBiomeLayer.OCEAN:
			return Biomes.OCEAN;
		case EpicFantasyBiomeLayer.SAVANNA:
			switch (context.nextRandom(5)) {
			case 0:
				return Biomes.SHATTERED_SAVANNA;
			case 1:
				return Biomes.DESERT_LAKES;
			default:
				return Biomes.SAVANNA;
			}
		case EpicFantasyBiomeLayer.TAIGA:
			switch (context.nextRandom(4)) {
			case 0:
				return Biomes.GIANT_TREE_TAIGA;
			case 1:
				return Biomes.WOODED_MOUNTAINS;
			default:
				return Biomes.TAIGA;
			}
		case EpicFantasyBiomeLayer.WETLAND:
			switch (context.nextRandom(5)) {
			case 0:
				return Biomes.PLAINS;
			case 1:
				return Gen.rollingPlains;
			case 2:
				return Biomes.WOODED_MOUNTAINS;
			default:
				return Biomes.SWAMP;
			}
		default:
			Logger.error("Gen", "Invalid Biome Group?!");
			return Biomes.DEFAULT;
		}
	}
}
