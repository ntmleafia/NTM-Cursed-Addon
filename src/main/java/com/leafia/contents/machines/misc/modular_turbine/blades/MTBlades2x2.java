// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.misc.modular_turbine.blades;

import com.hbm.handler.MultiblockHandlerXR;
import com.hbm.lib.ForgeDirection;
import com.hbm.tileentity.TileEntityProxyCombo;
import com.hbm.util.I18nUtil;
import com.leafia.contents.machines.misc.modular_turbine.ModularTurbineBlockBase;
import com.leafia.contents.machines.misc.modular_turbine.ModularTurbineComponentTE;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class MTBlades2x2 extends ModularTurbineBlockBase {
	public MTBlades2x2(String s,String variant) {
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
		return new int[]{2};
	}
	@Override
	public int size() {
		return 2;
	}
	@Override
	public double weight() {
		return 5;
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
