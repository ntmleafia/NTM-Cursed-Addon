package com.leafia.init;

import com.hbm.render.item.ItemRenderBase;
import com.leafia.contents.AddonBlocks;
import com.leafia.contents.network.spk_cable.SPKCableRender.SPKCableItemRender;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

import java.util.HashMap;
import java.util.Map.Entry;

public class ItemRendererInit {
	public static HashMap<Item,ItemRenderBase> renderers = new HashMap<>();
	public static void preInit() {
		register(AddonBlocks.spk_cable,new SPKCableItemRender());
	}
	private static void register(Item item,ItemRenderBase renderer) { renderers.put(item,renderer); }
	private static void register(Block block,ItemRenderBase renderer) { renderers.put(Item.getItemFromBlock(block),renderer); }
	public static void apply() {
		for (Entry<Item,ItemRenderBase> entry : renderers.entrySet()) {
			entry.getKey().setTileEntityItemStackRenderer(entry.getValue());
		}
	}
}
