package valoeghese.epic.abstraction.world.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import valoeghese.epic.util.OpenSimplexNoise;

public class ComplexOreGenerator implements Generator {
	@Override
	public boolean generate(World world, Random rand, BlockPos pos) {
		throw new UnsupportedOperationException("Do not use simplified Generate Method for ComplexOreGenerator!");
	}

	private static final OpenSimplexNoise NOISE = new OpenSimplexNoise(new Random(1032));
	private final List<OreTuple> features = new ArrayList<>();

	public ComplexOreGenerator addFeature(ConfiguredFeature<?, ?> ore, double target, double distance) {
		this.features.add(new OreTuple(ore, target, distance));
		return this;
	}

	@Override
	public boolean generate(World world, ChunkGenerator generator, Random rand, BlockPos pos) {
		boolean atLeastOneSuccess = false;
		int i = 0;
		double sample = NOISE.sample(pos.getX() * 0.008, pos.getZ() * 0.008);

		for (OreTuple ot : this.features) {
			if (ot.min < sample && sample < ot.max) {
				++i;
				atLeastOneSuccess |= ot.ore.place(world.getParent(), generator, rand, pos);
			}
		}

		return atLeastOneSuccess || i == 0;
	}

	private static class OreTuple {
		OreTuple(ConfiguredFeature<?, ?> ore, double target, double distance) {
			this.ore = ore;
			this.min = target - distance;
			this.max = target + distance;
		}

		private final ConfiguredFeature<?, ?> ore;
		private final double min;
		private final double max;
	}
}
