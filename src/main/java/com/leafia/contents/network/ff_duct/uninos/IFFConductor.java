// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.network.ff_duct.uninos;

import com.hbm.lib.DirPos;
import com.hbm.lib.Library;
import net.minecraft.tileentity.TileEntity;

public interface IFFConductor extends IFFConnector {
	default FFNode createNode() {
		TileEntity tile = (TileEntity) this;
		int x = tile.getPos().getX();
		int y = tile.getPos().getY();
		int z = tile.getPos().getZ();
		return new FFNode(tile.getPos()).setConnections(
				new DirPos(x + 1, y, z, Library.POS_X),
				new DirPos(x - 1, y, z, Library.NEG_X),
				new DirPos(x, y + 1, z, Library.POS_Y),
				new DirPos(x, y - 1, z, Library.NEG_Y),
				new DirPos(x, y, z + 1, Library.POS_Z),
				new DirPos(x, y, z - 1, Library.NEG_Z)
		);
	}
}
