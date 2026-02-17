package com.leafia.contents.machines.reactors.lftr.processing.separator;

import com.hbm.api.energymk2.IEnergyReceiverMK2;
import com.hbm.api.fluidmk2.IFluidStandardReceiverMK2;
import com.hbm.api.fluidmk2.IFluidStandardSenderMK2;
import com.hbm.interfaces.IControlReceiver;
import com.hbm.inventory.UpgradeManagerNT;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.hbm.items.machine.ItemMachineUpgrade;
import com.hbm.lib.DirPos;
import com.hbm.lib.Library;
import com.hbm.tileentity.IGUIProvider;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.util.BobMathUtil;
import com.hbm.util.Tuple.Pair;
import com.hbm.util.Tuple.Triplet;
import com.leafia.contents.machines.reactors.lftr.components.element.MSRElementTE;
import com.leafia.contents.machines.reactors.lftr.components.element.MSRElementTE.MSRFuel;
import com.leafia.contents.machines.reactors.lftr.processing.separator.container.SaltSeparatorContainer;
import com.leafia.contents.machines.reactors.lftr.processing.separator.container.SaltSeparatorGUI;
import com.leafia.contents.machines.reactors.lftr.processing.separator.recipes.SaltSeparatorRecipe;
import com.leafia.contents.machines.reactors.lftr.processing.separator.recipes.SaltSeparatorRecipes;
import com.leafia.contents.network.FFNBT;
import com.leafia.contents.network.ff_duct.uninos.IFFProvider;
import com.leafia.contents.network.ff_duct.uninos.IFFReceiver;
import com.leafia.dev.LeafiaDebug;
import com.leafia.dev.LeafiaUtil;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.container_utility.LeafiaPacketReceiver;
import com.llib.exceptions.LeafiaDevFlaw;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SaltSeparatorTE extends TileEntityMachineBase implements ITickable, IEnergyReceiverMK2, IControlReceiver, IGUIProvider, LeafiaPacketReceiver, IFluidStandardReceiverMK2, IFluidStandardSenderMK2, IFFReceiver, IFFProvider {
	public SaltSeparatorModule module;
	public long maxPower = 100_000;
	public long power = 0;
	public boolean didProcess = false;
	public FluidType saltType = Fluids.NONE;
	public FluidTank saltTank = new FluidTank(12000);
	public FluidTankNTM[] inputTanks = new FluidTankNTM[]{
			new FluidTankNTM(Fluids.NONE,12000),
			new FluidTankNTM(Fluids.NONE,12000)
	};
	public FluidTankNTM[] outputTanks = new FluidTankNTM[]{
			new FluidTankNTM(Fluids.NONE,12000),
			new FluidTankNTM(Fluids.NONE,12000),
			new FluidTankNTM(Fluids.NONE,12000)
	};
	public FluidTank bufferIn = new FluidTank(12000);
	public FluidTank bufferOut = new FluidTank(12000);
	public SaltSeparatorTE() {
		super(14);
		module = new SaltSeparatorModule(0,this,inventory)
				.fluidInput(inputTanks[0],inputTanks[1])
				.fluidOutput(outputTanks[0],outputTanks[1],outputTanks[2])
				.saltTank(saltTank);
				//.itemOutput(12,13,14,15);
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
	public UpgradeManagerNT upgradeManager = new UpgradeManagerNT(this);

	/// gets appropriate amount to be transferred to salt tank
	public Triplet<Integer,Integer,Double> getExtractionAmount(Map<MSRFuel,Double> recipe) {
		if (bufferIn.getFluid() != null && saltType.getFF() != null) {
			NBTTagCompound tag = MSRElementTE.nbtProtocol(bufferIn.getFluid().tag);
			bufferIn.getFluid().tag = tag;
			Map<String,Double> mixture = MSRElementTE.readMixture(tag);
			double multiplier = 99999999;
			int tankSpace = saltTank.getCapacity()-saltTank.getFluidAmount();
			int bufferSpace = bufferOut.getCapacity()-bufferOut.getFluidAmount();
			double sumAll = 0;
			for (Entry<String,Double> entry : mixture.entrySet())
				sumAll += entry.getValue();
			for (Entry<MSRFuel,Double> entry : recipe.entrySet()) {
				String name = entry.getKey().name();
				if (mixture.containsKey(name)) {
					double mixtureAmount = mixture.get(name);
					double recipeAmount = entry.getValue();
					multiplier = Math.min(multiplier,mixtureAmount/recipeAmount);
				} else return null;
			}
			double sumRecipe = 0;
			for (Entry<MSRFuel,Double> entry : recipe.entrySet())
				sumRecipe += entry.getValue()*multiplier;
			int total = Math.min(Math.min((int)(Math.min(bufferIn.getFluidAmount()*multiplier*(sumRecipe/sumAll),tankSpace)/multiplier/(sumRecipe/sumAll)),bufferSpace),bufferIn.getFluidAmount());
			int conversion = (int)(total/* *(sumRecipe/sumAll)*/*Math.min(multiplier,1));
			//LeafiaDebug.debugLog(world,multiplier);
			return new Triplet<>(total,conversion,multiplier);
		}
		return null;
	}
	public void transferToTank(Map<MSRFuel,Double> recipe,int total,int conversion,double multiplier) {
		if (bufferIn.getFluid() != null && saltType.getFF() != null && bufferIn.getFluidAmount() > 0 && total > 0 && conversion > 0 && bufferOut.getCapacity()-bufferOut.getFluidAmount() >= total-conversion) {
			int inAmt = bufferIn.getFluidAmount();
			NBTTagCompound tag = MSRElementTE.nbtProtocol(bufferIn.getFluid().tag);
			bufferIn.getFluid().tag = tag;
			Map<String,Double> mixture = MSRElementTE.readMixture(tag);
			FluidStack stack = bufferIn.drain(total,true);
			if (stack == null)
				throw new LeafiaDevFlaw("Salt Separator: Could not drain input buffer. How did this happen?");
			if (stack.amount != total)
				throw new LeafiaDevFlaw("Salt Separator: Drained amount does not match conversion value. How did this happen?");
			Map<String,Double> outputMixture = MSRElementTE.readMixture(tag);
			for (Entry<String,Double> entry : mixture.entrySet()) {
				try {
					if (recipe.containsKey(MSRFuel.valueOf(entry.getKey()))) {
						double mix = entry.getValue()-recipe.get(MSRFuel.valueOf(entry.getKey()))*multiplier;//conversion/inAmt;
						if (mix > 0)
							outputMixture.put(entry.getKey(),mix);
					} else
						outputMixture.put(entry.getKey(),entry.getValue());
				} catch (IllegalArgumentException ignored) { }
			}
			NBTTagCompound outTag = new NBTTagCompound();
			outTag.setTag("itemMixture",MSRElementTE.writeMixture(outputMixture));
			FluidTank buf = new FluidTank(total-conversion);
			buf.setFluid(new FluidStack(saltType.getFF(),total-conversion,outTag));
			LeafiaUtil.fillFF(buf,bufferOut,total-conversion);
			if (buf.getFluidAmount() > 0)
				throw new LeafiaDevFlaw("Salt Separator: "+buf.getFluidAmount()+"mB was sent into the backrooms. How?\n\nExtended Information: Output was "+bufferOut.getFluidAmount()+"/"+bufferOut.getCapacity()+"mB");
			Map<String,Double> fillMixture = new HashMap<>();
			for (Entry<MSRFuel,Double> entry : recipe.entrySet())
				fillMixture.put(entry.getKey().name(),entry.getValue());
			NBTTagCompound fillTag = new NBTTagCompound();
			fillTag.setTag("itemMixture",MSRElementTE.writeMixture(fillMixture));
			FluidStack tankFill = new FluidStack(saltType.getFF(),saltTank.getFluidAmount()+conversion,fillTag);
			saltTank.setFluid(tankFill);
		}
	}

	@Override
	public void update() {
		if (!world.isRemote) {
			SaltSeparatorRecipe recipe = SaltSeparatorRecipes.INSTANCE.recipeNameMap.get(module.recipe);
			if(recipe != null)
				this.maxPower = recipe.power * 100;
			this.maxPower = BobMathUtil.max(this.power, this.maxPower, 100_000);
			this.power = Library.chargeTEFromItems(inventory, 0, power, maxPower);
			if(recipe != null && recipe.inputFluid != null) {
				for(int i = 0; i < Math.min(2, recipe.inputFluid.length); i++)
					inputTanks[i].loadTank(4 + i, 6 + i, inventory);
			}
			outputTanks[0].unloadTank(8, 11, inventory);
			outputTanks[1].unloadTank(9, 12, inventory);
			outputTanks[2].unloadTank(10, 13, inventory);

			for (DirPos con : getConPos()) {
				trySubscribe(world,con);
				for (FluidTankNTM tank : inputTanks) {
					if (tank.getTankType() != Fluids.NONE)
						trySubscribe(tank.getTankType(),world,con);
				}
				for (FluidTankNTM tank : outputTanks) {
					if (tank.getFill() > 0)
						tryProvide(tank,world,con);
				}
				if (saltType.getFF() != null) {
					trySubscribe(bufferIn,new FluidStack(saltType.getFF(),0),world,con.getPos(),con.getDir());
					tryProvide(bufferOut,world,con.getPos(),con.getDir());
				}
			}

			double speed = 1D;
			double pow = 1D;

			speed += Math.min(upgradeManager.getLevel(ItemMachineUpgrade.UpgradeType.SPEED), 3) / 3D;
			speed += Math.min(upgradeManager.getLevel(ItemMachineUpgrade.UpgradeType.OVERDRIVE), 3);

			pow -= Math.min(upgradeManager.getLevel(ItemMachineUpgrade.UpgradeType.POWER), 3) * 0.25D;
			pow += Math.min(upgradeManager.getLevel(ItemMachineUpgrade.UpgradeType.SPEED), 3) * 1D;
			pow += Math.min(upgradeManager.getLevel(ItemMachineUpgrade.UpgradeType.OVERDRIVE), 3) * 10D / 3D;

			SaltSeparatorRecipe curRecipe = this.module.getRecipe();
			saltType = (curRecipe != null) ? curRecipe.saltType : Fluids.NONE;
			if (curRecipe != null) {
				Triplet<Integer,Integer,Double> conversion = getExtractionAmount(curRecipe.mixture);
				if (conversion != null && conversion.getY() > 0) {
					/*
					int fuckoff = (int)(conversion.getY()*conversion.getZ());
					if (fuckoff > 0)
						transferToTank(curRecipe.mixture,(int)(conversion.getX()*conversion.getZ()),fuckoff,1);
					 */
					transferToTank(curRecipe.mixture,conversion.getX(),conversion.getY(),1);
				}
				LeafiaUtil.fillFF(bufferIn,bufferOut,bufferIn.getFluidAmount());
			}

			this.module.update(speed, pow, true, inventory.getStackInSlot(1));
			this.didProcess = this.module.didProcess;
			if(this.module.markDirty) this.markDirty();

			NBTTagCompound tankData = new NBTTagCompound();
			for (int i = 0; i < inputTanks.length; i++)
				inputTanks[i].writeToNBT(tankData,"in"+i);
			for (int i = 0; i < outputTanks.length; i++)
				outputTanks[i].writeToNBT(tankData,"out"+i);
			tankData.setTag("salt",saltTank.writeToNBT(new NBTTagCompound()));
			tankData.setTag("bufIn",bufferIn.writeToNBT(new NBTTagCompound()));
			tankData.setTag("bufOut",bufferOut.writeToNBT(new NBTTagCompound()));

			LeafiaPacket._start(this)
					.__write(0,this.module.recipe)
					.__write(1,tankData)
					.__write(2,saltType.getID())
					.__write(3,module.progress)
					.__write(4,power)
					.__write(5,didProcess)
					.__sendToAffectedClients();
		}
	}

	@Override
	public @NotNull NBTTagCompound writeToNBT(NBTTagCompound compound) {
		for (int i = 0; i < inputTanks.length; i++)
			inputTanks[i].writeToNBT(compound,"in"+i);
		for (int i = 0; i < outputTanks.length; i++)
			outputTanks[i].writeToNBT(compound,"out"+i);
		compound.setTag("salt",saltTank.writeToNBT(new NBTTagCompound()));
		compound.setTag("bufIn",bufferIn.writeToNBT(new NBTTagCompound()));
		compound.setTag("bufOut",bufferOut.writeToNBT(new NBTTagCompound()));
		compound.setLong("power",power);
		compound.setLong("maxPower",maxPower);
		module.writeToNBT(compound);
		return super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		for (int i = 0; i < inputTanks.length; i++)
			inputTanks[i].readFromNBT(compound,"in"+i);
		for (int i = 0; i < outputTanks.length; i++)
			outputTanks[i].readFromNBT(compound,"out"+i);
		saltTank.readFromNBT(compound.getCompoundTag("salt"));
		bufferIn.readFromNBT(compound.getCompoundTag("bufIn"));
		bufferOut.readFromNBT(compound.getCompoundTag("bufOut"));
		power = compound.getLong("power");
		if (compound.getLong("maxPower") > 0) // you son of a
			maxPower = compound.getLong("maxPower");
		module.readFromNBT(compound);
	}

	@Override
	public String getDefaultName() {
		return "tile.salt_separator.name";
	}
	AxisAlignedBB bb = null;
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		if(bb == null) {
			bb = new AxisAlignedBB(
					pos.getX() - 1,
					pos.getY(),
					pos.getZ() - 1,
					pos.getX() + 2,
					pos.getY() + 6,
					pos.getZ() + 2
			);
		}
		return bb;
	}

	public DirPos[] getConPos() {
		return new DirPos[] {
				new DirPos(pos.getX() + 2, pos.getY(), pos.getZ() + 1, Library.POS_X),
				new DirPos(pos.getX() + 2, pos.getY(), pos.getZ() - 1, Library.POS_X),
				new DirPos(pos.getX() - 2, pos.getY(), pos.getZ() + 1, Library.NEG_X),
				new DirPos(pos.getX() - 2, pos.getY(), pos.getZ() - 1, Library.NEG_X),
				new DirPos(pos.getX() + 1, pos.getY(), pos.getZ() + 2, Library.POS_Z),
				new DirPos(pos.getX() - 1, pos.getY(), pos.getZ() + 2, Library.POS_Z),
				new DirPos(pos.getX() + 1, pos.getY(), pos.getZ() - 2, Library.NEG_Z),
				new DirPos(pos.getX() - 1, pos.getY(), pos.getZ() - 2, Library.NEG_Z)
		};
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

	@Override public boolean hasPermission(EntityPlayer player) { return this.isUseableByPlayer(player); }

	@Override
	public void receiveControl(NBTTagCompound data) {
		if(data.hasKey("index") && data.hasKey("selection")) {
			int index = data.getInteger("index");
			String selection = data.getString("selection");
			if(index == 0) {
				this.module.recipe = selection;
				this.markChanged();
			}
		}
	}

	@Override
	public Container provideContainer(int i,EntityPlayer entityPlayer,World world,int i1,int i2,int i3) {
		return new SaltSeparatorContainer(entityPlayer.inventory,getCheckedInventory());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen provideGUI(int i,EntityPlayer entityPlayer,World world,int i1,int i2,int i3) {
		return new SaltSeparatorGUI(entityPlayer.inventory,this);
	}

	@Override
	public String getPacketIdentifier() {
		return "SALT_SEPA";
	}

	@Override
	public void onReceivePacketLocal(byte key,Object value) {
		switch(key) {
			case 0:
				module.recipe = (String)value;
				break;
			case 1:
				NBTTagCompound nbt = (NBTTagCompound)value;
				for (int i = 0; i < inputTanks.length; i++)
					inputTanks[i].readFromNBT(nbt,"in"+i);
				for (int i = 0; i < outputTanks.length; i++)
					outputTanks[i].readFromNBT(nbt,"out"+i);
				saltTank.readFromNBT(nbt.getCompoundTag("salt"));
				bufferIn.readFromNBT(nbt.getCompoundTag("bufIn"));
				bufferOut.readFromNBT(nbt.getCompoundTag("bufOut"));
				break;
			case 2:
				saltType = Fluids.fromID((int)value);
				break;
			case 3:
				module.progress = (double)value;
				break;
			case 4:
				power = (long)value;
				break;
			case 5:
				didProcess = (boolean)value;
				break;
		}
	}

	@Override
	public void onReceivePacketServer(byte key,Object value,EntityPlayer plr) {

	}

	@Override
	public void onPlayerValidate(EntityPlayer plr) {

	}

	@Override
	public @NotNull FluidTankNTM[] getReceivingTanks() {
		return inputTanks;
	}

	@Override
	public @NotNull FluidTankNTM[] getSendingTanks() {
		return outputTanks;
	}

	@Override
	public FluidTankNTM[] getAllTanks() {
		return new FluidTankNTM[] {
				inputTanks[0],inputTanks[1],
				outputTanks[0],outputTanks[1],outputTanks[2]
		};
	}

	@Override
	public FluidTank getCorrespondingTank(FluidStack stack) {
		return bufferIn;
	}

	@Override
	public IFluidTankProperties[] getTankProperties() {
		return new IFluidTankProperties[0];
	}

	@Override
	public int fill(FluidStack resource,boolean doFill) {
		return bufferIn.fill(resource,doFill);
	}

	@Override
	public @Nullable FluidStack drain(FluidStack resource,boolean doDrain) {
		return bufferOut.drain(resource,doDrain);
	}

	@Override
	public @Nullable FluidStack drain(int maxDrain,boolean doDrain) {
		return bufferOut.drain(maxDrain,doDrain);
	}
}
