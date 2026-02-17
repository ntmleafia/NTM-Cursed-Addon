package com.leafia.contents.network.fluid.valves;

import com.hbm.inventory.fluid.Fluids;
import com.hbm.uninos.UniNodespace;
import com.leafia.contents.network.fluid.FluidDuctEquipmentTE;
import com.leafia.dev.container_utility.LeafiaPacket;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;

public class FluidDuctValveTE extends FluidDuctEquipmentTE implements ITickable {
	boolean open = false;
	boolean wasOpen = false;
	@Override
	public boolean shouldCreateNode() {
		return open;
	}

	public void close() {
		if (node != null) {
			UniNodespace.destroyNode(world,pos,this.getType().getNetworkProvider());
			this.node = null;
		}
	}

	public int local_angle = 0;
	public int local_angle_last = 0;
	@Override
	public void update() {
		super.update();
		if (!world.isRemote) {
			if (wasOpen && !open)
				close();
			wasOpen = open;
			LeafiaPacket._start(this).__write(0,open).__sendToAffectedClients();
		} else {
			local_angle_last = local_angle;
			if (open && local_angle < 10)
				local_angle++;
			if (!open && local_angle > 0)
				local_angle--;
		}
	}
	@Override
	public void onReceivePacketLocal(byte key,Object value) {
		super.onReceivePacketLocal(key,value);
		if (key == 0)
			open = (boolean)value;
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setBoolean("open",open);
		return super.writeToNBT(compound);
	}
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		open = compound.getBoolean("open");
		wasOpen = open;
	}

	@Override
	public String getPacketIdentifier() {
		return "DUCT_VALVE";
	}
}
