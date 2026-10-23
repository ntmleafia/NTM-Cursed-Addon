// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.misc.modular_turbine;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MTShaftBlock extends ModularTurbineBlockBase {
	public MTShaftBlock(String s) {
		super(s);
	}
	@Override
	public int shaftHeight() {
		return 0;
	}
	@Override
	public TurbineComponentType componentType() {
		return TurbineComponentType.SHAFT_ONLY;
	}
	@Override
	public int[] canConnectTo() {
		return new int[0];
	}
	@Override
	public int size() {
		return 1;
	}
	@Override
	public double weight() {
		return 0.001;
	}
	@Override
	public int[] getDimensions() {
		return new int[]{ 0,0,0,0,0,0 };
	}
	@Override
	public int getOffset() {
		return 0;
	}
	@Override
	public @Nullable TileEntity createNewTileEntity(World worldIn,int meta) {
		return new ModularTurbineComponentTE();
	}
}
