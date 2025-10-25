package com.leafia.contents;

import com.leafia.AddonBase;
import com.leafia.contents.building.pinkdoor.BlockPinkDoor;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class AddonBlocks {
	public static final List<Block> ALL_BLOCKS = new ArrayList();

	public static final Block door_fuckoff = new BlockPinkDoor(Material.WOOD, "door_fuckoff").setHardness(3);

	public static void preInit(){
		AddonBase._initMemberClasses(AddonBlocks.class);
		System.out.println("Blocks: "+ALL_BLOCKS.size());
		for(Block block : ALL_BLOCKS){
			ForgeRegistries.BLOCKS.register(block);
		}
	}
}
