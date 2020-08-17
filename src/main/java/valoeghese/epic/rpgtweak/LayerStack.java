package valoeghese.epic.rpgtweak;

import java.util.function.LongFunction;

import net.minecraft.data.models.blockstates.PropertyDispatch.TriFunction;
import net.minecraft.world.level.newbiome.area.AreaFactory;
import net.minecraft.world.level.newbiome.area.LazyArea;
import net.minecraft.world.level.newbiome.context.LazyAreaContext;
import net.minecraft.world.level.newbiome.layer.ZoomLayer;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer0;

public final class LayerStack {
	private LayerStack() {
	}

	public static AreaFactory<LazyArea> manufactureLayer(long seed, AreaTransformer0 start, TriFunction<AreaFactory<LazyArea>, Integer, LongFunction<LazyAreaContext>, AreaFactory<LazyArea>> operator) {
		LongFunction<LazyAreaContext> randomProvider = (salt) -> {
			return new LazyAreaContext(25, seed, salt);
		};

		AreaFactory<LazyArea> stack = start.run(randomProvider.apply(1L));

		for (int i = 0; i < SCALE; ++i) {
			if (i == 0) {
				stack = DirectScaleLayer.INSTANCE.run(randomProvider.apply(1000L), stack);
			} else {
				stack = ZoomLayer.NORMAL.run(randomProvider.apply(1000L + i), stack);
			}

			stack = operator.apply(stack, i, randomProvider);
		}

		return stack;
	}

	public static final int SCALE = 8;
}
