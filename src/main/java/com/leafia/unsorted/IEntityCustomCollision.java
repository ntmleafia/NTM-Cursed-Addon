// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.unsorted;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

public interface IEntityCustomCollision {
	List<AxisAlignedBB> getCollisionBoxes(Entity other);
}
