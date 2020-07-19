package valoeghese.epic.abstraction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import net.fabricmc.api.ModInitializer;
import valoeghese.epic.Setup;

public class Initialise implements ModInitializer {
	@Override
	public void onInitialize() {
		for (Method m : Setup.class.getMethods()) {
			if (Modifier.isStatic(m.getModifiers()) && m.getParameterTypes().length == 0 && m.getReturnType().equals(Void.TYPE)) {
				try {
					m.invoke(null);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new RuntimeException("Error invoking init method: " + m.getName(), e);
				}
			}
		}
	}
}
