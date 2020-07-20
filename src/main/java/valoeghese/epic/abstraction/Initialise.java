package valoeghese.epic.abstraction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;
import valoeghese.epic.Setup;
import valoeghese.epic.abstraction.core.Game;
import valoeghese.epic.abstraction.world.FluidResourceLoader;

public class Initialise implements ModInitializer, ClientModInitializer {
	@Override
	public void onInitialize() {
		setupMod(Setup.class);
	}

	public void setupMod(Class<?> mod) {
		// define a public static string called "MODID".
		try {
			Game.primedId = (String) mod.getField("MODID").get(null);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e1) {
			throw new RuntimeException(e1);
		}

		for (Method m : mod.getMethods()) {
			if (Modifier.isStatic(m.getModifiers()) && m.getParameterTypes().length == 0 && m.getReturnType().equals(Void.TYPE)) {
				try {
					m.invoke(null);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new RuntimeException("Error invoking init method: " + m.getName(), e);
				}
			}
		}
	}

	@Override
	public void onInitializeClient() {
		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new FluidResourceLoader());
	}
}
