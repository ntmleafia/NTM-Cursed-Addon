package com.leafia.contents.gear.combat.shields;

import com.leafia.contents.AddonItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AddonShieldItem extends ItemShield {
	private final String materialOreDict;

	public AddonShieldItem(String matOd,ToolMaterial material,String s){
		this(matOd, material.getMaxUses(), s);
	}

	public AddonShieldItem(String matOd,int dmg,String s){
		super();
		materialOreDict = matOd;
		this.setTranslationKey(s);
		this.setRegistryName(s);
		this.setMaxDamage(dmg);
		this.setCreativeTab(CreativeTabs.COMBAT);
		AddonItems.ALL_ITEMS.add(this);
	}

	@Override
	public boolean getIsRepairable(ItemStack toRepair,ItemStack repair) {
		for (int id : OreDictionary.getOreIDs(repair)) {
			if (OreDictionary.getOreName(id).equals(materialOreDict))
				return true;
		}
		return super.getIsRepairable(toRepair,repair);
	}

	@Override
	public @NotNull String getItemStackDisplayName(ItemStack stack) {
		return I18n.translateToLocal(this.getTranslationKey(stack) + ".name").trim();
	}

	@Override
	public boolean isShield(ItemStack stack,@Nullable EntityLivingBase entity) {
		return !stack.isEmpty() && stack.getItem() instanceof AddonShieldItem;
	}
}
