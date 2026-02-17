package com.leafia.contents.debug.ff_test.source;

import com.hbm.lib.ForgeDirection;
import com.leafia.contents.AddonFluids.AddonFF;
import com.leafia.contents.machines.reactors.lftr.components.element.MSRElementTE;
import com.leafia.contents.machines.reactors.lftr.components.element.MSRElementTE.MSRFuel;
import com.leafia.contents.network.ff_duct.uninos.IFFProvider;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.container_utility.LeafiaPacketReceiver;
import com.llib.group.LeafiaMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class DebugSourceTE extends TileEntity implements ITickable, IFFProvider, LeafiaPacketReceiver {
	FluidTank zaza = new FluidTank(10000);
	public DebugSourceTE() {
		//zaza.fill(new FluidStack(AddonFF.fluoride,10000),true);
	}
	@Override
	public void update() {
		FluidStack stacc = new FluidStack(AddonFF.fluoride,10000);
		Map<String,Double> mix = new LeafiaMap<>();
		//mix.put(MSRFuel.u233.name(),/*0.00000000001*/6d);
		mix.put(MSRFuel.th232.name(),4d);
		mix.put(MSRFuel.u235.name(),2d);
		stacc.tag = MSRElementTE.writeMixture(mix,new NBTTagCompound());
		zaza.drain(999999999,true); // reset shit
		zaza.fill(stacc,true); // and refill shit
		if (!world.isRemote) {
			for (EnumFacing facing : EnumFacing.values())
				tryProvide(zaza,world,pos.offset(facing),ForgeDirection.getOrientation(facing));
			//zaza.fill(new FluidStack(AddonFF.fluoride,10),true);
			LeafiaPacket._start(this).__write(0,zaza.writeToNBT(new NBTTagCompound())).__sendToAffectedClients();
		}
	}
	
	@Override
	public @Nullable <T> T getCapability(Capability<T> capability,@Nullable EnumFacing facing) {
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this);
		return super.getCapability(capability,facing);
	}

	@Override
	public boolean hasCapability(Capability<?> capability,@Nullable EnumFacing facing) {
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return true;
		return super.hasCapability(capability,facing);
	}

	@Override
	public String getPacketIdentifier() {
		return "debug_ff_source";
	}
	@Override
	public void onReceivePacketLocal(byte key,Object value) {
		zaza.readFromNBT((NBTTagCompound)value);
	}

	@Override
	public void onReceivePacketServer(byte key,Object value,EntityPlayer plr) { }

	@Override
	public void onPlayerValidate(EntityPlayer plr) { }

	@Override
	public IFluidTankProperties[] getTankProperties() {
		return zaza.getTankProperties();
	}
	@Override
	public int fill(FluidStack resource,boolean doFill) {
		return 0;
	}
	@Override
	public @Nullable FluidStack drain(FluidStack resource,boolean doDrain) {
		return zaza.drain(resource,doDrain);
	}
	@Override
	public @Nullable FluidStack drain(int maxDrain,boolean doDrain) {
		return zaza.drain(maxDrain,doDrain);
	}
}
