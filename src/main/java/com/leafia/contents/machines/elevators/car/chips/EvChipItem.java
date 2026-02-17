package com.leafia.contents.machines.elevators.car.chips;

import com.hbm.items.special.ItemCustomLore;
import com.leafia.contents.machines.elevators.car.ElevatorEntity;
import com.leafia.dev.items.itembase.AddonItemBaked;

public class EvChipItem extends AddonItemBaked {
	final String res;
	public EvChipItem(String s,String tex) {
		super(s,tex);
		res = s.substring(3);
		this.setMaxStackSize(1);
	}
	public EvChipBase getController(ElevatorEntity entity) {
		if (res.endsWith("s6")) return new EvChipS6(entity);
		if (res.endsWith("skylift")) return new EvChipSkylift(entity);
		return null;
	}
	public String getChipId() {
		return res;
	}
}
