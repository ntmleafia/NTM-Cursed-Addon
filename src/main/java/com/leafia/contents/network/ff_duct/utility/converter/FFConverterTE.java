package com.leafia.contents.network.ff_duct.utility.converter;

import com.hbm.api.fluidmk2.IFluidStandardReceiverMK2;
import com.hbm.api.fluidmk2.IFluidStandardSenderMK2;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.hbm.lib.ForgeDirection;
import com.hbm.util.I18nUtil;
import com.leafia.contents.network.ff_duct.uninos.IFFProvider;
import com.leafia.contents.network.ff_duct.utility.FFDuctUtilityBase;
import com.leafia.contents.network.ff_duct.utility.FFDuctUtilityTEBase;
import com.leafia.dev.container_utility.LeafiaPacket;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FFConverterTE extends FFDuctUtilityTEBase implements ITickable, IFluidHandler, IFFProvider, IFluidStandardReceiverMK2, IFluidStandardSenderMK2 {
	FluidTank ff = new FluidTank(10000);
	FluidTankNTM ntmf = new FluidTankNTM(Fluids.NONE,10000);
	@Override
	public void setType(FluidType type) {
		super.setType(type);
		ff.drain(ff.getCapacity()*2,true);
		ntmf.setTankType(type);
	}

	@Override
	public boolean hasCapability(Capability<?> capability,@Nullable EnumFacing facing) {
		IBlockState state = world.getBlockState(pos);
		if (state.getBlock() instanceof FFConverterBlock && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
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
			if (state.getBlock() instanceof FFConverterBlock) {
				EnumFacing facing = state.getValue(FFDuctUtilityBase.FACING);
				tryProvide(ff,world,pos.offset(facing),ForgeDirection.getOrientation(facing));
				//if (ntmf.getFill() > 0)
				//	tryProvide(ntmf,world,pos.offset(facing,-1),ForgeDirection.getOrientation(facing.getOpposite()));
				//else
					trySubscribe(getType(),world,pos.offset(facing,-1),ForgeDirection.getOrientation(facing.getOpposite()));
			}
		}
	}

	@Override
	public void update() {
		if (ntmf.getTankType().getFF() != null) {
			doUpdate();
			int filled = ff.fill(new FluidStack(ntmf.getTankType().getFF(),ntmf.getFill()),true);
			if (filled > 0)
				ntmf.setFill(ntmf.getFill()-filled);
		}
		LeafiaPacket packet = LeafiaPacket._start(this).__write(0,ff.getFluidAmount());
		if (ff.getFluid() != null && ff.getFluid().tag != null)
			packet.__write(1,ff.getFluid().tag);
		packet.__write(2,ntmf.getFill());
		packet.__sendToAffectedClients();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInfo(List<String> info) {
		info.add(TextFormatting.GRAY+I18nUtil.resolveKey("tile.ff_converter.ntmf",ntmf.getFill()+"mB"));
		info.add(TextFormatting.GRAY+I18nUtil.resolveKey("tile.ff_converter.ff",ff.getFluidAmount()+"mB"));
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		ff.readFromNBT(compound.getCompoundTag("ff"));
		ntmf.readFromNBT(compound,"ntmf");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setTag("ff",ff.writeToNBT(new NBTTagCompound()));
		ntmf.writeToNBT(compound,"ntmf");
		return super.writeToNBT(compound);
	}

	@Override
	public String getPacketIdentifier() {
		return "FF_CNVRTR";
	}
	@Override
	public void onReceivePacketLocal(byte key,Object value) {
		super.onReceivePacketLocal(key,value);
		if (key == 0) {
			if (getType().getFF() == null)
				ff.setFluid(null);
			else
				ff.setFluid(new FluidStack(getType().getFF(),(int) value));
		} else if (key == 1 && ff.getFluid() != null)
			ff.getFluid().tag = (NBTTagCompound)value;
		else if (key == 2)
			ntmf.setFill((int)value);
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

	@Override
	public @NotNull FluidTankNTM[] getReceivingTanks() {
		return new FluidTankNTM[]{ntmf};
	}

	@Override
	public FluidTankNTM[] getAllTanks() {
		return new FluidTankNTM[]{ntmf};
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
	public @NotNull FluidTankNTM[] getSendingTanks() {
		return new FluidTankNTM[]{ntmf};
	}
}
