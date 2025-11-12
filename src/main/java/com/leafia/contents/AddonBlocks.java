package com.leafia.contents;

import com.hbm.blocks.ModBlocks;
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

	private static void modifyBlockParams() {
		ModBlocks.dfc_core.setResistance(65000000);
		ModBlocks.dfc_emitter.setResistance(50);
		ModBlocks.dfc_receiver.setResistance(50);
		ModBlocks.dfc_injector.setResistance(50);
		ModBlocks.dfc_stabilizer.setResistance(50);
		ModBlocks.deco_aluminium.setResistance(30);
		ModBlocks.deco_asbestos.setResistance(30);
		ModBlocks.deco_beryllium.setResistance(30);
		ModBlocks.deco_lead.setResistance(30);
		ModBlocks.deco_steel.setResistance(30);
		ModBlocks.deco_tungsten.setResistance(30);
		ModBlocks.deco_titanium.setResistance(30);
		ModBlocks.deco_red_copper.setResistance(30);
		ModBlocks.control_panel_custom.setResistance(30);
		ModBlocks.steel_corner.setResistance(30);
		ModBlocks.steel_wall.setResistance(30);
		ModBlocks.steel_roof.setResistance(30);
		ModBlocks.steel_scaffold.setResistance(30);
		ModBlocks.steel_grate.setResistance(15);
		ModBlocks.steel_grate_wide.setResistance(15);
	}

	public static void preInit(){
		modifyBlockParams();
		AddonBase._initMemberClasses(AddonBlocks.class);
		System.out.println("Blocks: "+ALL_BLOCKS.size());
		for(Block block : ALL_BLOCKS){
			ForgeRegistries.BLOCKS.register(block);
		}
	}
}
