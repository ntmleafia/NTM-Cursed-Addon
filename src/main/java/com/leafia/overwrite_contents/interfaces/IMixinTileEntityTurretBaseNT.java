// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.interfaces;

import net.minecraft.entity.Entity;

public interface IMixinTileEntityTurretBaseNT {
	boolean leafia$detectFriendlyFire(Entity e);
}
