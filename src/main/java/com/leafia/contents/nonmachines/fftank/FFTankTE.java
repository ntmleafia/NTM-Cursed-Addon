package com.leafia.contents.nonmachines.fftank;

import com.hbm.api.fluidmk2.FluidNode;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.hbm.tileentity.TileEntityMachineBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fluids.FluidTank;

public class FFTankTE extends TileEntityMachineBase {
	private static final int[] slots_top = new int[]{2};
	private static final int[] slots_bottom = new int[]{3, 5};
	private static final int[] slots_side = new int[]{4};
	protected FluidNode node;
	protected FluidType lastType;
	public FluidTank tank;
	public FluidTankNTM tankNew;
	public FFTankTE() {
		super(6);
		tank = new FluidTank(0);
		tankNew = new FluidTankNTM(Fluids.NONE, 0);
	}
	public FFTankTE(int cap) {
		super(6,false,false);
		tank = new FluidTank(cap);
		tankNew = new FluidTankNTM(Fluids.NONE, cap);
	}
	@Override
	public String getDefaultName() {
		return "tile.ff_tank.name";
	}
	AxisAlignedBB bb = null;
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		if(bb == null) {
			bb = new AxisAlignedBB(
					pos.getX() - 1,
					pos.getY(),
					pos.getZ() - 1,
					pos.getX() + 2,
					pos.getY() + 3,
					pos.getZ() + 2
			);
		}
		return bb;
	}
}
