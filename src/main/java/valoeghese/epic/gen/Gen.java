package valoeghese.epic.gen;

import com.google.common.collect.ImmutableList;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.Features;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import net.minecraft.world.level.levelgen.placement.FrequencyWithExtraChanceDecoratorConfiguration;
import valoeghese.epic.Setup;
import valoeghese.epic.gen.deobf.BiomeGenProperties;

public class Gen {
	public static ConfiguredFeature<?, ?> groveTrees = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE,
			new ResourceLocation(Setup.MODID, "forest_less_trees"),
			Feature.RANDOM_SELECTOR
			.configured(new RandomFeatureConfiguration(ImmutableList.of(Features.PINE.weighted(0.1F), Features.FANCY_OAK_BEES_0002.weighted(0.05F)), Features.OAK_BEES_0002))
			.decorated(Features.Decorators.HEIGHTMAP_SQUARE)
			.decorated(FeatureDecorator.COUNT_EXTRA.configured(new FrequencyWithExtraChanceDecoratorConfiguration(10, 0.1F, 1))));

	public static void loadGenScripts() {
		new BiomeGenProperties();
		Registry.register(Registry.CHUNK_GENERATOR, new ResourceLocation(Setup.MODID, "epic_fantasy"), EpicFantasyChunkGenerator.CODEC);

		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			dispatcher.register(Commands.literal("reloadscripts")
					.requires(stack -> stack.hasPermission(2))
					.executes(context -> {
						try {
							new BiomeGenProperties();
							BiomeGenProperties.setupForChunkGen();
						} catch (Exception e) {
							e.printStackTrace();
							throw e;
						}
						return 1;
					}));
		});

		grove = BuiltinRegistries.BIOME.get(new ResourceLocation(Setup.MODID, "grove"));
		rollingPlains = BuiltinRegistries.BIOME.get(new ResourceLocation(Setup.MODID, "rolling_plains"));
	}

	public static Biome grove;
	public static Biome rollingPlains;
}
