// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.interfaces;

public interface IMixinTileEntityRBMKBase {
	int dmgIncrement = 2;
	int maxDamage = 30*20*dmgIncrement;
	int leafia$getDamage();
	double leafia$jumpHeight();
}
