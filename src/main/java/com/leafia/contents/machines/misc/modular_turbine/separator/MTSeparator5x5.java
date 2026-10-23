// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.misc.modular_turbine.separator;

import com.hbm.tileentity.TileEntityProxyCombo;
import com.leafia.contents.machines.misc.modular_turbine.ModularTurbineBlockBase;
import com.leafia.contents.machines.misc.modular_turbine.ModularTurbineComponentTE;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MTSeparator5x5 extends ModularTurbineBlockBase {
	public MTSeparator5x5(String s) {
		super(s);
	}
	@Override
	public int shaftHeight() {
		return 2;
	}
	@Override
	public TurbineComponentType componentType() {
		return TurbineComponentType.SEPARATOR;
	}
	@Override
	public int[] canConnectTo() {
		return new int[]{};
	}
	@Override
	public int size() {
		return 5;
	}
	@Override
	public double weight() {
		return 0.1;
	}
	@Override
	public int[] getDimensions() {
		return new int[]{4,0,0,0,2,2};
	}
	@Override
	public int getOffset() {
		return 0;
	}
	@Override
	public @Nullable TileEntity createNewTileEntity(World worldIn,int meta) {
		if (meta >= 12) return new ModularTurbineComponentTE();
		if (meta >= extra) return new TileEntityProxyCombo(false,false,true);
		return null;
	}
}
