package com.leafia.overwrite_contents.interfaces;

import net.minecraft.block.Block;

public interface IMixinTileEntityControlPanel {
	Block getSkin();
	void setSkin(Block b);
	void sendSkinPackets();
}
