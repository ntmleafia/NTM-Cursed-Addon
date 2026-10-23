// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.misc.modular_turbine.flywheel;

import com.hbm.blocks.BlockDummyable;
import com.hbm.tileentity.TileEntityProxyCombo;
import com.leafia.contents.machines.misc.modular_turbine.ModularTurbineBlockBase;
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

public abstract class MTFlywheelBase extends ModularTurbineBlockBase {
	public MTFlywheelBase(String s) {
		super(s);
	}
	@Override
	public @Nullable TileEntity createNewTileEntity(World worldIn,int meta) {
		if (meta >= 12) return new MTFlywheelTE();
		if (meta >= extra) return new TileEntityProxyCombo(false,false,true);
		return null;
	}
	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}
	@Override
	public void addCollisionBoxToList(@NotNull IBlockState state,@NotNull World worldIn,@NotNull BlockPos pos,@NotNull AxisAlignedBB entityBox,@NotNull List<AxisAlignedBB> collidingBoxes,@Nullable Entity entityIn,boolean isActualState) {
		BlockPos corePos = findCore(worldIn,pos);
		if (corePos != null) {
			EnumFacing facing = EnumFacing.byIndex(worldIn.getBlockState(corePos).getValue(BlockDummyable.META)-10).getOpposite();
			Vec3d shaft = new Vec3d(corePos.up(shaftHeight())).add(0.5,0.5,0.5);
			Vec3d myPos = new Vec3d(pos).add(0.5,0.5,0.5);
			if (facing.getAxis() == Axis.X)
				myPos = new Vec3d(shaft.x,myPos.y,myPos.z);
			else if (facing.getAxis() == Axis.Z)
				myPos = new Vec3d(myPos.x,myPos.y,shaft.z);
			if (shaft.distanceTo(myPos) <= size()/2d-0.333) // scary
				super.addCollisionBoxToList(state,worldIn,pos,entityBox,collidingBoxes,entityIn,isActualState);
		}
	}
}
