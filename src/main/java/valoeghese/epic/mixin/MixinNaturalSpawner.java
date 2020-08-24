package valoeghese.epic.mixin;

import java.util.concurrent.atomic.AtomicReference;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.ServerLevelAccessor;
import valoeghese.epic.abstraction.event.EntitySpawnCallback;

@Mixin(NaturalSpawner.class)
public class MixinNaturalSpawner {
	@Redirect(
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/ServerLevelAccessor;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"),
			method = "spawnMobsForChunkGeneration(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/world/level/biome/Biome;IILjava/util/Random;)V"
			)
	private static boolean epic_entitySpawnEventChunk(ServerLevelAccessor self, Entity entity) {
		AtomicReference<Entity> currentEntity = new AtomicReference<>(entity);
		InteractionResult result = EntitySpawnCallback.PRE.invoker().onEntitySpawnPre(entity, currentEntity, self, true);
		entity = currentEntity.get();

		if (result == InteractionResult.SUCCESS) {
			if (self.addFreshEntity(entity)) {
				EntitySpawnCallback.POST.invoker().onEntitySpawnPost(entity, self, entity.position(), true);
				return true;
			}
		}

		return false;
	}

	@Redirect(
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/ServerLevel;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"),
			method = "spawnCategoryForPosition(Lnet/minecraft/world/entity/MobCategory;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/NaturalSpawner$SpawnPredicate;Lnet/minecraft/world/level/NaturalSpawner$AfterSpawnCallback;)V"
			)
	private static boolean epic_entitySpawnEvent(ServerLevel self, Entity entity) {
		AtomicReference<Entity> currentEntity = new AtomicReference<>(entity);
		InteractionResult result = EntitySpawnCallback.PRE.invoker().onEntitySpawnPre(entity, currentEntity, self, true);
		entity = currentEntity.get();

		if (result == InteractionResult.SUCCESS) {
			if (self.addFreshEntity(entity)) {
				EntitySpawnCallback.POST.invoker().onEntitySpawnPost(entity, self, entity.position(), true);
				return true;
			}
		}

		return false;
	}
}
