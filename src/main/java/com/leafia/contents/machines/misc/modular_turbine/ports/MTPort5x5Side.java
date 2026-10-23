// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.misc.modular_turbine.ports;

import com.hbm.lib.DirPos;
import com.hbm.lib.ForgeDirection;
import com.hbm.tileentity.TileEntityProxyCombo;
import com.leafia.contents.machines.misc.modular_turbine.separator.MTSeparator5x5;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MTPort5x5Side extends MTSeparator5x5 implements IMTPortBlock {
	public MTPort5x5Side(String s) {
		super(s);
	}
	@Override
	public DirPos[] getConPos(BlockPos pos,EnumFacing facing) {
		return new DirPos[]{
				new DirPos(pos.offset(facing.rotateY(),3),ForgeDirection.getOrientation(facing.rotateY())),
				new DirPos(pos.offset(facing.rotateY(),-3),ForgeDirection.getOrientation(facing.rotateY().getOpposite()))
		};
	}
	@Override
	public TurbineComponentType componentType() {
		return TurbineComponentType.PORT;
	}
	@Override
	public @Nullable TileEntity createNewTileEntity(World worldIn,int meta) {
		if (meta >= 12) return new MTComponentPortTE();
		if (meta >= extra) return new TileEntityProxyCombo(false,false,true);
		return null;
	}
	@Override
	protected void fillSpace(World world,int x,int y,int z,ForgeDirection dir,int o) {
		super.fillSpace(world,x,y,z,dir,o);
		EnumFacing face = dir.toEnumFacing();
		EnumFacing rot = face.rotateY();
		makeExtra(world,x+rot.getXOffset()*2,y,z+rot.getZOffset()*2);
		makeExtra(world,x-rot.getXOffset()*2,y,z-rot.getZOffset()*2);
	}
}
