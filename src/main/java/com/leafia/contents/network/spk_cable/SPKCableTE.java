package com.leafia.contents.network.spk_cable;

import com.hbm.lib.ForgeDirection;
import com.hbm.tileentity.TileEntityLoadedBase;
import com.hbm.uninos.UniNodespace;
import com.leafia.contents.network.spk_cable.uninos.ISPKConductor;
import com.leafia.contents.network.spk_cable.uninos.SPKNet;
import com.leafia.contents.network.spk_cable.uninos.SPKNode;
import net.minecraft.util.ITickable;

/**
 * Copy-pasted TileEntityCableBaseNT
 */
public class SPKCableTE extends TileEntityLoadedBase implements ISPKConductor, ITickable {
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
                UniNodespace.destroyNode(world, pos, SPKNet.THE_NETWORK_PROVIDER);
            }
        }
    }

    public boolean pX = false;
    public boolean pY = false;
    public boolean pZ = false;
    public boolean nX = false;
    public boolean nY = false;
    public boolean nZ = false;
    public boolean isCorner = false;

    @Override
    public boolean canConnectSPK(ForgeDirection dir) {
        return dir != ForgeDirection.UNKNOWN;
    }
}
