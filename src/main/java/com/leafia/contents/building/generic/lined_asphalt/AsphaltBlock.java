// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.building.generic.lined_asphalt;

import com.leafia.contents.building.generic.lined_asphalt.LinedAsphaltBlock.AsphaltLine;
import com.leafia.dev.blocks.blockbase.AddonBlockBase;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AsphaltBlock extends AddonBlockBase {
	public AsphaltBlock(Material m,String s) {
		super(m,s);
	}
	public static boolean paintBlock(World worldIn,BlockPos pos,IBlockState state,EntityPlayer playerIn,EnumHand hand,EnumFacing facing,float hitX,float hitY,float hitZ) {
		hitX -= 0.5f;
		hitZ -= 0.5f;
		ItemStack stack = playerIn.getHeldItem(hand);
		if (stack.getItem() instanceof ItemDye) {
			if (EnumDyeColor.byDyeDamage(stack.getMetadata()).equals(EnumDyeColor.WHITE)) {
				AsphaltLine line = new AsphaltLine(worldIn.getBlockState(pos).getBlock());
				boolean xAxis = Math.abs(hitX) > Math.abs(hitZ);
				if (hand.equals(EnumHand.OFF_HAND)) {
					if (xAxis)
						line.x = true;
					else
						line.z = true;
				} else {
					if (xAxis) {
						if (hitX > 0)
							line.e = true;
						else
							line.w = true;
					} else {
						if (hitZ < 0)
							line.n = true;
						else
							line.s = true;
					}
				}
				Block b = AsphaltLine.getBlock(line.toString());
				if (b != null) {
					worldIn.setBlockState(pos,b.getDefaultState());
					return true;
				}
			}
		}
		return false;
	}
}
