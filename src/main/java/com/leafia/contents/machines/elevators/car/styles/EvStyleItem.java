// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.elevators.car.styles;

import com.hbm.items.special.ItemCustomLore;
import com.leafia.contents.AddonItems.ElevatorStyles;
import com.leafia.dev.items.itembase.AddonItemBase;

public class EvStyleItem extends AddonItemBase {
	final String style;
	public final StyleType type;
	public enum StyleType { FLOOR,CEILING,WALL }
	public EvStyleItem(String s,StyleType type) {
		super(s);
		this.type = type;
		ElevatorStyles.styleItems.add(this);
		style = s.substring(3);
		this.setMaxStackSize(1);
	}
	public String getStyleId() {
		return style;
	}
}
