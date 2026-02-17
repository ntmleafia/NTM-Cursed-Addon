package com.leafia.contents.machines.elevators.car.styles;

import com.hbm.items.special.ItemCustomLore;
import com.leafia.contents.AddonItems.ElevatorStyles;
import com.leafia.dev.items.itembase.AddonItemBase;

public class EvStyleItem extends AddonItemBase {
	final String style;
	public EvStyleItem(String s) {
		super(s);
		ElevatorStyles.styleItems.add(this);
		style = s.substring(3);
		this.setMaxStackSize(1);
	}
	public String getStyleId() {
		return style;
	}
}
