// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.misc.modular_turbine.blades;

import com.hbm.tileentity.TileEntityProxyCombo;
import com.leafia.contents.machines.misc.modular_turbine.ModularTurbineBlockBase;
import com.leafia.contents.machines.misc.modular_turbine.ModularTurbineComponentTE;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MTBlades3x3 extends ModularTurbineBlockBase {
	public MTBlades3x3(String s,String variant) {
		super(s);
		this.variant = variant;
	}
	String variant;
	@Override
	public String variant() {
		return variant;
	}
	@Override
	public int shaftHeight() {
		return 1;
	}
	@Override
	public TurbineComponentType componentType() {
		return TurbineComponentType.BLADES;
	}
	@Override
	public int[] canConnectTo() {
		return new int[]{3};
	}
	@Override
	public int size() {
		return 3;
	}
	@Override
	public double weight() {
		return 10;
	}
	@Override
	public int[] getDimensions() {
		return new int[]{2,0,0,0,1,1};
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
