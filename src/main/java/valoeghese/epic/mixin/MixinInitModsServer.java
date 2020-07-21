package valoeghese.epic.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import net.minecraft.server.dedicated.DedicatedServer;
import valoeghese.epic.abstraction.Initialise;

@Mixin(DedicatedServer.class)
public class MixinInitModsServer {
	@Inject(at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;)V", ordinal = 0), method = "initServer")
	private void loadModsServer() {
		Initialise.runMods();
	}
}
