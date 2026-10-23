// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.building.sign;

import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.container_utility.LeafiaPacketReceiver;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class SignTE extends TileEntity implements LeafiaPacketReceiver {
	public EnumDyeColor color = EnumDyeColor.WHITE;
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		color = EnumDyeColor.byMetadata(compound.getByte("color"));
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setByte("color",(byte)color.getMetadata());
		return super.writeToNBT(compound);
	}
	@Override
	public String getPacketIdentifier() {
		return "SIGN";
	}
	@Override
	public void onReceivePacketLocal(byte key,Object value) {
		if (key == 0)
			color = EnumDyeColor.byMetadata((int)value);
	}
	@Override
	public void onReceivePacketServer(byte key,Object value,EntityPlayer plr) { }
	@Override
	public void onPlayerValidate(EntityPlayer plr) {
		LeafiaPacket._start(this).__write(0,color.getMetadata()).__sendToClient(plr);
	}
}