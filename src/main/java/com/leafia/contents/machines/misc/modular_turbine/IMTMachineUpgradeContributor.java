// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.misc.modular_turbine;

import com.leafia.contents.machines.misc.modular_turbine.core.MTCoreData.MachineUpgradeSummary;
import com.leafia.contents.machines.misc.modular_turbine.core.MTCoreTE;

/** Contributes offsets to the shaft-wide machine model. */
public interface IMTMachineUpgradeContributor {
	default void contributeMachineUpgradeOffsets(MachineUpgradeSummary summary,MTCoreTE core,ModularTurbineComponentTE component) { }
}
