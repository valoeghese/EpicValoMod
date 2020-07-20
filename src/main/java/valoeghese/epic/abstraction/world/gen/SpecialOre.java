package valoeghese.epic.abstraction.world.gen;

import java.util.BitSet;
import java.util.Random;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration.Predicates;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;
import valoeghese.epic.abstraction.core.Game;

public class SpecialOre extends Feature<OreConfiguration> {
	public SpecialOre() {
		super(OreConfiguration.CODEC);
	}

	public static final Feature<OreConfiguration> FEATURE = Game.addFeature("special_ore", new SpecialOre());

	public boolean place(WorldGenLevel world, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, OreConfiguration config) {
		float f = random.nextFloat() * 3.1415927F;
		float g = (float)config.size / 8.0F;
		int i = Mth.ceil(((float)config.size / 16.0F * 2.0F + 1.0F) / 2.0F);
		double d = (double)blockPos.getX() + Math.sin((double)f) * (double)g;
		double e = (double)blockPos.getX() - Math.sin((double)f) * (double)g;
		double h = (double)blockPos.getZ() + Math.cos((double)f) * (double)g;
		double j = (double)blockPos.getZ() - Math.cos((double)f) * (double)g;
		double l = (double)(blockPos.getY() + random.nextInt(3) - 2);
		double m = (double)(blockPos.getY() + random.nextInt(3) - 2);
		int n = blockPos.getX() - Mth.ceil(g) - i;
		int o = blockPos.getY() - 2 - i;
		int p = blockPos.getZ() - Mth.ceil(g) - i;
		int q = 2 * (Mth.ceil(g) + i);
		int r = 2 * (2 + i);

		for(int s = n; s <= n + q; ++s) {
			for(int t = p; t <= p + q; ++t) {
				if (o <= world.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, s, t)) {
					return this.doPlace(world, random, config, d, e, h, j, l, m, n, o, p, q, r);
				}
			}
		}

		return false;
	}

	protected boolean doPlace(LevelAccessor world, Random random, OreConfiguration config, double d, double e, double f, double g, double h, double i, int j, int k, int l, int m, int n) {
		int o = 0;
		BitSet bitSet = new BitSet(m * n * m);
		BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
		int p = config.size;
		double[] ds = new double[p * 4];

		int x;
		double z;
		double aa;
		double ab;
		double ac;
		for(x = 0; x < p; ++x) {
			float r = (float)x / (float)p;
			z = Mth.lerp((double)r, d, e);
			aa = Mth.lerp((double)r, h, i);
			ab = Mth.lerp((double)r, f, g);
			ac = random.nextDouble() * (double)p / 16.0D;
			double w = ((double)(Mth.sin(3.1415927F * r) + 1.0F) * ac + 1.0D) / 2.0D;
			ds[x * 4 + 0] = z;
			ds[x * 4 + 1] = aa;
			ds[x * 4 + 2] = ab;
			ds[x * 4 + 3] = w;
		}

		for(x = 0; x < p - 1; ++x) {
			if (ds[x * 4 + 3] > 0.0D) {
				for(int y = x + 1; y < p; ++y) {
					if (ds[y * 4 + 3] > 0.0D) {
						z = ds[x * 4 + 0] - ds[y * 4 + 0];
						aa = ds[x * 4 + 1] - ds[y * 4 + 1];
						ab = ds[x * 4 + 2] - ds[y * 4 + 2];
						ac = ds[x * 4 + 3] - ds[y * 4 + 3];
						if (ac * ac > z * z + aa * aa + ab * ab) {
							if (ac > 0.0D) {
								ds[y * 4 + 3] = -1.0D;
							} else {
								ds[x * 4 + 3] = -1.0D;
							}
						}
					}
				}
			}
		}

		for(x = 0; x < p; ++x) {
			double ae = ds[x * 4 + 3];
			if (ae >= 0.0D) {
				double af = ds[x * 4 + 0];
				double ag = ds[x * 4 + 1];
				double ah = ds[x * 4 + 2];
				int ai = Math.max(Mth.floor(af - ae), j);
				int aj = Math.max(Mth.floor(ag - ae), k);
				int ak = Math.max(Mth.floor(ah - ae), l);
				int al = Math.max(Mth.floor(af + ae), ai);
				int am = Math.max(Mth.floor(ag + ae), aj);
				int an = Math.max(Mth.floor(ah + ae), ak);

				for(int ao = ai; ao <= al; ++ao) {
					double ap = ((double)ao + 0.5D - af) / ae;
					if (ap * ap < 1.0D) {
						for(int aq = aj; aq <= am; ++aq) {
							double ar = ((double)aq + 0.5D - ag) / ae;
							if (ap * ap + ar * ar < 1.0D) {
								for(int as = ak; as <= an; ++as) {
									double at = ((double)as + 0.5D - ah) / ae;
									if (ap * ap + ar * ar + at * at < 1.0D) {
										int au = ao - j + (aq - k) * m + (as - l) * m * n;
										if (!bitSet.get(au)) {
											bitSet.set(au);
											mutableBlockPos.set(ao, aq, as);
											if (Predicates.NATURAL_STONE.test(world.getBlockState(mutableBlockPos), random)) {
												world.setBlock(mutableBlockPos, random.nextBoolean() ? ((CodecHacks) config.target).blockState : config.state, 2);
												++o;
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

		return o > 0;
	}

	public static class CodecHacks extends RuleTest {
		public static final Codec<CodecHacks> CODEC = BlockState.CODEC.fieldOf("block_state").xmap(CodecHacks::new, (magic) -> {
			return magic.blockState;
		}).codec();

		public CodecHacks(BlockState blockState) {
			this.blockState = blockState;
		}

		public final BlockState blockState;

		@Override
		public boolean test(BlockState blockState, Random random) {
			throw new UnsupportedOperationException("CodecHacks is MAGIC! Not a stupid predicate!");
		}

		@Override
		protected RuleTestType<?> getType() {
			return TYPE;
		}

		public static final RuleTestType<CodecHacks> TYPE = RuleTestType.register("codec_hacks", CODEC);
	}
}
