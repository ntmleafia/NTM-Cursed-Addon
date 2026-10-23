// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.misc.modular_turbine;

import com.hbm.blocks.BlockDummyable;
import com.leafia.contents.machines.misc.modular_turbine.ModularTurbineBlockBase.TurbineComponentType;
import com.leafia.contents.machines.misc.modular_turbine.core.MTCoreTE;
import com.leafia.contents.machines.misc.modular_turbine.core.MTCoreTE.TurbineAssembly;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModularTurbineComponentTE extends TileEntity implements IMTStageUpgradeContributor, IMTMachineUpgradeContributor {
	public MTCoreTE core;
	public TurbineAssembly assembly;
	public void setBlockType(Block block) {
		blockType = block;
	}
	protected ModularTurbineComponentTE getTEFromShaftPos(BlockPos p,Axis myAxis) {
		if (world.getBlockState(p).getBlock() instanceof ModularTurbineBlockBase other) {
			BlockPos core = other.findCore(world,p);
			if (core != null && world.getTileEntity(core) instanceof ModularTurbineComponentTE mte) {
				EnumFacing facing = EnumFacing.byIndex(world.getBlockState(core).getValue(BlockDummyable.META)-10).getOpposite();
				if (facing.getAxis().equals(myAxis))
					return mte;
			}
		}
		return null;
	}
	AxisAlignedBB local$renderAABB;
	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		if (local$renderAABB == null) {
			local$renderAABB = new AxisAlignedBB(
					pos.getX()-5,pos.getY()-1,pos.getZ()-5,
					pos.getX()+5,pos.getY()+10,pos.getZ()+5
			);
		}
		return local$renderAABB;
	}

	public boolean local$firstRender = true;
	public ModularTurbineComponentTE local$nextBlade;
	//public TileEntity local$prevBlade;
	public void local$checkForBlades(boolean rebuild) {
		EnumFacing face = EnumFacing.byIndex(getBlockMetadata()-10).getOpposite();
		boolean valid = false;
		if (getBlockType() instanceof ModularTurbineBlockBase base) {
			valid = true;
			if (local$nextBlade != null && local$nextBlade.isInvalid()) { local$nextBlade = null; rebuild = true; }
			//if (local$prevBlade != null && local$prevBlade.isInvalid()) { local$prevBlade = null; rebuild = true; }
			if (rebuild && local$nextBlade == null) {
				BlockPos shaft = pos.up(base.shaftHeight());
				ModularTurbineComponentTE te = getTEFromShaftPos(shaft.offset(face),face.getAxis());
				if (te != null) {
					if (te.getBlockType() instanceof ModularTurbineBlockBase other) {
						if (other.componentType() == TurbineComponentType.BLADES)
							local$nextBlade = te;
					}
				}
			}
			/*if (rebuild && local$prevBlade == null) {
				BlockPos shaft = pos.up(base.shaftHeight());
				ModularTurbineComponentTE te = getTEFromShaftPos(shaft.offset(face.getOpposite()),face.getAxis());
				if (te != null) {
					if (te.getBlockType() instanceof ModularTurbineBlockBase other) {
						if (other.componentType() == TurbineComponentType.BLADES)
							local$prevBlade = te;
					}
				}
			}*/
		}
		if (!valid) {
			local$nextBlade = null;
			//local$prevBlade = null;
		}
	}

	@Override
	public void invalidate() {
		if (core != null)
			core.disassemble();
		super.invalidate();
	}
}
