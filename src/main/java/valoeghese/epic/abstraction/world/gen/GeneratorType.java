package valoeghese.epic.abstraction.world.gen;

import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public final class GeneratorType {
	public GeneratorType(Generator generator) {
		this.generator = generator;
		this.feature = new Generator.GeneratorFeature(generator);
		this.featureConfigured = feature.configured(new NoneFeatureConfiguration());
	}

	public final Generator generator;
	public final Feature<NoneFeatureConfiguration> feature;
	public final ConfiguredFeature<NoneFeatureConfiguration,?> featureConfigured;
}
