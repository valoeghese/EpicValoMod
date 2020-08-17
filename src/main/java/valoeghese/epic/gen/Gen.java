package valoeghese.epic.gen;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import valoeghese.epic.Setup;
import valoeghese.epic.gen.deobf.BiomeGenProperties;

public class Gen {
	public static void loadGenScripts() {
		new BiomeGenProperties();
		Registry.register(Registry.CHUNK_GENERATOR, new ResourceLocation(Setup.MODID, "epic_fantasy"), EpicFantasyChunkGenerator.CODEC);

		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			dispatcher.register(Commands.literal("reloadscripts")
					.requires(stack -> stack.hasPermission(2))
					.executes(context -> {
						try {
							new BiomeGenProperties();
							BiomeGenProperties.setupForChunkGen();
						} catch (Exception e) {
							e.printStackTrace();
							throw e;
						}
						return 1;
					}));
		}
				);
	}
}
