package com.leafia.unsorted;

import com.hbm.blocks.generic.BlockMeta;
import com.leafia.contents.AddonBlocks.LegacyBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSand.EnumType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class LeafiaBlockReplacer {
	public static final Map<String,Block> replacementMap = new HashMap<>();
	public static IBlockState withProperties(Block newBlock,IBlockState missingBlock) {
		/*IBlockState state = newBlock.getDefaultState();
		for (IProperty<?> property : missingBlock.getPropertyKeys())
			copyProperty(missingBlock,state,property);
		return state;*/
		return newBlock.getStateFromMeta(missingBlock.getBlock().getMetaFromState(missingBlock));
	}
	public interface SpecialReplacer extends BiFunction<String,IBlockState,IBlockState> { }
	public static final Map<String,SpecialReplacer> specialReplacer = new HashMap<>();
	static {
		SpecialReplacer wasteReplacer = (reg,state)->{
			Block newBlock = null;
			IBlockState vanillaBlock = null;
			switch(reg) {
				case "hbm:waste_dirt" -> {
					newBlock = LegacyBlocks.waste_dirt;
					vanillaBlock = Blocks.DIRT.getDefaultState();
				}
				case "hbm:waste_gravel" -> {
					newBlock = LegacyBlocks.waste_gravel;
					vanillaBlock = Blocks.GRAVEL.getDefaultState();
				}
				case "hbm:waste_sand" -> {
					newBlock = LegacyBlocks.waste_sand;
					vanillaBlock = Blocks.SAND.getDefaultState();
				}
				case "hbm:waste_sandstone" -> {
					newBlock = LegacyBlocks.waste_sandstone;
					vanillaBlock = Blocks.SANDSTONE.getDefaultState();
				}
				case "hbm:waste_sand_red" -> {
					newBlock = LegacyBlocks.waste_sand_red;
					vanillaBlock = Blocks.SAND.getDefaultState().withProperty(BlockSand.VARIANT,EnumType.RED_SAND);
				}
				case "hbm:waste_red_sandstone" -> {
					newBlock = LegacyBlocks.waste_red_sandstone;
					vanillaBlock = Blocks.RED_SANDSTONE.getDefaultState();
				}
				case "hbm:waste_terracotta" -> {
					newBlock = LegacyBlocks.waste_terracotta;
					vanillaBlock = Blocks.HARDENED_CLAY.getDefaultState();
				}
			}
			if (newBlock == null) return state;
			if (state.getBlock().getMetaFromState(state) < 2)
				return vanillaBlock;
			return withProperties(newBlock,state);
		};
		replacementMap.put("hbm:waste_ice",LegacyBlocks.waste_ice);
		replacementMap.put("hbm:waste_snow",LegacyBlocks.waste_snow);
		replacementMap.put("hbm:waste_snow_block",LegacyBlocks.waste_snow_block);
		specialReplacer.put("hbm:waste_dirt",wasteReplacer);
		specialReplacer.put("hbm:waste_gravel",wasteReplacer);
		specialReplacer.put("hbm:waste_sand",wasteReplacer);
		specialReplacer.put("hbm:waste_sandstone",wasteReplacer);
		specialReplacer.put("hbm:waste_sand_red",wasteReplacer);
		specialReplacer.put("hbm:waste_red_sandstone",wasteReplacer);
		specialReplacer.put("hbm:waste_terracotta",wasteReplacer);
		replacementMap.put("hbm:ore_coal_oil",LegacyBlocks.ore_coal_oil);
		replacementMap.put("hbm:ore_coal_oil_burning",LegacyBlocks.ore_coal_oil_burning);
	}
	public static IBlockState replaceBlock(IBlockState missingBlock) {
		ResourceLocation reg = missingBlock.getBlock().getRegistryName();
		//System.out.println("LEAFIA: Replacing block "+reg.toString());
		if (specialReplacer.containsKey(reg.toString())) {
			BiFunction<String,IBlockState,IBlockState> processor = specialReplacer.get(reg.toString());
			return processor.apply(reg.toString(),missingBlock);
		} else {
			Block newBlock = replacementMap.get(reg.toString());
			if (newBlock == null)
				return missingBlock;
			return withProperties(newBlock,missingBlock);
		}
	}
	/// thanks https://forums.minecraftforge.net/topic/117047-copy-all-property-values-from-one-blockstate-to-another/
	static <T extends Comparable<T>> IBlockState copyProperty(IBlockState from,IBlockState to,IProperty<T> property) {
		return from.withProperty(property,from.getValue(property));
	}
	/*
	public static final MethodHandle add = MethodHandleHelper.findVirtual(ForgeRegistry.class,"add",MethodType.methodType(int.class,int.class,IForgeRegistryEntry.class));
	public static void replace(ResourceLocation key,int id,ForgeRegistry<? extends IForgeRegistryEntry<?>> reg) {
		if (reg.getRegistrySuperType().equals(Block.class)) {
			try {
				/*Block block = ModBlocks.fissure_bomb;
				int realId = (int)add.invokeExact(reg,id,block);
				if (realId != id)
					FMLLog.log.warn("WARNING/LEAFIACORE: Registry {}: Object did not get ID it asked for. Name: {} Expected: {} Got: {}", reg.getRegistrySuperType().getSimpleName(), key, id, realId);
				 */ /*
			} catch (Throwable e) {
				throw new LeafiaDevFlaw(e);
			}
		}
	}
	public static IForgeRegistryEntry<?> getDummy(ResourceLocation key,IForgeRegistryEntry<?> dummy,ForgeRegistry<? extends IForgeRegistryEntry<?>> reg) {
		if (reg.getRegistrySuperType().equals(Block.class)) {
			System.out.println("FUCK YOU "+dummy.getClass().getName());
			return dummy;
		}
		return dummy;
	}*/
}
