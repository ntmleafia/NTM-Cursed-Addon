// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.miscellanous.slop;

import com.hbm.blocks.ITooltipProvider;
import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.machine.BlockMachineBase;
import com.hbm.main.MainRegistry;
import com.leafia.contents.AddonBlocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SlopBlock extends BlockMachineBase implements ITooltipProvider {
	public SlopBlock(Material materialIn,String s) {
		super(materialIn,0,s);
		ModBlocks.ALL_BLOCKS.remove(this);
		AddonBlocks.ALL_BLOCKS.add(this);
	}
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}
	@Override
	public TileEntity createNewTileEntity(World worldIn,int meta) {
		return new SlopTE();
	}
	@Override
	protected boolean rotatable() {
		return true;
	}
	@Override
	public boolean onBlockActivated(World world,BlockPos pos,IBlockState state,EntityPlayer player,EnumHand hand,EnumFacing facing,float hitX,float hitY,float hitZ) {
		if (!world.isRemote) {
			TileEntity te = world.getTileEntity(pos);
			if (te instanceof SlopTE slop) {
				if (slop.checkFormed(true))
					player.openGui(MainRegistry.instance,0,world,pos.getX(),pos.getY(),pos.getZ());
			}
		}
		return true;
	}
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack,@Nullable World worldIn,List<String> tooltip,ITooltipFlag flagIn) {
		addStandardInfo(tooltip);
		super.addInformation(stack,worldIn,tooltip,flagIn);
	}
}
