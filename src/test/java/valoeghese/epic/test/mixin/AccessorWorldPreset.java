package valoeghese.epic.test.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.gui.screens.worldselection.WorldPreset;
import net.minecraft.world.level.chunk.ChunkGenerator;

@Mixin(WorldPreset.class)
public interface AccessorWorldPreset {
	@Invoker("generator")
	ChunkGenerator epic_execGenerator(long l);
}
