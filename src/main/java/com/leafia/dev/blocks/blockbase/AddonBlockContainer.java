// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.dev.blocks.blockbase;

import com.leafia.contents.AddonBlocks;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;

public abstract class AddonBlockContainer extends BlockContainer {
	public AddonBlockContainer(Material materialIn,String s) {
		super(materialIn);
		this.setTranslationKey(s);
		this.setRegistryName(s);

		AddonBlocks.ALL_BLOCKS.add(this);
	}
}
