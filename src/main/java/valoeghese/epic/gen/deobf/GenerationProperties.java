package valoeghese.epic.gen.deobf;

import static valoeghese.epic.gen.deobf.BiomeGenProperties.FORMAT;

public class GenerationProperties {
	private GenerationProperties(float depth, float projection, float projectionFrequency, float scale, int interpolation, float hilliness, float period) {
		this.depth = depth;
		this.projection = projection;
		this.projectionFrequency = projectionFrequency;
		this.scale = scale;
		this.interpolation = interpolation;
		this.hilliness = hilliness;
		this.period = period;
	}

	public final float depth;
	public final float projection;
	public final float projectionFrequency;
	public final float scale;
	public final int interpolation;
	public final float hilliness;
	public final float period;

	public static class Builder {
		private float depth = 0.0f;
		private float projection = 0.0f;
		private float projectionFrequency = 0.0f;
		private float scale = 0.0f;
		private int interpolation = 4; // VANILLA DEFAULT = 2
		private float hilliness = 1.0f;
		private float period = 1.0f;

		public Builder depthScale(float depth, float scale) {
			this.depth = depth;
			this.scale = scale;
			return this;
		}

		public Builder projection(float projection, float projectionFrequency) {
			this.projection = projection;
			this.projectionFrequency = projectionFrequency;
			return this;
		}

		public Builder interpolation(int interpolation) {
			this.interpolation = interpolation;
			return this;
		}

		public Builder hillinessFactor(float hilliness) {
			this.hilliness = hilliness;
			return this;
		}

		public Builder periodFactor(float period) {
			this.period = period;
			return this;
		}

		public GenerationProperties build() {
			return new GenerationProperties(
					this.depth,
					this.projection,
					this.projectionFrequency,
					this.scale,
					this.interpolation,
					this.hilliness,
					this.period);
		}

		public String toJS(String indent) {
			StringBuilder result = new StringBuilder("new GenerationProperties()\n")
					.append(indent).append("    .depthScale(").append(FORMAT.format(this.depth)).append(", ").append(FORMAT.format(this.scale)).append(")");

			if (this.projection != 0.0f) {
				result.append("\n").append(indent).append("    .projection(").append(FORMAT.format(this.projection)).append(", ").append(FORMAT.format(this.projectionFrequency)).append(")");
			}

			if (this.interpolation != 2) {
				result.append("\n").append(indent).append("    .interpolation(").append(this.interpolation).append(")");
			}

			if (this.hilliness != 1.0f) {
				result.append("\n").append(indent).append("    .hillinessFactor(").append(FORMAT.format(this.hilliness)).append(")");
			}

			if (this.period != 1.0f) {
				result.append("\n").append(indent).append("    .periodFactor(").append(FORMAT.format(this.period)).append(")");
			}

			return result.toString();
		}
	}
}