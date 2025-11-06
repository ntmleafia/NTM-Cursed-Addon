package com.leafia.contents.machines.powercores.dfc.cable;

import com.hbm.api.energymk2.Nodespace;
import com.hbm.lib.ForgeDirection;
import com.hbm.tileentity.TileEntityLoadedBase;
import com.hbm.uninos.UniNodespace;
import com.leafia.dev.uninos.ISPKConductor;
import com.leafia.dev.uninos.SPKNet;
import com.leafia.dev.uninos.SPKNode;
import net.minecraft.util.ITickable;

/**
 * Copy-pasted TileEntityCableBaseNT
 */
public class TileEntitySPKCable extends TileEntityLoadedBase implements ISPKConductor, ITickable {
    protected SPKNode node;

    @Override
    public void update() {
        if (!world.isRemote) {

            if (this.node == null || this.node.expired) {

                if (this.shouldCreateNode()) {
                    this.node = UniNodespace.getNode(world, pos, SPKNet.THE_NETWORK_PROVIDER);

                    if (this.node == null || this.node.expired) {
                        this.node = this.createNode();
                        UniNodespace.createNode(world, node);
                    }
                }
            }
        }
    }

    public boolean shouldCreateNode() {
        return true;
    }

    @Override
    public void invalidate() {
        super.invalidate();

        if(!world.isRemote) {
            if(this.node != null) {
                Nodespace.destroyNode(world, pos);
            }
        }
    }

    @Override
    public boolean canConnectSPK(ForgeDirection dir) {
        return dir != ForgeDirection.UNKNOWN;
    }
}
