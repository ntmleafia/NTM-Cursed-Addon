// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.dev.blocks.blockbase;

import com.hbm.handler.radiation.RadiationSystemNT;
import com.hbm.interfaces.IRadResistantBlock;
import com.hbm.util.I18nUtil;
import com.leafia.dev.blocks.AddonBlockBakeFrame;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class AddonRadResistantBlockBaked extends AddonBlockBaked implements IRadResistantBlock {
	public AddonRadResistantBlockBaked(Material m,String s) {
		super(m,s);
	}
	public AddonRadResistantBlockBaked(Material m,String s,AddonBlockBakeFrame blockFrame) {
		super(m,s,blockFrame);
	}
	public AddonRadResistantBlockBaked(Material m,String s,String texture) {
		super(m,s,texture);
	}
	public AddonRadResistantBlockBaked(Material m,String s,String textureTop,String textureSide) {
		super(m,s,textureTop,textureSide);
	}
	public boolean isRadResistant(int meta) {
		return true;
	}
	@Override
	public void onBlockAdded(World worldIn,BlockPos pos,IBlockState state) {
		if (isRadResistant(worldIn.getBlockState(pos).getBlock().getMetaFromState(worldIn.getBlockState(pos))))
			RadiationSystemNT.markSectionForRebuild(worldIn, pos);
		super.onBlockAdded(worldIn, pos, state);
	}
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		if (isRadResistant(worldIn.getBlockState(pos).getBlock().getMetaFromState(worldIn.getBlockState(pos))))
			RadiationSystemNT.markSectionForRebuild(worldIn, pos);
		super.breakBlock(worldIn, pos, state);
	}
	@Override
	public void addInformation(ItemStack stack,World player,List<String> tooltip,ITooltipFlag advanced) {
		if (isRadResistant(stack.getMetadata()))
			tooltip.add("§2[" + I18nUtil.resolveKey("trait.radshield") + "]");
		super.addInformation(stack, player, tooltip, advanced);
	}
}
