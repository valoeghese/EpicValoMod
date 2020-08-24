package valoeghese.epic.gen;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.OverworldBiomeSource;
import net.minecraft.world.level.newbiome.layer.Layer;

public final class EpicFantasyBiomeSource extends BiomeSource {
	public EpicFantasyBiomeSource(long seed) {
		super(POSSIBLE_BIOMES);
		this.seed = seed;
		this.layer = EpicFantasyBiomeLayer.getDefaultLayer(seed);
	}

	private final long seed;
	private final Layer layer;

	@Override
	public Biome getNoiseBiome(int x, int j, int z) {
		return this.layer.get(x, z);
	}

	@Override
	protected Codec<? extends BiomeSource> codec() {
		return CODEC;
	}

	@Override
	public BiomeSource withSeed(long l) {
		return new EpicFantasyBiomeSource(l);
	}

	public static final Codec<EpicFantasyBiomeSource> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(Codec.LONG.fieldOf("seed").stable().forGetter(source -> source.seed))
				.apply(instance, instance.stable(EpicFantasyBiomeSource::new));
	});

	private static final List<Biome> POSSIBLE_BIOMES = new OverworldBiomeSource(0, false, false).possibleBiomes();
}
