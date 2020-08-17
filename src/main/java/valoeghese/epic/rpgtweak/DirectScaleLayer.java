package valoeghese.epic.rpgtweak;

import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.context.BigContext;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer1;

public enum DirectScaleLayer implements AreaTransformer1 {
	INSTANCE;

	@Override
	public int getParentX(int i) {
		return i >> 1;
	}

	@Override
	public int getParentY(int i) {
		return i >> 1;
	}

	@Override
	public int applyPixel(BigContext<?> bigContext, Area area, int x, int z) {
		return area.get(this.getParentX(x), this.getParentY(z));
	}
}
