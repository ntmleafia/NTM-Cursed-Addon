// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.dev.gui;

import com.leafia.dev.LeafiaDebug;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class DummyContainer extends Container {
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}
	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		LeafiaDebug.debugLog(playerIn.world,playerIn.world.isRemote);
		super.onContainerClosed(playerIn);
	}
}
