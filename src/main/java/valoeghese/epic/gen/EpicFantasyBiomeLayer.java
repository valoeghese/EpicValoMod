package valoeghese.epic.gen;

import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.LongFunction;

import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.area.AreaFactory;
import net.minecraft.world.level.newbiome.area.LazyArea;
import net.minecraft.world.level.newbiome.context.BigContext;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.AddIslandLayer;
import net.minecraft.world.level.newbiome.layer.BiomeEdgeLayer;
import net.minecraft.world.level.newbiome.layer.Layer;
import net.minecraft.world.level.newbiome.layer.RareBiomeSpotLayer;
import net.minecraft.world.level.newbiome.layer.RegionHillsLayer;
import net.minecraft.world.level.newbiome.layer.RiverInitLayer;
import net.minecraft.world.level.newbiome.layer.RiverLayer;
import net.minecraft.world.level.newbiome.layer.RiverMixerLayer;
import net.minecraft.world.level.newbiome.layer.ShoreLayer;
import net.minecraft.world.level.newbiome.layer.SmoothLayer;
import net.minecraft.world.level.newbiome.layer.ZoomLayer;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer0;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer1;
import valoeghese.epic.rpgtweak.LayerStack;
import valoeghese.epic.util.OpenSimplexNoise;

public final class EpicFantasyBiomeLayer implements AreaTransformer0 {
	private EpicFantasyBiomeLayer(long seed) {
		Random rand = new Random(seed);

		this.temperature = new OpenSimplexNoise(rand);
		this.rainfall = new OpenSimplexNoise(rand);
		this.ocean = new OpenSimplexNoise(rand);
	}

	private final OpenSimplexNoise temperature;
	private final OpenSimplexNoise rainfall;
	private final OpenSimplexNoise ocean;

	@Override
	public int applyPixel(Context context, int i, int j) {
		if ((i != 0 || j != 0) && ocean.sample(i / 10, j / 10) > 0) {
			return OCEAN;
		} else {
			int temp = sampleTemp(i, j);
			int rain = sampleRain(i, j);

			switch (temp) {
			case 1:
				switch (rain) {
				case 1: return JUNGLE;
				case 0: return context.nextRandom(2) == 0 ? GRASSLAND : SAVANNA;
				case -1: return context.nextRandom(3) == 0 ? SAVANNA : DESERT;
				}
			case 0:
				switch (rain) {
				case 1: return context.nextRandom(2) == 0 ? WETLAND : FOREST;
				default: return randomTemperate(context);
				}
			case -1:
				switch (rain) {
				case 1: return context.nextRandom(3) == 0 ? FOREST : TAIGA;
				case 0: return context.nextRandom(2) == 0 ? TAIGA : ICE_FLATS;
				case -1: return ICE_FLATS;
				}
			}
		}

		System.out.println("Impossible Biome Category?");
		return OCEAN;
	}

	private int sampleRain(int i, int j) {
		double sample = this.rainfall.sample(i / 3.5, j / 3.5);

		if (sample < -0.29) return -1;
		if (sample < 0.29) return 0;
		return 1;
	}

	private int sampleTemp(int i, int j) {
		double temp = this.temperature.sample(i / 3.5, j / 3.5);

		if (temp < -0.29) return -1;
		if (temp < 0.29) return 0;
		return 1;
	}

	private static <T extends Area, C extends BigContext<T>> AreaFactory<T> zoom(long l, AreaTransformer1 areaTransformer1, AreaFactory<T> areaFactory, int i, LongFunction<C> longFunction) {
		AreaFactory<T> areaFactory2 = areaFactory;

		for(int j = 0; j < i; ++j) {
			areaFactory2 = areaTransformer1.run(longFunction.apply(l + (long)j), areaFactory2);
		}

		return areaFactory2;
	}

	public static Layer getDefaultLayer(long seed) {
		AtomicReference<AreaFactory<LazyArea>> river = new AtomicReference<>();

		return new Layer(LayerStack.manufactureLayer(seed, new EpicFantasyBiomeLayer(seed), (stack, i, cp) -> {
			switch (i) {
			case BIOME_SIZE:
				stack = BiomeGroupToBiomeLayer.INSTANCE.run(cp.apply(100L), stack);
				AreaFactory<LazyArea> riverr = RiverInitLayer.INSTANCE.run(cp.apply(100L), stack);
				riverr = zoom(1001L, ZoomLayer.NORMAL, riverr, 2, cp);
				river.set(riverr);
				break;
			case HILL_SIZE:
				stack = BiomeEdgeLayer.INSTANCE.run(cp.apply(200L), stack);
				stack = RegionHillsLayer.INSTANCE.run(cp.apply(200L), stack, river.get());
				stack = RareBiomeSpotLayer.INSTANCE.run(cp.apply(1001L), stack);
				break;
			case ZOOM_1_SIZE:
				stack = AddIslandLayer.INSTANCE.run(cp.apply(3L), stack);
				break;
			case ZOOM_2_SIZE:
				stack = ShoreLayer.INSTANCE.run(cp.apply(1000L), stack);
				break;
			case MERGE_SIZE:
				AreaFactory<LazyArea> river0 = zoom(1000L, ZoomLayer.NORMAL, river.get(), MERGE_SIZE - 2, cp);
				river0 = RiverLayer.INSTANCE.run(cp.apply(1L), river0);
				river0 = SmoothLayer.INSTANCE.run(cp.apply(1000L), river0);
				stack = SmoothLayer.INSTANCE.run(cp.apply(1000L), stack);
				stack = RiverMixerLayer.INSTANCE.run(cp.apply(100L), stack, river0);
				break;
			}
			return stack;
		}));
	}

	private static final int BIOME_SIZE = 0;
	private static final int HILL_SIZE = 2;
	private static final int ZOOM_1_SIZE = 3;
	private static final int ZOOM_2_SIZE = 4;
	private static final int MERGE_SIZE = LayerStack.SCALE - 1;

	private static int randomTemperate(Context context) {
		switch (context.nextRandom(7)) {
		case 0:
		case 1: return FOREST;
		case 2:
		case 3: return WETLAND;
		default: return GRASSLAND;
		}
	}

	// Inspired by the cube world biomes
	public static final int OCEAN = 0;
	public static final int DESERT = 1;
	public static final int SAVANNA = 2;
	public static final int GRASSLAND = 3;
	public static final int FOREST = 4;
	public static final int JUNGLE = 5;
	public static final int WETLAND = 6;
	public static final int TAIGA = 7;
	public static final int ICE_FLATS = 8;
}
