package com.leafia.dev.blocks.blockbase.meta;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class MetaPlacableItemBlock extends ItemBlock {
	IMetaPlacable imeta;
	public MetaPlacableItemBlock(Block block) {
		super(block);
		imeta = (IMetaPlacable)block;
		setHasSubtypes(true);
	}
	@Override
	public String getTranslationKey(ItemStack stack) {
		return imeta.getTranslationKey(stack.getMetadata());
	}
	@Override
	public int getMetadata(int damage) {
		return damage;
	}
}
