// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.elevators.car.styles.panels;

import com.leafia.contents.machines.elevators.car.styles.EvWallBase;

public abstract class ElevatorPanelBase extends EvWallBase {
	abstract public String getId();
	abstract public int getStaticX();
	public double getStaticZ() { return 0.9365; };
	public ElevatorPanelBase(int rotation) {
		super(rotation);
	}
}