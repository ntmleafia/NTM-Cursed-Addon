package com.leafia.contents.machines.powercores.dfc;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.machine.CoreComponent;
import com.leafia.contents.AddonBlocks;
import net.minecraft.block.material.Material;

public class AddonCoreComponent extends CoreComponent {
	public AddonCoreComponent(Material materialIn,String s) {
		super(materialIn,s);
		ModBlocks.ALL_BLOCKS.remove(this);
		AddonBlocks.ALL_BLOCKS.add(this);
	}
}
