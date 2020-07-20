package valoeghese.epic.abstraction.world;

import java.util.function.BiFunction;

import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import valoeghese.epic.abstraction.core.Game;

public abstract class Fluid {
	protected abstract boolean allowInfiniteSource();
	protected abstract boolean destructive();
	protected abstract boolean lavaLike();
	public abstract String getName();
	public abstract int getColour();

	int getSlowness(LevelReader level) {
		return this.lavaLike() ? (level.dimensionType().ultraWarm() ? 10 : 30) : 5;
	}

	public Item bucket;
	public Tag<net.minecraft.world.level.material.Fluid> tag;

	public static abstract class Impl extends FlowingFluid {
		public Impl(Fluid fluid) {
			this.fluid = fluid;
			this.tag = fluid.tag;
		}

		public final Fluid fluid;
		private final Tag<net.minecraft.world.level.material.Fluid> tag;

		private Item bucket;
		private Impl flowingFluid, sourceFluid;
		private Block source;

		@Override
		public net.minecraft.world.level.material.Fluid getFlowing() {
			return this.flowingFluid;
		}

		@Override
		public net.minecraft.world.level.material.Fluid getSource() {
			return this.sourceFluid;
		}

		@Override
		protected boolean canConvertToSource() {
			return this.fluid.allowInfiniteSource();
		}

		@Override
		protected void beforeDestroyingBlock(LevelAccessor level, BlockPos pos, BlockState state) {
			if (this.fluid.destructive()) {
				level.levelEvent(1501, pos, 0);
			} else {
				BlockEntity blockEntity = state.getBlock().isEntityBlock() ? level.getBlockEntity(pos) : null;
				Block.dropResources(state, level, pos, blockEntity);
			}
		}

		@Override
		protected int getSlopeFindDistance(LevelReader levelReader) {
			return this.fluid.lavaLike() ? 2 : 4;
		}

		@Override
		protected int getDropOff(LevelReader levelReader) {
			return this.fluid.lavaLike() ? 2 : 1;
		}

		@Override
		public Item getBucket() {
			return bucket;
		}

		@Override
		protected boolean canBeReplacedWith(FluidState fluidState, BlockGetter blockGetter, BlockPos blockPos,
				net.minecraft.world.level.material.Fluid fluid, Direction direction) {
			return direction == Direction.DOWN && !fluid.is(tag);
		}

		@Override
		public int getTickDelay(LevelReader level) {
			return this.fluid.getSlowness(level);
		}

		@Override
		protected float getExplosionResistance() {
			return 100.0F;
		}

		@Override
		protected BlockState createLegacyBlock(FluidState fluidState) {
			return this.source.defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(fluidState));
		}

		private static Item createBucket(Impl source) {
			return Game.addItem(source.fluid.getName() + "_bucket", new BucketItem(source, (new Item.Properties()).craftRemainder(Items.BUCKET).stacksTo(1).tab(CreativeModeTab.TAB_MISC)));
		}

		public static Tuple<Impl, Impl> compileFluid(Fluid fluid) {
			fluid.tag = TagRegistry.fluid(new ResourceLocation(Game.primedId, fluid.getName()));
			Impl source = new ImplSource(fluid);
			Impl flowing = new ImplFlowign(fluid);

			source.flowingFluid = flowing;
			flowing.flowingFluid = flowing;
			source.sourceFluid = source;
			flowing.sourceFluid = source;

			Item bucket = createBucket(source);
			source.bucket = bucket;
			flowing.bucket = bucket;

			return new Tuple<>(source, flowing);
		}

		public static void postRegister(Fluid fluid, Impl source, Impl flowing, BiFunction<Impl, Block.Properties, Block> blockConstructor) {
			Block block = Game.addRawBlock(fluid.getName(), blockConstructor.apply(source, BlockBehaviour.Properties.of(fluid.lavaLike() ? Material.LAVA : Material.WATER).noCollission().strength(100.0F).noDrops()));
			source.source = block;
			flowing.source = block;
		}
	}

	public static class ImplSource extends Impl {
		public ImplSource(Fluid fluid) {
			super(fluid);
		}

		@Override
		public int getAmount(FluidState state) {
			return 8;
		}

		@Override
		public boolean isSource(FluidState fluidState) {
			return true;
		}
	}

	public static class ImplFlowign extends Impl {
		public ImplFlowign(Fluid fluid) {
			super(fluid);
		}

		@Override
		public int getAmount(FluidState state) {
			return state.getValue(LEVEL);
		}

		@Override
		protected void createFluidStateDefinition(Builder<net.minecraft.world.level.material.Fluid, FluidState> builder) {
			super.createFluidStateDefinition(builder);
			builder.add(LEVEL);
		}

		@Override
		public boolean isSource(FluidState fluidState) {
			return false;
		}
	}
	public static class SimpleLiquidBlock extends LiquidBlock {
		SimpleLiquidBlock(FlowingFluid flowingFluid, BlockBehaviour.Properties properties) {
			super(flowingFluid, properties);
		}
	}
}
