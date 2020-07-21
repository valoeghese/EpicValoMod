package valoeghese.epic.mixin;

import java.util.Arrays;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.newbiome.area.LazyArea;
import net.minecraft.world.level.newbiome.layer.traits.PixelTransformer;

/**
 * Shortens the number of possible hash values via using an `original & mask` algorithm, and uses an array lookup.
 * Original concept by Gegy, rewritten by me without looking at the original code, since the licence was incompatible.
 */
@Mixin(LazyArea.class)
public class MixinLazyArea {
	@Inject(at = @At("RETURN"), method = "<init>")
	private void onInit(Long2IntLinkedOpenHashMap map, int size, PixelTransformer transformer, CallbackInfo info) {
		int arrSize = 1; // 2^n = 2 * (2^(n-1))
		int nextArrSize;

		while (true) {
			if ((nextArrSize = (arrSize << 1)) > size) {
				break;
			}

			arrSize = nextArrSize;
		}

		if (arrSize > size) {
			throw new RuntimeException("Fast Array Size " + arrSize + " must be smaller or equal to LazyArea size! (" + size + ")");
		}

		this.epic_mask = arrSize - 1;
		this.epic_positions = new long[arrSize];
		this.epic_biomes = new int[arrSize];

		Arrays.fill(this.epic_positions, Long.MAX_VALUE);
	}

	@Shadow
	@Final
	private PixelTransformer transformer;

	private int epic_mask;
	private long[] epic_positions;
	private int[] epic_biomes;

	@Overwrite
	public int get(int x, int y) {
		try {
			long pos = ChunkPos.asLong(x, y);
			int loc = epic_mix5(x, y) & this.epic_mask;

			if (this.epic_positions[loc] != pos) {
				this.epic_positions[loc] = pos;
				return this.epic_biomes[loc] = this.transformer.apply(x, y);
			} else {
				return this.epic_biomes[loc];
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("FastLazyArea broke! You'll need to restart your game, sadly. If this issue persists, let me (Valoeghese) know!");
			throw new RuntimeException(e);
		}
	}

	private static int epic_mix5(int a, int b) {
		return (((a >> 4) & 1) << 9) |
				(((b >> 4) & 1) << 8) |
				(((a >> 3) & 1) << 7) |
				(((b >> 3) & 1) << 6) |
				(((a >> 2) & 1) << 5) |
				(((b >> 2) & 1) << 4) |
				(((a >> 1) & 1) << 3) |
				(((b >> 1) & 1) << 2) |
				((a & 1) << 1) |
				(b & 1);
	}
}
