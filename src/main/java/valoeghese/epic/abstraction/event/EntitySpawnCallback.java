package valoeghese.epic.abstraction.event;

import java.util.concurrent.atomic.AtomicReference;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;

/**
 * Collection of events for entity spawning.
 */
public final class EntitySpawnCallback {
	/**
	 * Callback for before the entity spawns. Use this to cancel, force succeed, or alter the entity spawning.
	 */
	public static final Event<EntitySpawnCallback.Pre> PRE = EventFactory.createArrayBacked(EntitySpawnCallback.Pre.class, listeners -> (original, entity, level, natural) -> {
		for (EntitySpawnCallback.Pre callback : listeners) {
			InteractionResult result = callback.onEntitySpawnPre(original, entity, level, natural);

			if (result == InteractionResult.CONSUME) {
				return InteractionResult.SUCCESS;
			} else if (result != InteractionResult.PASS) {
				return result;
			}
		}

		return InteractionResult.SUCCESS;
	});

	/**
	 * Callback for after the entity succeeds in spawning. Use this for general functions after an entity has spawned.
	 */
	public static final Event<EntitySpawnCallback.Post> POST = EventFactory.createArrayBacked(EntitySpawnCallback.Post.class, listeners -> (entity, level, pos, natural) -> {
		for (EntitySpawnCallback.Post callback : listeners) {
			callback.onEntitySpawnPost(entity, level, pos, natural);
		}
	});

	/**
	 * Callback for before the entity spawns. Use this to cancel, force succeed, or alter the entity spawning.
	 * @author Valoeghese
	 */
	@FunctionalInterface
	public interface Pre {
		/**
		 * @param original the entity that was originally going to spawn.
		 * @param entity the entity that is going to spawn. If this is different to {@code original}, then a mod has modified the entity to spawn.
		 * @param level the level in which the entity is to spawn.
		 * @param natural if this is natural spawning. If this is false it implies that the spawning is caused by some event, such as a monster spawner or spawn egg.
		 * @return <ul>
		 * <li>{@code SUCCESS} or {@code CONSUME} to instantly succeed in spawning the entity in the level at its specified position.<br/>
		 * <li>{@code PASS} to leave SUCCESS/FAIL handling to subsequent events. If all events PASS, the action is determined to be a SUCCESS.<br/>
		 * <li>{@code FAIL} cancel spawning the mob.
		 * </ul>
		 */
		InteractionResult onEntitySpawnPre(Entity original, AtomicReference<Entity> entity, LevelAccessor level, boolean natural);
	}

	/**
	 * Callback for after the entity succeeds in spawning. Use this for general functions after an entity has spawned.
	 * @author Valoeghese
	 */
	@FunctionalInterface
	public interface Post {
		/**
		 * @param entity the entity that has spawned.
		 * @param level the level in which the entity spawned.
		 * @param pos the position at which the entity spawned.
		 * @param natural if this was a natural spawn. If this is false it implies that the spawning is caused by some event, such as a monster spawner or spawn egg.
		 */
		void onEntitySpawnPost(Entity entity, LevelAccessor level, Vec3 pos, boolean natural);
	}
}
