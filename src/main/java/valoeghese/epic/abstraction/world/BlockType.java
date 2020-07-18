package valoeghese.epic.abstraction.world;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;

@FunctionalInterface
public interface BlockType {
	/**
	 * Creates a block with the specific properties.
	 * @param properties the properties of the block.
	 * @return the constructed block.
	 */
	Block create(Block.Properties properties);

	BlockType BASIC = Block::new;
	BlockType FALLING = FallingBlock::new;
}
