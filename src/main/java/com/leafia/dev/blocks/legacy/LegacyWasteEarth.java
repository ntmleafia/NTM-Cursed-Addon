package com.leafia.dev.blocks.legacy;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.WasteEarth;
import com.leafia.contents.AddonBlocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class LegacyWasteEarth extends WasteEarth {
	public LegacyWasteEarth(Material materialIn,boolean tick,String s) {
		super(materialIn,tick,s);
		ModBlocks.ALL_BLOCKS.remove(this);
		AddonBlocks.ALL_BLOCKS.add(this);
	}
	public LegacyWasteEarth(Material materialIn,SoundType type,boolean tick,String s) {
		super(materialIn,type,tick,s);
		ModBlocks.ALL_BLOCKS.remove(this);
		AddonBlocks.ALL_BLOCKS.add(this);
	}
}
