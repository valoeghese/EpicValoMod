package valoeghese.epic;

import valoeghese.epic.abstraction.Logger;
import valoeghese.epic.metallurgy.Metallurgy;

public class Setup {
	public static final String MODID = "epic_fantasy";

	public static void setupMetallurgy() {
		Logger.info("Metallurgy", "Setting Up!");
		Metallurgy.addMetals();
		Metallurgy.addBiomes();
		Metallurgy.addOreGen();
	}
}
