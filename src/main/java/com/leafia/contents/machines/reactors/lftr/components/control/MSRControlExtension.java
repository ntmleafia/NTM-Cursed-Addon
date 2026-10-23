// SPDX-FileCopyrightText: 2025-2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.reactors.lftr.components.control;

import com.hbm.blocks.ITooltipProvider;
import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BlockPipe;
import com.leafia.contents.AddonBlocks;
import com.leafia.dev.machine.MachineTooltip;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MSRControlExtension extends BlockPipe implements ITooltipProvider {
	public MSRControlExtension(Material mat,String s) {
		super(mat,s);
		ModBlocks.ALL_BLOCKS.remove(this);
		AddonBlocks.ALL_BLOCKS.add(this);
	}
	@Override
	public void addInformation(ItemStack stack,@Nullable World worldIn,List<String> tooltip,ITooltipFlag flagIn) {
		MachineTooltip.addMultiblock(tooltip);
		MachineTooltip.addModular(tooltip);
		addStandardInfo(tooltip);
		super.addInformation(stack,worldIn,tooltip,flagIn);
	}
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		if (state.getBlock() instanceof BlockPipe) {
			EnumFacing.Axis axis = state.getValue(AXIS);
			if (axis == EnumFacing.Axis.X)
				return new AxisAlignedBB(0,2d/16,2d/16,1,1-2d/16,1-2d/16);
			if (axis == EnumFacing.Axis.Y)
				return new AxisAlignedBB(2d/16,0,2d/16,1-2d/16,1,1-2d/16);
			if (axis == EnumFacing.Axis.Z)
				return new AxisAlignedBB(2d/16,2d/16,0,1-2d/16,1-2d/16,1);
		}
		return super.getBoundingBox(state, source, pos);
	}
}
