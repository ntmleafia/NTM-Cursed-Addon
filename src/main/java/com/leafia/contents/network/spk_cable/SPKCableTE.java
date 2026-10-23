// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-FileCopyrightText: 2025-2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.network.spk_cable;

import com.hbm.lib.ForgeDirection;
import com.leafia.contents.network.NetworkNodeLifecycle;
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

public class SPKCableTE extends TileEntityLoadedBase implements ISPKConductor, ITickable {
    protected SPKNode node;

    @Override
    public void update() {
        if (!world.isRemote) {
            if ((node == null || node.expired) && shouldCreateNode()) {
                node = NetworkNodeLifecycle.attach(world, pos, SPKNet.THE_NETWORK_PROVIDER, this::createNode);
            }
            return;
        }
        if (!isCorner) return;
        var cursor = new BlockPos.MutableBlockPos();
        for (var link : links) {
            if (link.link != null) validateLink(link);
            cursor.setPos(pos);
            while (true) {
                cursor.move(link.direction);
                var tile = world.getTileEntity(cursor);
                if (tile == null || tile.isInvalid()) break;
                if (tile instanceof SPKCableTE cable) {
                    if (!cable.isCorner) continue;
                    if (link.link == cable.getPos() && !link.nonCable) break;
                    if (!cable.getFromFacing(link.direction.getOpposite())) continue;
                    link.setLink(cable);
                    break;
                }
                if (tile instanceof ISPKReceiver) {
                    if (link.link != tile.getPos() || !link.nonCable) link.setLinkNonCable(tile.getPos());
                }
                break;
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void validateLink(EffectLink link) {
        if (!getFromFacing(link.direction)) {
            link.reset();
            return;
        }
        var tile = world.getTileEntity(link.link);
        if (tile == null || tile.isInvalid()) {
            link.reset();
        } else if (!link.nonCable && (!(tile instanceof SPKCableTE cable)
                || !pos.equals(cable.links[link.direction.getOpposite().ordinal()].link))) {
            link.reset();
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
            direction = EnumFacing.VALUES[i];
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
    public EffectLink[] links = new EffectLink[EnumFacing.VALUES.length];
    public SPKCableTE() {
        for (int i = 0; i < EnumFacing.VALUES.length; i++)
            links[i] = new EffectLink(this,i);
    }

    @Override
    public boolean canConnectSPK(ForgeDirection dir) {
        return dir != ForgeDirection.UNKNOWN;
    }
}
