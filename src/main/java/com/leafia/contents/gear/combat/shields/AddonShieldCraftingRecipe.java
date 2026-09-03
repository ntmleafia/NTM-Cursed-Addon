package com.leafia.contents.gear.combat.shields;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.NotNull;

public class AddonShieldCraftingRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
	@Override
	public boolean matches(InventoryCrafting inv,World worldIn) {
		ItemStack shield = ItemStack.EMPTY;
		ItemStack banner = ItemStack.EMPTY;

		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.isEmpty())
				continue;

			if (stack.getItem() instanceof ItemBanner) {
				if (!banner.isEmpty())
					return false;
				banner = stack;
			} else {
				if (!(stack.getItem() instanceof AddonShieldItem) || !shield.isEmpty() || stack.getSubCompound("BlockEntityTag") != null)
					return false;
				shield = stack;
			}
		}

		return !shield.isEmpty() && !banner.isEmpty();
	}

	@Override
	public @NotNull ItemStack getCraftingResult(InventoryCrafting inv) {
		ItemStack shield = ItemStack.EMPTY;
		ItemStack banner = ItemStack.EMPTY;

		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.getItem() instanceof ItemBanner)
				banner = stack;
			else if (stack.getItem() instanceof AddonShieldItem)
				shield = stack.copy();
		}

		if (!shield.isEmpty()) {
			NBTTagCompound bannerTag = banner.getSubCompound("BlockEntityTag");
			NBTTagCompound shieldTag = bannerTag == null ? new NBTTagCompound() : bannerTag.copy();
			shieldTag.setInteger("Base",banner.getMetadata() & 15);
			shield.setTagInfo("BlockEntityTag",shieldTag);
		}
		return shield;
	}

	@Override
	public @NotNull ItemStack getRecipeOutput() {
		return ItemStack.EMPTY;
	}

	@Override
	public @NotNull NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		NonNullList<ItemStack> remaining = NonNullList.withSize(inv.getSizeInventory(),ItemStack.EMPTY);
		for (int i = 0; i < remaining.size(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.getItem().hasContainerItem())
				remaining.set(i,new ItemStack(stack.getItem().getContainerItem()));
		}
		return remaining;
	}

	@Override
	public boolean isDynamic() {
		return true;
	}

	@Override
	public boolean canFit(int width,int height) {
		return width * height >= 2;
	}
}
