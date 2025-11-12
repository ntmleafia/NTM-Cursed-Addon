package com.leafia.contents;

import com.hbm.blocks.generic.BlockModDoor;
import com.hbm.items.ModItems;
import com.hbm.main.MainRegistry;
import com.leafia.AddonBase;
import com.leafia.contents.building.pinkdoor.ItemPinkDoor;
import com.leafia.contents.gear.wands.ItemWandV;
import com.leafia.contents.machines.powercores.dfc.CrucifixItem;
import com.leafia.dev.items.itembase.AddonItemBaked;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class AddonItems {
	public static final List<Item> ALL_ITEMS = new ArrayList<Item>();
	public static final Item door_fuckoff = new ItemPinkDoor("door_fuckoff").setCreativeTab(null);

	public static final Item fix_tool = new CrucifixItem("fix_tool","leafia/crucifix").setMaxStackSize(1).setCreativeTab(MainRegistry.controlTab);
	public static final Item fix_survival = new CrucifixItem("fix_survival","leafia/fix_survival").setMaxStackSize(1).setCreativeTab(MainRegistry.controlTab);

	public static final Item dfcsh_cable = new AddonItemBaked("dfcsh_cable","leafia/absorber_shrapnels/framecable").setCreativeTab(MainRegistry.controlTab);
	public static final Item dfcsh_core = new AddonItemBaked("dfcsh_core","leafia/absorber_shrapnels/core").setCreativeTab(MainRegistry.controlTab);
	public static final Item dfcsh_corner = new AddonItemBaked("dfcsh_corner","leafia/absorber_shrapnels/framecorner").setCreativeTab(MainRegistry.controlTab);
	public static final Item dfcsh_front = new AddonItemBaked("dfcsh_front","leafia/absorber_shrapnels/framefront").setCreativeTab(MainRegistry.controlTab);
	public static final Item dfcsh_beam = new AddonItemBaked("dfcsh_beam","leafia/absorber_shrapnels/framebeam").setCreativeTab(MainRegistry.controlTab);

	public static final Item wand_v = new ItemWandV("wand_v","wands/wand_v");

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
