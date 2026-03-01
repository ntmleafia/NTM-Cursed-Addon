package com.leafia.contents.machines.processing.mixingvat;

import com.hbm.api.energymk2.IEnergyReceiverMK2;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.machine.IItemFluidIdentifier;
import com.hbm.lib.ForgeDirection;
import com.hbm.lib.Library;
import com.hbm.tileentity.IGUIProvider;
import com.leafia.contents.AddonFluids;
import com.leafia.contents.fluids.traits.FT_LFTRCoolant;
import com.leafia.contents.machines.processing.mixingvat.container.MixingVatContainer;
import com.leafia.contents.machines.processing.mixingvat.container.MixingVatNclrGUI;
import com.leafia.contents.machines.reactors.lftr.components.MSRTEBase;
import com.leafia.contents.machines.reactors.lftr.components.element.MSRElementTE.MSRFuel;
import com.leafia.contents.network.ff_duct.uninos.IFFProvider;
import com.leafia.contents.network.ff_duct.uninos.IFFReceiver;
import com.leafia.dev.blocks.blockbase.AddonBlockDummyable;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.container_utility.LeafiaPacketReceiver;
import com.leafia.dev.machine.LCETileEntityMachineBase;
import com.leafia.dev.math.FiaMatrix;
import com.llib.group.LeafiaMap;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Map.Entry;

public class MixingVatTE extends LCETileEntityMachineBase implements LeafiaPacketReceiver, ITickable, IEnergyReceiverMK2, IFluidHandler, IGUIProvider, IFFReceiver, IFFProvider {
	public long power;
	public static final int maxPower = 100_000;
	static int maxProgress = 20*3;
	int progress = 0;
	public FluidTank tankNc0 = new FluidTank(4000);
	public FluidTank tankNc1 = new FluidTank(4000);
	public FluidType inputTypeNc = AddonFluids.FLUORIDE;

	public float mixerRot = -1;
	public float prevRot = 0;

	public MixingVatTE() {
		super(3+10+10);
	}
	public long getProgressScaled(long i) {
		return (progress * i) / maxProgress;
	}

	@Override
	public String getName() {
		return "tile.mixingvat.name";
	}

	@Override
	public String getDefaultName() {
		return getName();
	}

	@Override
	public void setPower(long power) {
		this.power = power;
	}
	@Override
	public long getPower() {
		return power;
	}
	@Override
	public long getMaxPower() {
		return maxPower;
	}
	public long getPowerRemainingScaled(long i) {
		return (power * i) / maxPower;
	}

