// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2025 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.interfaces;

import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.leafia.contents.machines.powercores.dfc.IDFCBase;
import com.leafia.contents.network.spk_cable.uninos.ISPKReceiver;
import net.minecraft.util.math.RayTraceResult;

public interface IMixinTileEntityCoreEmitter extends IDFCBase, ISPKReceiver {

    RayTraceResult leafia$raycast(long out);

    boolean leafia$isActive();

    void leafia$isActive(boolean active);

    RayTraceResult leafia$lastRaycast();

    FluidTankNTM leafia$getOutputTank();
}
