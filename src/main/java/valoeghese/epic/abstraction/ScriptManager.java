package valoeghese.epic.abstraction;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import net.fabricmc.loader.api.FabricLoader;

public abstract class ScriptManager {
	private static final ScriptEngineManager ENGINE_MANAGER = new ScriptEngineManager();
	private static final Supplier<ScriptEngine> ENGINE_SOURCE = () -> ENGINE_MANAGER.getEngineByName("nashorn");
	@SuppressWarnings("deprecation")
	private static final File LOC = new File(FabricLoader.getInstance().getConfigDirectory(), "../scripts");

	protected static File getScript(String name) {
		if (!name.endsWith(".js")) {
			name += ".js";
		}

		return new File(LOC, name);
	}

	protected static void createIfNotExists(File file, Consumer<PrintWriter> writer) throws IOException {
		if (file.createNewFile()) {
			try (PrintWriter pr = new PrintWriter(file)) {
				writer.accept(pr);
			}
		}
	}

	protected static File[] getScripts(String folder) {
		File folderLoc = new File(LOC, folder);
		List<File> scripts = new ArrayList<>();

		for (File file : folderLoc.listFiles()) {
			if (file.getName().endsWith(".js")) {
				scripts.add(file);
			}
		}

		return scripts.toArray(new File[0]);
	}

	protected static class ScriptContext {
		public ScriptContext() {
		}

		final Set<String> definitions = new HashSet<>();
		final StringBuilder classDefinitions = new StringBuilder();
		final Map<String, Object> objectDefinitions = new HashMap<>();
		final Map<String, Triple<String, String, Integer>> methodDefinitions = new HashMap<>();

		private void checkDef(String def) {
			if (!definitions.add(def)) {
				throw new RuntimeException("Definition " + def + " already exists in this ScriptContext!");
			}
		}

		public void addClassDefinition(String def, Class<?> clazz) {
			checkDef(def);
			this.classDefinitions.append("var " + def + " = Java.type(\"" + clazz.getName() + "\");\n");
		}

		public void addFunctionDefinition(String def, Class<?> clazz, String methodName, int parameterCount) {
			if (parameterCount > 26) {
				throw new RuntimeException("Too many parameters! " + parameterCount);
			}

			checkDef(def);
			String classDef = "generated_" + def.hashCode() + def.substring(0, 3);
			this.addClassDefinition(classDef, clazz);
			this.methodDefinitions.put(def, new ImmutableTriple<>(classDef, methodName, parameterCount));
		}

		public void addObjectDefinition(String def, Object object) {
			checkDef(def);
			this.objectDefinitions.put(def, object);
		}

		public Invocable runScript(File file) throws ScriptException, IOException {
			ScriptEngine engine = ENGINE_SOURCE.get();
			// add objects
			engine.getBindings(javax.script.ScriptContext.ENGINE_SCOPE).putAll(this.objectDefinitions);
			// add classes
			engine.eval(this.classDefinitions.toString());

			// add methods
			for (Map.Entry<String, Triple<String, String, Integer>> function : this.methodDefinitions.entrySet()) {
				StringBuilder params = new StringBuilder();
				final int max = 97 + function.getValue().getRight();

				for (int i = 97; i < max; ++i) {
					params.append((char) i);

					if (i < max - 1) {
						params.append(',');
					}
				}

				String paramString = params.toString();

				engine.eval(
						new StringBuilder("function ").append(function.getKey()).append('(').append(paramString).append("){")
						.append(function.getValue().getLeft()).append('.')
						.append(function.getValue().getMiddle()).append('(').append(paramString).append(");")
						.append("}\n")
						.toString());
			}

			// eval
			try (FileReader reader = new FileReader(file)) {
				engine.eval(reader);
			}

			return (Invocable) engine;
		}
	}

	static {
		LOC.mkdirs();
	}
}
