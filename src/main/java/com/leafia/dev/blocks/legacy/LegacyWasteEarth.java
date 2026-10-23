// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.dev.blocks.legacy;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.WasteEarth;
import com.leafia.contents.AddonBlocks;
import com.leafia.contents.AddonBlocks.LegacyBlocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
		if (this == LegacyBlocks.scorched_earth)
			setDefaultState(getBlockState().getBaseState().withProperty(META,2));
	}
	@Override
	public IBlockState getStateForPlacement(World world,BlockPos pos,EnumFacing facing,float hitX,float hitY,float hitZ,int meta,EntityLivingBase placer,EnumHand hand) {
		return super.getStateForPlacement(world,pos,facing,hitX,hitY,hitZ,meta,placer,hand);
	}
}
