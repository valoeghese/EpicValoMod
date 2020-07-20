package valoeghese.epic.metallurgy;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import valoeghese.epic.abstraction.world.Fluid;

public class Mercury extends Fluid {
	@Override
	protected boolean allowInfiniteSource() {
		return false;
	}

	@Override
	protected boolean destructive() {
		return false;
	}

	@Override
	protected boolean lavaLike() {
		return false;
	}

	@Override
	public String getName() {
		return "mercury";
	}

	@Override
	public int getColour() {
		return 0xa9a9a9;
	}

	public static class Block extends LiquidBlock {
		public Block(FlowingFluid flowingFluid, Properties properties) {
			super(flowingFluid, properties);
		}

		@Override
		public void entityInside(BlockState blockState, Level level, BlockPos pos, Entity entity) {
			if (entity instanceof LivingEntity) {
				((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.WITHER, 30, 1));
			}
		}
	}
}
