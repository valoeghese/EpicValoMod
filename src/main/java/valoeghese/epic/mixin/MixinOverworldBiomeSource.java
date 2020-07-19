package valoeghese.epic.mixin;

import java.util.concurrent.atomic.AtomicReference;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.OverworldBiomeSource;
import valoeghese.epic.abstraction.event.BiomePlacementCallback;

@Mixin(OverworldBiomeSource.class)
public class MixinOverworldBiomeSource {
	@Inject(at = @At("RETURN"), method = "getNoiseBiome", cancellable = true)
	private void injectBiomePlacementEventOverworld(int genX, int useless, int genZ, CallbackInfoReturnable<Biome> arr) {
		AtomicReference<Biome> funni = new AtomicReference<Biome>(arr.getReturnValue());
		boolean b = BiomePlacementCallback.OVERWORLD.invoker().onBiomePlace(funni, genX, genZ);

		if (b) {
			arr.setReturnValue(funni.get());
		}
	}
}
