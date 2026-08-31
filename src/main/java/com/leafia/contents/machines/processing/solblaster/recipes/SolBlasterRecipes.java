package com.leafia.contents.machines.processing.solblaster.recipes;

import com.custom_hbm.util.LCETuple.Pair;
import com.hbm.blocks.ModBlocks;
import com.hbm.hazard.HazardEntry;
import com.hbm.hazard.HazardSystem;
import com.hbm.hazard.type.HazardTypeRadiation;
import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemRTGPellet;
import com.leafia.contents.control.fuel.nuclearfuel.LeafiaRodItem;
import com.leafia.init.hazards.types.LCERad;
import com.llib.exceptions.LeafiaDevFlaw;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.function.Function;

public class SolBlasterRecipes {
	public static Map<Item,Map<Integer,Pair<Item,Integer>>> recipes = new HashMap<>();
	public static Map<Item,Map<Integer,Pair<Item,Integer>>> fallback = new HashMap<>();
	public static Item item(Block block) {
		return Item.getItemFromBlock(block);
	}
	public static void register() {
		addRecipe(ModItems.ingot_schrabidium,0,ModItems.nugget_euphemium,0);
		addRecipe(ModItems.ingot_solinium,0,ModItems.nugget_euphemium,0);
		addRecipe(item(ModBlocks.block_schrabidium),0,ModItems.ingot_euphemium,0);
		addRecipe(item(ModBlocks.block_solinium),0,ModItems.ingot_euphemium,0);

		addFallback(ModItems.egg_balefire_shard,0);
		addFallback(ModItems.egg_balefire,0);
	}
	public static List<Function<ItemStack,ItemStack>> fallbackEvaluators = new ArrayList<>();
	static {
		// radioactive items
		fallbackEvaluators.add((stack)->{
			boolean isRadioactive = false;
			List<HazardEntry> hazards = HazardSystem.getHazardsFromStack(stack);
			for (HazardEntry hazard : hazards) {
				if (hazard.type instanceof HazardTypeRadiation || hazard.type instanceof LCERad) {
					isRadioactive = true;
					break;
				}
			}
			if (isRadioactive)
				stack = new ItemStack(ModBlocks.sellafield_slaked);
			return stack;
		});
		// organics
		fallbackEvaluators.add((stack)->{
			if (stack.getItem() instanceof ItemFood)
				stack = ItemStack.EMPTY;
			else {
				if (stack.getItem() instanceof ItemBlock ib) {
					try {
						Material mat = ib.getBlock().getMaterial(ib.getBlock().getStateFromMeta(stack.getMetadata()));
						if (mat == Material.WOOD || mat == Material.PLANTS || mat == Material.CACTUS || mat == Material.DRAGON_EGG || mat == Material.WEB)
							stack = ItemStack.EMPTY;
					} catch (Exception ignored) {}
				}
			}
			return stack;
		});
		// nuclear fuels
		fallbackEvaluators.add(stack->{
			if (stack.getItem() instanceof ItemRTGPellet rtg)
				return rtg.getDecayItem();
			else if (stack.getItem() instanceof LeafiaRodItem rod) {
				if (rod.newFuel != null) {
					NBTTagCompound data = stack.getTagCompound();
					if (data == null) data = new NBTTagCompound();
					NBTTagCompound newData = data.copy();
					newData.setDouble("depletion",0);
					ItemStack newStack = new ItemStack(rod.newFuel,1,0,newData);
					newStack.setTagCompound(newData);
					return newStack;
				}
			}
			return stack;
		});
	}
	public static boolean isValidInput(ItemStack stack) {
		return isValidInput(stack.getItem(),stack.getMetadata());
	}
	public static boolean isValidInput(Item item,int meta) {
		if (recipes.containsKey(item)) {
			if (recipes.get(item).containsKey(meta))
				return true;
		}
		return false;
	}
	public static ItemStack getOutput(Map<Item,Map<Integer,Pair<Item,Integer>>> map,ItemStack stack) {
		return getOutput(map,stack.getItem(),stack.getMetadata());
	}
	public static ItemStack getOutput(Map<Item,Map<Integer,Pair<Item,Integer>>> map,Item item,int meta) {
		if (map.containsKey(item)) {
			if (map.get(item).containsKey(meta)) {
				Pair<Item,Integer> pair = map.get(item).get(meta);
				if (pair == null)
					return ItemStack.EMPTY;
				return new ItemStack(pair.getA(),1,pair.getB());
			}
		}
		return null;
	}
	public static void addRecipe(Item itemIn,int metaIn,Item itemOut,int metaOut) {
		if (!recipes.containsKey(itemIn))
			recipes.put(itemIn,new HashMap<>());
		Map<Integer,Pair<Item,Integer>> map = recipes.get(itemIn);
		if (map.containsKey(metaIn))
			throw new LeafiaDevFlaw("SOL Blaster recipe conflict! ("+itemIn.getRegistryName()+":"+metaIn+")");
		map.put(metaIn,new Pair<>(itemOut,metaOut));
	}
	public static void addFallback(Item itemIn,int metaIn,Item itemOut,int metaOut) {
		if (!fallback.containsKey(itemIn))
			fallback.put(itemIn,new HashMap<>());
		Map<Integer,Pair<Item,Integer>> map = fallback.get(itemIn);
		map.put(metaIn,new Pair<>(itemOut,metaOut));
	}
	public static void addFallback(Item itemIn,int metaIn) {
		if (!fallback.containsKey(itemIn))
			fallback.put(itemIn,new HashMap<>());
		Map<Integer,Pair<Item,Integer>> map = fallback.get(itemIn);
		map.put(metaIn,null);
	}
	public static ItemStack processItem(ItemStack stack) {
		if (stack.isEmpty()) return stack;
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("ntmContagion")) {
			stack.getTagCompound().removeTag("ntmContagion");
			if (stack.getTagCompound().isEmpty())
				stack.setTagCompound(null);
		}
		ItemStack out = getOutput(recipes,stack);
		if (out != null)
			stack = out;
		else {
			out = getOutput(fallback,stack);
			if (out != null)
				stack = out;
			else {
				for (Function<ItemStack,ItemStack> func : fallbackEvaluators) {
					ItemStack ret = func.apply(stack);
					if (stack != ret && ret != null) {
						stack = ret;
						break;
					}
				}
			}
		}
		return stack;
	}
}
