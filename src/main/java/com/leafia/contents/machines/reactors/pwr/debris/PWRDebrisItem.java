// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.reactors.pwr.debris;

import com.hbm.util.I18nUtil;
import com.leafia.Tags;
import com.leafia.contents.AddonItems;
import com.leafia.contents.machines.reactors.pwr.debris.PWRDebrisEntity.DebrisType;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PWRDebrisItem extends Item {
	public final DebrisType type;
	final String typeNameKey;
	public boolean canBeCraftedBack = true;
	public PWRDebrisItem(String s,DebrisType type) {
		this.setTranslationKey(s);
        this.setRegistryName(Tags.MODID, s);
		this.type = type;
		typeNameKey = s.replace("lwr_","item.lwrdebris.");
		AddonItems.ALL_ITEMS.add(this);
	}
	public PWRDebrisItem disableCrafting() { canBeCraftedBack = false; return this; }
	@Override
	@SideOnly(Side.CLIENT)
	public String getItemStackDisplayName(ItemStack stack) {
		String typename = I18nUtil.resolveKey(typeNameKey);
		String itemNameKey = super.getItemStackDisplayName(stack);
		if (stack.getTagCompound() != null)
			itemNameKey = stack.getTagCompound().getString("block");
		Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(itemNameKey));
		String itemName = itemNameKey;
		if (block != null)
			itemName = block.getLocalizedName();
		String mainKey = "item.lwrdebris.conjunctive";
		if (itemName.toLowerCase().contains(I18nUtil.resolveKey("item.lwrdebris.conjunctive.switch").toLowerCase()))
			mainKey = mainKey+".alt";
		return I18nUtil.resolveKey(mainKey).replace("{type}",typename).replace("{block}",itemName);
	}
}