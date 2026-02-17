package com.leafia.contents.machines.elevators;

import com.hbm.api.energymk2.IEnergyReceiverMK2;
import net.minecraft.tileentity.TileEntity;

public class EvPulleyTE extends TileEntity implements IEnergyReceiverMK2 {
	@Override
	public void setPower(long power) {

	}

	@Override
	public long getPower() {
		return 0;
	}

	@Override
	public long getMaxPower() {
		return 0;
	}

	@Override
	public boolean isLoaded() {
		return false;
	}
}
