package com.leafia.init;

import com.hbm.items.ModItems;
import com.leafia.contents.AddonItems.Resources;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;

public class AddonToolMaterials {
	public static ToolMaterial tmMysticite;
	public static ToolMaterial tmFissite;
	public static ToolMaterial tmPu238;
	public static void init() {
		tmMysticite = EnumHelper.addToolMaterial("leafia:MYSTICITE",5,0,15,5,20);
		tmFissite = EnumHelper.addToolMaterial("leafia:FISSITE",3,2500,8,3,10);
		tmPu238 = EnumHelper.addToolMaterial("leafia:PU238",3,300,6,2,5);
	}
	public static void initFixMaterials() {
		//tmMysticite.setRepairItem(new ItemStack(Resources.ingot_mysticite)); // you can't though
		tmFissite.setRepairItem(new ItemStack(Resources.ingot_fissite));
		tmPu238.setRepairItem(new ItemStack(ModItems.ingot_pu238));
	}
}
