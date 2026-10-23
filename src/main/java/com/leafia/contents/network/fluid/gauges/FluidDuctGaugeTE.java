// SPDX-FileCopyrightText: 2025-2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.network.fluid.gauges;

import com.hbm.inventory.control_panel.ControlEventSystem;
import com.hbm.inventory.control_panel.types.DataValue;
import com.hbm.inventory.control_panel.types.DataValueFloat;
import com.hbm.inventory.control_panel.IControllable;
import com.hbm.inventory.fluid.Fluids;
import com.leafia.contents.network.fluid.FluidDuctEquipmentTE;
import com.leafia.dev.container_utility.LeafiaPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class FluidDuctGaugeTE extends FluidDuctEquipmentTE implements ITickable, IControllable {
	long[] fills = new long[20];
	public long maximum = 10_000;
	int needle = 0;
	public int local_dialRand = 0;
	@Override
	public void update() {
		super.update();

		if (!world.isRemote) {
			needle = Math.floorMod(needle+1,20);
			fills[needle] = 0;
			if (node != null && node.net != null && getType() != Fluids.NONE)
				fills[needle] = node.net.fluidTracker;

			long sum = 0;
			for (long fill : fills)
				sum += fill;

			fillPerSec = sum;

			LeafiaPacket._start(this).__write(0,sum).__write(1,maximum).__sendToAffectedClients();
		} else
			local_dialRand = world.rand.nextInt(5);
	}
	@Override
	public Map<String,DataValue> getQueryData() {
		Map<String,DataValue> map = new HashMap<>();
		map.put("perSecSmooth",new DataValueFloat(fillPerSec));
		return map;
	}
	public long fillPerSec = 0;
	@Override
	public void onReceivePacketLocal(byte key,Object value) {
		super.onReceivePacketLocal(key,value);
		if (key == 0)
			fillPerSec = (long)value;
		if (key == 1)
			maximum = (long)value;
	}
	@Override
	public void onReceivePacketServer(byte key,Object value,EntityPlayer plr) {
		if (key == 0 && value instanceof Long)
			maximum = (long)value;
	}
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if (compound.hasKey("maximum"))
			maximum = compound.getLong("maximum");
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setLong("maximum",maximum);
		return super.writeToNBT(compound);
	}
	@Override
	public String getPacketIdentifier() {
		return "DUCT_GAUGE";
	}
	@Override
	public BlockPos getControlPos() {
		return getPos();
	}
	@Override
	public World getControlWorld() {
		return getWorld();
	}
	@Override
	public void validate() {
		super.validate();
		ControlEventSystem.get(world).addControllable(this);
	}
	@Override
	public void invalidate() {
		super.invalidate();
		ControlEventSystem.get(world).removeControllable(this);
	}
}
