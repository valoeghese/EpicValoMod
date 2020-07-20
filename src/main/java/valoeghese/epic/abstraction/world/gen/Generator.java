package valoeghese.epic.abstraction.world.gen;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

@FunctionalInterface
public interface Generator {
	boolean generate(World world, Random rand, BlockPos pos);

	default boolean generate(World world, ChunkGenerator generator, Random rand, BlockPos pos) {
		return this.generate(world, rand, pos);
	}

	class GeneratorFeature extends Feature<NoneFeatureConfiguration> {
		public GeneratorFeature(Generator generator) {
			super(NoneFeatureConfiguration.CODEC);

			this.generator = generator;
		}

		private final Generator generator;

		@Override
		public boolean place(WorldGenLevel level, ChunkGenerator generator, Random rand, BlockPos pos, NoneFeatureConfiguration config) {
			return this.generator.generate(new World(level), generator, rand, pos);
		}
	}
}
