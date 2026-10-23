// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.network.pipe_amat.uninos;

import com.hbm.lib.DirPos;
import com.hbm.uninos.GenNode;
import com.hbm.uninos.INetworkProvider;
import net.minecraft.util.math.BlockPos;

public class AmatNode extends GenNode<AmatNet> {
	public AmatNode(INetworkProvider<AmatNet> provider,BlockPos... positions) {
		super(provider,positions);
	}
	@Override
	public AmatNode setConnections(DirPos... connections) {
		super.setConnections(connections);
		return this;
	}
}
