package valoeghese.epic.util;

import java.util.Random;

/**
 * 2D OpenSimplexNoise
 */
public class OpenSimplexNoise extends RawOpenSimplexNoise {
	public OpenSimplexNoise(Random rand) {
		super(rand.nextLong());

		this.xOffset = rand.nextDouble();
		this.yOffset = rand.nextDouble();
		this.zOffset = rand.nextDouble();
	}

	public final double xOffset, yOffset, zOffset;

	public double sample(double x) {
		return super.sample(x + this.xOffset, 0.0);
	}

	@Override
	public double sample(double x, double y) {
		return super.sample(x + this.xOffset, y + this.yOffset);
	}

	@Override
	public double sample(double x, double y, double z) {
		return super.sample(x + this.xOffset, y + this.yOffset, z + this.zOffset);
	}

	public double rawSample(double x, double y, double z) {
		return super.sample(x, y, z);
	}
}
