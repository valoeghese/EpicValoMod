package valoeghese.epic.abstraction.world;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.AABB;

public final class World {
	public World(WorldGenLevel parent) {
		this.parent = parent;
	}

	private final WorldGenLevel parent;

	public WorldGenLevel getParent() {
		return this.parent;
	}

	public LevelData getWorldProperties() {
		return this.parent.getLevelData();
	}

	public DifficultyInstance getCurrentDifficultyAt(BlockPos pos) {
		return this.parent.getCurrentDifficultyAt(pos);
	}

	public ChunkSource getChunkManager() {
		return this.parent.getChunkSource();
	}

	public Random getRandom() {
		return this.parent.getRandom();
	}

	public void playSound(Player player, BlockPos blockPos, SoundEvent soundEvent, SoundSource soundSource, float f, float g) {
		this.parent.playSound(player, blockPos, soundEvent, soundSource, f, g);
	}

	public void addParticle(ParticleOptions particleOptions, double d, double e, double f, double g, double h, double i) {
		this.parent.addParticle(particleOptions, d, e, f, g, h, i);
	}

	public List<Entity> getEntities(Entity entity, AABB aABB, Predicate<? super Entity> predicate) {
		return this.parent.getEntities(entity, aABB, predicate);
	}

	public List<Entity> getEntities(Entity entity, AABB aABB) {
		return this.parent.getEntities(entity, aABB, EntitySelector.NO_SPECTATORS);
	}

	public <T extends Entity> List<T> getEntitiesOfClass(Class<? extends T> class_, AABB aABB, Predicate<? super T> predicate) {
		return this.parent.getEntitiesOfClass(class_, aABB, predicate);
	}

	public <T extends Entity> List<T> getEntitiesOfClass(Class<? extends T> class_, AABB aABB) {
		return this.parent.getEntitiesOfClass(class_, aABB, EntitySelector.NO_SPECTATORS);
	}

	public List<? extends Player> getPlayers() {
		return this.parent.players();
	}

	public ChunkAccess getChunk(int x, int z, ChunkStatus chunkStatus, boolean bl) {
		return this.parent.getChunk(x, z, chunkStatus, bl);
	}

	public ChunkAccess getChunk(int x, int z, ChunkStatus chunkStatus) {
		return this.parent.getChunk(x, z, chunkStatus, true);
	}

	public ChunkAccess getChunk(int x, int z) {
		return this.parent.getChunk(x, z, ChunkStatus.FULL, true);
	}

	public int getHeight(Types types, int x, int z) {
		return this.parent.getHeight(types, x, z);
	}

	public int getHeight() {
		return this.parent.getHeight();
	}

	public int getSkyDarken() {
		return this.parent.getSkyDarken();
	}

	public BiomeManager getBiomeManager() {
		return this.parent.getBiomeManager();
	}

	public Biome getUncachedNoiseBiome(int x, int y, int z) {
		return this.parent.getUncachedNoiseBiome(x, y, z);
	}

	public boolean isClient() {
		return this.parent.isClientSide();
	}

	public int getSeaLevel() {
		return this.parent.getSeaLevel();
	}

	public DimensionType getDimensionType() {
		return this.parent.dimensionType();
	}

	public float getShade(Direction direction, boolean bl) {
		return this.parent.getShade(direction, bl);
	}

	public BlockEntity getBlockEntity(BlockPos blockPos) {
		return this.parent.getBlockEntity(blockPos);
	}

	public BlockState getBlockState(BlockPos blockPos) {
		return this.parent.getBlockState(blockPos);
	}

	public FluidState getFluidState(BlockPos blockPos) {
		return this.parent.getFluidState(blockPos);
	}

	public WorldBorder getWorldBorder() {
		return this.parent.getWorldBorder();
	}

	public boolean testBlockState(BlockPos blockPos, Predicate<BlockState> predicate) {
		return this.parent.isStateAtPosition(blockPos, predicate);
	}

	public boolean setBlockState(BlockPos blockPos, BlockState blockState, int i, int j) {
		return this.parent.setBlock(blockPos, blockState, i, j);
	}

	public boolean setBlockState(BlockPos blockPos, BlockState blockState, int i) {
		return this.parent.setBlock(blockPos, blockState, i, 512);
	}

	public boolean removeBlockState(BlockPos blockPos, boolean bl) {
		return this.parent.removeBlock(blockPos, bl);
	}

	public boolean destroyBlockState(BlockPos blockPos, boolean bl, Entity entity, int i) {
		return this.parent.destroyBlock(blockPos, bl, entity, i);
	}

	public boolean destroyBlockState(BlockPos blockPos, boolean bl, Entity entity) {
		return this.parent.destroyBlock(blockPos, bl, entity, 512);
	}

	public boolean destroyBlockState(BlockPos blockPos, boolean bl) {
		return this.parent.destroyBlock(blockPos, bl, null, 512);
	}

	public long getSeed() {
		return this.parent.getSeed();
	}
}
