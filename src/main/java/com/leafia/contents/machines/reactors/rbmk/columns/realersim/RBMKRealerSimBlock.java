// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.reactors.rbmk.columns.realersim;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.machine.rbmk.RBMKRodReaSim;
import com.hbm.tileentity.TileEntityProxyInventory;
import com.leafia.contents.AddonBlocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class RBMKRealerSimBlock extends RBMKRodReaSim {
	public RBMKRealerSimBlock(boolean moderated,String s,String c) {
		super(moderated,s,c);
		ModBlocks.ALL_BLOCKS.remove(this);
		AddonBlocks.ALL_BLOCKS.add(this);
	}
	@Override
	public TileEntity createNewTileEntity(World world,int meta) {
		if(meta >= offset)
			return new RBMKRealerSimTE();
		if(hasExtra(meta))
			return new TileEntityProxyInventory();
		return null;
	}
}
