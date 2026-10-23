// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.misc.modular_turbine.power_port;

import com.hbm.api.energymk2.IEnergyProviderMK2;
import com.hbm.lib.ForgeDirection;
import com.leafia.contents.machines.misc.modular_turbine.ModularTurbineBlockBase;
import com.leafia.contents.machines.misc.modular_turbine.ModularTurbineComponentTE;
import com.leafia.contents.machines.misc.modular_turbine.core.MTCoreTE;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

public class MTPowerPortTE extends TileEntity implements ITickable, IEnergyProviderMK2 {
	public EnumFacing getDirection() {
		IBlockState state = world.getBlockState(pos);
		if (state.getBlock() instanceof MTPowerPortBlock)
			return state.getValue(BlockDirectional.FACING);
		return EnumFacing.DOWN;
	}
	@Override
	public boolean canConnect(ForgeDirection dir) {
		return dir.toEnumFacing().equals(getDirection());
	}
	public MTCoreTE getCore() {
		BlockPos p = pos.offset(getDirection().getOpposite());
		if (world.getBlockState(p).getBlock() instanceof ModularTurbineBlockBase base) {
			BlockPos core = base.findCore(world,p);
			if (core != null) {
				TileEntity te = world.getTileEntity(core);
				if (te instanceof ModularTurbineComponentTE component)
					return component.core;
				else if (te instanceof MTCoreTE corete)
					return corete;
			}
		}
		return null;
	}
	MTCoreTE lastCore;
	@Override
	public long getPower() {
		if (lastCore != null && !lastCore.isInvalid())
			return lastCore.powerOutput;
		return 0;
	}
	@Override
	public void setPower(long power) {
		if (lastCore != null && !lastCore.isInvalid())
			lastCore.powerOutput = power;
	}
	@Override
	public long getMaxPower() {
		if (lastCore != null && !lastCore.isInvalid())
			return lastCore.powerOutput;
		return 0;
	}
	boolean isLoaded = true;
	@Override
	public void onChunkUnload() {
		isLoaded = false;
		super.onChunkUnload();
	}
	@Override
	public boolean isLoaded() {
		return isLoaded;
	}
	@Override
	public void update() {
		if (!world.isRemote) {
			lastCore = getCore();
			EnumFacing dir = getDirection();
			tryProvide(world,pos.offset(dir),ForgeDirection.getOrientation(dir));
		}
	}
}
