package com.leafia.contents.machines.reactors.apr.blocks.port;

import com.hbm.api.fluidmk2.IFluidStandardReceiverMK2;
import com.hbm.api.fluidmk2.IFluidStandardSenderMK2;
import com.hbm.interfaces.Spaghetti;
import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.hbm.tileentity.TileEntityLoadedBase;
import com.leafia.contents.machines.reactors.apr.blocks.core.APRCoreTE;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class APRFluidIOTE extends TileEntityLoadedBase implements IFluidStandardReceiverMK2, IFluidStandardSenderMK2 {
	public BlockPos targetPos = null;
	public APRCoreTE target = null;
	@Spaghetti("this sucks")
	public int getChamberIndex() {
		double distance = Math.sqrt(getPos().distanceSq(target.getPos()));
		int closestIndex = 0;
		double closestDistance = Double.MAX_VALUE;
		for (int i = 0; i < target.chambers.size(); i++) {
			double dist = Math.abs(distance-target.chambers.get(i));
			if (dist < closestDistance) {
				closestDistance = dist;
				closestIndex = i;
			}
		}
		return closestIndex;
	}
	public boolean validateTarget() {
		World world = getWorld();
		TileEntity te = world.getTileEntity(targetPos);
		if (te instanceof APRCoreTE apr) {
			target = apr;
			return true;
		}
		target = null;
		return false;
	}
	@Override
	public @NotNull FluidTankNTM[] getReceivingTanks() {
		if (validateTarget())
			return new FluidTankNTM[]{target.inputs.get(getChamberIndex()),target.oxygen};
		return new FluidTankNTM[0];
	}
	@Override
	public @NotNull FluidTankNTM[] getSendingTanks() {
		if (validateTarget())
			return new FluidTankNTM[]{target.outputs.get(getChamberIndex())};
		return new FluidTankNTM[0];
	}
	@Override
	public FluidTankNTM[] getAllTanks() {
		if (validateTarget())
			return target.getAllTanks();
		return new FluidTankNTM[0];
	}
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		if (nbt.hasKey("targetX"))
			targetPos = new BlockPos(nbt.getInteger("targetX"),nbt.getInteger("targetY"),nbt.getInteger("targetZ"));
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		if (targetPos != null) {
			nbt.setInteger("targetX",targetPos.getX());
			nbt.setInteger("targetY",targetPos.getY());
			nbt.setInteger("targetZ",targetPos.getZ());
		}
		return super.writeToNBT(nbt);
	}
}
