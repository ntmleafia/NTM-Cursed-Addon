// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.reactors.lftr.processing.separator.recipes;

import com.hbm.inventory.RecipesCommon;
import com.hbm.inventory.fluid.FluidStack;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.recipes.loader.GenericRecipe;
import com.hbm.inventory.recipes.loader.GenericRecipes.IOutput;
import com.hbm.util.BobMathUtil;
import com.hbm.util.I18nUtil;
import com.leafia.contents.machines.reactors.lftr.components.element.MSRElementTE.MSRFuel;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SaltSeparatorRecipe extends GenericRecipe {
	public FluidType saltType;
	public int saltAmount;
	public Map<MSRFuel,Double> mixture = new HashMap<>();
	public SaltSeparatorRecipe(String name) {
		super(name);
	}
	public SaltSeparatorRecipe addMixture(MSRFuel type,double amt) {
		mixture.put(type,amt);
		return this;
	}

	@Override
	public List<String> print() {
		List<String> list = new ArrayList();
		list.add(TextFormatting.YELLOW + this.getLocalizedName());
		this.autoSwitch(list);
		this.duration(list);
		this.power(list);
		list.add(TextFormatting.BOLD + I18nUtil.resolveKey("gui.recipe.inputMix") + ":");
		list.add("  " + TextFormatting.AQUA + saltAmount + "mB " + saltType.getLocalizedName());

		for (Entry<MSRFuel,Double> entry : mixture.entrySet())
			list.add("  "+TextFormatting.LIGHT_PURPLE+entry.getValue()+"/B "+I18nUtil.resolveKey("tile.msr.fuel."+entry.getKey()));
		list.add(TextFormatting.BOLD + I18nUtil.resolveKey("gui.recipe.input") + ":");
		if(inputFluid != null) for(FluidStack fluid : inputFluid) list.add("  " + TextFormatting.BLUE + fluid.fill + "mB " + fluid.type.getLocalizedName() + (fluid.pressure == 0 ? "" : " at " + TextFormatting.RED + fluid.pressure + " PU"));
		this.output(list);
		return list;
	}

	public SaltSeparatorRecipe setSalt(FluidType salt,int amount) {
		saltType = salt;
		saltAmount = amount;
		return this;
	}

	@Override
	public SaltSeparatorRecipe setupNamed(int duration,long power) {
		return (SaltSeparatorRecipe)super.setupNamed(duration,power);
	}
	@Override
	public SaltSeparatorRecipe setIcon(Block block) {
		return (SaltSeparatorRecipe)super.setIcon(block);
	}
	@Override
	public SaltSeparatorRecipe setIcon(ItemStack icon) {
		return (SaltSeparatorRecipe)super.setIcon(icon);
	}
	@Override
	public SaltSeparatorRecipe setIcon(Item item) {
		return (SaltSeparatorRecipe)super.setIcon(item);
	}
	@Override
	public SaltSeparatorRecipe setIcon(Item item,int meta) {
		return (SaltSeparatorRecipe)super.setIcon(item,meta);
	}
	@Override
	public SaltSeparatorRecipe setIconToFirstIngredient() {
		return (SaltSeparatorRecipe)super.setIconToFirstIngredient();
	}
	@Override
	public SaltSeparatorRecipe inputFluidsEx(FluidStack... input) {
		return (SaltSeparatorRecipe)super.inputFluidsEx(input);
	}
	@Override
	public SaltSeparatorRecipe inputFluids(FluidStack... input) {
		return (SaltSeparatorRecipe)super.inputFluids(input);
	}
	@Override
	public SaltSeparatorRecipe outputFluids(FluidStack... output) {
		return (SaltSeparatorRecipe)super.outputFluids(output);
	}
	@Override
	public SaltSeparatorRecipe outputItems(IOutput... output) {
		return (SaltSeparatorRecipe)super.outputItems(output);
	}
	@Override
	public SaltSeparatorRecipe outputItems(ItemStack... output) {
		return (SaltSeparatorRecipe)super.outputItems(output);
	}
}
