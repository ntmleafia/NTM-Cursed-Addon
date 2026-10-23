// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.misc.modular_turbine.flywheel;

import com.hbm.handler.MultiblockHandlerXR;
import com.hbm.lib.ForgeDirection;
import com.hbm.tileentity.TileEntityProxyCombo;
import com.leafia.contents.machines.misc.modular_turbine.ModularTurbineBlockBase;
import com.leafia.contents.machines.misc.modular_turbine.ModularTurbineComponentTE;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MTFlywheel5x5 extends MTFlywheelBase {
	public MTFlywheel5x5(String s) {
		super(s);
	}
	@Override
	public int shaftHeight() {
		return 2;
	}
	@Override
	public TurbineComponentType componentType() {
		return TurbineComponentType.FLYWHEEL;
	}
	@Override
	public int[] canConnectTo() {
		return new int[]{5};
	}
	@Override
	public int size() {
		return 5;
	}
	@Override
	public double weight() {
		return 320;
	}
	@Override
	public int[] getDimensions() {
		return new int[]{3,0,0,0,2,2};
	}
	@Override
	public int getOffset() {
		return 0;
	}
	@Override
	protected void fillSpace(World world,int x,int y,int z,ForgeDirection dir,int o) {
		super.fillSpace(world,x,y,z,dir,o);
		x += dir.offsetX * o;
		z += dir.offsetZ * o;
		MultiblockHandlerXR.fillSpace(world,x,y,z,new int[]{4,-4,0,0,1,1},this,dir);
	}
	@Override
	public boolean checkRequirement(World world,int x,int y,int z,ForgeDirection dir,int o) {
		x += dir.offsetX * o;
		z += dir.offsetZ * o;
		if (!MultiblockHandlerXR.checkSpace(world,x,y,z,new int[]{4,-4,0,0,1,1},x,y,z,dir))
			return false;
		return MultiblockHandlerXR.checkSpace(world,x,y,z,getDimensions(),x,y,z,dir);
	}
}
