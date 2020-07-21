package valoeghese.epic;

import valoeghese.epic.abstraction.Client;
import valoeghese.epic.abstraction.Logger;
import valoeghese.epic.abstraction.Priority;
import valoeghese.epic.gen.Gen;
import valoeghese.epic.gen.GenClient;
import valoeghese.epic.metallurgy.Metallurgy;

public class Setup {
	public static final String MODID = "epic_fantasy";

	@Priority(2)
	public static void setupMetallurgy() {
		Logger.info("Metallurgy", "Setting Up!");
		Metallurgy.addMetals();
		Metallurgy.addBiomes();
		Metallurgy.addOreGen();
	}

	public static void setupGen() {
		Logger.info("Gen", "Setting Up!");
		Gen.loadGenScripts();
	}

	@Priority(-1)
	@Client
	public static void setupWorldPreset() {
		GenClient.addPreset();
	}
}
