// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.misc.modular_turbine.flywheel;

import com.hbm.handler.MultiblockHandlerXR;
import com.hbm.lib.ForgeDirection;
import net.minecraft.world.World;

public class MTFlywheel2x2 extends MTFlywheelBase {
	public MTFlywheel2x2(String s) {
		super(s);
	}
	@Override
	public int shaftHeight() {
		return 1;
	}
	@Override
	public TurbineComponentType componentType() {
		return TurbineComponentType.FLYWHEEL;
	}
	@Override
	public int[] canConnectTo() {
		return new int[]{3};
	}
	@Override
	public int size() {
		return 2;
	}
	@Override
	public double weight() {
		return 120;
	}
	@Override
	public int[] getDimensions() {
		return new int[]{1,0,0,0,1,1};
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
		MultiblockHandlerXR.fillSpace(world,x,y,z,new int[]{2,-2,0,0,0,0},this,dir);
	}
	@Override
	public boolean checkRequirement(World world,int x,int y,int z,ForgeDirection dir,int o) {
		x += dir.offsetX * o;
		z += dir.offsetZ * o;
		if (!MultiblockHandlerXR.checkSpace(world,x,y,z,new int[]{2,-2,0,0,0,0},x,y,z,dir))
			return false;
		return MultiblockHandlerXR.checkSpace(world,x,y,z,getDimensions(),x,y,z,dir);
	}
}
