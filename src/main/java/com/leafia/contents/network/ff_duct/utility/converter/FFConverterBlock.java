// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.network.ff_duct.utility.converter;

import com.leafia.contents.network.ff_duct.utility.FFDuctUtilityBase;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FFConverterBlock extends FFDuctUtilityBase {
	public FFConverterBlock(Material materialIn,String s) {
		super(materialIn,s);
	}
	@Override
	public @Nullable TileEntity createNewTileEntity(World worldIn,int meta) {
		return new FFConverterTE();
	}
}
