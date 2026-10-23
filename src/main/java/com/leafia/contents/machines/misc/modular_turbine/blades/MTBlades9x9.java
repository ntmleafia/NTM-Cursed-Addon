// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.misc.modular_turbine.blades;

import com.hbm.handler.MultiblockHandlerXR;
import com.hbm.lib.ForgeDirection;
import com.hbm.tileentity.TileEntityProxyCombo;
import com.leafia.contents.machines.misc.modular_turbine.ModularTurbineBlockBase;
import com.leafia.contents.machines.misc.modular_turbine.ModularTurbineComponentTE;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MTBlades9x9 extends ModularTurbineBlockBase {
	public MTBlades9x9(String s,String variant) {
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
		return 4;
	}
	@Override
	public TurbineComponentType componentType() {
		return TurbineComponentType.BLADES;
	}
	@Override
	public int[] canConnectTo() {
		return new int[]{9};
	}
	@Override
	public int size() {
		return 9;
	}
	@Override
	public double weight() {
		return 40;
	}
	@Override
	public int[] getDimensions() {
		return new int[]{5,0,0,0,4,4};
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
	@Override
	protected void fillSpace(World world,int x,int y,int z,ForgeDirection dir,int o) {
		super.fillSpace(world,x,y,z,dir,o);
		x += dir.offsetX * o;
		z += dir.offsetZ * o;
		MultiblockHandlerXR.fillSpace(world,x,y,z,new int[]{7,-7,0,0,2,2},this,dir);
		MultiblockHandlerXR.fillSpace(world,x,y,z,new int[]{6,-6,0,0,3,3},this,dir);
	}
	@Override
	public boolean checkRequirement(World world,int x,int y,int z,ForgeDirection dir,int o) {
		x += dir.offsetX * o;
		z += dir.offsetZ * o;
		if (!MultiblockHandlerXR.checkSpace(world,x,y,z,new int[]{7,-7,0,0,2,2},x,y,z,dir))
			return false;
		if (!MultiblockHandlerXR.checkSpace(world,x,y,z,new int[]{6,-6,0,0,3,3},x,y,z,dir))
			return false;
		return MultiblockHandlerXR.checkSpace(world,x,y,z,getDimensions(),x,y,z,dir);
	}
}
