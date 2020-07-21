package valoeghese.epic.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.VirtualScreen;
import valoeghese.epic.abstraction.Initialise;

@Mixin(remap = false, value = VirtualScreen.class)
public class MixinInitModsClient {
	@Inject(at = @At("RETURN"), method = "<init>", cancellable = true)
	private void loadModsClient(Minecraft minecraft, CallbackInfo info) {
		Initialise.runMods();
	}
}