	@Override
	public <T> T getCapability(Capability<T> capability,EnumFacing facing) {
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this);
		} else {
			return super.getCapability(capability, facing);
		}
	}
	EnumFacing getFacingForward() {
		AddonBlockDummyable dummyable = (AddonBlockDummyable)getBlockType();
		FiaMatrix mat = dummyable.getRotationMat(world,pos);
		if (mat == null) return null;
		return EnumFacing.getFacingFromVector((float)mat.frontVector.x,(float)mat.frontVector.y,(float)mat.frontVector.z);
	}
	EnumFacing getFacing() {
		AddonBlockDummyable dummyable = (AddonBlockDummyable)getBlockType();
		FiaMatrix mat = dummyable.getRotationMat(world,pos);
		if (mat == null) return null;
		mat = mat.rotateY(180);
		return EnumFacing.getFacingFromVector((float)mat.frontVector.x,(float)mat.frontVector.y,(float)mat.frontVector.z);
	}
	@Override
	public boolean hasCapability(Capability<?> capability,EnumFacing facing) {
		if(capability == CapabilityEnergy.ENERGY || capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return true; // getting lazier and lazier //facing == null || facing.equals(getFacing());
		return super.hasCapability(capability, facing);
	}

	@Override
	public IFluidTankProperties[] getTankProperties() {
		return new IFluidTankProperties[]{tankNc0.getTankProperties()[0], tankNc1.getTankProperties()[0]};
	}

	@Override
	public int fill(FluidStack resource,boolean doFill) {
		if (!nuclearMode) {
			return 0;
		} else {
			FluidType ntmf = AddonFluids.fromFF(resource.getFluid());
			if (ntmf != Fluids.NONE) {
				if (ntmf.hasTrait(FT_LFTRCoolant.class))
					return tankNc0.fill(resource,doFill);
			}
		}
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
	public @Nullable FluidStack drainOut(FluidStack resource,boolean doDrain) {
		if (!nuclearMode) {

		} else {
			return tankNc1.drain(resource,doDrain);
		}
		return null;
	}
	public @Nullable FluidStack drainOut(int maxDrain,boolean doDrain) {
		if (!nuclearMode) {

		} else {
			return tankNc1.drain(maxDrain,doDrain);
		}
		return null;
	}


	MSRFuel getFuelType(ItemStack stack) {
		Item item = stack.getItem();
		/*
		if (item == Nuggies.nugget_uranium_fuel)
			return MSRFuel.meu;
		 */
		for (MSRFuel fuel : MSRFuel.values()) {
			for (Item i : fuel.items)
				if (i.equals(item))
					return fuel;
		}
		if (!stack.isEmpty()) {
			for (int oreID : OreDictionary.getOreIDs(stack)) {
				String id = OreDictionary.getOreName(oreID);
				for (MSRFuel fuel : MSRFuel.values()) {
					for (String dict : fuel.dicts)
						if (dict.equals(id))
							return fuel;
				}
			}
		}
		return null;
	}

	Map<String,Double> createMixture() {
		Map<String,Double> mixture = new LeafiaMap<>();
		for (int i = 15; i < 21; i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			MSRFuel fuel = getFuelType(stack);
			if (fuel != null)
				mixture.put(fuel.name(),mixture.getOrDefault(fuel.name(),0d)+1);
		}
		return mixture;
	}
	@Override
	public void update() {
		if (!world.isRemote) {
			EnumFacing facing = getFacingForward(); {
				trySubscribe(world,pos.offset(facing.getOpposite()),ForgeDirection.getOrientation(facing.getOpposite()));
				trySubscribe(world,pos.offset(facing.rotateY()).offset(facing.getOpposite()),ForgeDirection.getOrientation(facing.getOpposite()));
				trySubscribe(world,pos.offset(facing,2).offset(facing.rotateY().getOpposite()),ForgeDirection.getOrientation(facing.rotateY().getOpposite()));
				trySubscribe(world,pos.offset(facing,2).offset(facing.rotateY(),2),ForgeDirection.getOrientation(facing.rotateY()));
			}
			power = Library.chargeTEFromItems(inventory,0,power,maxPower);
			FluidStack stack = tankNc0.getFluid();
			Map<String,Double> itemMixture = createMixture();
			if (stack != null && !itemMixture.isEmpty() && stack.amount >= 1000 && power > 1000/20) {
				FluidStack mixed = stack.copy();
				mixed.amount = 1000;
				Map<String,Double> mixture = MSRTEBase.readMixture(MSRTEBase.nbtProtocol(mixed.tag));
				for (Entry<String,Double> entry : itemMixture.entrySet())
					mixture.put(entry.getKey(),mixture.getOrDefault(entry.getKey(),0d)+entry.getValue());
				NBTTagCompound tag = new NBTTagCompound();
				tag.setTag("itemMixture",MSRTEBase.writeMixture(mixture));
				mixed.tag = tag;
				if (tankNc1.fill(mixed,false) >= 1000) {
					power -= 1000/20;
					progress++;
					if (progress >= maxProgress) {
						progress = 0;
						tankNc1.fill(mixed,true);
						tankNc0.drain(1000,true);
						for (int i = 15; i < 21; i++) {
							if (getFuelType(inventory.getStackInSlot(i)) != null)
								inventory.getStackInSlot(i).shrink(1);
						}
					}
				} else
					progress = 0;
			} else
				progress = 0;
			if (!nuclearMode) {

			} else {
				ItemStack filterStack = inventory.getStackInSlot(21);
				if (!filterStack.isEmpty() && filterStack.getItem() instanceof IItemFluidIdentifier id) {
					FluidType type = id.getType(world,0,0,0,filterStack);
					if (type.hasTrait(FT_LFTRCoolant.class) && inventory.getStackInSlot(22).isEmpty() && type.getFF() != null) {
						inventory.insertItem(22,filterStack.copy(),false);
						filterStack.shrink(1);
						inputTypeNc = type;
						if (tankNc0.getFluid() != null) {
							if (!type.getFF().equals(tankNc0.getFluid().getFluid()))
								tankNc0.drain(9999,true);
						}
						if (tankNc1.getFluid() != null) {
							if (!type.getFF().equals(tankNc1.getFluid().getFluid()))
								tankNc1.drain(9999,true);
						}
					}
				}
				tryProvide(tankNc1,world,pos.offset(facing,2).offset(facing.rotateY().getOpposite()),ForgeDirection.getOrientation(facing.rotateY().getOpposite()));
				tryProvide(tankNc1,world,pos.offset(facing,2).offset(facing.rotateY(),2),ForgeDirection.getOrientation(facing.rotateY()));
				trySubscribe(tankNc0,new FluidStack(inputTypeNc.getFF(),0),world,pos.offset(facing.getOpposite()),ForgeDirection.getOrientation(facing.getOpposite()));
				trySubscribe(tankNc0,new FluidStack(inputTypeNc.getFF(),0),world,pos.offset(facing.rotateY()).offset(facing.getOpposite()),ForgeDirection.getOrientation(facing.getOpposite()));
				//fillFluid(pos.offset(facing,2).offset(facing.rotateY(),-1),tankNc1);
				//fillFluid(pos.offset(facing,2).offset(facing.rotateY(),2),tankNc1);
			}
			LeafiaPacket._start(this)
					.__write(0,power)
					.__write(1,nuclearMode)
					.__write(2,progress)
					.__write(6,tankNc0.writeToNBT(new NBTTagCompound()))
					.__write(7,tankNc1.writeToNBT(new NBTTagCompound()))
					.__write(8,inputTypeNc.getID())
					.__sendToAffectedClients();
			markChanged();
		} else {
			if (mixerRot == -1)
				mixerRot = world.rand.nextFloat()*360;
			prevRot = mixerRot;
			if (progress > 0) {
				this.mixerRot += 9F;
				if (this.mixerRot >= 360F) {
					this.mixerRot -= 360F;
					this.prevRot -= 360F;
				}
			}
		}
	}

	public boolean nuclearMode = true;

	@Override
	public Container provideContainer(int ID,EntityPlayer player,World world,int x,int y,int z) {
		return new MixingVatContainer(player.inventory,this);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public GuiScreen provideGUI(int ID,EntityPlayer player,World world,int x,int y,int z) {
		return new MixingVatNclrGUI(player.inventory,this);
	}

	@Override
	public String getPacketIdentifier() {
		return "MIXING_VAT";
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		nuclearMode = compound.getBoolean("nuclear");
		inputTypeNc = Fluids.fromID(compound.getInteger("filter"));
		tankNc0.readFromNBT(compound.getCompoundTag("tankNc0"));
		tankNc1.readFromNBT(compound.getCompoundTag("tankNc1"));
		progress = compound.getInteger("progress");
	}
	@Override
	public @NotNull NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setBoolean("nuclear",nuclearMode);
		compound.setInteger("filter",inputTypeNc.getID());
		compound.setTag("tankNc0",tankNc0.writeToNBT(new NBTTagCompound()));
		compound.setTag("tankNc1",tankNc1.writeToNBT(new NBTTagCompound()));
		compound.setInteger("progress",progress);
		return super.writeToNBT(compound);
	}

	@Override
	public void onReceivePacketLocal(byte key,Object value) {
		switch(key) {
			case 0:
				power = (long)value;
				break;
			case 1:
				nuclearMode = (boolean)value;
				break;
			case 2:
				progress = (int)value;
				break;
			case 6:
				tankNc0.readFromNBT((NBTTagCompound)value);
				break;
			case 7:
				tankNc1.readFromNBT((NBTTagCompound)value);
				break;
			case 8:
				inputTypeNc = Fluids.fromID((int)value);
				break;
		}
	}

	@Override
	public void onReceivePacketServer(byte key,Object value,EntityPlayer plr) {
		if (key == 0)
			tankNc1.drain(5000,true);
	}

	@Override
	public void onPlayerValidate(EntityPlayer plr) {

	}

	@Override
	public FluidTank getSendingTank(FluidStack stack) {
		return tankNc1;
	}

	@Override
	public FluidTank getCorrespondingTank(FluidStack stack) {
		return tankNc0;
	}

	AxisAlignedBB bb = null;
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		if(bb == null) {
			bb = new AxisAlignedBB(
					pos.getX() - 2,
					pos.getY(),
					pos.getZ() - 2,
					pos.getX() + 3,
					pos.getY() + 2,
					pos.getZ() + 3
			);
		}
		return bb;
	}
}
