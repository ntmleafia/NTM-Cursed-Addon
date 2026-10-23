// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.interfaces;

import net.minecraft.block.Block;

public interface IMixinTileEntityControlPanel {
	Block getSkin();
	void setSkin(Block b);
	void sendSkinPackets();
}
