// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.debug.fluid_voider;

import com.hbm.api.fluidmk2.IFluidStandardReceiverMK2;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.hbm.lib.ForgeDirection;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.container_utility.LeafiaPacketReceiver;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import org.jetbrains.annotations.NotNull;

public class VoiderTE extends TileEntity implements ITickable, IFluidStandardReceiverMK2, LeafiaPacketReceiver {
	boolean loaded = true;
	@Override
	public void onChunkUnload() {
		loaded = false;
		super.onChunkUnload();
	}
	public FluidTankNTM buffer = new FluidTankNTM(Fluids.SPENTSTEAM,Integer.MAX_VALUE);
	@Override
	public @NotNull FluidTankNTM[] getReceivingTanks() {
		return new FluidTankNTM[]{ buffer };
	}
	@Override
	public FluidTankNTM[] getAllTanks() {
		return new FluidTankNTM[]{ buffer };
	}
	@Override
	public boolean isLoaded() {
		return loaded;
	}
	@Override
	public void update() {
		if (!world.isRemote) {
			for (EnumFacing value : EnumFacing.values())
				trySubscribe(buffer.getTankType(),world,pos.offset(value),ForgeDirection.getOrientation(value));
			LeafiaPacket._start(this)
					.__write(0,buffer.getTankType().getName())
					.__write(1,buffer.getFill())
					.__sendToAffectedClients();
			buffer.setFill(0);
		}
	}
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		buffer.readFromNBT(compound,"buffer");
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		buffer.writeToNBT(compound,"buffer");
		return super.writeToNBT(compound);
	}
	@Override
	public String getPacketIdentifier() {
		return "DEBUG_VOIDER";
	}
	@Override
	public void onReceivePacketLocal(byte key,Object value) {
		if (key == 0)
			buffer.setTankType(Fluids.fromName((String)value));
		else if (key == 1)
			buffer.setFill((int)value);
	}
	@Override
	public void onReceivePacketServer(byte key,Object value,EntityPlayer plr) { }
	@Override
	public void onPlayerValidate(EntityPlayer plr) { }
}
