// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.interfaces;

import com.custom_hbm.util.LCETuple.Pair;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.container_utility.LeafiaPacketReceiver;
import net.minecraft.item.Item;
import net.minecraftforge.items.ItemStackHandler;

public interface IMixinTileEntityCrate extends LeafiaPacketReceiver {
	ItemStackHandler leafia$getIconHandler();
	String[] leafia$verticalLabels();
	String leafia$upperLabel();
	String leafia$middleLabel();
	String leafia$lowerLabel();
	Pair<Item,Integer> leafia$icon();
	@Override
	default String getPacketIdentifier() {
		return "CRATE";
	}
	@Override
	default double affectionRange() {
		return 64;
	}
	LeafiaPacket generateSyncPacket();
	void leafia$setUpperLabel(String label);
	void leafia$setMiddleLabel(String label);
	void leafia$setLowerLabel(String label);
}
