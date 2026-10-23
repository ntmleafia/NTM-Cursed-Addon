// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.misc.modular_turbine.flywheel;

import com.hbm.blocks.BlockDummyable;
import com.hbm.tileentity.TileEntityProxyCombo;
import com.leafia.contents.machines.misc.modular_turbine.ModularTurbineBlockBase;
import com.leafia.contents.machines.misc.modular_turbine.ModularTurbineComponentTE;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MTFlywheel3x3 extends MTFlywheelBase {
	public MTFlywheel3x3(String s) {
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
		return 3;
	}
	@Override
	public double weight() {
		return 160;
	}
	@Override
	public int[] getDimensions() {
		return new int[]{2,0,0,0,1,1};
	}
	@Override
	public int getOffset() {
		return 0;
	}
}
