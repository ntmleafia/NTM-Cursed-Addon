// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.debug.fluid_voider;

import com.hbm.blocks.ILookOverlay;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.items.machine.IItemFluidIdentifier;
import com.hbm.util.I18nUtil;
import com.leafia.dev.blocks.blockbase.AddonBlockBase;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class VoiderBlock extends AddonBlockBase implements ITileEntityProvider, ILookOverlay {
	public VoiderBlock(Material m,String s) {
		super(m,s);
	}
	@Override
	public @Nullable TileEntity createNewTileEntity(World worldIn,int meta) {
		return new VoiderTE();
	}
	@SideOnly(Side.CLIENT)
	@Override
	public void printHook(Pre event,World world,BlockPos pos) {
		List<String> texts = new ArrayList<>();
		if (world.getTileEntity(pos) instanceof VoiderTE te)
			texts.add(TextFormatting.GREEN+"-> "+TextFormatting.RESET+te.buffer.getTankType().getLocalizedName()+": "+te.buffer.getFill()+"/"+te.buffer.getMaxFill()+"mB");
		ILookOverlay.printGeneric(event,I18nUtil.resolveKey(getTranslationKey()+".name"),0xFFFF55,0x3F3F15,texts);
	}
	@Override
	public boolean onBlockActivated(World world,BlockPos pos,IBlockState state,EntityPlayer player,EnumHand hand,EnumFacing facing,float hitX,float hitY,float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		if (stack.getItem() instanceof IItemFluidIdentifier identifier) {
			FluidType type = identifier.getType(world,pos.getX(),pos.getY(),pos.getZ(),stack);
			if (!world.isRemote && world.getTileEntity(pos) instanceof VoiderTE voider)
				voider.buffer.setTankType(type);
			return true;
		}
		return super.onBlockActivated(world,pos,state,player,hand,facing,hitX,hitY,hitZ);
	}
}
