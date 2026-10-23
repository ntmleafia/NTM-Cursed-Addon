// SPDX-FileCopyrightText: 2025-2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.dev.blocks.blockbase;

import com.hbm.main.MainRegistry;
import com.hbm.util.I18nUtil;
import com.leafia.AddonBase;
import com.leafia.contents.AddonBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.List;

public class AddonBlockBase extends Block {
	public AddonBlockBase(Material m,String s) {
		super(m);
		this.setTranslationKey(s);
		this.setRegistryName(AddonBase.MODID, s);
		this.setHarvestLevel("pickaxe", 0);
		this.setCreativeTab(MainRegistry.controlTab);
		AddonBlocks.ALL_BLOCKS.add(this);
	}

	public AddonBlockBase(Material material) {
		super(material);
		AddonBlocks.ALL_BLOCKS.add(this);
	}

	public AddonBlockBase() {
		super(Material.ROCK);
		AddonBlocks.ALL_BLOCKS.add(this);
	}

	public AddonBlockBase(Material m,SoundType sound,String s) {
		super(m);
		this.setTranslationKey(s);
		this.setRegistryName(AddonBase.MODID, s);
		this.setSoundType(sound);
		this.setHarvestLevel("pickaxe", 0);
		this.setCreativeTab(MainRegistry.controlTab);
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
