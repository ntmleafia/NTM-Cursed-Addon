package com.leafia.init.hazards.types;

import com.hbm.hazard.modifier.IHazardModifier;
import com.hbm.hazard.type.IHazardType;
import com.hbm.util.I18nUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

public class HazardTypeSharpEdges implements IHazardType {
	@Override
	public void onUpdate(EntityLivingBase entityLivingBase,double v,ItemStack itemStack) { }
	@Override
	public void updateEntity(EntityItem entityItem,double v) {}
	public static float sharpStackNerf = 0.75f;
	@Override
	public void addHazardInformation(EntityPlayer entityPlayer,List<String> list,double v,ItemStack stack,List<IHazardModifier> list1) {
		list.add(TextFormatting.DARK_RED + "[" + I18nUtil.resolveKey("trait._hazarditem.sharp") + "]");
		list.add(TextFormatting.DARK_RED+" -::" + TextFormatting.RED + "" + I18nUtil.resolveKey("trait._hazarditem.sharp.add",Math.round(v*1)+"%"));
		if(stack.getCount() > 1) {
			list.add(TextFormatting.DARK_RED+" -::" + TextFormatting.RED + I18nUtil.resolveKey("desc.stack") + " " + Math.round((v*stack.getCount()*(1-sharpStackNerf)+v*sharpStackNerf)*1)+"%");
		}
	}
}
