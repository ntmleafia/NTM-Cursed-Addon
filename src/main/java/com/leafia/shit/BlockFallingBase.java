package com.leafia.shit;

import com.hbm.main.MainRegistry;
import com.leafia.contents.AddonBlocks;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockFallingBase extends BlockFalling {
	
	public BlockFallingBase(Material m, String s, SoundType type){
		super(m);
		this.setTranslationKey(s);
		this.setRegistryName(s);
		this.setCreativeTab(MainRegistry.controlTab);
		this.setHarvestLevel("shovel", 0);
		this.setSoundType(type);
		AddonBlocks.ALL_BLOCKS.add(this);
	}
	
}
