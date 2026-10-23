// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.elevators;

import com.hbm.api.energymk2.IEnergyReceiverMK2;
import com.hbm.lib.ForgeDirection;
import com.leafia.contents.machines.elevators.car.ElevatorEntity;
import com.leafia.contents.machines.elevators.weight.EvWeightEntity;
import com.leafia.dev.LeafiaDebug;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.container_utility.LeafiaPacketReceiver;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class EvPulleyTE extends TileEntity implements IEnergyReceiverMK2, ITickable, LeafiaPacketReceiver {
	public ElevatorEntity elevator;
	public EvWeightEntity counterweight;
	public double setupDistCabin = -1;
	public double setupDistWeight = -1;
	public static long consumption = 10;
	long power = 0;

	@Override
	public void update() {
		if (!world.isRemote) {
			if (elevator != null) {
				if (!elevator.isEntityAlive()) {
					setupDistCabin = -1;
					elevator = null;
				}
			}
			if (counterweight != null) {
				if (!counterweight.isEntityAlive()) {
					setupDistWeight = -1;
					counterweight = null;
				}
			}
			if (counterweight != null && elevator != null) {
				double distEv = pos.getY()-elevator.posY;
				double offset = distEv-setupDistCabin;
				counterweight.posY = pos.getY()-setupDistWeight+offset;
			}
			EnumFacing face = EnumFacing.byIndex(getBlockMetadata()-10);
			trySubscribe(world,pos.offset(face.getOpposite().rotateY()).down(),ForgeDirection.DOWN);
			if (power >= consumption)
				power -= consumption;
			LeafiaPacket._start(this).__write(0,power).__sendToAffectedClients();
		}
	}

	@Override
	public void setPower(long power) {
		this.power = power;
	}

	@Override
	public long getPower() {
		return power;
	}

	@Override
	public long getMaxPower() {
		return consumption*5;
	}

	boolean loaded = true;

	@Override
	public boolean isLoaded() {
		return loaded;
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		loaded = false;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		if (compound.hasKey("setupDistCabin"))
			setupDistCabin = compound.getDouble("setupDistCabin");
		if (compound.hasKey("setupDistWeight"))
			setupDistWeight = compound.getDouble("setupDistWeight");
		power = compound.getLong("power");
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setDouble("setupDistCabin",setupDistCabin);
		compound.setDouble("setupDistWeight",setupDistWeight);
		compound.setLong("power",power);
		return super.writeToNBT(compound);
	}

	@Override
	public double affectionRange() {
		return 256;
	}

	@Override
	public String getPacketIdentifier() {
		return "EV_PULLEY";
	}

	@Override
	public void onReceivePacketLocal(byte key,Object value) {
		if (key == 0)
			power = (long)value;
	}

	@Override
	public void onReceivePacketServer(byte key,Object value,EntityPlayer plr) { }

	@Override
	public void onPlayerValidate(EntityPlayer plr) { }
}
