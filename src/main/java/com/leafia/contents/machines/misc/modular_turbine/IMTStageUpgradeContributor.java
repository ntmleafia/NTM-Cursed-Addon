// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.misc.modular_turbine;

import com.leafia.contents.machines.misc.modular_turbine.core.MTCoreData.StageUpgradeContext;
import com.leafia.contents.machines.misc.modular_turbine.core.MTCoreData.StageUpgradeSummary;
import com.leafia.contents.machines.misc.modular_turbine.core.MTCoreTE;

/** Contributes offsets to one compiled steam stage. */
public interface IMTStageUpgradeContributor {
	default void contributeStageUpgradeOffsets(StageUpgradeSummary summary,MTCoreTE core,StageUpgradeContext context,ModularTurbineComponentTE component) { }
}
