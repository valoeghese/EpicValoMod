package valoeghese.epic.abstraction;

import org.apache.logging.log4j.LogManager;

public class Logger {
	private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger("Valoeghese's Epic Fantasy");

	public static void info(String category, String msg) {
		LOGGER.info("[Epic:" + category + "] " + msg);
	}

	public static void debug(String category, String msg) {
		LOGGER.debug("[Epic:" + category + "] " + msg);
	}

	public static void warn(String category, String msg) {
		LOGGER.warn("[Epic:" + category + "] " + msg);
	}

	public static void error(String category, String msg) {
		LOGGER.error("[Epic:" + category + "] " + msg);
	}
}
