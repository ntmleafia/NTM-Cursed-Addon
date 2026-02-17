package com.leafia.contents.machines.reactors.pwr.blocks.components.port;

import com.hbm.api.fluidmk2.IFluidStandardReceiverMK2;
import com.hbm.api.fluidmk2.IFluidStandardSenderMK2;
import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.hbm.lib.ForgeDirection;
import com.leafia.contents.machines.reactors.pwr.PWRData;
import com.leafia.contents.machines.reactors.pwr.blocks.components.PWRAssignableEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import org.jetbrains.annotations.NotNull;

public class PWRPortTE extends PWRAssignableEntity implements IFluidStandardReceiverMK2, IFluidStandardSenderMK2, ITickable {
	@Override
	public @NotNull FluidTankNTM[] getReceivingTanks() {
		PWRData core = getLinkedCore();
		if (core != null) {
			return new FluidTankNTM[]{
					core.tanks[0],
					core.tanks[3]
			};
		}
		return new FluidTankNTM[0];
	}

	@Override
	public @NotNull FluidTankNTM[] getSendingTanks() {
		PWRData core = getLinkedCore();
		if (core != null) {
			return new FluidTankNTM[]{
					core.tanks[1],
					core.tanks[4]
			};
		}
		return new FluidTankNTM[0];
	}

	@Override
	public FluidTankNTM[] getAllTanks() {
		PWRData core = getLinkedCore();
		if (core != null)
			return core.tanks;
		return new FluidTankNTM[0];
	}

	boolean loaded = true;
	@Override
	public void onChunkUnload() {
		loaded = false;
		super.onChunkUnload();
	}
	@Override
	public boolean isLoaded() {
		return loaded;
	}

	@Override
	public void update() {
		super.update();
		if (!world.isRemote) {
			PWRData core = getLinkedCore();
			if (core != null) {
				for (EnumFacing facing : EnumFacing.values()) {
					trySubscribe(core.tankTypes[0],world,pos.offset(facing),ForgeDirection.getOrientation(facing));
					trySubscribe(core.tankTypes[3],world,pos.offset(facing),ForgeDirection.getOrientation(facing));
					tryProvide(core.tanks[1],world,pos.offset(facing),ForgeDirection.getOrientation(facing));
					tryProvide(core.tanks[4],world,pos.offset(facing),ForgeDirection.getOrientation(facing));
				}
			}
		}
	}

	@Override
	public String getPacketIdentifier() {
		return "PWR_PORT";
	}

	@Override
	public void onPlayerValidate(EntityPlayer plr) {

	}

	/*
	// redirects
	@Override
	public IFluidTankProperties[] getTankProperties() {
		PWRData gathered = gatherData();
		return (gathered != null) ? gathered.getTankProperties() : new IFluidTankProperties[0];
	}
	@Override
	public int fill(FluidStack resource,boolean doFill) {
		PWRData gathered = gatherData();
		return (gathered != null) ? gathered.fill(resource,doFill) : 0;
	}
	@Nullable
	@Override
	public FluidStack drain(FluidStack resource,boolean doDrain) {
		PWRData gathered = gatherData();
		return (gathered != null) ? gathered.drain(resource,doDrain) : null;
	}
	@Nullable
	@Override
	public FluidStack drain(int maxDrain,boolean doDrain) {
		PWRData gathered = gatherData();
		return (gathered != null) ? gathered.drain(maxDrain,doDrain) : null;
	}

	@Override
	public boolean hasCapability(Capability<?> capability,@Nullable EnumFacing facing) {
		return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability,facing);
	}
	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability,@Nullable EnumFacing facing) {
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this);
		}
		return super.getCapability(capability,facing);
	}*/
}
