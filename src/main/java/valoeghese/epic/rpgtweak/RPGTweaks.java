package valoeghese.epic.rpgtweak;

import java.util.function.BinaryOperator;

import net.minecraft.world.level.newbiome.area.LazyArea;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer0;

public enum RPGTweaks implements AreaTransformer0 {
	INSTANCE;

	@Override
	public int applyPixel(Context context, int i, int j) {
		return i + j;
	}

	public static void setupWorldLoad(long seed) {
		ds = LayerStack.manufactureLayer(seed, INSTANCE, (a, b, c) -> a).make();
	}

	private static LazyArea ds;

	public static BinaryOperator<Integer> difficultySupplier = (x, z) -> {
		return ds.get(x >> 2, z >> 2);
	};
}
