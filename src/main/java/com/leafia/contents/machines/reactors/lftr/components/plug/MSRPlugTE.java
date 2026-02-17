package com.leafia.contents.machines.reactors.lftr.components.plug;

import com.hbm.handler.radiation.ChunkRadiationManager;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.lib.ForgeDirection;
import com.leafia.contents.AddonBlocks;
import com.leafia.contents.AddonFluids;
import com.leafia.contents.fluids.traits.FT_LFTRCoolant;
import com.leafia.contents.machines.reactors.lftr.components.MSRTEBase;
import com.leafia.contents.network.ff_duct.uninos.IFFProvider;
import com.leafia.contents.network.ff_duct.uninos.IFFReceiver;
import com.leafia.dev.LeafiaDebug;
import com.leafia.dev.container_utility.LeafiaPacket;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import org.jetbrains.annotations.Nullable;

public class MSRPlugTE extends MSRTEBase implements IFluidHandler, IFFReceiver, IFFProvider {
	public boolean molten = false;
	FluidType inputType = AddonFluids.FLUORIDE;
	public void setType(FluidType type) {
		inputType = type;
		sendTypeUpdatePacket();
	}
	EnumFacing getDirection() {
		IBlockState state = world.getBlockState(pos);
		if (state.getBlock() instanceof MSRPlugBlock)
			return state.getValue(BlockHorizontal.FACING);
		return EnumFacing.NORTH;
	}
	public MSRPlugTE() {
		tank = new FluidTank(10000);
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
		return (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && (facing == null || facing.equals(getDirection()) || facing.equals(EnumFacing.DOWN))) || super.hasCapability(capability, facing);
	}
	@Override
	public IFluidTankProperties[] getTankProperties() {
		return tank.getTankProperties();
	}
	@Override
	public int fill(FluidStack resource,boolean doFill) {
		FluidType ntmf = AddonFluids.fromFF(resource.getFluid());
		if (ntmf.equals(Fluids.NONE)) return 0;
		boolean compatible = true;
		if (tank.getFluid() != null)
			compatible = resource.getFluid().equals(tank.getFluid().getFluid());
		if (compatible && ntmf.hasTrait(FT_LFTRCoolant.class)) {
			if (doFill) {
				if (tank.getFluid() != null) {
					transferStats(resource,tank.getFluid(),1);
					return tank.fill(new FluidStack(tank.getFluid(),resource.amount),doFill);
				} else
					return tank.fill(resource,doFill);
			} else {
				if (tank.getFluid() != null)
					return tank.fill(new FluidStack(tank.getFluid(),resource.amount),false);
				else
					return tank.fill(resource,false);
			}
		} else
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
	public String getPacketIdentifier() {
		return "MSRPlug";
	}
	/*@Override
	public void sendFluids() {
		Tracker._startProfile(this,"sendFluids");
		int demand = 0;
		List<MSRTEBase> list = new ArrayList<>();
		for (EnumFacing facing : EnumFacing.values()) {
			BlockPos target = pos.add(facing.getDirectionVec());
			if (world.getTileEntity(target) instanceof MSRTEBase te && !(te instanceof MSRPlugTE)) {
				demand += te.tank.getCapacity()-te.tank.getFluidAmount();
				list.add(te);
			}
		}
		demand = Math.min(demand,tank.getFluidAmount());
		if (!list.isEmpty()) {
			demand /= list.size();
			if (demand > 0) {
				for (MSRTEBase te : list) {
					//Tracker._tracePosition(this,te.pos,"+"+demand+"mB");
					transferStats(tank.getFluid(),te.tank.getFluid(),list.size());
					assert tank.getFluid() != null;
					tank.drain(te.tank.fill(new FluidStack(te.tank.getFluid() == null ? tank.getFluid() : te.tank.getFluid(),demand),true),true);
				}
			}
		}
		Tracker._endProfile(this);
	}*/
	@Override
	public void update() {
		if (!world.isRemote) {
			sendFluids();
			trySubscribe(tank,new FluidStack(inputType.getFF(),0),world,pos.offset(getDirection()),ForgeDirection.getOrientation(getDirection()));
			if (tank.getFluid() != null) {
				if (nbtProtocol(tank.getFluid().tag).getDouble("heat") > 4000-getBaseTemperature(AddonFluids.fromFF(tank.getFluid().getFluid())))
					molten = true;
				if (molten)
					tryProvide(tank,world,pos.down(),ForgeDirection.DOWN);
				Material mat = world.getBlockState(pos.down()).getMaterial();
				if (molten && mat.isReplaceable() && !mat.isLiquid()) {
					this.world.playSound(null,pos,SoundEvents.ENTITY_GENERIC_SPLASH,SoundCategory.BLOCKS,3.0F,0.5F);
					world.setBlockState(pos.down(),AddonBlocks.fluid_fluoride.getDefaultState());
				}
				if (molten && world.getBlockState(pos.down()).getBlock() == AddonBlocks.fluid_fluoride) {
					tank.drain(1000,true);
					double rad = ChunkRadiationManager.proxy.getRadiation(world,pos.up());
					ChunkRadiationManager.proxy.incrementRad(world,pos.down(),rad,rad);
				}
			}
			LeafiaDebug.debugPos(world,pos,0.05f,0xFFFF00,tank.getFluidAmount()+"mB");
			generateTankPacket().__write(0,molten).__sendToAffectedClients();
		}
	}

	@Override
	public void onReceivePacketLocal(byte key,Object value) {
		super.onReceivePacketLocal(key,value);
		if (key == 0)
			molten = (boolean)value;
		else if (key == 1)
			inputType = Fluids.fromID((int)value);
	}

	@Override
	public void onPlayerValidate(EntityPlayer plr) {
		LeafiaPacket._start(this).__write(1,inputType.getID()).__sendToClient(plr);
	}

	public void sendTypeUpdatePacket() {
		LeafiaPacket._start(this).__write(1,inputType.getID()).__sendToAffectedClients();
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		molten = compound.getBoolean("molten");
		inputType = Fluids.fromID(compound.getInteger("filter"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setBoolean("molten",molten);
		compound.setInteger("filter",inputType.getID());
		return super.writeToNBT(compound);
	}

	@Override
	public FluidTank getCorrespondingTank(FluidStack stack) {
		return tank;
	}
}
