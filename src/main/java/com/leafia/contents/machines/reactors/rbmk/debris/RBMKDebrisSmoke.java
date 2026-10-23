// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.reactors.rbmk.debris;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.machine.rbmk.RBMKDebris;
import com.leafia.contents.AddonBlocks;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class RBMKDebrisSmoke extends RBMKDebris implements ITileEntityProvider {
	public RBMKDebrisSmoke(String s) {
		super(s);
		ModBlocks.ALL_BLOCKS.remove(this);
		AddonBlocks.ALL_BLOCKS.add(this);
	}
	@Override
	public @Nullable TileEntity createNewTileEntity(World worldIn,int meta) {
		return new RBMKDebrisSmokeTE();
	}
}
