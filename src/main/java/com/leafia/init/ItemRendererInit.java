package com.leafia.init;

import com.hbm.blocks.ModBlocks;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.render.tileentity.IItemRendererProvider;
import com.leafia.contents.AddonBlocks;
import com.leafia.contents.AddonItems;
import com.leafia.contents.machines.powercores.dfc.render.DFCComponentRender;
import com.leafia.contents.network.spk_cable.SPKCableRender.SPKCableItemRender;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

import java.util.*;
import java.util.Map.Entry;

public class ItemRendererInit {
	public static HashMap<Item,ItemRenderBase> renderers = new HashMap<>();
	public static List<Item> fixFuckingLocations = new ArrayList<>();
	public static void preInit() {
		fix(AddonItems.door_fuckoff);
		// DFC
		register(true,AddonBlocks.spk_cable,new SPKCableItemRender());
		register(false,ModBlocks.dfc_emitter,new DFCComponentRender());
		register(false,ModBlocks.dfc_receiver,new DFCComponentRender());
		register(false,ModBlocks.dfc_injector,new DFCComponentRender());
		register(false,ModBlocks.dfc_stabilizer,new DFCComponentRender());
		register(true,AddonBlocks.dfc_cemitter,new DFCComponentRender());
		register(true,AddonBlocks.dfc_exchanger,new DFCComponentRender());
		register(true,AddonBlocks.dfc_reinforced,new DFCComponentRender());
	}
	private static void register(boolean doFix,Item item,ItemRenderBase renderer) { if (doFix) fixFuckingLocations.add(item); renderers.put(item,renderer); }
	private static void register(boolean doFix,Block block,ItemRenderBase renderer) { if (doFix) fixFuckingLocations.add(Item.getItemFromBlock(block)); renderers.put(Item.getItemFromBlock(block),renderer); }
	private static void register(boolean doFix,Item item,IItemRendererProvider provider) { if (doFix) fixFuckingLocations.add(item); renderers.put(item,provider.getRenderer(item)); }
	private static void register(boolean doFix,Block block,IItemRendererProvider provider) { if (doFix) fixFuckingLocations.add(Item.getItemFromBlock(block)); renderers.put(Item.getItemFromBlock(block),provider.getRenderer(Item.getItemFromBlock(block))); }
	private static void fix(Item item) { fixFuckingLocations.add(item); }
	private static void fix(Block block) { fixFuckingLocations.add(Item.getItemFromBlock(block)); }
	public static void apply() {
		for (Entry<Item,ItemRenderBase> entry : renderers.entrySet()) {
			entry.getKey().setTileEntityItemStackRenderer(entry.getValue());
		}
	}
}
