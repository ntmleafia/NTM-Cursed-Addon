package com.leafia.contents.network.ff_duct.utility.pump;

import com.hbm.inventory.fluid.FluidType;
import com.hbm.lib.ForgeDirection;
import com.hbm.util.I18nUtil;
import com.leafia.contents.network.ff_duct.FFDuctTE;
import com.leafia.contents.network.ff_duct.uninos.IFFProvider;
import com.leafia.contents.network.ff_duct.uninos.IFFReceiver;
import com.leafia.contents.network.ff_duct.utility.FFDuctUtilityBase;
import com.leafia.contents.network.ff_duct.utility.FFDuctUtilityTEBase;
import com.leafia.dev.container_utility.LeafiaPacket;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FFPumpTE extends FFDuctUtilityTEBase implements ITickable, IFluidHandler, IFFProvider, IFFReceiver {
	FluidTank tank = new FluidTank(10000);
	@Override
	public void setType(FluidType type) {
		super.setType(type);
		tank.drain(tank.getCapacity()*2,true);
	}

	@Override
	public boolean hasCapability(Capability<?> capability,@Nullable EnumFacing facing) {
		IBlockState state = world.getBlockState(pos);
		if (state.getBlock() instanceof FFPumpBlock && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			EnumFacing face = state.getValue(FFDuctUtilityBase.FACING);
			return facing == null || facing.getAxis().equals(face.getAxis());
		}
		return super.hasCapability(capability,facing);
	}

	@Override
	public @Nullable <T> T getCapability(Capability<T> capability,@Nullable EnumFacing facing) {
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this);
		return super.getCapability(capability,facing);
	}

	void doUpdate() {
		if (getType().getFF() == null) return;
		if (!world.isRemote) {
			IBlockState state = world.getBlockState(pos);
			if (state.getBlock() instanceof FFPumpBlock) {
				EnumFacing facing = state.getValue(FFDuctUtilityBase.FACING);
				TileEntity behind = world.getTileEntity(pos.offset(facing,-1));
				if (behind != null && !(behind instanceof FFDuctUtilityTEBase)) {
					if (behind.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,facing)) {
						IFluidHandler handler = behind.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,facing);
						FluidStack stack = new FluidStack(getType().getFF(),tank.getCapacity()-tank.getFluidAmount());
						if (tank.fill(handler.drain(stack,false),false) > 0)
							tank.fill(handler.drain(stack,true),true);
						tryProvide(tank,world,pos.offset(facing),ForgeDirection.getOrientation(facing));
						return;
					}
				}
				TileEntity ahead = world.getTileEntity(pos.offset(facing));
				if (ahead != null && !(ahead instanceof FFDuctUtilityTEBase)) {
					if (ahead.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,facing.getOpposite())) {
						IFluidHandler handler = ahead.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,facing.getOpposite());
						tank.drain(handler.fill(tank.getFluid(),true),true);
						if (tank.getFluidAmount() > 0)
							tryProvide(tank,world,pos.offset(facing,-1),ForgeDirection.getOrientation(facing.getOpposite()));
						else
							trySubscribe(tank,new FluidStack(getType().getFF(),0),world,pos.offset(facing,-1),ForgeDirection.getOrientation(facing.getOpposite()));
						return;
					}
				}
				if (ahead instanceof FFDuctTE && behind instanceof FFDuctTE) {
					tryProvide(tank,world,pos.offset(facing),ForgeDirection.getOrientation(facing));
					trySubscribe(tank,new FluidStack(getType().getFF(),0),world,pos.offset(facing,-1),ForgeDirection.getOrientation(facing.getOpposite()));
				}
			}
		}
	}

	@Override
	public void update() {
		doUpdate();
		LeafiaPacket packet = LeafiaPacket._start(this).__write(0,tank.getFluidAmount());
		if (tank.getFluid() != null && tank.getFluid().tag != null)
			packet.__write(1,tank.getFluid().tag);
		packet.__sendToAffectedClients();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInfo(List<String> info) {
		info.add(TextFormatting.GRAY+I18nUtil.resolveKey("tile.ff_pump.buffer",tank.getFluidAmount()+"mB"));
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		tank.readFromNBT(compound.getCompoundTag("buffer"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setTag("buffer",tank.writeToNBT(new NBTTagCompound()));
		return super.writeToNBT(compound);
	}

	@Override
	public String getPacketIdentifier() {
		return "FF_PUMP";
	}
	@Override
	public void onReceivePacketLocal(byte key,Object value) {
		super.onReceivePacketLocal(key,value);
		if (key == 0) {
			if (getType().getFF() == null)
				tank.setFluid(null);
			else
				tank.setFluid(new FluidStack(getType().getFF(),(int) value));
		} else if (key == 1 && tank.getFluid() != null)
			tank.getFluid().tag = (NBTTagCompound)value;
	}

	@Override
	public FluidTank getCorrespondingTank(FluidStack stack) {
		return tank;
	}
	@Override
	public IFluidTankProperties[] getTankProperties() {
		return new IFluidTankProperties[0];
	}
	@Override
	public int fill(FluidStack resource,boolean doFill) {
		return 0;
	}
	@Override
	public @Nullable FluidStack drain(FluidStack resource,boolean doDrain) {
		return null;
	}
	@Override
	public @Nullable FluidStack drain(int maxDrain,boolean doDrain) {
		return null;
	}
}
