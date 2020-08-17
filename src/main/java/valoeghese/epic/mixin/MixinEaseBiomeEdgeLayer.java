package valoeghese.epic.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.data.BuiltinRegistries;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.BiomeEdgeLayer;

@Mixin(BiomeEdgeLayer.class)
public class MixinEaseBiomeEdgeLayer {
	// TODO is it faster to collect ints then compare those once in the if statement?
	@Inject(at = @At("HEAD"), method = "apply", cancellable = true)
	private void onApply(Context context, int b0, int b1, int b2, int b3, int centre, CallbackInfoReturnable<Integer> info) {
		Biome b = BuiltinRegistries.BIOME.byId(centre);

		if (!isMountain(b)) {
			Biome c = BuiltinRegistries.BIOME.byId(b0);
			Biome d = BuiltinRegistries.BIOME.byId(b1);
			Biome e = BuiltinRegistries.BIOME.byId(b2);
			Biome f = BuiltinRegistries.BIOME.byId(b3);

			if (isMountain(c) || isMountain(d) || isMountain(e) || isMountain(f)) {
				info.setReturnValue(BuiltinRegistries.BIOME.getId(Biomes.MOUNTAIN_EDGE));
			}
		}
	}

	private static boolean isMountain(Biome b) {
		return b == Biomes.MOUNTAINS || b == Biomes.GRAVELLY_MOUNTAINS || b == Biomes.WOODED_MOUNTAINS || b == Biomes.MODIFIED_GRAVELLY_MOUNTAINS;
	}
}
