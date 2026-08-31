package com.leafia.contents.gear.utility.radglasses;

import com.hbm.render.model.RadGlassesModel;
import com.leafia.contents.AddonItems;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class RadGlassesItem extends ItemArmor {
	@SideOnly(Side.CLIENT)
	public RadGlassesModel mdl;
	public RadGlassesItem(ArmorMaterial armorMaterial,int renderIndex,EntityEquipmentSlot armorType,String s) {
		super(armorMaterial, renderIndex, armorType);
		this.setTranslationKey(s);
		this.setRegistryName(s);
		AddonItems.ALL_ITEMS.add(this);
	}
	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entityLiving,ItemStack itemStack,EntityEquipmentSlot armorSlot,ModelBiped _default){
		if(mdl == null) {
			mdl = new RadGlassesModel(0);
		}
		return mdl;
	}
	@Override
	public void addInformation(ItemStack stack,World worldIn,List<String> list,ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, list, flagIn);
		list.add("Locates sources of chunk radiations");
	}
}
