package com.leafia.dev.blocks.blockbase;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BlockMeta;
import com.hbm.render.block.BlockBakeFrame;
import com.leafia.contents.AddonBlocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class AddonBlockMeta extends BlockMeta {
	public AddonBlockMeta(Material m,String s) {
		super(m, s);
		ModBlocks.ALL_BLOCKS.remove(this);
		AddonBlocks.ALL_BLOCKS.add(this);
	}

	public AddonBlockMeta(Material m, String s, BlockBakeFrame... frame) {
		super(m, s,frame);
		ModBlocks.ALL_BLOCKS.remove(this);
		AddonBlocks.ALL_BLOCKS.add(this);
	}

	public AddonBlockMeta(Material mat,SoundType type,String s,short metaCount) {
		super(mat, type, s,metaCount);
		ModBlocks.ALL_BLOCKS.remove(this);
		AddonBlocks.ALL_BLOCKS.add(this);
	}


	public AddonBlockMeta(Material m, String s, short metaCount, boolean showMetaInCreative) {
		super(m, s,metaCount,showMetaInCreative);
		ModBlocks.ALL_BLOCKS.remove(this);
		AddonBlocks.ALL_BLOCKS.add(this);
	}

	public AddonBlockMeta(Material m, String s, short metaCount, boolean showMetaInCreative, BlockBakeFrame... frames) {
		super(m, s,metaCount,showMetaInCreative,frames);
		ModBlocks.ALL_BLOCKS.remove(this);
		AddonBlocks.ALL_BLOCKS.add(this);
	}


	public AddonBlockMeta(Material mat, SoundType type, String s, BlockBakeFrame... blockFrames) {
		super(mat, type, s, blockFrames);
		ModBlocks.ALL_BLOCKS.remove(this);
		AddonBlocks.ALL_BLOCKS.add(this);
	}

	public AddonBlockMeta(Material mat, SoundType type, String s, String... simpleModelTextures) {
		super(mat, type, s, simpleModelTextures);
		ModBlocks.ALL_BLOCKS.remove(this);
		AddonBlocks.ALL_BLOCKS.add(this);
	}
}
