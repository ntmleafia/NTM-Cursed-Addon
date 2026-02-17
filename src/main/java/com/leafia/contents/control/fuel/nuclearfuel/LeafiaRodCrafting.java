package com.leafia.contents.control.fuel.nuclearfuel;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;

public class LeafiaRodCrafting extends Impl<IRecipe> implements IRecipe {
	public ItemStack getStack(InventoryCrafting inv) {
		ItemStack stackie = null;
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.isEmpty()) continue;
			if (stackie == null)
				stackie = stack;
			else
				return null;
		}
		return stackie;
	}
	@Override
	public boolean matches(InventoryCrafting inv,World worldIn) {
		ItemStack stackie = getStack(inv);
		if (stackie != null && stackie.getItem() instanceof LeafiaRodItem rod) {
			NBTTagCompound tag = stackie.getTagCompound();
			if (tag != null) {
				if (tag.getDouble("depletion") >= rod.life*0.05 && rod.life > 0)
					return false;
			}
			return true;
		}
		return false;
	}
	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		ItemStack stackie = getStack(inv);
		if (stackie != null && stackie.getItem() instanceof LeafiaRodItem rod) {
			NBTTagCompound tag = stackie.getTagCompound();
			if (tag != null) {
				if (tag.getDouble("depletion") > rod.life*0.05 && rod.life > 0)
					return null;
			}
			return new ItemStack(rod.baseItem,1,rod.baseMeta);
		}
		return null;
	}
	@Override
	public boolean canFit(int width,int height) {
		return width >= 1 && height >= 1;
	}
	@Override
	public boolean isDynamic() {
		return true;
	}
	@Override
	public ItemStack getRecipeOutput() {
		return ItemStack.EMPTY;
	}
}
