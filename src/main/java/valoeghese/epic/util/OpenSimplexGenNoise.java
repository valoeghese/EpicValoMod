package valoeghese.epic.util;

import java.util.Random;

import net.minecraft.util.Mth;

public class OpenSimplexGenNoise extends OpenSimplexNoise {
	public OpenSimplexGenNoise(Random rand) {
		super(rand);
	}

	public double noise(double x, double y, double z, double yOffsetModifier, double minYOffset) {
		double offsetY = y + this.yOffset;
		int floorY = Mth.floor(offsetY);
		double yProgress = offsetY - floorY;
		double w;

		if (yOffsetModifier != 0.0D) {
			double u = Math.min(minYOffset, yProgress);
			w = (double) Mth.floor(u / yOffsetModifier) * yOffsetModifier;
		} else {
			w = 0.0D;
		}

		return this.rawSample(x + this.xOffset, floorY + (yProgress - w), z + this.zOffset);
	}
}
