// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.network.ff_duct.utility;

import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.container_utility.LeafiaPacketReceiver;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import java.util.List;

public abstract class FFDuctUtilityTEBase extends TileEntity implements LeafiaPacketReceiver {
	FluidType type = Fluids.NONE;
	public void setType(FluidType type) {
		this.type = type;
		sendTypeUpdatePacket();
	}
	public FluidType getType() {
		return type;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		type = Fluids.fromID(compound.getInteger("fluid"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setInteger("fluid",type.getID());
		return super.writeToNBT(compound);
	}

	public void addInfo(List<String> info) {}

	@Override
	public void onReceivePacketLocal(byte key,Object value) {
		if (key == 31)
			type = Fluids.fromID((int)value);
	}

	@Override
	public void onReceivePacketServer(byte key,Object value,EntityPlayer plr) { }

	@Override
	public void onPlayerValidate(EntityPlayer plr) {
		LeafiaPacket._start(this).__write(31,type.getID()).__sendToClient(plr);
	}

	public void sendTypeUpdatePacket() {
		LeafiaPacket._start(this).__write(31,type.getID()).__sendToAffectedClients();
	}
}
