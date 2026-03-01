package com.leafia.contents.machines.reactors.lftr.components.ejector;

import com.hbm.forgefluid.FFUtils;
import com.hbm.lib.ForgeDirection;
import com.leafia.contents.machines.reactors.lftr.components.MSRTEBase;
import com.leafia.contents.network.ff_duct.uninos.IFFProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import org.jetbrains.annotations.Nullable;

public class MSREjectorTE extends MSRTEBase implements IFluidHandler, IFFProvider {
	public MSREjectorTE() {
		tank = new FluidTank(1000);
	}
	EnumFacing getDirection() {
		IBlockState state = world.getBlockState(pos);
		if (state.getBlock() instanceof MSREjectorBlock)
			return state.getValue(MSREjectorBlock.FACING);
		return EnumFacing.NORTH;
	}
	@Override
	public void update() {
		super.update();
		//fillFluid(pos.add(getDirection().getDirectionVec()),tank);
		if (!world.isRemote)
			tryProvide(tank,world,pos.offset(getDirection()),ForgeDirection.getOrientation(getDirection()));
	}
	public void fillFluid(BlockPos pos1,FluidTank tank) {
		//FFUtils.fillFluid(this, tank, world, pos1, 100);
	}
	@Override
	public <T> T getCapability(Capability<T> capability,EnumFacing facing) {
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this);
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && (facing == null || facing.equals(getDirection()))) || super.hasCapability(capability, facing);
	}
	@Override
	public FluidTank getSendingTank(FluidStack stack) {
		return tank;
	}
	@Override
	public IFluidTankProperties[] getTankProperties() {
		return tank.getTankProperties();
	}
	@Override
	public int fill(FluidStack resource,boolean doFill) {
		return 0;
	}
	@Override
	public @Nullable FluidStack drain(FluidStack resource,boolean doDrain) {
		return tank.drain(resource,doDrain);
	}
	@Override
	public @Nullable FluidStack drain(int maxDrain,boolean doDrain) {
		return tank.drain(maxDrain,doDrain);
	}
	@Override
	public String getPacketIdentifier() {
		return "LFTR_EJECTOR";
	}
}