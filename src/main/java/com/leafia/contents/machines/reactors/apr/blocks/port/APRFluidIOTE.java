package com.leafia.contents.machines.reactors.apr.blocks.port;

import com.hbm.api.fluidmk2.IFluidStandardReceiverMK2;
import com.hbm.api.fluidmk2.IFluidStandardSenderMK2;
import com.hbm.interfaces.Spaghetti;
import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.hbm.lib.DirPos;
import com.hbm.lib.ForgeDirection;
import com.hbm.tileentity.TileEntityLoadedBase;
import com.leafia.contents.machines.reactors.apr.blocks.core.APRCoreTE;
import com.leafia.contents.machines.reactors.apr.blocks.core.APRCoreTE.APRChamber;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class APRFluidIOTE extends TileEntityLoadedBase implements IFluidStandardReceiverMK2, IFluidStandardSenderMK2, ITickable {
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
		if (targetPos == null)
			return false;
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
		if (validateTarget()) {
			APRChamber chamber = target.getChamberSafely(getChamberIndex());
			if (chamber == null) return new FluidTankNTM[0];
			return new FluidTankNTM[]{chamber.input,target.oxygen};
		}
		return new FluidTankNTM[0];
	}
	@Override
	public @NotNull FluidTankNTM[] getSendingTanks() {
		if (validateTarget()) {
			APRChamber chamber = target.getChamberSafely(getChamberIndex());
			if (chamber == null) return new FluidTankNTM[0];
			return new FluidTankNTM[]{chamber.output};
		}
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
	@Override
	public void update() {
		if (world.isRemote) return;
		if (targetPos != null) {
			if (getWorld().getTileEntity(targetPos) instanceof APRCoreTE core)
				target = core;
		}
		if (target != null && target.isInvalid())
			target = null;
		if (target != null) {
			APRChamber chamber = target.getChamberSafely(getChamberIndex());
			if (chamber == null) return;
			for (EnumFacing value : EnumFacing.values()) {
				DirPos dp = new DirPos(getPos().offset(value),ForgeDirection.getOrientation(value));
				trySubscribe(target.oxygen.getTankType(),world,dp);
				trySubscribe(chamber.input.getTankType(),world,dp);
				tryProvide(chamber.output.getTankType(),world,dp);
			}
		}
	}
}
