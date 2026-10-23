// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.network;

import com.hbm.lib.DirPos;
import com.hbm.lib.ForgeDirection;
import com.hbm.uninos.GenNode;
import com.hbm.uninos.INetworkProvider;
import com.hbm.uninos.NodeNet;
import com.hbm.uninos.UniNodespace;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Supplier;

public final class NetworkNodeLifecycle {
    private static final ForgeDirection[] CONNECTION_ORDER = {
            ForgeDirection.EAST, ForgeDirection.WEST, ForgeDirection.UP,
            ForgeDirection.DOWN, ForgeDirection.SOUTH, ForgeDirection.NORTH
    };

    private NetworkNodeLifecycle() {
    }

    public static <N extends NodeNet<R, P, L, N>, L extends GenNode<N>, R, P> L attach(
            World world, BlockPos position, INetworkProvider<N> provider, Supplier<L> factory) {
        var registered = UniNodespace.getNode(world, position, provider);
        if (registered != null && !registered.expired) return registered;
        var replacement = factory.get();
        UniNodespace.createNode(world, replacement);
        return replacement;
    }

    public static DirPos[] adjacentConnections(BlockPos position) {
        var connections = new DirPos[CONNECTION_ORDER.length];
        for (var index = 0; index < connections.length; index++) {
            var direction = CONNECTION_ORDER[index];
            connections[index] = new DirPos(position.offset(direction.toEnumFacing()), direction);
        }
        return connections;
    }
}
