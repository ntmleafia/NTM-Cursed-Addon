package com.leafia.contents.network.fluid.gauges;

import com.hbm.inventory.fluid.Fluids;
import com.leafia.contents.network.fluid.FluidDuctEquipmentTE;
import com.leafia.dev.container_utility.LeafiaPacket;
import net.minecraft.util.ITickable;

public class FluidDuctGaugeTE extends FluidDuctEquipmentTE implements ITickable {
	long[] fills = new long[20];
	public long maximum = 100_000;
	int needle = 0;
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

			LeafiaPacket._start(this).__write(0,sum).__write(1,maximum).__sendToAffectedClients();
		}
	}
	public long local_fillPerSec = 0;
	@Override
	public void onReceivePacketLocal(byte key,Object value) {
		super.onReceivePacketLocal(key,value);
		if (key == 0)
			local_fillPerSec = (long)value;
		if (key == 1)
			maximum = (long)value;
	}
	@Override
	public String getPacketIdentifier() {
		return "DUCT_GAUGE";
	}
}
