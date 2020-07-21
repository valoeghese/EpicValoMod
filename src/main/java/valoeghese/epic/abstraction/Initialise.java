package valoeghese.epic.abstraction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;
import valoeghese.epic.Setup;
import valoeghese.epic.abstraction.core.Game;
import valoeghese.epic.abstraction.world.FluidResourceLoader;

public class Initialise implements ModInitializer, ClientModInitializer {
	private static Int2ObjectMap<List<Method>> methods = new Int2ObjectArrayMap<>();

	@Override
	public void onInitialize() {
		setupMod(Setup.class);
	}

	public static void setupMod(Class<?> mod) {
		// define a public static string called "MODID".
		try {
			Game.primedId = (String) mod.getField("MODID").get(null);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e1) {
			throw new RuntimeException(e1);
		}

		for (Method m : mod.getMethods()) {
			if (Modifier.isStatic(m.getModifiers()) && m.getParameterTypes().length == 0 && m.getReturnType().equals(Void.TYPE)) {
				int priority = m.isAnnotationPresent(Priority.class) ? m.getAnnotation(Priority.class).value() : 0;
				methods.computeIfAbsent(priority, p -> new ArrayList<>()).add(m);
			}
		}
	}

	public static void runMods() {
		Logger.info("Abstraction", "Running mods.");

		IntSet keys = methods.keySet();
		int[] ints = keys.toIntArray();
		Arrays.sort(ints);

		for (int i = ints.length - 1; i >= 0; --i) {
			List<Method> mList = methods.get(ints[i]);

			for (Method m : mList) {
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
