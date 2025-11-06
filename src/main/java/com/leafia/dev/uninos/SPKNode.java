package com.leafia.dev.uninos;

import com.hbm.lib.DirPos;
import com.hbm.uninos.GenNode;
import net.minecraft.util.math.BlockPos;

public class SPKNode extends GenNode<SPKNet> {

    public SPKNode(BlockPos... positions) {
        super(SPKNet.THE_NETWORK_PROVIDER, positions);
        this.positions = positions;
    }

    @Override
    public SPKNode setConnections(DirPos... connections) {
        super.setConnections(connections);
        return this;
    }
}
