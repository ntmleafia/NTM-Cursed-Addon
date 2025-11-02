package com.leafia.dev.hazards.types;

import com.hbm.hazard.modifier.IHazardModifier;
import com.hbm.hazard.type.IHazardType;
import com.hbm.lib.Library;
import com.hbm.util.I18nUtil;
import com.leafia.dev.hazards.MultiRad;
import com.leafia.dev.hazards.MultiRad.RadiationType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

public class HazardTypeMultiRad implements IHazardType {

	public MultiRad rad;

	@Override
	public void onUpdate(EntityLivingBase entityLivingBase,double v,ItemStack itemStack) {
		if (rad == null) {
			rad = new MultiRad();
			rad.setActivation((float)v);
		}
	}

	@Override
	public void updateEntity(EntityItem entityItem,double v) {
		if (rad == null) {
			rad = new MultiRad();
			rad.setActivation((float)v);
		}
	}

	@Override
	public void addHazardInformation(EntityPlayer entityPlayer, List<String> list, double v, ItemStack stack, List<IHazardModifier> list1) {
		if(rad.total() /* module.tempMod*/ > 0) {
			list.add(TextFormatting.GREEN + "[" + I18nUtil.resolveKey("trait._hazarditem.radioactive") + "]");
			rad.forEach((type,rad)->{
				if (rad > 0)
					list.add(TextFormatting.GREEN+" -::" + type.color + I18nUtil.resolveKey(type.translationKey) + " " + (Library.roundFloat(getNewValue(rad), 3)+ getSuffix(rad) + " " + I18nUtil.resolveKey("desc.rads")));
			});
			if(stack.getCount() > 1) {
				float stackRad = rad.total() /* module.tempMod*/ * stack.getCount();
				list.add(TextFormatting.GREEN+" -::" + TextFormatting.GOLD + I18nUtil.resolveKey("desc.stack")+" " + Library.roundFloat(getNewValue(stackRad), 3) + getSuffix(stackRad) + " " + I18nUtil.resolveKey("desc.rads"));
			}
			if (rad.radon > 0)
				list.add(TextFormatting.GREEN+" -::" + I18nUtil.resolveKey(RadiationType.RADON.translationKey) + " " + (Library.roundFloat(getNewValue(rad.radon), 3)+ getSuffix(rad.radon) + " " + I18nUtil.resolveKey("desc.rads")));
		}
	}
	public static double getNewValue(double radiation) {
		if (radiation < 1000000) {
			return radiation;
		} else if (radiation < 1000000000) {
			return radiation * 0.000001D;
		} else {
			return radiation * 0.000000001D;
		}
	}

	public static String getSuffix(double radiation) {
		if (radiation < 1000000) {
			return "";
		} else if (radiation < 1000000000) {
			return I18nUtil.resolveKey("desc.mil");
		} else {
			return I18nUtil.resolveKey("desc.bil");
		}
	}
}
