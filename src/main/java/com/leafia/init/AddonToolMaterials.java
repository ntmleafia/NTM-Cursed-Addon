package com.leafia.init;

import com.leafia.contents.AddonItems.Resources;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;

public class AddonToolMaterials {
	public static ToolMaterial tmMysticite;
	public static void init() {
		tmMysticite = EnumHelper.addToolMaterial("leafia:MYSTICITE",5,0,15,5,20);
	}
	public static void initFixMaterials() {
		tmMysticite.setRepairItem(new ItemStack(Resources.ingot_mysticite)); // you can't though
	}
}
