// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.network.ff_duct.uninos;

import com.hbm.lib.DirPos;
import com.hbm.uninos.GenNode;
import net.minecraft.util.math.BlockPos;

public class FFNode extends GenNode<FFNet> {
	public FFNode(BlockPos... positions) {
		super(FFNet.PROVIDER,positions);
	}
	@Override
	public FFNode setConnections(DirPos... connections) {
		super.setConnections(connections);
		return this;
	}
}
