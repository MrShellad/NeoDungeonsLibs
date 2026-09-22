package net.firefoxsalesman.dungeonslibs.items.gearconfig;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ToolGear extends MeleeGear {
	private final TagKey<Block> blocks;

	public ToolGear(TagKey<Block> blocks, Properties properties) {
		super(properties);
		this.blocks = blocks;
	}

	@Override
	public float getDestroySpeed(ItemStack pStack, BlockState pState) {
		return pState.is(this.blocks) ? this.getTier().getSpeed() : 1.0F;
	}

	@Override
	public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
		return state.is(this.blocks) && !state.is(this.getTier().getIncorrectBlocksForDrops());
	}
}
