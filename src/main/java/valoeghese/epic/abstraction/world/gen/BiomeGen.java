package valoeghese.epic.abstraction.world.gen;

import java.util.Random;

import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderBaseConfiguration;
import valoeghese.epic.abstraction.core.Game;

public class BiomeGen extends Biome {
	public BiomeGen(Properties properties) {
		super(construct(properties));

		BiomeDefaultFeatures.addSurfaceFreezing(this);
		((DelegatingSurfaceBuilder) this.getSurfaceBuilder().get().surfaceBuilder).delegate = this;
		this.location = new ResourceLocation(Game.primedId, properties.name);
	}

	public final ResourceLocation location;

	public BiomeGen addDefaultFeatures(boolean lakes, boolean foliage) {
		BiomeDefaultFeatures.addDefaultCarvers(this);

		if (lakes) {
			BiomeDefaultFeatures.addDefaultLakes(this);
		}

		BiomeDefaultFeatures.addDefaultMonsterRoom(this);
		BiomeDefaultFeatures.addDefaultUndergroundVariety(this);
		BiomeDefaultFeatures.addDefaultOres(this);
		BiomeDefaultFeatures.addDefaultSoftDisks(this);

		if (foliage) {
			BiomeDefaultFeatures.addDefaultFlowers(this);
			BiomeDefaultFeatures.addDefaultGrass(this);
			BiomeDefaultFeatures.addDefaultMushrooms(this);
			BiomeDefaultFeatures.addDefaultExtraVegetation(this);
		}

		BiomeDefaultFeatures.addDefaultSprings(this);
		return this;
	}

	protected void buildSurface(Random rand, ChunkAccess chunk, Biome biome, int x, int z, int height, double noise,
			BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderBaseConfiguration config) {
		SurfaceBuilder.DEFAULT.apply(rand, chunk, biome, x, z, height, noise, defaultBlock, defaultFluid, seaLevel, seed, config);
	}

	private static BiomeBuilder construct(Properties properties) {
		ConfiguredSurfaceBuilder<SurfaceBuilderBaseConfiguration> sb = new DelegatingSurfaceBuilder().configured(properties.config);

		BiomeBuilder result = new BiomeBuilder()
				.depth(properties.baseHeight)
				.scale(properties.heightVariation)
				.biomeCategory(properties.category)
				.precipitation(properties.precipitation)
				.temperature(properties.temperature)
				.downfall(properties.rainfall)
				.surfaceBuilder(() -> sb)
				.specialEffects(new BiomeSpecialEffects.Builder()
						.waterColor(properties.waterColour)
						.waterFogColor(properties.waterFogColour)
						.fogColor(Biomes.PLAINS.getFogColor())
						.ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
						.build())
				.parent(null);
		return result;
	}

	public static class Properties {
		public Properties(String name, BiomeCategory category) {
			this.name = name;
			this.category = category;
		}

		public final String name;
		public final BiomeCategory category;
		private float baseHeight = 0.125f;
		private float heightVariation = 0.0f;
		private Precipitation precipitation = Precipitation.RAIN;
		private float temperature = 0.5f;
		private float rainfall = 0.5f;
		private int waterColour = Biomes.PLAINS.getWaterColor();
		private int waterFogColour = Biomes.PLAINS.getWaterFogColor();
		private SurfaceBuilderBaseConfiguration config = SurfaceBuilder.CONFIG_GRASS;

		public float getTemperature() {
			return this.temperature;
		}

		public float getRainfall() {
			return this.rainfall;
		}

		public Properties surfaceBlocks(SurfaceBuilderBaseConfiguration config) {
			this.config = config;
			return this;
		}

		public Properties shape(float baseHeight, float heightVariation) {
			this.baseHeight = baseHeight;
			this.heightVariation = heightVariation;
			return this;
		}

		public Properties climate(float temperature, float rainfall) {
			this.temperature = temperature;
			this.rainfall = rainfall;
			return this;
		}

		public Properties climate(Precipitation precipitation, float temperature, float rainfall) {
			this.precipitation = precipitation;
			return this.climate(temperature, rainfall);
		}
	}

	static class DelegatingSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderBaseConfiguration> {
		public DelegatingSurfaceBuilder() {
			super(SurfaceBuilderBaseConfiguration.CODEC);
		}

		private BiomeGen delegate;

		@Override
		public void apply(Random rand, ChunkAccess chunk, Biome biome, int x, int z, int height, double noise,
				BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderBaseConfiguration config) {
			this.delegate.buildSurface(rand, chunk, biome, x, z, height, noise, defaultBlock, defaultFluid, seaLevel, seed, config);
		}
	}
}
