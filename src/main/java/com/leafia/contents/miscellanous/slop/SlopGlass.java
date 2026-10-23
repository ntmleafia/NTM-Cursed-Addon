// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.miscellanous.slop;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BlockNTMGlass;
import com.leafia.contents.AddonBlocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.BlockRenderLayer;

public class SlopGlass extends BlockNTMGlass {
	public SlopGlass(Material materialIn,BlockRenderLayer layer,String s) {
		super(materialIn,layer,true,true,s);
		ModBlocks.ALL_BLOCKS.remove(this);
		AddonBlocks.ALL_BLOCKS.add(this);
	}
}
