// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.misc.modular_turbine.ports;

import com.hbm.lib.DirPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public interface IMTPortBlock {
	DirPos[] getConPos(BlockPos pos,EnumFacing facing);
}
