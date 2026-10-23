// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.elevators.items;

import com.hbm.blocks.BlockDummyable;
import com.leafia.contents.machines.elevators.EvPulleyTE;
import com.leafia.contents.machines.elevators.EvShaft;
import com.leafia.contents.machines.elevators.EvShaftNeo;
import com.leafia.contents.machines.elevators.weight.EvWeightEntity;
import com.leafia.dev.LeafiaDebug;
import com.leafia.dev.items.itembase.AddonItemBase;
import com.leafia.dev.machine.MachineTooltip;
import net.minecraft.block.BlockDirectional;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WeightSpawnItem extends AddonItemBase {
	@Override
	public void addInformation(ItemStack stack,@Nullable World worldIn,List<String> tooltip,ITooltipFlag flagIn) {
		MachineTooltip.addBeta(tooltip);
		super.addInformation(stack,worldIn,tooltip,flagIn);
	}
	public WeightSpawnItem(String s) {
		super(s);
		setMaxStackSize(1);
	}
	public EvPulleyTE findPulley(World world, BlockPos basePos) {
		EvPulleyTE pulley = null;
		for (int i = basePos.getY(); i < 255; i++) {
			TileEntity te = world.getTileEntity(new BlockPos(basePos.getX(),i,basePos.getZ()));
			if (te instanceof EvPulleyTE) {
				pulley = (EvPulleyTE)te;
				return pulley;
			}
		}
		return null;
	}
	@Override
	public EnumActionResult onItemUse(EntityPlayer player,World world,BlockPos pos,EnumHand hand,EnumFacing facing,float hitX,float hitY,float hitZ) {
		if (world.getBlockState(pos).getBlock() instanceof EvShaftNeo) {
			EnumFacing face = world.getBlockState(pos).getValue(BlockDirectional.FACING);
			//LeafiaDebug.debugPos(world,pos.offset(face),1,0x00FF00,"UWU");
			BlockPos centerPos = pos.offset(face);
			EvPulleyTE pulley = findPulley(world,centerPos);
			if (pulley != null) {
				if (!world.isRemote) {
					player.getHeldItem(hand).shrink(1);
					EvWeightEntity entity = new EvWeightEntity(world);
					entity.setPositionAndRotation(
							pos.getX() + 0.5 - face.getXOffset()*0.4,
							pos.getY(),
							pos.getZ() + 0.5 - face.getZOffset()*0.4,
							face.getHorizontalAngle() - 90,
							0
					);
					world.spawnEntity(entity);
					player.inventoryContainer.detectAndSendChanges();
				}
			} else {
				if (world.isRemote)
					player.sendMessage(new TextComponentTranslation("item.ev_spawn.error.pulley").setStyle(new Style().setColor(TextFormatting.RED)));
			}
		} else {
			if (world.isRemote)
				player.sendMessage(new TextComponentTranslation("item.ev_spawn.error.pulley").setStyle(new Style().setColor(TextFormatting.RED)));
		}
		return EnumActionResult.SUCCESS;
	}
}
