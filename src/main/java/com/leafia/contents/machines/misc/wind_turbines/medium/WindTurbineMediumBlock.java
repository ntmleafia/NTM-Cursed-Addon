// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.misc.wind_turbines.medium;

import com.hbm.blocks.ILookOverlay;
import com.hbm.blocks.ITooltipProvider;
import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.hbm.inventory.fluid.tank.IFluidLoadingHandler;
import com.hbm.util.BobMathUtil;
import com.hbm.util.I18nUtil;
import com.leafia.contents.machines.misc.wind_turbines.WindTurbineTEBase;
import com.leafia.dev.blocks.blockbase.AddonBlockDummyable;
import com.leafia.dev.machine.MachineTooltip;
import com.llib.math.SIPfx;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WindTurbineMediumBlock extends AddonBlockDummyable implements ILookOverlay, ITooltipProvider {
	public WindTurbineMediumBlock(Material m,String s) {
		super(m,s);
	}
	@Override
	public int[] getDimensions() {
		return new int[]{ 6,0,0,0,0,0 };
	}
	@Override
	public int getOffset() {
		return 0;
	}
	@Override
	public @Nullable TileEntity createNewTileEntity(World worldIn,int meta) {
		if (meta >= 12)
			return new WindTurbineMediumTE();
		return null;
	}
	@Override
	public void addInformation(ItemStack stack,@Nullable World worldIn,List<String> tooltip,ITooltipFlag flagIn) {
		MachineTooltip.addGenerator(tooltip);
		addStandardInfo(tooltip);
		super.addInformation(stack,worldIn,tooltip,flagIn);
	}
	@Override
	public boolean canPlaceBlockAt(World worldIn,BlockPos pos) {
		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				for (TileEntity te : worldIn.getChunk(pos.add(x*16,0,z*16)).getTileEntityMap().values()) {
					if (te instanceof WindTurbineTEBase)
						return false;
				}
			}
		}
		return super.canPlaceBlockAt(worldIn,pos);
	}
	@Override
	public boolean onBlockActivated(World world,BlockPos pos,IBlockState state,EntityPlayer playerIn,EnumHand hand,EnumFacing facing,float hitX,float hitY,float hitZ) {
		BlockPos corePos = this.findCore(world,pos);
		if (corePos == null)
			return false;
		TileEntity te = world.getTileEntity(corePos);
		if (!(te instanceof WindTurbineTEBase turbine))
			return false;
		int slot = playerIn.inventory.currentItem;
		ItemStackHandler bruh = new ItemStackHandler(playerIn.inventory.mainInventory);
		for (int fuck = 0; fuck < playerIn.inventory.mainInventory.size(); fuck++) {
			// i hate this
			if (turbine.lube.loadTank(slot,fuck,bruh)) {
				playerIn.inventoryContainer.detectAndSendChanges();
				return true;
			}
		}
		return super.onBlockActivated(world,pos,state,playerIn,hand,facing,hitX,hitY,hitZ);
	}
	@Override
	public void printHook(Pre evt,World world,BlockPos pos) {
		BlockPos corePos = this.findCore(world,pos);
		if (corePos == null)
			return;
		TileEntity te = world.getTileEntity(corePos);
		if (!(te instanceof WindTurbineTEBase turbine))
			return;
		List<String> text = new ArrayList<>();
		text.add(TextFormatting.YELLOW+"-- "+TextFormatting.RESET+turbine.lube.getTankType().getLocalizedName()+": "+turbine.lube.getFill()+"/"+turbine.lube.getMaxFill()+"mB");
		text.add(TextFormatting.RED+"<- "+TextFormatting.RESET+SIPfx.auto(turbine.getPower())+"HE");
		if (turbine.obstructed)
			text.add("&[" + (BobMathUtil.getBlink() ? 0xff0000 : 0xffff00) + "&]"+I18nUtil.resolveKey("tile.wind_turbine.obstructed"));
		ILookOverlay.printGeneric(evt,I18nUtil.resolveKey(getTranslationKey()+".name"),0xffff00,0x404000,text);
	}
}
