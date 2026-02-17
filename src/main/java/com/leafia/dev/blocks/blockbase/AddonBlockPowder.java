package com.leafia.dev.blocks.blockbase;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BlockPowder;
import com.leafia.contents.AddonBlocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class AddonBlockPowder extends BlockPowder {
	public AddonBlockPowder(Material mat,SoundType soundType,String s) {
		super(mat,soundType,s);
		ModBlocks.ALL_BLOCKS.remove(this);
		AddonBlocks.ALL_BLOCKS.add(this);
	}
}
