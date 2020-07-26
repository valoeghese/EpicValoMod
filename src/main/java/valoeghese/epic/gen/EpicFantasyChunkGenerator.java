package valoeghese.epic.gen;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.TheEndBiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.structures.JigsawJunction;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;
import net.minecraft.world.level.levelgen.synth.SurfaceNoise;
import valoeghese.epic.util.BetterNoise;
import valoeghese.epic.util.OpenSimplexGenNoise;
import valoeghese.epic.util.OpenSimplexNoise;

public final class EpicFantasyChunkGenerator extends ChunkGenerator {
	public static final Codec<EpicFantasyChunkGenerator> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(BiomeSource.CODEC.fieldOf("biome_source").forGetter((EpicFantasyChunkGenerator) -> {
			return EpicFantasyChunkGenerator.biomeSource;
		}), Codec.LONG.fieldOf("seed").stable().forGetter((EpicFantasyChunkGenerator) -> {
			return EpicFantasyChunkGenerator.seed;
		}), NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter((EpicFantasyChunkGenerator) -> {
			return EpicFantasyChunkGenerator.settings;
		})).apply(instance, instance.stable(EpicFantasyChunkGenerator::new));
	});
	private static final float[] BEARD_KERNEL = Util.make(new float[13824], (fs) -> {
		for(int i = 0; i < 24; ++i) {
			for(int j = 0; j < 24; ++j) {
				for(int k = 0; k < 24; ++k) {
					fs[i * 24 * 24 + j * 24 + k] = (float)computeContribution(j - 12, k - 12, i - 12);
				}
			}
		}

	});

	private static final Int2ObjectMap<float[]> BIOME_WEIGHTS_PROVIDER = new Int2ObjectArrayMap<>();

	private static float[] getBiomeWeights(int smoothness) {
		return BIOME_WEIGHTS_PROVIDER.computeIfAbsent(smoothness, n -> {
			int largeN = 2 * n + 1;

			return Util.make(new float[largeN * largeN], (fs) -> {
				for(int i = -n; i <= n; ++i) {
					for(int j = -n; j <= n; ++j) {
						float f = 10.0F / Mth.sqrt((float)(i * i + j * j) + 0.2F);
						fs[i + n + (j + n) * largeN] = f;
					}
				}
			});
		});
	}

	private static final BlockState AIR;
	private final int chunkHeight;
	private final int chunkWidth;
	private final int chunkCountX;
	private final int chunkCountY;
	private final int chunkCountZ;
	private final WorldgenRandom random;
	private final BetterNoise minLimitNoise;
	private final BetterNoise maxLimitNoise;
	private final BetterNoise mainNoise;
	private final SurfaceNoise surfaceNoise;
	private final BetterNoise depthNoise;
	@Nullable
	private final SimplexNoise islandNoise;
	private final BlockState defaultBlock;
	private final BlockState defaultFluid;
	private final OpenSimplexNoise projectionNoise;
	private final long seed;
	protected final Supplier<NoiseGeneratorSettings> settings;
	private final int height;

	public EpicFantasyChunkGenerator(BiomeSource biomeSource, long l, Supplier<NoiseGeneratorSettings> supplier) {
		this(biomeSource, biomeSource, l, supplier);
	}

	private EpicFantasyChunkGenerator(BiomeSource biomeSource, BiomeSource biomeSource2, long l, Supplier<NoiseGeneratorSettings> supplier) {
		super(biomeSource, biomeSource2, ((NoiseGeneratorSettings)supplier.get()).structureSettings(), l);
		this.seed = l;
		NoiseGeneratorSettings noiseGeneratorSettings = (NoiseGeneratorSettings)supplier.get();
		this.settings = supplier;
		NoiseSettings noiseSettings = noiseGeneratorSettings.noiseSettings();
		this.height = noiseSettings.height();
		this.chunkHeight = noiseSettings.noiseSizeVertical() * 4;
		this.chunkWidth = noiseSettings.noiseSizeHorizontal() * 4;
		this.defaultBlock = noiseGeneratorSettings.getDefaultBlock();
		this.defaultFluid = noiseGeneratorSettings.getDefaultFluid();
		this.chunkCountX = 16 / this.chunkWidth;
		this.chunkCountY = noiseSettings.height() / this.chunkHeight;
		this.chunkCountZ = 16 / this.chunkWidth;
		this.random = new WorldgenRandom(l);
		this.minLimitNoise = new BetterNoise(this.random, IntStream.rangeClosed(-15, 0));
		this.maxLimitNoise = new BetterNoise(this.random, IntStream.rangeClosed(-15, 0));
		this.mainNoise = new BetterNoise(this.random, IntStream.rangeClosed(-7, 0));
		this.surfaceNoise = (SurfaceNoise)(noiseSettings.useSimplexSurfaceNoise() ? new PerlinSimplexNoise(this.random, IntStream.rangeClosed(-3, 0)) : new PerlinNoise(this.random, IntStream.rangeClosed(-3, 0)));
		this.random.consumeCount(2620);
		this.depthNoise = new BetterNoise(this.random, IntStream.rangeClosed(-15, 0));

		if (noiseSettings.islandNoiseOverride()) {
			WorldgenRandom rand2 = new WorldgenRandom(l);
			rand2.consumeCount(17292);
			this.islandNoise = new SimplexNoise(rand2);
		} else {
			this.islandNoise = null;
		}

		this.projectionNoise = new OpenSimplexNoise(this.random);
		BiomeGenProperties.setupForChunkGen();
	}

	protected Codec<? extends ChunkGenerator> codec() {
		return CODEC;
	}

	@Environment(EnvType.CLIENT)
	public ChunkGenerator withSeed(long l) {
		return new EpicFantasyChunkGenerator(this.biomeSource.withSeed(l), l, this.settings);
	}

	public boolean stable(long l, NoiseGeneratorSettings noiseGeneratorSettings) {
		return this.seed == l && ((NoiseGeneratorSettings)this.settings.get()).stable(noiseGeneratorSettings);
	}

	private double sampleAndClampNoise(int x, int y, int z, double d, double e, double f, double g, float hilliness) {
		double h = 0.0D;
		double l = 0.0D;
		double m = 0.0D;
		double n = 1.0D;

		for(int o = 0; o < 16; ++o) {
			double p = PerlinNoise.wrap((double)x * d * n);
			double q = PerlinNoise.wrap((double)y * e * n);
			double r = PerlinNoise.wrap((double)z * d * n);
			double s = e * n;
			OpenSimplexGenNoise improvedNoise = this.minLimitNoise.getOctaveNoise(o);
			if (improvedNoise != null) {
				h += improvedNoise.noise(p, q, r, s, (double)y * s) / n;
			}

			OpenSimplexGenNoise improvedNoise2 = this.maxLimitNoise.getOctaveNoise(o);
			if (improvedNoise2 != null) {
				l += improvedNoise2.noise(p, q, r, s, (double)y * s) / n;
			}

			if (o < 8) {
				OpenSimplexGenNoise improvedNoise3 = this.mainNoise.getOctaveNoise(o);
				if (improvedNoise3 != null) {
					m += improvedNoise3.noise(PerlinNoise.wrap((double)x * f * n), PerlinNoise.wrap((double)y * g * n), PerlinNoise.wrap((double)z * f * n), g * n, (double)y * g * n) / n;
				}
			}

			n /= 2.0D;
		}

		// vanilla uses 10.0D instead of 15.0D
		return Mth.clampedLerp(hilliness * h / 512.0D, hilliness * l / 512.0D, (m / 15.0D + 1.0D) / 2.0D);
	}

	private double[] makeAndFillNoiseColumn(int i, int j) {
		double[] ds = new double[this.chunkCountY + 1];
		this.getNoiseParameters(ds, i, j);
		return ds;
	}

	private void getNoiseParameters(double[] result, int genX, int genZ) {
		NoiseSettings noiseSettings = ((NoiseGeneratorSettings)this.settings.get()).noiseSettings();

		double ac;
		double ad;
		double progress;
		double topSlideSize;

		int seaLevel = this.getSeaLevel();
		GenerationProperties centralProperties = BiomeGenProperties.getGenerationProperties(this.biomeSource.getNoiseBiome(genX, seaLevel, genZ));

		if (this.islandNoise != null) {
			ac = (double)(TheEndBiomeSource.getHeightValue(this.islandNoise, genX, genZ) - 8.0F);
			if (ac > 0.0D) {
				ad = 0.25D;
			} else {
				ad = 1.0D;
			}
		} else {
			float finalSummedScale = 0.0F;
			float finalSummedDepth = 0.0F;
			float totalWeight = 0.0F;

			float projectionNoise = (float) this.projectionNoise.sample(genX * centralProperties.projectionPeriod, genZ * centralProperties.projectionPeriod);
			float centralBiomeDepth = centralProperties.depth + centralProperties.projection * projectionNoise;
			int smoothness = centralProperties.interpolation;
			int smoothnessUpper = smoothness * 2 + 1;

			for(int genXOff = -smoothness; genXOff <= smoothness; ++genXOff) {
				for(int genZOff = -smoothness; genZOff <= smoothness; ++genZOff) {
					GenerationProperties biomeProperties = BiomeGenProperties.getGenerationProperties(this.biomeSource.getNoiseBiome(genX + genXOff, seaLevel, genZ + genZOff));
					projectionNoise = (float) this.projectionNoise.sample((genX + genXOff) * biomeProperties.projectionPeriod, (genZ + genZOff) * biomeProperties.projectionPeriod);
					float depth = biomeProperties.depth + biomeProperties.projection * projectionNoise;
					float scale = biomeProperties.scale;
					float trueNoiseDepth;
					float trueNoiseScale;

					if (noiseSettings.isAmplified() && depth > 0.0F) {
						trueNoiseDepth = 1.0F + depth * 2.0F;
						trueNoiseScale = 1.0F + scale * 4.0F;
					} else {
						trueNoiseDepth = depth;
						trueNoiseScale = scale;
					}

					float weightMultiplier = depth > centralBiomeDepth ? 0.5F : 1.0F;
					float weight = weightMultiplier * getBiomeWeights(smoothness)[genXOff + smoothness + (genZOff + smoothness) * smoothnessUpper] / (trueNoiseDepth + 2.0F);
					finalSummedScale += trueNoiseScale * weight;
					finalSummedDepth += trueNoiseDepth * weight;
					totalWeight += weight;
				}
			}

			float finalDepth = finalSummedDepth / totalWeight;
			float finalScale = finalSummedScale / totalWeight;
			progress = (double)(finalDepth * 0.5F - 0.125F);
			topSlideSize = (double)(finalScale * 0.9F + 0.1F);
			ac = progress * 0.265625D;
			ad = 96.0D / topSlideSize;
		}

		double ae = 684.412D * centralProperties.period * noiseSettings.noiseSamplingSettings().xzScale();
		double af = 684.412D * centralProperties.period * noiseSettings.noiseSamplingSettings().yScale();
		double ag = ae / noiseSettings.noiseSamplingSettings().xzFactor();
		double ah = af / noiseSettings.noiseSamplingSettings().yFactor();
		progress = (double)noiseSettings.topSlideSettings().target();
		topSlideSize = (double)noiseSettings.topSlideSettings().size();
		double topSlideOffset = (double)noiseSettings.topSlideSettings().offset();
		double bottomSlideTarget = (double)noiseSettings.bottomSlideSettings().target();
		double bottomSlideSize = (double)noiseSettings.bottomSlideSettings().size();
		double bottomSlideOffset = (double)noiseSettings.bottomSlideSettings().offset();
		double densityRandomOffset = noiseSettings.randomDensityOffset() ? this.getRandomDensity(genX, genZ) : 0.0D;
		double densityFactor = noiseSettings.densityFactor();
		double densityOffset = noiseSettings.densityOffset();

		for(int genY = 0; genY <= this.chunkCountY; ++genY) {
			double value = this.sampleAndClampNoise(genX, genY, genZ, ae, af, ag, ah, centralProperties.hilliness);
			double rawDensity = 1.0D - (double)genY * 2.0D / (double)this.chunkCountY + densityRandomOffset;
			double density = rawDensity * densityFactor + densityOffset;
			double processedDensity = (density + ac) * ad;

			if (processedDensity > 0.0D) {
				value += processedDensity * 4.0D;
			} else {
				value += processedDensity;
			}

			double end;
			if (topSlideSize > 0.0D) {
				end = ((double)(this.chunkCountY - genY) - topSlideOffset) / topSlideSize;
				value = Mth.clampedLerp(progress, value, end);
			}

			if (bottomSlideSize > 0.0D) {
				end = ((double)genY - bottomSlideOffset) / bottomSlideSize;
				value = Mth.clampedLerp(bottomSlideTarget, value, end);
			}

			result[genY] = value;
		}
	}

	private double getRandomDensity(int i, int j) {
		double d = this.depthNoise.getValue((double)(i * 200), 10.0D, (double)(j * 200), 1.0D, 0.0D, true);
		double f;
		if (d < 0.0D) {
			f = -d * 0.3D;
		} else {
			f = d;
		}

		double g = f * 24.575625D - 2.0D;
		return g < 0.0D ? g * 0.009486607142857142D : Math.min(g, 1.0D) * 0.006640625D;
	}

	public int getBaseHeight(int i, int j, Heightmap.Types types) {
		return this.iterateNoiseColumn(i, j, (BlockState[])null, types.isOpaque());
	}

	public BlockGetter getBaseColumn(int i, int j) {
		BlockState[] blockStates = new BlockState[this.chunkCountY * this.chunkHeight];
		this.iterateNoiseColumn(i, j, blockStates, null);
		return new NoiseColumn(blockStates);
	}

	private int iterateNoiseColumn(int i, int j, @Nullable BlockState[] blockStates, @Nullable Predicate<BlockState> predicate) {
		int k = Math.floorDiv(i, this.chunkWidth);
		int l = Math.floorDiv(j, this.chunkWidth);
		int m = Math.floorMod(i, this.chunkWidth);
		int n = Math.floorMod(j, this.chunkWidth);
		double d = (double)m / (double)this.chunkWidth;
		double e = (double)n / (double)this.chunkWidth;
		double[][] ds = new double[][]{this.makeAndFillNoiseColumn(k, l), this.makeAndFillNoiseColumn(k, l + 1), this.makeAndFillNoiseColumn(k + 1, l), this.makeAndFillNoiseColumn(k + 1, l + 1)};

		for(int o = this.chunkCountY - 1; o >= 0; --o) {
			double f = ds[0][o];
			double g = ds[1][o];
			double h = ds[2][o];
			double p = ds[3][o];
			double q = ds[0][o + 1];
			double r = ds[1][o + 1];
			double s = ds[2][o + 1];
			double t = ds[3][o + 1];

			for(int u = this.chunkHeight - 1; u >= 0; --u) {
				double v = (double)u / (double)this.chunkHeight;
				double w = Mth.lerp3(v, d, e, f, q, h, s, g, r, p, t);
				int x = o * this.chunkHeight + u;
				BlockState blockState = this.generateBaseState(w, x);
				if (blockStates != null) {
					blockStates[x] = blockState;
				}

				if (predicate != null && predicate.test(blockState)) {
					return x + 1;
				}
			}
		}

		return 0;
	}

	protected BlockState generateBaseState(double d, int i) {
		BlockState blockState3;
		if (d > 0.0D) {
			blockState3 = this.defaultBlock;
		} else if (i < this.getSeaLevel()) {
			blockState3 = this.defaultFluid;
		} else {
			blockState3 = AIR;
		}

		return blockState3;
	}

	public void buildSurfaceAndBedrock(WorldGenRegion level, ChunkAccess chunk) {
		ChunkPos chunkPos = chunk.getPos();
		int i = chunkPos.x;
		int j = chunkPos.z;
		WorldgenRandom rand = new WorldgenRandom();
		rand.setBaseChunkSeed(i, j);
		int startX = chunkPos.getMinBlockX();
		int startZ = chunkPos.getMinBlockZ();
		double oneSixteenth = 0.0625D;
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

		for(int localX = 0; localX < 16; ++localX) {
			for(int localZ = 0; localZ < 16; ++localZ) {
				int x = startX + localX;
				int z = startZ + localZ;
				int height = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, localX, localZ) + 1;
				double blockNoise = this.surfaceNoise.getSurfaceNoiseValue((double)x * oneSixteenth, (double)z * oneSixteenth, oneSixteenth, (double)localX * oneSixteenth) * 15.0D;
				level.getBiome(pos.set(startX + localX, height, startZ + localZ)).buildSurfaceAt(rand, chunk, x, z, height, blockNoise, this.defaultBlock, this.defaultFluid, this.getSeaLevel(), level.getSeed());
			}
		}

		this.setBedrock(chunk, rand);
	}

	private void setBedrock(ChunkAccess chunk, Random random) {
		BlockPos.MutableBlockPos startPos = new BlockPos.MutableBlockPos();
		int startX = chunk.getPos().getMinBlockX();
		int startZ = chunk.getPos().getMinBlockZ();
		NoiseGeneratorSettings noiseGeneratorSettings = (NoiseGeneratorSettings)this.settings.get();
		int floorPosition = noiseGeneratorSettings.getBedrockFloorPosition();

		boolean useFloorBedrock = floorPosition + 4 >= 0 && floorPosition < this.height;

		if (useFloorBedrock) {
			for (BlockPos pos : BlockPos.betweenClosed(startX, 0, startZ, startX + 15, 0, startZ + 15)) {
				if (useFloorBedrock) {
					for(int offset = 4; offset >= 0; --offset) {
						if (offset <= random.nextInt(5)) {
							chunk.setBlockState(startPos.set(pos.getX(), floorPosition + offset, pos.getZ()), Blocks.BEDROCK.defaultBlockState(), false);
						}
					}
				}
			}
		}
	}

	public void fillFromNoise(LevelAccessor levelAccessor, StructureFeatureManager structureFeatureManager, ChunkAccess chunk) {
		ObjectList<StructurePiece> objectList = new ObjectArrayList<>(10);
		ObjectList<JigsawJunction> objectList2 = new ObjectArrayList<>(32);
		ChunkPos chunkPos = chunk.getPos();
		int i = chunkPos.x;
		int j = chunkPos.z;
		int k = i << 4;
		int l = j << 4;
		Iterator<StructureFeature<?>> var11 = StructureFeature.NOISE_AFFECTING_FEATURES.iterator();

		while(var11.hasNext()) {
			StructureFeature<?> structureFeature = (StructureFeature<?>)var11.next();
			structureFeatureManager.startsForFeature(SectionPos.of(chunkPos, 0), structureFeature).forEach((structureStart) -> {
				Iterator<StructurePiece> var6 = structureStart.getPieces().iterator();

				while(true) {
					while(true) {
						StructurePiece structurePiece;
						do {
							if (!var6.hasNext()) {
								return;
							}

							structurePiece = (StructurePiece)var6.next();
						} while(!structurePiece.isCloseToChunk(chunkPos, 12));

						if (structurePiece instanceof PoolElementStructurePiece) {
							PoolElementStructurePiece poolElementStructurePiece = (PoolElementStructurePiece)structurePiece;
							StructureTemplatePool.Projection projection = poolElementStructurePiece.getElement().getProjection();
							if (projection == StructureTemplatePool.Projection.RIGID) {
								objectList.add(poolElementStructurePiece);
							}

							Iterator<JigsawJunction> var10 = poolElementStructurePiece.getJunctions().iterator();

							while(var10.hasNext()) {
								JigsawJunction jigsawJunction = (JigsawJunction)var10.next();
								int kx = jigsawJunction.getSourceX();
								int lx = jigsawJunction.getSourceZ();
								if (kx > k - 12 && lx > l - 12 && kx < k + 15 + 12 && lx < l + 15 + 12) {
									objectList2.add(jigsawJunction);
								}
							}
						} else {
							objectList.add(structurePiece);
						}
					}
				}
			});
		}

		double[][][] noiseGen = new double[2][this.chunkCountZ + 1][this.chunkCountY + 1];

		for(int m = 0; m < this.chunkCountZ + 1; ++m) {
			noiseGen[0][m] = new double[this.chunkCountY + 1];
			this.getNoiseParameters(noiseGen[0][m], i * this.chunkCountX, j * this.chunkCountZ + m);
			noiseGen[1][m] = new double[this.chunkCountY + 1];
		}

		ProtoChunk protoChunk = (ProtoChunk) chunk;
		Heightmap heightmap = protoChunk.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
		Heightmap heightmap2 = protoChunk.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);
		BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
		ObjectListIterator<StructurePiece> objectListIterator = objectList.iterator();
		ObjectListIterator<JigsawJunction> objectListIterator2 = objectList2.iterator();

		for(int cgenX = 0; cgenX < this.chunkCountX; ++cgenX) {
			int p;

			for(p = 0; p < this.chunkCountZ + 1; ++p) {
				this.getNoiseParameters(noiseGen[1][p], i * this.chunkCountX + cgenX + 1, j * this.chunkCountZ + p);
			}

			for(p = 0; p < this.chunkCountZ; ++p) {
				LevelChunkSection levelChunkSection = protoChunk.getOrCreateSection(15);
				levelChunkSection.acquire();

				for(int q = this.chunkCountY - 1; q >= 0; --q) {
					double d = noiseGen[0][p][q];
					double e = noiseGen[0][p + 1][q];
					double f = noiseGen[1][p][q];
					double g = noiseGen[1][p + 1][q];
					double h = noiseGen[0][p][q + 1];
					double r = noiseGen[0][p + 1][q + 1];
					double s = noiseGen[1][p][q + 1];
					double t = noiseGen[1][p + 1][q + 1];

					for(int u = this.chunkHeight - 1; u >= 0; --u) {
						int v = q * this.chunkHeight + u;
						int w = v & 15;
						int x = (v >> 4);

						if (levelChunkSection.bottomBlockY() >> 4 != x) {
							levelChunkSection.release();
							levelChunkSection = protoChunk.getOrCreateSection(x);
							levelChunkSection.acquire();
						}

						double y = (double)u / (double)this.chunkHeight;
						double z = Mth.lerp(y, d, h);
						double aa = Mth.lerp(y, f, s);
						double ab = Mth.lerp(y, e, r);
						double ac = Mth.lerp(y, g, t);

						for(int ad = 0; ad < this.chunkWidth; ++ad) {
							int ae = k + cgenX * this.chunkWidth + ad;
							int af = ae & 15;
							double ag = (double)ad / (double)this.chunkWidth;
							double ah = Mth.lerp(ag, z, aa);
							double ai = Mth.lerp(ag, ab, ac);

							for(int aj = 0; aj < this.chunkWidth; ++aj) {
								int ak = l + p * this.chunkWidth + aj;
								int al = ak & 15;
								double am = (double)aj / (double)this.chunkWidth;
								double an = Mth.lerp(am, ah, ai);
								double ao = Mth.clamp(an / 200.0D, -1.0D, 1.0D);

								int at;
								int au;
								int ar;
								for(ao = ao / 2.0D - ao * ao * ao / 24.0D; objectListIterator.hasNext(); ao += getContribution(at, au, ar) * 0.8D) {
									StructurePiece structurePiece = (StructurePiece)objectListIterator.next();
									BoundingBox boundingBox = structurePiece.getBoundingBox();
									at = Math.max(0, Math.max(boundingBox.x0 - ae, ae - boundingBox.x1));
									au = v - (boundingBox.y0 + (structurePiece instanceof PoolElementStructurePiece ? ((PoolElementStructurePiece)structurePiece).getGroundLevelDelta() : 0));
									ar = Math.max(0, Math.max(boundingBox.z0 - ak, ak - boundingBox.z1));
								}

								objectListIterator.back(objectList.size());

								while(objectListIterator2.hasNext()) {
									JigsawJunction jigsawJunction = (JigsawJunction)objectListIterator2.next();
									int as = ae - jigsawJunction.getSourceX();
									at = v - jigsawJunction.getSourceGroundY();
									au = ak - jigsawJunction.getSourceZ();
									ao += getContribution(as, at, au) * 0.4D;
								}

								objectListIterator2.back(objectList2.size());
								BlockState blockState = this.generateBaseState(ao, v);
								if (blockState != AIR) {
									if (blockState.getLightEmission() != 0) {
										mutableBlockPos.set(ae, v, ak);
										protoChunk.addLight(mutableBlockPos);
									}

									levelChunkSection.setBlockState(af, w, al, blockState, false);
									heightmap.update(af, v, al, blockState);
									heightmap2.update(af, v, al, blockState);
								}
							}
						}
					}
				}

				levelChunkSection.release();
			}

			double[][] es = noiseGen[0];
			noiseGen[0] = noiseGen[1];
			noiseGen[1] = es;
		}

	}

	private static double getContribution(int x, int y, int z) {
		int l = x + 12;
		int m = y + 12;
		int n = z + 12;
		if (l >= 0 && l < 24) {
			if (m >= 0 && m < 24) {
				return n >= 0 && n < 24 ? (double)BEARD_KERNEL[n * 24 * 24 + l * 24 + m] : 0.0D;
			} else {
				return 0.0D;
			}
		} else {
			return 0.0D;
		}
	}

	private static double computeContribution(int i, int j, int k) {
		double d = (double)(i * i + k * k);
		double e = (double)j + 0.5D;
		double f = e * e;
		double g = Math.pow(2.718281828459045D, -(f / 16.0D + d / 16.0D));
		double h = -e * Mth.fastInvSqrt(f / 2.0D + d / 2.0D) / 2.0D;
		return h * g;
	}

	public int getGenDepth() {
		return this.height;
	}

	public int getSeaLevel() {
		return ((NoiseGeneratorSettings)this.settings.get()).seaLevel();
	}

	public List<Biome.SpawnerData> getMobsAt(Biome biome, StructureFeatureManager sfManager, MobCategory mobCategory, BlockPos pos) {
		if (sfManager.getStructureAt(pos, true, StructureFeature.SWAMP_HUT).isValid()) {
			if (mobCategory == MobCategory.MONSTER) {
				return StructureFeature.SWAMP_HUT.getSpecialEnemies();
			}

			if (mobCategory == MobCategory.CREATURE) {
				return StructureFeature.SWAMP_HUT.getSpecialAnimals();
			}
		}

		if (mobCategory == MobCategory.MONSTER) {
			if (sfManager.getStructureAt(pos, false, StructureFeature.PILLAGER_OUTPOST).isValid()) {
				return StructureFeature.PILLAGER_OUTPOST.getSpecialEnemies();
			}

			if (sfManager.getStructureAt(pos, false, StructureFeature.OCEAN_MONUMENT).isValid()) {
				return StructureFeature.OCEAN_MONUMENT.getSpecialEnemies();
			}

			if (sfManager.getStructureAt(pos, true, StructureFeature.NETHER_BRIDGE).isValid()) {
				return StructureFeature.NETHER_BRIDGE.getSpecialEnemies();
			}
		}

		return super.getMobsAt(biome, sfManager, mobCategory, pos);
	}

	public void spawnOriginalMobs(WorldGenRegion level) {
		int centerX = level.getCenterX();
		int centerZ = level.getCenterZ();
		Biome biome = level.getBiome((new ChunkPos(centerX, centerZ)).getWorldPosition());
		WorldgenRandom rand = new WorldgenRandom();
		rand.setDecorationSeed(level.getSeed(), centerX << 4, centerZ << 4);
		NaturalSpawner.spawnMobsForChunkGeneration(level, biome, centerX, centerZ, rand);
	}

	static {
		AIR = Blocks.AIR.defaultBlockState();
	}
}
