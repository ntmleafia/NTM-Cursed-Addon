package com.leafia.contents.gear.combat.shields;

import com.hbm.inventory.OreDictManager;
import com.hbm.inventory.RecipesCommon;
import com.hbm.inventory.RecipesCommon.AStack;
import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.inventory.RecipesCommon.OreDictStack;
import com.hbm.items.ItemEnums.EnumCircuitType;
import com.hbm.items.ModItems;
import com.leafia.contents.AddonItems;
import com.leafia.init.AddonOreDict;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
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
		if (checkForRifle(inv))
			return true;

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

	public boolean checkForItem(ItemStack stack,AStack as) {
		return as.matchesRecipe(stack,true);
	}

	// Congratulations for finding the recipe of the rifle, now good luck making it.
	public boolean checkForRifle(InventoryCrafting inv) {
		return
				checkForItem(inv.getStackInRowAndColumn(0,0),new OreDictStack(OreDictManager.SA326.crystal())) &&
				checkForItem(inv.getStackInRowAndColumn(1,0),new ComparableStack(ModItems.circuit,1,EnumCircuitType.BISMOID)) &&

				checkForItem(inv.getStackInRowAndColumn(0,1),new OreDictStack(OreDictManager.ANY_BISMOIDBRONZE.lightBarrel())) &&
				checkForItem(inv.getStackInRowAndColumn(1,1),new OreDictStack(AddonOreDict.ANY_ULTRAALLOY.lightReceiver())) &&
				checkForItem(inv.getStackInRowAndColumn(2,1),new ComparableStack(AddonItems.am_rifle_cell_mysticite_filled)) &&

				checkForItem(inv.getStackInRowAndColumn(0,2),new OreDictStack(OreDictManager.BIGMT.mechanism())) &&
				checkForItem(inv.getStackInRowAndColumn(1,2),new OreDictStack(OreDictManager.ANY_HARDPLASTIC.grip()));
	}

	@Override
	public @NotNull ItemStack getCraftingResult(InventoryCrafting inv) {
		if (checkForRifle(inv))
			return new ItemStack(Item.getByNameOrId("leafia:gun_leafia_amrifle"));

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
