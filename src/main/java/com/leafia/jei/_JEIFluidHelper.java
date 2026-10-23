// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.jei;

import com.hbm.inventory.fluid.Fluids;
import mezz.jei.api.recipe.IFocus;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class _JEIFluidHelper {
	private _JEIFluidHelper() { }

	public static @Nullable FluidStack toForge(com.hbm.inventory.fluid.FluidStack stack) {
		if (stack == null || stack.type == null || stack.type == Fluids.NONE) {
			return null;
		}

		Fluid fluid = stack.type.getFF();
		if (fluid == null) {
			throw new IllegalStateException("No Forge fluid is registered for JEI recipe display: " + stack.type.getName());
		}

		return new FluidStack(fluid,stack.fill);
	}

	public static List<FluidStack> toForge(List<com.hbm.inventory.fluid.FluidStack> stacks) {
		List<FluidStack> converted = new ArrayList<>(stacks.size());
		for (com.hbm.inventory.fluid.FluidStack stack : stacks) {
			converted.add(toForge(stack));
		}
		return converted;
	}

	public static boolean handleClick(com.hbm.inventory.fluid.FluidStack stack,int mouseX,int mouseY,int x,int y,int width,int height,int mouseButton) {
		if (mouseButton != 0 && mouseButton != 1) {
			return false;
		}

		FluidStack forgeStack = toForge(stack);
		if (forgeStack == null || _AddonJEI.runtime == null) {
			return false;
		}

		mouseX--;
		mouseY--;
		if (mouseX < x || mouseX > x + width || mouseY < y || mouseY > y + height) {
			return false;
		}

		IFocus.Mode mode = mouseButton == 0 ? IFocus.Mode.OUTPUT : IFocus.Mode.INPUT;
		_AddonJEI.runtime.getRecipesGui().show(_AddonJEI.runtime.getRecipeRegistry().createFocus(mode, forgeStack));
		return true;
	}
}
