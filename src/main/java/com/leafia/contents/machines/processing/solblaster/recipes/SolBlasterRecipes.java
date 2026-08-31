package com.leafia.contents.machines.processing.solblaster.recipes;

import com.custom_hbm.util.LCETuple.Pair;
import com.hbm.blocks.ModBlocks;
import com.hbm.hazard.HazardEntry;
import com.hbm.hazard.HazardSystem;
import com.hbm.hazard.type.HazardTypeRadiation;
import com.hbm.interfaces.Spaghetti;
import com.hbm.inventory.OreDictManager;
import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemRTGPellet;
import com.leafia.contents.AddonItems;
import com.leafia.contents.AddonItems.Resources;
import com.leafia.contents.control.fuel.nuclearfuel.LeafiaRodItem;
import com.leafia.init.AddonMats;
import com.leafia.init.AddonOreDict;
import com.leafia.init.hazards.types.LCERad;
import com.llib.exceptions.LeafiaDevFlaw;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

import java.util.*;
import java.util.function.Function;

public class SolBlasterRecipes {
	public static Map<ItemOrOreDict,Map<Integer,Pair<Item,Integer>>> recipes = new HashMap<>();
	public static Map<ItemOrOreDict,Map<Integer,Pair<Item,Integer>>> fallback = new HashMap<>();
	public static Item item(Block block) {
		return Item.getItemFromBlock(block);
	}
	public static void register() {
		addRecipe(OreDictManager.SA326.ingot(),ModItems.nugget_euphemium,0);
		addRecipe(OreDictManager.SA327.ingot(),ModItems.nugget_euphemium,0);
		addRecipe(OreDictManager.SA326.block(),ModItems.ingot_euphemium,0);
		addRecipe(OreDictManager.SA327.block(),ModItems.ingot_euphemium,0);
		addRecipe(AddonOreDict.FS.ingot(),Resources.ingot_chydalium,0);
		addRecipe(AddonOreDict.FS.billet(),Resources.billet_chydalium,0);
		addRecipe(AddonOreDict.FSALLOY.ingot(),Resources.ingot_mysticite,0);
		addRecipe(AddonOreDict.FSALLOY.plate(),Resources.plate_mysticite,0);
		addFallback(AddonOreDict.FSALLOY.lightReceiver(),ModItems.part_receiver_light,AddonMats.MAT_MYSTICITE.id); // supposed to be secret lmao
		addFallback(AddonItems.am_rifle_cell_fissite,0,AddonItems.am_rifle_cell_mysticite,0);

		addFallback(ModItems.egg_balefire_shard,0);
		addFallback(ModItems.egg_balefire,0);
	}
	public static class ItemOrOreDict {
		public final Item item;
		public final String dict;
		public final boolean isOreDict;
		public ItemOrOreDict(Item item) {
			this.item = item;
			dict = null;
			isOreDict = false;
		}
		public ItemOrOreDict(String dict) {
			item = null;
			this.dict = dict;
			isOreDict = true;
		}
		@Override
		public int hashCode() {
			if (isOreDict)
				return dict.hashCode();
			return item.hashCode();
		}
		@Override
		@Spaghetti("i want to explode")
		public boolean equals(Object obj) {
			if (obj instanceof ItemOrOreDict other) {
				List<ComparableStack> a = new ArrayList<>();
				List<ComparableStack> b = new ArrayList<>();
				if (isOreDict) {
					for (ItemStack ore : OreDictionary.getOres(dict))
						a.add(new ComparableStack(ore));
				} else
					a.add(new ComparableStack(item,1,0));
				if (other.isOreDict) {
					for (ItemStack ore : OreDictionary.getOres(other.dict))
						b.add(new ComparableStack(ore));
				} else
					b.add(new ComparableStack(item,1,0));
				for (ComparableStack c : a) {
					for (ComparableStack d : b) {
						if (c.equals(d))
							return true;
					}
				}
				return false;
			}
			return super.equals(obj);
		}
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
		ItemOrOreDict iod = getValidIOD(recipes,item,meta);
		if (recipes.containsKey(iod)) {
			if (iod.isOreDict) meta = 0;
			if (recipes.get(iod).containsKey(meta))
				return true;
		}
		return false;
	}
	@Spaghetti("tf is this")
	public static ItemOrOreDict getValidIOD(Map<ItemOrOreDict,Map<Integer,Pair<Item,Integer>>> map,Item item,int meta) {
		if (item == Items.AIR || item == null) return null;
		ItemOrOreDict iod = new ItemOrOreDict(item);
		if (!map.containsKey(iod)) {
			int[] ids = OreDictionary.getOreIDs(new ItemStack(item,1,meta));
			for (int id : ids) {
				ItemOrOreDict iod1 = new ItemOrOreDict(OreDictionary.getOreName(id));
				if (map.containsKey(iod1)) {
					iod = iod1;
					break;
				}
			}
		}
		return iod;
	}
	public static ItemStack getOutput(Map<ItemOrOreDict,Map<Integer,Pair<Item,Integer>>> map,ItemStack stack) {
		return getOutput(map,stack.getItem(),stack.getMetadata());
	}
	public static ItemStack getOutput(Map<ItemOrOreDict,Map<Integer,Pair<Item,Integer>>> map,Item item,int meta) {
		ItemOrOreDict iod = getValidIOD(map,item,meta);
		if (map.containsKey(iod)) {
			if (iod.isOreDict) meta = 0;
			if (map.get(iod).containsKey(meta)) {
				Pair<Item,Integer> pair = map.get(iod).get(meta);
				if (pair == null)
					return ItemStack.EMPTY;
				return new ItemStack(pair.getA(),1,pair.getB());
			}
		}
		return null;
	}
	public static void addRecipe(Item itemIn,int metaIn,Item itemOut,int metaOut) {
		ItemOrOreDict iod = new ItemOrOreDict(itemIn);
		if (!recipes.containsKey(iod))
			recipes.put(iod,new HashMap<>());
		Map<Integer,Pair<Item,Integer>> map = recipes.get(iod);
		if (map.containsKey(metaIn))
			throw new LeafiaDevFlaw("SOL Blaster recipe conflict! ("+itemIn.getRegistryName()+":"+metaIn+")");
		map.put(metaIn,new Pair<>(itemOut,metaOut));
	}
	public static void addFallback(Item itemIn,int metaIn,Item itemOut,int metaOut) {
		ItemOrOreDict iod = new ItemOrOreDict(itemIn);
		if (!fallback.containsKey(iod))
			fallback.put(iod,new HashMap<>());
		Map<Integer,Pair<Item,Integer>> map = fallback.get(iod);
		map.put(metaIn,new Pair<>(itemOut,metaOut));
	}
	public static void addFallback(Item itemIn,int metaIn) {
		ItemOrOreDict iod = new ItemOrOreDict(itemIn);
		if (!fallback.containsKey(iod))
			fallback.put(iod,new HashMap<>());
		Map<Integer,Pair<Item,Integer>> map = fallback.get(iod);
		map.put(metaIn,null);
	}
	public static void addRecipe(String itemIn,Item itemOut,int metaOut) {
		ItemOrOreDict iod = new ItemOrOreDict(itemIn);
		if (!recipes.containsKey(iod))
			recipes.put(iod,new HashMap<>());
		Map<Integer,Pair<Item,Integer>> map = recipes.get(iod);
		if (map.containsKey(0))
			throw new LeafiaDevFlaw("SOL Blaster recipe conflict! ("+itemIn+")");
		map.put(0,new Pair<>(itemOut,metaOut));
	}
	public static void addFallback(String itemIn,Item itemOut,int metaOut) {
		ItemOrOreDict iod = new ItemOrOreDict(itemIn);
		if (!fallback.containsKey(iod))
			fallback.put(iod,new HashMap<>());
		Map<Integer,Pair<Item,Integer>> map = fallback.get(iod);
		map.put(0,new Pair<>(itemOut,metaOut));
	}
	public static void addFallback(String itemIn) {
		ItemOrOreDict iod = new ItemOrOreDict(itemIn);
		if (!fallback.containsKey(iod))
			fallback.put(iod,new HashMap<>());
		Map<Integer,Pair<Item,Integer>> map = fallback.get(iod);
		map.put(0,null);
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
