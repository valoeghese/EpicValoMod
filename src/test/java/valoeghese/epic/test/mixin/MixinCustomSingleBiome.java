package valoeghese.epic.test.mixin;

import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.gui.screens.worldselection.WorldPreset;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import valoeghese.epic.gen.EpicFantasyChunkGenerator;

@Mixin(WorldPreset.class)
public abstract class MixinCustomSingleBiome {
	@Shadow
	@Final
	private static WorldPreset SINGLE_BIOME_SURFACE;

	@Redirect(
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/screens/worldselection/WorldPreset;generator(J)Lnet/minecraft/world/level/chunk/ChunkGenerator;"
					),
			method = "create(Lnet/minecraft/core/RegistryAccess$RegistryHolder;JZZ)Lnet/minecraft/world/level/levelgen/WorldGenSettings;"
			)
	private ChunkGenerator epic_putCustomGenerator(WorldPreset caller, long seed) {
		if (caller == SINGLE_BIOME_SURFACE) {
			return new EpicFantasyChunkGenerator(new FixedBiomeSource(Biomes.PLAINS), seed, () -> {
				return NoiseGeneratorSettings.OVERWORLD;
			});
		} else {
			return ((AccessorWorldPreset) caller).epic_execGenerator(seed);
		}
	}

	@Redirect(
			at = @At(
					value = "NEW",
					target = "Lnet/minecraft/world/level/levelgen/NoiseBasedChunkGenerator;"
					),
			method = "fromBuffetSettings(Lnet/minecraft/world/level/levelgen/WorldGenSettings;Lnet/minecraft/client/gui/screens/worldselection/WorldPreset;Lnet/minecraft/world/level/biome/Biome;)Lnet/minecraft/world/level/levelgen/WorldGenSettings;"
			)
	private static NoiseBasedChunkGenerator epic_putCustomGenerator2(BiomeSource biomeSource, long l, Supplier<NoiseGeneratorSettings> supplier,/**/ WorldGenSettings sparam, WorldPreset presetparam, Biome biomeparam) {
		if (presetparam == SINGLE_BIOME_SURFACE) {
			return new EpicFantasyChunkGenerator(biomeSource, l, supplier);
		} else {
			return new NoiseBasedChunkGenerator(biomeSource, l, supplier);
		}
	}
}
