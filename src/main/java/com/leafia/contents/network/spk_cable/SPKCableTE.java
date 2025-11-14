package com.leafia.contents.network.spk_cable;

import com.hbm.lib.ForgeDirection;
import com.hbm.tileentity.TileEntityLoadedBase;
import com.hbm.uninos.UniNodespace;
import com.leafia.contents.network.spk_cable.uninos.ISPKConductor;
import com.leafia.contents.network.spk_cable.uninos.ISPKReceiver;
import com.leafia.contents.network.spk_cable.uninos.SPKNet;
import com.leafia.contents.network.spk_cable.uninos.SPKNode;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
        } else if (isCorner) {
            for (EffectLink link : links) {
                boolean allow = getFromFacing(link.direction);
                if (link.link != null) {
                    if (!allow) link.reset();
                    else {
                        TileEntity te = world.getTileEntity(link.link);
                        if (te != null && !te.isInvalid()) {
                            if (te instanceof SPKCableTE && !link.nonCable) {
                                SPKCableTE cab = (SPKCableTE)te;
                                if (!pos.equals(cab.links[link.direction.getOpposite().ordinal()].link))
                                    link.reset();
                            } else if (!link.nonCable)
                                link.reset();
                        } else
                            link.reset();
                    }
                }
                for (int d = 1; true; d++) {
                    TileEntity te = world.getTileEntity(pos.offset(link.direction,d));
                    if (te instanceof SPKCableTE && !te.isInvalid()) {
                        SPKCableTE cab = (SPKCableTE)te;
                        if (!cab.isCorner) continue;
                        if (link.link == cab.getPos() && !link.nonCable) break;
                        if (cab.getFromFacing(link.direction.getOpposite())) {
                            link.setLink(cab);
                            break;
                        }
                    } else if (te instanceof ISPKReceiver && !te.isInvalid()) {
                        if (link.link == te.getPos() && link.nonCable) break;
                        link.setLinkNonCable(te.getPos());
                        break;
                    } else break;
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
    @SideOnly(Side.CLIENT)
    protected boolean getFromFacing(EnumFacing facing) {
        switch(facing) {
            case EAST: return pX;
            case UP: return pY;
            case SOUTH: return pZ;
            case WEST: return nX;
            case DOWN: return nY;
            case NORTH: return nZ;
            default: return false;
        }
    }

    public static class EffectLink {
        BlockPos link = null;
        boolean emit = false;
        boolean nonCable = false;
        final EnumFacing direction;
        final SPKCableTE self;
        EffectLink(SPKCableTE self,int i) {
            this.self = self;
            direction = EnumFacing.values()[i];
        }
        public void reset() {
            link = null;
            emit = false;
            nonCable = false;
        }
        public void setLink(SPKCableTE other) {
            reset();
            link = other.getPos();
            EffectLink lnk = other.links[direction.getOpposite().ordinal()];
            if (lnk.link == null || !lnk.link.equals(self.getPos())) {
                lnk.reset();
                lnk.link = self.getPos();
                emit = true;
            }
        }
        public void setLinkNonCable(BlockPos pos) {
            reset();
            link = pos;
            nonCable = true;
            emit = true;
        }
    }
    public EffectLink[] links = new EffectLink[EnumFacing.values().length];
    public SPKCableTE() {
        for (int i = 0; i < EnumFacing.values().length; i++)
            links[i] = new EffectLink(this,i);
    }

    @Override
    public boolean canConnectSPK(ForgeDirection dir) {
        return dir != ForgeDirection.UNKNOWN;
    }
}
