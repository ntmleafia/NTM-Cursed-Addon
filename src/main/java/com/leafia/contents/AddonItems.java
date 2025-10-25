package com.leafia.contents;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BlockModDoor;
import com.hbm.items.ModItems;
import com.hbm.items.tool.ItemModDoor;
import com.hbm.main.MainRegistry;
import com.leafia.AddonBase;
import com.leafia.contents.building.pinkdoor.ItemPinkDoor;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class AddonItems {
	public static final List<Item> ALL_ITEMS = new ArrayList<Item>();
	public static final Item door_fuckoff = new ItemPinkDoor("door_fuckoff").setCreativeTab(null);

	public static void preInit(){
		AddonBase._initMemberClasses(ModItems.class);

		for(Item item : ALL_ITEMS){
			ForgeRegistries.ITEMS.register(item);
		}

		for(Block block : AddonBlocks.ALL_BLOCKS){
			/*if(block instanceof IItemHazard){
				ForgeRegistries.ITEMS.register(new ItemBlockHazard(block).setRegistryName(block.getRegistryName()));
			} else if(block == ModBlocks.block_scrap){
				ForgeRegistries.ITEMS.register(new ItemBlockScrap(block).setRegistryName(block.getRegistryName()));
			} else */if(block instanceof BlockModDoor){
			} else {
				ForgeRegistries.ITEMS.register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
			}
		}
	}
}
