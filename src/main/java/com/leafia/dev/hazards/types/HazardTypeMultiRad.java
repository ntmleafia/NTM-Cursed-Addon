package com.leafia.dev.hazards.types;

import com.hbm.hazard.modifier.HazardModifier;
import com.hbm.hazard.type.HazardTypeBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.List;

public class HazardTypeMultiRad extends HazardTypeBase {

	@Override
	public void onUpdate(EntityLivingBase entityLivingBase,double v,ItemStack itemStack) {

	}

	@Override
	public void updateEntity(EntityItem entityItem,double v) {

	}

	@Override
	public void addHazardInformation(EntityPlayer entityPlayer,List<String> list,double v,ItemStack itemStack,List<HazardModifier> list1) {
		// lol no
	}
}
