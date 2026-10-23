// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-FileCopyrightText: 2025-2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.network.pipe_amat;

import com.leafia.contents.network.NetworkNodeLifecycle;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.lib.ForgeDirection;
import com.hbm.tileentity.network.TileEntityPipeBaseNT;
import com.hbm.uninos.UniNodespace;
import com.leafia.contents.network.pipe_amat.uninos.AmatNet;
import com.leafia.contents.network.pipe_amat.uninos.AmatNode;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.container_utility.LeafiaPacketReceiver;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;

public class AmatDuctTE extends TileEntityPipeBaseNT implements LeafiaPacketReceiver {
	@Override
	public boolean canConnect(FluidType type,ForgeDirection dir) {
		TileEntity te = world.getTileEntity(pos.offset(dir.toEnumFacing()));
		if ((te instanceof TileEntityPipeBaseNT) && !(te instanceof AmatDuctTE))
			return false;
		return super.canConnect(type,dir);
	}

	protected AmatNode node;

    @Override
    public void update() {
        if (world.isRemote) {
            if (++timeout > 5) ductPower = -1;
            return;
        }
        if (canUpdate()) ensureAmatNode();
    }

    protected void ensureAmatNode() {
        if (node != null && !node.expired) return;
        if (!shouldCreateNode()) return;
        node = NetworkNodeLifecycle.attach(world, pos, AmatNet.getProvider(type), () -> createNodeAmat(type));
    }

    public AmatNode createNodeAmat(FluidType type) {
        var created = new AmatNode(AmatNet.getProvider(type), pos);
        return created.setConnections(NetworkNodeLifecycle.adjacentConnections(pos));
    }
	public void setType(FluidType type) {
		FluidType prev = this.type;
		this.type = type;
		this.markDirty();

		if (world instanceof WorldServer) {
			IBlockState state = world.getBlockState(pos);
			world.notifyBlockUpdate(pos, state, state, 3);
			world.markBlockRangeForRenderUpdate(pos, pos);
		}

		UniNodespace.destroyNode(world, pos, AmatNet.getProvider(prev));

		if(this.node != null) {
			this.node = null;
		}
	}
	@Override
	public void invalidate() {
		super.invalidate();

		if(!world.isRemote) {
			if(this.node != null) {
				UniNodespace.destroyNode(world, pos, AmatNet.getProvider(type));
			}
		}
	}

	@Override
	public String getPacketIdentifier() {
		return "AMAT_DUCT";
	}

	long ductPower = -1;
	int timeout = 0;

	@Override
	public void onReceivePacketLocal(byte key,Object value) {
		if (key == 31) {
			ductPower = (long)value;
			timeout = 0;
		}
	}

	/// unused
	@Override
	public void onReceivePacketServer(byte key,Object value,EntityPlayer plr) {
		long power = 0;
		if (node != null && node.net != null)
			power = node.net.power;
		LeafiaPacket._start(this).__write(31,power).__sendToClient(plr);
	}

	/// signals with 0 data will be interpreted as validation packet
	@Override
	public void onPlayerValidate(EntityPlayer plr) {
	}
}
