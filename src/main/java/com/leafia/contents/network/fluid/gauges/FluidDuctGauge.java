// SPDX-FileCopyrightText: 2025-2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.network.fluid.gauges;

import com.hbm.api.block.IToolable.ToolType;
import com.hbm.blocks.ILookOverlay;
import com.hbm.items.tool.ItemTooling;
import com.hbm.tileentity.network.TileEntityPipeBaseNT;
import com.hbm.util.I18nUtil;
import com.leafia.contents.network.fluid.FluidDuctEquipmentBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class FluidDuctGauge extends FluidDuctEquipmentBase {
	public FluidDuctGauge(Material materialIn,String s) {
		super(materialIn,s);
	}
	@Override
	public TileEntity createNewTileEntity(World worldIn,int meta) {
		return new FluidDuctGaugeTE();
	}

	@Override
	public void printHook(Pre event,World world,BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (!(te instanceof TileEntityPipeBaseNT duct))
			return;

		List<String> text = new ArrayList<>();
		text.add("&[" + duct.getType().getColor() + "&]" + duct.getType().getLocalizedName());
		FluidDuctGaugeTE gauge = (FluidDuctGaugeTE)te;
		text.add(gauge.fillPerSec+"mB/s");
		ILookOverlay.printGeneric(event, I18nUtil.resolveKey(getTranslationKey() + ".name"), 0xffff00, 0x404000, text);
	}
	@SideOnly(Side.CLIENT)
	void openGui(TileEntity te) {
		if (te instanceof FluidDuctGaugeTE gauge)
			Minecraft.getMinecraft().displayGuiScreen(new FluidDuctGaugeGUI(gauge));
	}
	@Override
	public boolean onBlockActivated(World worldIn,BlockPos pos,IBlockState state,EntityPlayer playerIn,EnumHand hand,EnumFacing facing,float hitX,float hitY,float hitZ) {
		if (playerIn.getHeldItem(hand).getItem() instanceof ItemTooling tool) {
			if (tool.getType() == ToolType.SCREWDRIVER) {
				if (worldIn.isRemote)
					openGui(worldIn.getTileEntity(pos));
			}
		}
		return super.onBlockActivated(worldIn,pos,state,playerIn,hand,facing,hitX,hitY,hitZ);
	}
}
