package valoeghese.epic.util;

import java.util.List;
import java.util.stream.IntStream;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.synth.SurfaceNoise;

public class BetterNoise implements SurfaceNoise {
	private final OpenSimplexGenNoise[] field_15744;
	private final double highestFreqValueFactor;
	private final double highestFreqInputFactor;

	public BetterNoise(WorldgenRandom worldgenRandom, IntStream intStream) {
		this(worldgenRandom, intStream.boxed().collect(ImmutableList.toImmutableList()));
	}

	public BetterNoise(WorldgenRandom worldgenRandom, List<Integer> list) {
		this(worldgenRandom, (IntSortedSet)(new IntRBTreeSet(list)));
	}

	private BetterNoise(WorldgenRandom worldgenRandom, IntSortedSet intSortedSet) {
		if (intSortedSet.isEmpty()) {
			throw new IllegalArgumentException("Need some octaves!");
		} else {
			int i = -intSortedSet.firstInt();
			int j = intSortedSet.lastInt();
			int k = i + j + 1;
			if (k < 1) {
				throw new IllegalArgumentException("Total number of octaves needs to be >= 1");
			} else {
				OpenSimplexGenNoise improvedNoise = new OpenSimplexGenNoise(worldgenRandom);
				int l = j;
				this.field_15744 = new OpenSimplexGenNoise[k];
				if (j >= 0 && j < k && intSortedSet.contains(0)) {
					this.field_15744[j] = improvedNoise;
				}

				for(int m = j + 1; m < k; ++m) {
					if (m >= 0 && intSortedSet.contains(l - m)) {
						this.field_15744[m] = new OpenSimplexGenNoise(worldgenRandom);
					} else {
						worldgenRandom.consumeCount(262);
					}
				}

				if (j > 0) {
					long n = (long)(improvedNoise.noise(0.0D, 0.0D, 0.0D, 0.0D, 0.0D) * 9.223372036854776E18D);
					WorldgenRandom worldgenRandom2 = new WorldgenRandom(n);

					for(int o = l - 1; o >= 0; --o) {
						if (o < k && intSortedSet.contains(l - o)) {
							this.field_15744[o] = new OpenSimplexGenNoise(worldgenRandom2);
						} else {
							worldgenRandom2.consumeCount(262);
						}
					}
				}

				this.highestFreqInputFactor = Math.pow(2.0D, (double)j);
				this.highestFreqValueFactor = 1.0D / (Math.pow(2.0D, (double)k) - 1.0D);
			}
		}
	}

	public double getValue(double d, double e, double f) {
		return this.getValue(d, e, f, 0.0D, 0.0D, false);
	}

	public double getValue(double d, double e, double f, double g, double h, boolean bl) {
		double i = 0.0D;
		double j = this.highestFreqInputFactor;
		double k = this.highestFreqValueFactor;
		OpenSimplexGenNoise[] var18 = this.field_15744;
		int var19 = var18.length;

		for(int var20 = 0; var20 < var19; ++var20) {
			OpenSimplexGenNoise improvedNoise = var18[var20];
			if (improvedNoise != null) {
				i += improvedNoise.noise(wrap(d * j), bl ? -improvedNoise.yOffset : wrap(e * j), wrap(f * j), g * j, h * j) * k;
			}

			j /= 2.0D;
			k *= 2.0D;
		}

		return i;
	}

	@Nullable
	public OpenSimplexGenNoise getOctaveNoise(int i) {
		return this.field_15744[i];
	}

	public static double wrap(double d) {
		return d - (double)Mth.lfloor(d / 3.3554432E7D + 0.5D) * 3.3554432E7D;
	}

	public double getSurfaceNoiseValue(double d, double e, double f, double g) {
		return this.getValue(d, e, 0.0D, f, g, false);
	}
}
