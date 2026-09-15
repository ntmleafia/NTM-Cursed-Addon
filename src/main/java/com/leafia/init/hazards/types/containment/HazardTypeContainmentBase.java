package com.leafia.init.hazards.types.containment;

import com.hbm.hazard.modifier.IHazardModifier;
import com.hbm.hazard.type.IHazardType;
import com.hbm.util.I18nUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.List;

public abstract class HazardTypeContainmentBase implements IHazardType {
	@Override
	public void onUpdate(EntityLivingBase entityLivingBase,double v,ItemStack itemStack) { }
	@Override
	public void updateEntity(EntityItem entityItem,double v) { }
	@Override
	public void addHazardInformation(EntityPlayer entityPlayer,List<String> list,double v,ItemStack itemStack,List<IHazardModifier> list1) {
		list.add(TextFormatting.GOLD+"["+I18nUtil.resolveKey("trait._hazarditem.containment")+"]");
	}
	public abstract void onDestroyed(DamageSource source,World world,Vec3d pos,double level);
}
