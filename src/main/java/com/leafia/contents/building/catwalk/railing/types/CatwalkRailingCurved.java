// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.building.catwalk.railing.types;

import com.leafia.contents.building.catwalk.railing.CatwalkRailingBase;
import net.minecraft.block.material.Material;

public class CatwalkRailingCurved extends CatwalkRailingBase {
	public CatwalkRailingCurved(String s) {
		super(Material.IRON,s);
		spritePath = "curved";
		modelPath = "curved";
	}
}
