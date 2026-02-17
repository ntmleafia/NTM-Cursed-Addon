package com.leafia.contents.machines.misc.heatex;

import com.hbm.api.energymk2.IEnergyReceiverMK2;
import com.hbm.api.fluidmk2.IFluidStandardReceiverMK2;
import com.hbm.api.fluidmk2.IFluidStandardSenderMK2;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.hbm.inventory.fluid.trait.FT_Coolable;
import com.hbm.inventory.fluid.trait.FT_Coolable.CoolingType;
import com.hbm.inventory.fluid.trait.FT_Heatable;
import com.hbm.inventory.fluid.trait.FT_Heatable.HeatingStep;
import com.hbm.inventory.fluid.trait.FT_Heatable.HeatingType;
import com.hbm.items.machine.IItemFluidIdentifier;
import com.hbm.lib.DirPos;
import com.hbm.lib.ForgeDirection;
import com.hbm.lib.Library;
import com.hbm.tileentity.IGUIProvider;
import com.hbm.tileentity.TileEntityMachineBase;
import com.leafia.contents.AddonFluids;
import com.leafia.contents.fluids.traits.FT_LFTRCoolant;
import com.leafia.contents.machines.misc.heatex.container.CoolantHeatexContainer;
import com.leafia.contents.machines.misc.heatex.container.CoolantHeatexGUI;
import com.leafia.contents.machines.reactors.lftr.components.MSRTEBase;
import com.leafia.contents.network.ff_duct.uninos.IFFProvider;
import com.leafia.contents.network.ff_duct.uninos.IFFReceiver;
import com.leafia.dev.LeafiaDebug;
import com.leafia.dev.LeafiaUtil;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.container_utility.LeafiaPacketReceiver;
import com.leafia.passive.LeafiaPassiveServer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CoolantHeatexTE extends TileEntityMachineBase implements ITickable, LeafiaPacketReceiver, IGUIProvider, IFluidHandler, IEnergyReceiverMK2, IFluidStandardReceiverMK2, IFFReceiver, IFluidStandardSenderMK2, IFFProvider {
	public long power;
	public static long powerConsumption = 5;
	public static long maxPower = 10_000;
	public float spin;
	public float lastSpin;
	public boolean ntmfMode = false;
	public int amountToCool = 24_000;
	public int tickDelay = 1;
	public int heatEnergy = 0;
	public int coolantMode = 2;
	public static final FluidType[] coolants = new FluidType[] {
			Fluids.AIR,
			Fluids.WATER,
			Fluids.COOLANT
	};
	public static final FluidType[] hot_coolants = new FluidType[] {
			AddonFluids.HOT_AIR,
			AddonFluids.HOT_WATER,
			Fluids.COOLANT_HOT
	};
	public FluidType inputFilter = AddonFluids.FLUORIDE;
	public FluidType outputFilter = AddonFluids.FLUORIDE;
	public static final int capacity = 2400000;
	public FluidTank ff_inputA = new FluidTank(capacity);
	public FluidTank ff_outputA = new FluidTank(capacity);
	public FluidTankNTM ntmf_inputA = new FluidTankNTM(inputFilter,capacity);
	public FluidTankNTM ntmf_outputA = new FluidTankNTM(outputFilter,capacity);
	public FluidTankNTM ntmf_inputB = new FluidTankNTM(Fluids.COOLANT,capacity);
	public FluidTankNTM ntmf_outputB = new FluidTankNTM(Fluids.COOLANT_HOT,capacity);

	public void updateCoolantMode() {
		FluidType input = coolants[coolantMode];
		FluidType output = hot_coolants[coolantMode];
		ntmf_inputB = changeFluidType(ntmf_inputB,input);
		ntmf_outputB = changeFluidType(ntmf_outputB,output);
	}

	public boolean isValidFluid(FluidType type) {
		FT_Coolable coolable = getCoolable(type);
		return (coolable != null) || type.hasTrait(FT_LFTRCoolant.class);
	}

	public void updateResource() {
		if (!ntmf_inputA.getTankType().equals(inputFilter)) {
			FT_Coolable coolable = getCoolable(inputFilter);
			if (inputFilter.hasTrait(FT_LFTRCoolant.class))
				outputFilter = inputFilter;
			else if (coolable != null) {
				if (coolable.getEfficiency(FT_Coolable.CoolingType.HEATEXCHANGER) > 0)
					outputFilter = coolable.coolsTo;
				else
					return;
			} else
				return;
			ntmf_inputA = changeFluidType(ntmf_inputA,inputFilter);
			ntmf_outputA = changeFluidType(ntmf_outputA,outputFilter);
			changeFluidType(ff_inputA,inputFilter);
			changeFluidType(ff_outputA,outputFilter);
		}
	}

	public void changeFluidType(FluidTank tank,FluidType type) {
		if (tank.getFluid() == null) return;
		FluidType currentType = AddonFluids.fromFF(tank.getFluid().getFluid());
		if (!currentType.equals(type))
			tank.drain(capacity*2,true);
	}
	public FluidTankNTM changeFluidType(FluidTankNTM tank,FluidType type) {
		if (!tank.getTankType().equals(type))
			return new FluidTankNTM(type,tank.getMaxFill());
		return tank;
	}

	public CoolantHeatexTE() {
		super(2,true,true);
		/*ff_inputA.fill(new FluidStack(AddonFluids.FLUORIDE.getFF(),24000),true);
		ff_outputA.fill(new FluidStack(AddonFluids.FLUORIDE.getFF(),24000),true);
		ntmf_inputA.setFill(24000);
		ntmf_inputB.setFill(24000);
		ntmf_outputA.setFill(24000);
		ntmf_outputB.setFill(24000);*/
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if (compound.hasKey("power"))
			power = compound.getLong("power");
		if (compound.hasKey("ntmfMode"))
			ntmfMode = compound.getBoolean("ntmfMode");
		if (compound.hasKey("coolantMode"))
			coolantMode = compound.getInteger("coolantMode");
		if (compound.hasKey("inputFilter")) {
			FluidType f = Fluids.fromID(compound.getInteger("inputFilter"));
			if (f.getFF() != null)
				inputFilter = f;
		}
		updateResource();
		updateCoolantMode();
		if (compound.hasKey("ntmf_inputB")) {
			ntmf_inputB.readFromNBT(compound,"ntmf_inputB");
			ntmf_outputB.readFromNBT(compound,"ntmf_outputB");
		}
		if (compound.hasKey("ntmf_inputA")) {
			ntmf_inputA.readFromNBT(compound,"ntmf_inputA");
			ntmf_outputA.readFromNBT(compound,"ntmf_outputA");
		}
		if (compound.hasKey("ff_inputA")) {
			ff_inputA.readFromNBT(compound.getCompoundTag("ff_inputA"));
			ff_outputA.readFromNBT(compound.getCompoundTag("ff_outputA"));
		}
		if (compound.hasKey("cycle"))
			amountToCool = compound.getInteger("cycle");
		if (compound.hasKey("delay"))
			tickDelay = compound.getInteger("delay");
	}

	@Override
	public @NotNull NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setLong("power",power);
		compound.setBoolean("ntmfMode",ntmfMode);
		compound.setInteger("coolantMode",coolantMode);
		ntmf_inputA.writeToNBT(compound,"ntmf_inputA");
		ntmf_outputA.writeToNBT(compound,"ntmf_outputA");
		ntmf_inputB.writeToNBT(compound,"ntmf_inputB");
		ntmf_outputB.writeToNBT(compound,"ntmf_outputB");
		compound.setTag("ff_inputA",ff_inputA.writeToNBT(new NBTTagCompound()));
		compound.setTag("ff_outputA",ff_outputA.writeToNBT(new NBTTagCompound()));
		compound.setInteger("inputFilter",inputFilter.getID());
		compound.setInteger("cycle",amountToCool);
		compound.setInteger("delay",tickDelay);
		return super.writeToNBT(compound);
	}

	public DirPos[] getConPos() {

		ForgeDirection dir = ForgeDirection.getOrientation(this.getBlockMetadata() - 10);
		ForgeDirection rot = dir.getRotation(ForgeDirection.UP);

		return new DirPos[] {
				new DirPos(pos.getX() + rot.offsetX * 4, pos.getY() + 1, pos.getZ() + rot.offsetZ * 4, rot),
				new DirPos(pos.getX() - rot.offsetX * 4, pos.getY() + 1, pos.getZ() - rot.offsetZ * 4, rot.getOpposite()),
				new DirPos(pos.getX() + dir.offsetX * 2 - rot.offsetX, pos.getY() + 1, pos.getZ() + dir.offsetZ * 2 - rot.offsetZ, dir),
				new DirPos(pos.getX() + dir.offsetX * 2 + rot.offsetX, pos.getY() + 1, pos.getZ() + dir.offsetZ * 2 + rot.offsetZ, dir),
				new DirPos(pos.getX() - dir.offsetX * 2 - rot.offsetX, pos.getY() + 1, pos.getZ() - dir.offsetZ * 2 - rot.offsetZ, dir.getOpposite()),
				new DirPos(pos.getX() - dir.offsetX * 2 + rot.offsetX, pos.getY() + 1, pos.getZ() - dir.offsetZ * 2 + rot.offsetZ, dir.getOpposite())
		};
	}

	public void subscribeToAllAround() {
		for(DirPos dirPos : getConPos()) {
			BlockPos pos = dirPos.getPos();
			trySubscribe(world,pos.getX(),pos.getY(),pos.getZ(),dirPos.getDir());
			trySubscribe(coolants[coolantMode],world,pos.getX(),pos.getY(),pos.getZ(),dirPos.getDir());
			if (ntmfMode)
				trySubscribe(inputFilter,world,pos.getX(),pos.getY(),pos.getZ(),dirPos.getDir());
			else
				trySubscribe(ff_inputA,new FluidStack(inputFilter.getFF(),0),world,pos,dirPos.getDir());

		}
	}

	public void sendFluidToAll() {
		for(DirPos dirPos : getConPos()) {
			BlockPos pos = dirPos.getPos();
			tryProvide(ntmf_outputB,world,pos.getX(),pos.getY(),pos.getZ(),dirPos.getDir());
			if (ntmfMode)
				tryProvide(ntmf_outputA,world,pos.getX(),pos.getY(),pos.getZ(),dirPos.getDir());
			else
				tryProvide(ff_outputA,world,pos,dirPos.getDir());

		}
	}

	@Override
	public void update() {
		if (!world.isRemote) {
			this.power = Library.chargeTEFromItems(inventory, 1, power, maxPower);
			ItemStack filterStack = inventory.getStackInSlot(0);
			if (!filterStack.isEmpty() && filterStack.getItem() instanceof IItemFluidIdentifier id) {
				FluidType type = id.getType(world,getPos().getX(),pos.getY(),pos.getZ(),filterStack);
				if (isValidFluid(type) && type.getFF() != null) {
					inputFilter = type;
					updateResource();
				}
			}
			subscribeToAllAround();
			if (heatEnergy <= getHeatRequired()*capacity) {
				if (power >= powerConsumption) {
					power -= powerConsumption;
					if (ntmfMode)
						tryConvert();
					else
						tryConvertFF();
				}
			}
			tryConvertB();
			sendFluidToAll();
			NBTTagCompound fluidTags = new NBTTagCompound();
			ntmf_inputB.writeToNBT(fluidTags,"ntmf_inputB");
			ntmf_outputB.writeToNBT(fluidTags,"ntmf_outputB");
			if (ntmfMode) {
				ntmf_inputA.writeToNBT(fluidTags,"ntmf_inputA");
				ntmf_outputA.writeToNBT(fluidTags,"ntmf_outputA");
			} else {
				fluidTags.setTag("ff_inputA",ff_inputA.writeToNBT(new NBTTagCompound()));
				fluidTags.setTag("ff_outputA",ff_outputA.writeToNBT(new NBTTagCompound()));
			}
			LeafiaPacket._start(this)
					.__write(0,power)
					.__write(1,ntmfMode)
					.__write(2,coolantMode)
					.__write(3,fluidTags)
					.__write(4,inputFilter.getID())
					.__write(5,outputFilter.getID())
					.__write(6,amountToCool)
					.__write(7,tickDelay)
					.__sendToAffectedClients();
			markChanged();
		} else {
			this.lastSpin = this.spin;
			if (power >= powerConsumption) {
				this.spin += 15F;
				if (this.spin >= 360F) {
					this.spin -= 360F;
					this.lastSpin -= 360F;
				}
			}
		}
	}

	AxisAlignedBB bb = null;
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		if(bb == null) {
			bb = new AxisAlignedBB(
					pos.getX() - 3,
					pos.getY(),
					pos.getZ() - 3,
					pos.getX() + 4,
					pos.getY() + 3,
					pos.getZ() + 4
			);
		}
		return bb;
	}

	static FT_Heatable virtualft_air = new FT_Heatable().addStep(50,2,AddonFluids.HOT_AIR,3).setEff(HeatingType.BOILER, 1.0D);
	static FT_Heatable virtualft_water = new FT_Heatable().addStep(70,1,AddonFluids.HOT_WATER,1).setEff(HeatingType.BOILER, 1.0D);
	static FT_Coolable virtualft_lps = new FT_Coolable(Fluids.WATER,1,1,20).setEff(CoolingType.HEATEXCHANGER, 1.0D);
	FT_Heatable getHeatable(FluidType fluid) {
		if (fluid.equals(Fluids.AIR))
			return virtualft_air;
		if (fluid.equals(Fluids.WATER))
			return virtualft_water;
		return fluid.getTrait(FT_Heatable.class);
	}
	FT_Coolable getCoolable(FluidType fluid) {
		if (fluid.equals(Fluids.SPENTSTEAM))
			return virtualft_lps;
		if (fluid.hasTrait(FT_Coolable.class)) {
			FT_Coolable trait = fluid.getTrait(FT_Coolable.class);
			if (trait.getEfficiency(CoolingType.HEATEXCHANGER) > 0)
				return trait;
		}
		return null;
	}

	double getHeatRequired() {
		FT_Heatable heatable = getHeatable(coolants[coolantMode]);
		return heatable.getFirstStep().heatReq/(double)heatable.getFirstStep().amountProduced*heatable.getFirstStep().amountReq;
	}

	protected void tryConvertB() {
		FT_Heatable trait = getHeatable(coolants[coolantMode]);
		if (trait.getEfficiency(FT_Heatable.HeatingType.BOILER) > 0) {

			HeatingStep entry = trait.getFirstStep();
			int inputOps = ntmf_inputB.getFill() / entry.amountReq;
			int outputOps = (ntmf_outputB.getMaxFill() - ntmf_outputB.getFill()) / entry.amountProduced;
			int heatOps = this.heatEnergy / entry.heatReq;

			int ops = Math.min(inputOps, Math.min(outputOps, heatOps));

			ntmf_inputB.setFill(ntmf_inputB.getFill() - entry.amountReq * ops);
			ntmf_outputB.setFill(ntmf_outputB.getFill() + entry.amountProduced * ops);
			heatEnergy -= entry.heatReq * ops;
		}
	}

	protected void tryConvert() {
		FT_Coolable trait = getCoolable(inputFilter);
		if(trait == null) return;
		if(tickDelay < 1) tickDelay = 1;
		if(world.getTotalWorldTime() % tickDelay != 0) return;

		int inputOps = ntmf_inputA.getFill() / trait.amountReq;
		int outputOps = (ntmf_outputA.getMaxFill() - ntmf_outputA.getFill()) / trait.amountProduced;
		int opCap = this.amountToCool;

		int ops = Math.min(inputOps, Math.min(outputOps, opCap));
		ntmf_inputA.setFill(ntmf_inputA.getFill() - trait.amountReq * ops);
		ntmf_outputA.setFill(ntmf_outputA.getFill() + trait.amountProduced * ops);
		this.heatEnergy += (int) (trait.heatEnergy * ops * trait.getEfficiency(FT_Coolable.CoolingType.HEATEXCHANGER));
		this.markDirty();
	}
	protected void tryConvertFF() {
		if(tickDelay < 1) tickDelay = 1;
		if(world.getTotalWorldTime() % tickDelay != 0) return;

		int amountReq = 0;
		int amountProduced = 0;
		int heat = 0;
		double efficiency = 1;
		FluidStack stack1 = ff_outputA.getFluid() != null ? ff_outputA.getFluid() : new FluidStack(outputFilter.getFF(),1);

		FT_Coolable trait = getCoolable(inputFilter);
		if(trait != null) {
			amountReq = trait.amountReq;
			amountProduced = trait.amountProduced;
			heat = trait.heatEnergy;
			efficiency = trait.getEfficiency(CoolingType.HEATEXCHANGER);
		} else if (inputFilter.hasTrait(FT_LFTRCoolant.class)) {
			amountProduced = 1;
			amountReq = 1;
			FluidStack stack = ff_inputA.getFluid();
			if (stack != null) {
				NBTTagCompound compound = MSRTEBase.nbtProtocol(stack.tag);
				double h = compound.getDouble("heat");
				compound.setDouble("heat",0);
				heat = (int)(h*2);
				//stack.tag = compound;
				stack1 = new FluidStack(stack.getFluid(),stack.amount,compound);
			} else return;
		}

		int inputOps = ff_inputA.getFluidAmount() / amountReq;
		int outputOps = (ff_outputA.getCapacity() - ff_outputA.getFluidAmount()) / amountProduced;
		int opCap = this.amountToCool;

		int ops = Math.min(inputOps, Math.min(outputOps, opCap));
		ff_inputA.drain(amountReq * ops,true);
		stack1 = new FluidStack(stack1,amountProduced * ops);
		FluidTank tank = new FluidTank(stack1,stack1.amount);
		LeafiaUtil.fillFF(tank,ff_outputA,amountProduced * ops);
		//ff_outputA.fill(new FluidStack(stack1,amountProduced * ops),true);
		this.heatEnergy += (int) (heat * ops * efficiency);
		this.markDirty();
	}

	@Override
	public String getDefaultName() {
		return "container.coolant_heatex";
	}

	@Override
	public Container provideContainer(int i,EntityPlayer entityPlayer,World world,int i1,int i2,int i3) {
		return new CoolantHeatexContainer(entityPlayer.inventory,this);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public GuiScreen provideGUI(int i,EntityPlayer entityPlayer,World world,int i1,int i2,int i3) {
		return new CoolantHeatexGUI(entityPlayer.inventory,this);
	}

	@Override
	public String getPacketIdentifier() {
		return "C_HEATEX";
	}

	@Override
	public void onReceivePacketLocal(byte key,Object value) {
		switch(key) {
			case 0:
				power = (long)value;
				break;
			case 1:
				ntmfMode = (boolean)value;
				break;
			case 2:
				coolantMode = (int)value;
				break;
			case 3:
				NBTTagCompound tag = (NBTTagCompound)value;
				ntmf_inputB.readFromNBT(tag,"ntmf_inputB");
				ntmf_outputB.readFromNBT(tag,"ntmf_outputB");
				if (tag.hasKey("ntmf_inputA")) {
					ntmf_inputA.readFromNBT(tag,"ntmf_inputA");
					ntmf_outputA.readFromNBT(tag,"ntmf_outputA");
				}
				if (tag.hasKey("ff_inputA")) {
					ff_inputA.readFromNBT(tag.getCompoundTag("ff_inputA"));
					ff_outputA.readFromNBT(tag.getCompoundTag("ff_outputA"));
				}
				break;
			case 4:
				inputFilter = Fluids.fromID((int)value);
				break;
			case 5:
				outputFilter = Fluids.fromID((int)value);
				break;
			case 6:
				amountToCool = (int)value;
				break;
			case 7:
				tickDelay = (int)value;
				break;
		}
	}

	@Override
	public void onReceivePacketServer(byte key,Object value,EntityPlayer plr) {
		switch(key) {
			case 0:
				if ((boolean)value) {
					LeafiaPassiveServer.queueFunction(()->{
						coolantMode = Math.floorMod(coolantMode+1,3);
						updateCoolantMode();
					});
				} else
					ntmfMode = !ntmfMode;
				break;
			case 1:
				amountToCool = (int)value;
				break;
			case 2:
				tickDelay = (int)value;
				break;
		}
	}

	@Override
	public void onPlayerValidate(EntityPlayer plr) {

	}

	@Override
	public IFluidTankProperties[] getTankProperties() {
		return new IFluidTankProperties[]{
				ff_inputA.getTankProperties()[0],
				ff_outputA.getTankProperties()[0]
		};
	}
	@Override
	public int fill(FluidStack resource,boolean doFill) {
		if (!ntmfMode) {
			FluidType ntmf = AddonFluids.fromFF(resource.getFluid());
			if (ntmf.equals(inputFilter))
				return ff_inputA.fill(resource,doFill);
			//if (ntmf.equals(coolants[coolantMode]))
			//	return ff_inputB.fill(resource,doFill);
		}
		return 0;
	}
	@Override
	public @Nullable FluidStack drain(FluidStack resource,boolean doDrain) {
		/*if (!ntmfMode) {
			FluidType ntmf = AddonFluids.fromFF(resource.getFluid());
			if (ntmf.equals(outputFilter))
				return ff_inputB.drain(resource,doDrain);
			if (ntmf.equals(hot_coolants[coolantMode]))
				return ff_outputB.drain(resource,doDrain);
		}*/
		return null;
	}
	@Override
	public @Nullable FluidStack drain(int maxDrain,boolean doDrain) {
		return null;
	}

	@Override
	public long getPower() {
		return power;
	}

	@Override
	public void setPower(long l) {
		power = l;
	}

	@Override
	public long getMaxPower() {
		return maxPower;
	}

	@Override
	public @NotNull FluidTankNTM[] getReceivingTanks() {
		if (ntmfMode)
			return new FluidTankNTM[]{ntmf_inputA,ntmf_inputB};
		return new FluidTankNTM[]{ntmf_inputB};
	}

	@Override
	public @NotNull FluidTankNTM[] getSendingTanks() {
		if (ntmfMode)
			return new FluidTankNTM[]{ntmf_outputA,ntmf_outputB};
		return new FluidTankNTM[]{ntmf_outputB};
	}

	@Override
	public FluidTankNTM[] getAllTanks() {
		if (ntmfMode)
			return new FluidTankNTM[]{ntmf_inputA,ntmf_inputB,ntmf_outputA,ntmf_outputB};
		return new FluidTankNTM[]{ntmf_inputB,ntmf_outputB};
	}

	@Override
	public FluidTank getCorrespondingTank(FluidStack stack) {
		FluidType type = AddonFluids.fromFF(stack.getFluid());
		if (inputFilter.equals(type))
			return ff_inputA;
		//else if (coolants[coolantMode].equals(type))
		//	return ff_inputB;
		return null;
	}
}