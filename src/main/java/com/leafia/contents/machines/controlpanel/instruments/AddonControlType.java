// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.controlpanel.instruments;

import com.hbm.inventory.control_panel.controls.ControlType;

public class AddonControlType {
	public static final ControlType RBMK_DISPLAY = ControlType.registerOrGet("RBMK Display");
	public static final ControlType GRAPH = ControlType.registerOrGet("Graph");
	public static final ControlType LEVER = ControlType.registerOrGet("Lever");
}
