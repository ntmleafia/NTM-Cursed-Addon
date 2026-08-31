package com.leafia.contents.minerals;

import com.hbm.main.MainRegistry;
import com.hbm.util.I18nUtil;
import com.leafia.AddonBase;
import com.leafia.contents.AddonBlocks;
import net.minecraft.block.BlockOre;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.List;

public class AddonOreBase extends BlockOre {
	public AddonOreBase(String s,int harvestLvl) {
		super();
		this.setTranslationKey(s);
		this.setRegistryName(s);
		this.setCreativeTab(MainRegistry.controlTab);
		this.setTickRandomly(false);
		this.setHarvestLevel("pickaxe",harvestLvl);
		AddonBlocks.ALL_BLOCKS.add(this);
	}

	public AddonOreBase() {
		super();
		AddonBlocks.ALL_BLOCKS.add(this);
	}

	@Override
	public void addInformation(ItemStack stack,World player,List<String> list,ITooltipFlag advanced) {
		float hardness = getExplosionResistance(null);
		if (hardness > 50) {
			list.add(TextFormatting.GOLD + I18nUtil.resolveKey("trait.blastres", hardness));
		}
	}
}
