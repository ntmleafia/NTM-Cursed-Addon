package com.leafia.contents.machines.reactors.apr.blocks.core;

import com.custom_hbm.contents.torex.LCETorex;
import com.custom_hbm.sound.LCEAudioWrapper;
import com.custom_hbm.util.LCETuple.Pair;
import com.hbm.api.fluidmk2.IFluidStandardReceiverMK2;
import com.hbm.api.fluidmk2.IFluidStandardSenderMK2;
import com.hbm.config.MobConfig;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.hbm.inventory.fluid.trait.FT_Coolable;
import com.hbm.items.ModItems;
import com.hbm.lib.DirPos;
import com.hbm.lib.ForgeDirection;
import com.hbm.lib.HBMSoundHandler;
import com.hbm.main.AdvancementManager;
import com.hbm.tileentity.IGUIProvider;
import com.leafia.AddonBase;
import com.leafia.contents.AddonBlocks.APR;
import com.leafia.contents.AddonFluids;
import com.leafia.contents.AddonItems;
import com.leafia.contents.effects.nuke.NukeLCA;
import com.leafia.contents.effects.nuke.NukeLCA.ExplosionType;
import com.leafia.contents.fluids.traits.FT_APRCoolable;
import com.leafia.contents.fluids.traits.FT_APRCoolant;
import com.leafia.contents.machines.reactors.apr.blocks.APRComponentBlock;
import com.leafia.contents.machines.reactors.apr.blocks.APRComponentBlock.APRComponentType;
import com.leafia.contents.machines.reactors.apr.blocks.core.container.APRMBUI;
import com.leafia.contents.machines.reactors.apr.blocks.core.container.APRMainContainer;
import com.leafia.contents.machines.reactors.apr.blocks.core.container.APRMainUI;
import com.leafia.contents.machines.reactors.apr.blocks.port.APRFluidIOBlock;
import com.leafia.contents.machines.reactors.apr.blocks.port.APRFluidIOTE;
import com.leafia.dev.LeafiaDebug.Tracker.Action;
import com.leafia.dev.LeafiaDebug.Tracker.LeafiaTrackerPacket;
import com.leafia.dev.LeafiaUtil;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.container_utility.LeafiaPacketReceiver;
import com.leafia.dev.machine.LCETileEntityMachineBase;
import com.leafia.dev.math.FiaMatrix;
import com.leafia.dev.math.FiaMatrix.RotationOrder;
import com.leafia.eventbuses.interfaces.INotifyEventListener;
import com.leafia.init.AddonAdvancements;
import com.leafia.init.LeafiaSoundEvents;
import com.llib.technical.FifthString;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent.NeighborNotifyEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.Map.Entry;

public class APRCoreTE extends LCETileEntityMachineBase implements IGUIProvider, LeafiaPacketReceiver, ITickable, IFluidStandardReceiverMK2, IFluidStandardSenderMK2, INotifyEventListener {
	public static final byte idChambers = 0;
	public static final byte idAssembled = 1;
	public static final byte idHighlight = 2;
	public static final byte idParticles = 3;
	public static final byte idParticleCap = 4;
	public static final byte idFunnel = 5;
	public static final byte idVoid = 5;
	public static final byte idFunnelDesired = 6;
	public static final byte idChamberSync = 7;
	public static final byte idPage = 8;
	public static final byte idDamage = 9;
	public static final int minChamberRadius = 6;
	public static final int maxChamberRadius = 32;
	public final Map<BlockPos,IBlockState> mbRequirement = new HashMap<>();
	public final List<Integer> chambers = new ArrayList<>();
	public boolean assembled = false;
	public int page = 0;
	public final List<APRChamber> chamberDatas = new ArrayList<>();
	@Nullable public APRChamber getChamberSafely(int index) {
		if (index < chamberDatas.size())
			return chamberDatas.get(index);
		return null;
	}
	public static final int graphXSegments = 16;
	public static final int graphRefreshRate = 10;
	public int graphTimer = 0;
	public static class APRChamber {
		public FluidTankNTM input = new FluidTankNTM(Fluids.NONE,0);
		public FluidTankNTM output = new FluidTankNTM(Fluids.NONE,0);
		public double blanketTemp = 20;
		public double[] graph = new double[graphXSegments];
		public NBTTagCompound writeToNBT() {
			NBTTagCompound nbt = new NBTTagCompound();
			input.writeToNBT(nbt,"in");
			output.writeToNBT(nbt,"out");
			nbt.setDouble("temp",blanketTemp);
			return nbt;
		}
		public void readFromNBT(NBTTagCompound nbt) {
			input.readFromNBT(nbt,"in");
			output.readFromNBT(nbt,"out");
			blanketTemp = nbt.getDouble("temp");
		}
	}
	public double hullTemp = 20;
	public FluidTankNTM oxygen = new FluidTankNTM(Fluids.NONE,0);
	public FluidTankNTM lox = new FluidTankNTM(Fluids.NONE,0);
	public double particles = 0;
	public int particleCap = 0;
	public int damage = 0;
	public double rps;
	public double control = 10;
	public double targetControl = 10;
	public double lastControl = 10;
	public double controlQueue = 10;
	public static final double controlSpeed = 20;
	public static final int maxDamage = 20*30;
	public int conversionInput = 1;
	public int conversionOutput = 1;
	public int getAvailableCycles(int in,int out,int inAmt,int outAmt,int outCap) {
		int outFree = outCap-outAmt;
		return Math.min(inAmt/in,outFree/out);
	}
	// server only
	public void onAssemble() {
		int capacity = 0;
		page = 0;
		chamberDatas.clear();
		for (Integer r : chambers) {
			int myCap = getCapacityFromRadius(r);
			APRChamber data = new APRChamber();
			data.input = new FluidTankNTM(Fluids.NONE,myCap);
			data.output = new FluidTankNTM(Fluids.NONE,myCap);
			chamberDatas.add(data);
			capacity += myCap;
		}
		particleCap = capacity/10;
		setupMainTank(AddonFluids.OXYGEN_GAS);
		writeState(LeafiaPacket._start(this)).__sendToAffectedClients();
	}
	public int getCapacityFromRadius(int radius) {
		return radius*50000;
	}
	public void setupMainTank(FluidType type) {
		int capacity = 0;
		for (Integer r : chambers)
			capacity += getCapacityFromRadius(r);
		oxygen = new FluidTankNTM(type,capacity);
		FT_APRCoolant info = type.getTrait(FT_APRCoolant.class);
		lox = new FluidTankNTM(info.conversion,capacity*info.out/info.in);
		conversionInput = info.in;
		conversionOutput = info.out;
	}
	public static double getHeatDifference(FluidType type) {
		if (type.hasTrait(FT_APRCoolant.class)) {
			FT_APRCoolant info = type.getTrait(FT_APRCoolant.class);
			return type.temperature-info.conversion.temperature;
		}
		if (type.hasTrait(FT_APRCoolable.class)) {
			FT_APRCoolable info = type.getTrait(FT_APRCoolable.class);
			return type.temperature-info.conversion.temperature;
		}
		if (type.hasTrait(FT_Coolable.class)) {
			FT_Coolable info = type.getTrait(FT_Coolable.class);
			return type.temperature-info.coolsTo.temperature;
		}
		return -1;
	}
	public static Pair<Integer,Integer> getConversionRates(FluidType type) {
		if (type.hasTrait(FT_APRCoolant.class)) {
			FT_APRCoolant info = type.getTrait(FT_APRCoolant.class);
			return new Pair<Integer,Integer>(info.in,info.out);
		}
		if (type.hasTrait(FT_APRCoolable.class)) {
			FT_APRCoolable info = type.getTrait(FT_APRCoolable.class);
			return new Pair<Integer,Integer>(info.in,info.out);
		}
		if (type.hasTrait(FT_Coolable.class)) {
			FT_Coolable info = type.getTrait(FT_Coolable.class);
			return new Pair<Integer,Integer>(info.amountReq,info.amountProduced);
		}
		return new Pair<Integer,Integer>(1,1);
	}
	public static FluidType getConversionTo(FluidType type) {
		if (type.hasTrait(FT_APRCoolant.class))
			return type.getTrait(FT_APRCoolant.class).conversion;
		if (type.hasTrait(FT_APRCoolable.class))
			return type.getTrait(FT_APRCoolable.class).conversion;
		if (type.hasTrait(FT_Coolable.class))
			return type.getTrait(FT_Coolable.class).coolsTo;
		return Fluids.NONE;
	}
	public static double getFinalHeatDifference(FluidType type) {
		double difference = 0;
		while (isValidFluid(type)) {
			difference += getHeatDifference(type);
			type = getConversionTo(type);
		}
		return difference;
	}
	public static Pair<Integer,Integer> getFinalConversionRates(FluidType type) {
		int in = 1;
		int out = 1;
		while (isValidFluid(type)) {
			Pair<Integer,Integer> rate = getConversionRates(type);
			in *= rate.getA();
			out *= rate.getB();
			type = getConversionTo(type);
		}
		return new Pair<Integer,Integer>(in,out);
	}
	public static FluidType getFinalConversionTo(FluidType type) {
		FluidType output = Fluids.NONE;
		while (isValidFluid(type)) {
			output = getConversionTo(type);
			type = output;
		}
		return output;
	}
	public static double getEnergy(FluidType type) {
		return getEnergy(getHeatDifference(type));
	}
	public static double getFinalEnergy(FluidType type) {
		return Math.pow(getFinalHeatDifference(type),0.65);
	}
	public static double getEnergy(double heatDifference) {
		return Math.pow(heatDifference,0.75);
	}
	public static double energyFor(double energy,int amount) {
		return amount*energy/1000;
	}
	public void addRPSFromEnergy(double energy) {
		double ratio = Math.min((rps-30)/(110-30),1);
		double neg = 1-ratio*0.85;
		rps += energy/1600/Math.pow(particleCap/30000d,0.75)*neg;
	}
	public int accountForParticles(int amt) {
		return (int)(amt*(particles/particleCap));
	}
	public void addHeatFromEnergy(double energy,APRChamber chamber) {
		double ratio = (chamber.blanketTemp-20)/(8192-20);
		double neg = 1-ratio*0.5;
		//double particleRatio = particles/particleCap;
		double addition = energy/100*neg;//*particleRatio;
		chamber.blanketTemp += addition;
		//particles = Math.max(particles-addition/10000,0); maybe not make it deplete since that sucks
	}
	public void addHeatFromEnergy(double energy,List<APRChamber> chambers) {
		for (APRChamber chamber : chambers)
			addHeatFromEnergy(energy,chamber);
	}
	public APRCoreTE() {
		super(6);
	}
	public void sortChambers() {
		chambers.sort(Comparator.naturalOrder());
	}
	static int[] toArray(List<Integer> radii) {
		int[] array = new int[radii.size()];
		for (int i = 0; i < array.length; i++)
			array[i] = radii.get(i);
		return array;
	}
	public void rebuildMBRequirement() {
		sortChambers();
		mbRequirement.clear();
		if (!chambers.isEmpty()) {
			// DISC
			IBlockState plate = APR.apr_component.getDefaultState()
					.withProperty(APRComponentBlock.VARIANT,APRComponentType.PLATING);
			IBlockState dark = APR.apr_component.getDefaultState()
					.withProperty(APRComponentBlock.VARIANT,APRComponentType.PLATING_DARK);
			IBlockState parts = APR.apr_component.getDefaultState()
					.withProperty(APRComponentBlock.VARIANT,APRComponentType.MECHANICAL);
			int radius = chambers.get(chambers.size()-1);
			for (int x = -radius; x <= radius; x++) {
				for (int z = -radius; z <= radius; z++) {
					BlockPos p = new BlockPos(pos.getX()+x,pos.getY(),pos.getZ()+z);
					if (Math.sqrt(p.distanceSq(getPos())) <= radius) {
						IBlockState cover = (x == 0 || z == 0) ? dark : plate;
						if (x == 0 && z == 0) cover = APR.apr_outlet.getDefaultState();
						mbRequirement.put(p.down(),cover);
						mbRequirement.put(p.down(2),parts);
						mbRequirement.put(p.down(3),parts);
						mbRequirement.put(p.down(4),parts);
						mbRequirement.put(p.down(5),cover);
					}
				}
			}
			// COILS
			IBlockState coil = APR.apr_component.getDefaultState()
					.withProperty(APRComponentBlock.VARIANT,APRComponentType.COIL);
			for (int x = -1; x <= 1; x++) {
				for (int y = -1; y >= -4; y--) {
					mbRequirement.put(getPos().add(x,y,2),coil);
					mbRequirement.put(getPos().add(x,y,-2),coil);
				}
			}
			for (int z = -1; z <= 1; z++) {
				for (int y = -1; y >= -4; y--) {
					mbRequirement.put(getPos().add(2,y,z),coil);
					mbRequirement.put(getPos().add(-2,y,z),coil);
				}
			}
			// FUNNEL CHAMBER
			for (int x = -1; x <= 1; x++) {
				for (int z = -1; z <= 1; z++) {
					for (int y = -1; y >= -4; y--)
						mbRequirement.put(getPos().add(x,y,z),Blocks.AIR.getDefaultState());
				}
			}
		}
		for (Integer r : chambers)
			APRCoreBlock.generateTorus(getPos(),r,mbRequirement);
	}
	@Override
	public String getDefaultName() {
		return "container.apr";
	}
	@Override
	protected void setWorldCreate(World worldIn) {
		this.world = worldIn;
	}
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		assembled = nbt.getBoolean("assembled");
		setChambers(nbt.getIntArray("radius"),false);
		if (chambers.isEmpty())
			assembled = false; // fuck you
		if (assembled)
			onAssemble();
		readChamberData(nbt);
		particles = nbt.getDouble("particles");
		damage = nbt.getInteger("damage");
		if (nbt.hasKey("convIn"))
			conversionInput = nbt.getInteger("convIn");
		if (nbt.hasKey("convOut"))
			conversionOutput = nbt.getInteger("convOut");
		if (nbt.hasKey("controlC"))
			control = nbt.getDouble("controlC");
		if (nbt.hasKey("controlD"))
			targetControl = nbt.getDouble("controlD");
		page = nbt.getInteger("page");
	}
	@Override
	public @NotNull NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setBoolean("assembled",assembled);
		nbt.setIntArray("radius",toArray(chambers));
		addChamberData(nbt);
		nbt.setDouble("particles",particles);
		nbt.setInteger("damage",damage);
		nbt.setInteger("convIn",conversionInput);
		nbt.setInteger("convOut",conversionOutput);
		nbt.setDouble("controlC",control);
		nbt.setDouble("controlD",targetControl);
		nbt.setInteger("page",page);
		return super.writeToNBT(nbt);
	}
	@Override
	public String getPacketIdentifier() {
		return "APR_CORE";
	}
	LeafiaPacket writeState(LeafiaPacket packet) {
		return packet.__write(idAssembled,assembled).__write(idChambers,toArray(chambers));
	}
	@Override
	public void onPlayerValidate(EntityPlayer plr) {
		writeState(LeafiaPacket._start(this)).__sendToClient(plr);
	}
	public void requestChambers(List<Integer> edited,boolean highlightOnClose) {
		LeafiaPacket._start(this).__write(idChambers,toArray(edited)).__write(idHighlight,highlightOnClose).__sendToServer();
	}
	public void addChamberData(NBTTagCompound nbt) {
		oxygen.writeToNBT(nbt,"in");
		lox.writeToNBT(nbt,"out");
		nbt.setDouble("hull",hullTemp);
		nbt.setDouble("rps",rps);
		NBTTagList list = new NBTTagList();
		for (APRChamber data : chamberDatas)
			list.appendTag(data.writeToNBT());
		nbt.setTag("chambers",list);
	}
	public void readChamberData(NBTTagCompound nbt) {
		if (nbt.hasKey("in"))
			oxygen.readFromNBT(nbt,"in");
		if (nbt.hasKey("out"))
			lox.readFromNBT(nbt,"out");
		if (nbt.hasKey("hull"))
			hullTemp = nbt.getDouble("hull");
		if (nbt.hasKey("rps"))
			rps = nbt.getDouble("rps");
		if (nbt.hasKey("chambers")) {
			NBTTagList list = nbt.getTagList("chambers",10);
			int i = 0;
			for (NBTBase raw : list) {
				NBTTagCompound tag = (NBTTagCompound)raw;
				APRChamber chamber;
				if (chamberDatas.size() > i) {
					chamber = chamberDatas.get(i);
					chamber.readFromNBT(tag);
				} else
					break;
				i++;
			}
		}
	}
	@Override
	public void onReceivePacketLocal(byte key,Object value) {
		switch (key) {
			case idAssembled -> assembled = (boolean)value;
			case idChambers -> setChambers(value,false);
			case idParticles -> particles = (double)value;
			case idParticleCap -> particleCap = (int)value;
			case idFunnel -> controlQueue = (double)value;
			case idFunnelDesired -> targetControl = (double)value;
			case idChamberSync -> readChamberData((NBTTagCompound)value);
			case idPage -> page = (int)value;
			case idDamage -> damage = (int)value;
		}
	}
	@Override
	public void onReceivePacketServer(byte key,Object value,EntityPlayer plr) {
		if (key == idFunnelDesired) {
			targetControl = (double)value;
		} else if (key == idChambers) {
			if (assembled) return;
			setChambers(value,true);
			markDirty();
			writeState(LeafiaPacket._start(this)).__sendToAffectedClients();
			rebuildMBRequirement();
		} else if (key == idHighlight && (boolean)value) {
			int highlightsEmitted = 0;
			for (Entry<BlockPos,IBlockState> entry : mbRequirement.entrySet()) {
				if (plr.isCreative()) {
					world.setBlockState(entry.getKey(),entry.getValue());
					continue;
				}
				if (!world.isAirBlock(entry.getKey())) {
					if (!isValidBlock(entry.getKey())) {
						LeafiaTrackerPacket packet = new LeafiaTrackerPacket();
						packet.mode = Action.SHOW_BOX;
						packet.writer = (buf)->{
							buf.writeFloat(10);
							buf.writeInt(0xFF0000);
							buf.writeByte((byte)1);
							buf.writeFifthString(new FifthString("Invalid Block"));
							buf.writeVec3i(entry.getKey());
						};
						LeafiaPacket._sendToClient(packet,plr);
						highlightsEmitted++;
						//if (highlightsEmitted > 50)
						//	break;
					}
				}
			}
		} else if (key == idPage)
			page = (int)value;
		else if (key == idVoid) {
			for (APRChamber data : chamberDatas) {
				if (data.blanketTemp > 100)
					return;
			}
			if (!inventory.getStackInSlot(0).isEmpty())
				return;
			int req = (int)(particles/1000);
			int slot = 0;
			List<ItemStack> stacks = plr.inventory.mainInventory;
			for (int i = 0; i < req; i++) {
				while (slot < stacks.size()) {
					if (!stacks.get(slot).isEmpty()) {
						if (stacks.get(slot).getItem() == ModItems.particle_empty)
							break;
					}
					slot++;
				}
				if (slot >= stacks.size()) {
					// fail
					plr.inventoryContainer.detectAndSendChanges();
					return;
				}
				if (!stacks.get(slot).isEmpty()) {
					if (stacks.get(slot).getItem() == ModItems.particle_empty) {
						particles -= 1000;
						stacks.get(slot).shrink(1);
						if (!plr.addItemStackToInventory(new ItemStack(AddonItems.particle_absorption)))
							plr.dropItem(new ItemStack(AddonItems.particle_absorption),false);
					}
				}
			}
			plr.inventoryContainer.detectAndSendChanges();
			particles = 0;
		}
	}
	public boolean checkAssembled() {
		if (chambers.isEmpty()) return false;
		for (BlockPos bp : mbRequirement.keySet()) {
			if (!isValidBlock(bp))
				return false;
		}
		return true;
	}
	void setChambers(Object received,boolean canReject) {
		this.chambers.clear();
		List<Integer> chambers = new ArrayList<>();
		if (received instanceof int[] radii) {
			for (int r : radii) {
				//if (chambers.size() >= maxChambers) break;
				chambers.add(MathHelper.clamp(r,1,maxChamberRadius));
			}
		}
		chambers.sort(Comparator.naturalOrder());
		int lastR = 0;
		for (Integer r : chambers) {
			if (r-lastR <= 3 && canReject)
				return;
			lastR = r;
		}
		this.chambers.addAll(chambers);
		rebuildMBRequirement();
	}
	AxisAlignedBB bb = null;
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		if (!assembled) return INFINITE_EXTENT_AABB;
		if(bb == null) {
			bb = new AxisAlignedBB(
					pos.getX() - 3,
					pos.getY() - 2,
					pos.getZ() - 3,
					pos.getX() + 4,
					pos.getY() + 3,
					pos.getZ() + 4
			);
		}
		return bb;
	}
	@Override
	public Container provideContainer(int i,EntityPlayer entityPlayer,World world,int i1,int i2,int i3) {
		if (!assembled)
			return null;
		return new APRMainContainer(entityPlayer,this);
	}
	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen provideGUI(int i,EntityPlayer entityPlayer,World world,int i1,int i2,int i3) {
		if (!assembled)
			return new APRMBUI(this);
		return new APRMainUI(entityPlayer,this);
	}
	@Override
	public double affectionRange() {
		return 256;
	}
	public boolean isValidBlock(BlockPos pos) {
		BlockPos center = getPos();
		Vec3i relative = pos.subtract(center);
		if (Math.abs(relative.getX()) <= 1 && Math.abs(relative.getZ()) <= 1 && relative.getY() <= -1 && relative.getY() >= -4) {
			Material mat = getWorld().getBlockState(pos).getMaterial();
			return mat.isReplaceable() && !mat.isSolid() && !mat.isLiquid();
		}
		IBlockState desired = mbRequirement.get(pos);
		if (desired != null) {
			IBlockState state = getWorld().getBlockState(pos);
			if (state.equals(desired)) return true;
			if (desired.getBlock() == APR.apr_component) {
				if (desired.getValue(APRComponentBlock.VARIANT) == APRComponentType.PLATING || desired.getValue(APRComponentBlock.VARIANT) == APRComponentType.PLATING_DARK) {
					if (state.getBlock() == APR.apr_component) {
						if (state.getValue(APRComponentBlock.VARIANT) == APRComponentType.PLATING || state.getValue(APRComponentBlock.VARIANT) == APRComponentType.PLATING_DARK)
							return true;
					}
				} else if (desired.getValue(APRComponentBlock.VARIANT) == APRComponentType.SUPPORT) {
					if (state.getBlock() instanceof APRFluidIOBlock) {
						if (world.getTileEntity(pos) instanceof APRFluidIOTE fte)
							fte.targetPos = getPos();
						return true;
					}
				}/* else if (desired.getValue(APRComponentBlock.VARIANT) == APRComponentType.PLATING_DARK) {
					if (state.getBlock() instanceof APRPowerOutletBlock) {
						if (pos.getX() == this.getPos().getX() && pos.getZ() == this.getPos().getZ())
							return true;
					}
				}*/
			}
			return false;
		} else
			return true;
	}
	public static boolean isValidCoolant(FluidType type) {
		return type.hasTrait(FT_APRCoolant.class);
	}
	public static boolean isValidFluid(FluidType type) {
		if (type.hasTrait(FT_APRCoolant.class))
			return true;
		if (type.hasTrait(FT_APRCoolable.class))
			return true;
		if (type.hasTrait(FT_Coolable.class))
			return true;
		return false;
	}
	public Vec3d getRandomVector(BlockPos bp) {
		EnumFacing face = EnumFacing.values()[world.rand.nextInt(6)];
		if (face == EnumFacing.UP)
			return new Vec3d(bp.getX()+world.rand.nextDouble(),bp.getY()+1,bp.getZ()+world.rand.nextDouble());
		else if (face == EnumFacing.DOWN)
			return new Vec3d(bp.getX()+world.rand.nextDouble(),bp.getY(),bp.getZ()+world.rand.nextDouble());
		else {
			return new Vec3d(
					bp.getX()+0.5+face.getXOffset()/2d+(face.rotateY().getXOffset()*world.rand.nextDouble()-0.5),
					bp.getY()+world.rand.nextDouble(),
					bp.getZ()+0.5+face.getZOffset()/2d+(face.rotateY().getZOffset()*world.rand.nextDouble()-0.5)
			);
		}
	}
	public long getPowerOutput() {
		return (long)(rps*particleCap*100/3/20/5);
	}
	LCEAudioWrapper controlSnd = null;
	LCEAudioWrapper turbineSnd = null;
	public double attenuationFunc(float vol,double dist) {
		double radius = 1;
		if (!chambers.isEmpty())
			radius = chambers.get(chambers.size()-1);
		double maxRadius = radius+30;
		double ratio = (dist-radius)/(maxRadius-radius);
		return Math.pow(1-Math.min(Math.max(ratio,0),1),2)*0.25;
	}
	boolean controlSoundPlaying = false;
	public int stressSoundTimer = 0;
	public double stressTimer = 300;
	@Override
	public void onChunkUnload() {
		if (controlSnd != null)
			controlSnd.stopSound();
		controlSnd = null;
		if (turbineSnd != null)
			turbineSnd.stopSound();
		turbineSnd = null;
		super.onChunkUnload();
	}
	@Override
	public void invalidate() {
		if (controlSnd != null)
			controlSnd.stopSound();
		controlSnd = null;
		if (turbineSnd != null)
			turbineSnd.stopSound();
		turbineSnd = null;
		super.invalidate();
	}
	@SideOnly(Side.CLIENT)
	public void updateLocal() {
		if (turbineSnd == null) {
			controlSnd = AddonBase.proxy.getLoopedSoundStartStop(world,LeafiaSoundEvents.pwrRodLoop,LeafiaSoundEvents.pwrRodStart,LeafiaSoundEvents.pwrRodStop,SoundCategory.BLOCKS,pos.getX()+0.5f,pos.getY()+0.5f,pos.getZ()+0.5f,0.15f,0.75f);
			turbineSnd = AddonBase.proxy.getLoopedSoundStartStop(
					world,
					LeafiaSoundEvents.modular_turbine,
					null,null,
					SoundCategory.BLOCKS,
					pos.getX()+0.5f,pos.getY()+0.5f,pos.getZ()+0.5f,
					0.01f,0.5f
			).setLooped(true).setCustomAttenuation(this::attenuationFunc).startSound();
		}
		if (control != targetControl) {
			if (!controlSoundPlaying) {
				controlSoundPlaying = true;
				controlSnd.startSound();
			}
		} else {
			if (controlSoundPlaying) {
				controlSoundPlaying = false;
				controlSnd.stopSound();
			}
		}
		float ratio = (float)(Math.pow(rps/60,0.75));
		turbineSnd.updatePitch(0.5f+ratio);
		turbineSnd.updateVolume(ratio);
		if (stressSoundTimer <= 0) {
			stressSoundTimer = 70;
			double vol = Math.pow((Math.max(rps-65,0)/35),3)*0.8;
			if (vol > 0) {
				world.playSound(
						null,
						pos,
						LeafiaSoundEvents.pipestressed,
						SoundCategory.BLOCKS,
						(float)vol,0.65f
				);
			}
		}
		stressSoundTimer--;
		double stress = damage/(double)maxDamage;
		stressTimer -= Math.pow(stress, 0.9) * 64;
		if (stressTimer <= 0)
			getWorld().playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, LeafiaSoundEvents.stressSounds[getWorld().rand.nextInt(7)], SoundCategory.BLOCKS, (float) MathHelper.clampedLerp(0.25, 14, Math.pow(stress, 4)), 1.0F);
		if (hullTemp >= 650) {
			for (Entry<BlockPos,IBlockState> entry : mbRequirement.entrySet()) {
				if (entry.getValue().getBlock() == APR.apr_component) {
					if (entry.getValue().getValue(APRComponentBlock.VARIANT) == APRComponentType.HULL_ASSEMBLED) {
						if (world.rand.nextInt(hullTemp > 850 ? 20 : 150) == 0) {
							Vec3d ranVec = getRandomVector(entry.getKey());
							world.spawnParticle(
									EnumParticleTypes.SMOKE_LARGE,
									ranVec.x,
									ranVec.y,
									ranVec.z,
									0,0,0
							);
						}
						if (hullTemp >= 1100 && world.rand.nextInt(10) == 0) {
							Vec3d ranVec = getRandomVector(entry.getKey());
							world.spawnParticle(
									EnumParticleTypes.FLAME,
									ranVec.x,
									ranVec.y,
									ranVec.z,
									0,0,0
							);
						}
					}
				}
			}
		}
	}
	@Override
	public void update() {
		if (world.isRemote) {
			lastControl = control;
			control = controlQueue;
			if (chamberDatas.size() != chambers.size()) {
				chamberDatas.clear();
				for (int i = 0; i < chambers.size(); i++)
					chamberDatas.add(new APRChamber());
			}
			graphTimer++;
			if (graphTimer >= graphRefreshRate) {
				graphTimer = 0;
				for (APRChamber data : chamberDatas) {
					for (int i = 0; i < graphXSegments-1; i++)
						data.graph[i] = data.graph[i+1];
					double sub = data.blanketTemp-20;
					data.graph[graphXSegments-1] = sub*(world.rand.nextDouble()*(sub/(8192-20))*0.75+0.35)+20;
				}
			}
			updateLocal();
			return;
		}
		subscribeToBlockNotification();

		ItemStack particleIn = inventory.getStackInSlot(0);
		if (!particleIn.isEmpty() && particleIn.getItem() == AddonItems.particle_absorption && particles < particleCap) {
			ItemStack stack = inventory.getStackInSlot(1);
			if (stack.isEmpty() || (stack.getItem() == ModItems.particle_empty && stack.getCount() < stack.getMaxStackSize())) {
				int count = stack.getCount()+1;
				particleIn.shrink(1);
				particles = Math.min(particles+1000,particleCap);
				inventory.setStackInSlot(1,new ItemStack(ModItems.particle_empty,count));
			}
		}
		if (LeafiaUtil.setTypeBy(oxygen,2,3,inventory,APRCoreTE::isValidCoolant,oxygen.getTankType()))
			setupMainTank(oxygen.getTankType());

		if (assembled) {
			if (Math.abs(targetControl-control) < controlSpeed/20)
				control = targetControl;
			else
				control += Math.signum(targetControl-control)*(controlSpeed/20);
			double highest = 0;
			for (APRChamber chamber : chamberDatas)
				highest = Math.max(highest,chamber.blanketTemp);
			hullTemp = hullTemp+(highest-hullTemp)*0.05;
			int cycles = getAvailableCycles(conversionInput,conversionOutput,(int)(accountForParticles(oxygen.getFill())/20d*(control/100)),lox.getFill(),lox.getMaxFill());
			if (cycles > 0) {
				oxygen.setFill(oxygen.getFill()-cycles*conversionInput);
				lox.setFill(lox.getFill()+cycles*conversionOutput);
				double fah = (cycles*conversionInput)/(double)oxygen.getMaxFill();
				hullTemp = (hullTemp-20)*(1-Math.pow(Math.max(fah/*-5 nuh uh that was a bad idea*/,0),0.25)*0.65)+20;
				double energy = energyFor(getEnergy(oxygen.getTankType()),cycles*conversionInput);
				if (energy > 0) {
					addRPSFromEnergy(energy);
					addHeatFromEnergy(energy,chamberDatas);
				}
			}
			rps *= Math.pow(0.99,1/(particleCap/30000d));
			int curPage = 0;
			boolean damaging = rps > 110;
			double highestTemp = 0;
			for (APRChamber data : chamberDatas) {
				if (curPage == page && LeafiaUtil.setTypeBy(data.input,4,5,inventory,APRCoreTE::isValidFluid,data.input.getTankType()))
					data.output.setTankType(getFinalConversionTo(data.input.getTankType()));
				Pair<Integer,Integer> rate = getFinalConversionRates(data.input.getTankType());
				int cyclesProcess = getAvailableCycles(rate.getA(),rate.getB(),accountForParticles(data.input.getFill()),data.output.getFill(),data.output.getMaxFill());
				if (cyclesProcess > 0) {
					data.input.setFill(data.input.getFill()-cyclesProcess*rate.getA());
					data.output.setFill(data.output.getFill()+cyclesProcess*rate.getB());
					double energy = energyFor(getFinalEnergy(data.input.getTankType()),cyclesProcess*rate.getA());
					if (energy > 0) {
						addRPSFromEnergy(energy);
						addHeatFromEnergy(energy,data);
					}
				}
				data.blanketTemp = (data.blanketTemp-20)*0.998+20;
				curPage++;
				highestTemp = Math.max(highest,data.blanketTemp);
				if (data.blanketTemp > 8192)
					damaging = true;
			}
			if (damaging) {
				damage++;
				if (damage > maxDamage) {
					// adios
					damage = maxDamage;
					for (int i = 0; i < chambers.size(); i++) {
						if (chamberDatas.get(i).blanketTemp > 8192) {
							FiaMatrix mat = new FiaMatrix(new Vec3d(pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5));
							mat = mat.rotate(RotationOrder.XYZ,0,world.rand.nextDouble()*360,0).translate(0,0,-chambers.get(i));
							world.createExplosion(null,mat.getX(),mat.getY(),mat.getZ(),8,true);
						}
					}
					if (rps > 110)
						disassemble(pos);
				}
			} else if (highestTemp < 500)
				damage = Math.max(damage-1,0);
			if (hullTemp > 1538)
				disassemble(pos);
		} else
			rps = 0;
		/*
		LeafiaDebug.debugLog(world,"-----------------------------");
		LeafiaDebug.debugLog(world,"RPS: "+rps);
		LeafiaDebug.debugLog(world,"Hull: "+hullTemp);
		for (int i = 0; i < chamberDatas.size(); i++) {
			LeafiaDebug.debugLog(world,"Chamber "+i+" blanket: "+chamberDatas.get(i).blanketTemp);
		}
		 */
		tryProvide(lox.getTankType(),world,new DirPos(getPos().up(2),ForgeDirection.UP));
		NBTTagCompound nbt = new NBTTagCompound();
		addChamberData(nbt);
		LeafiaPacket._start(this)
				.__write(idAssembled,assembled)
				.__write(idParticles,particles)
				.__write(idParticleCap,particleCap)
				.__write(idFunnel,control)
				.__write(idFunnelDesired,targetControl)
				.__write(idChamberSync,nbt)
				.__write(idPage,page)
				.__write(idDamage,damage)
				.__sendToAffectedClients();
	}

	@Override
	public @NotNull FluidTankNTM[] getReceivingTanks() {
		return new FluidTankNTM[0];
	}

	@Override
	public @NotNull FluidTankNTM[] getSendingTanks() {
		return new FluidTankNTM[]{lox};
	}

	@Override
	public FluidTankNTM[] getAllTanks() {
		FluidTankNTM[] tanks = new FluidTankNTM[chamberDatas.size()*2+2];
		tanks[0] = oxygen;
		tanks[1] = lox;
		for (int i = 0; i < chamberDatas.size(); i++) {
			tanks[2+i*2] = chamberDatas.get(i).input;
			tanks[2+i*2+1] = chamberDatas.get(i).output;
		}
		return tanks;
	}

	@Override
	public void onBlockNotify(NeighborNotifyEvent evt) {
		if (assembled && !isValidBlock(evt.getPos()))
			disassemble(evt.getPos());
	}

	public void disassemble(BlockPos p) {
		if (!assembled) return;
		if (getWorld().isRemote) return;
		assembled = false;
		boolean destructive = false;
		if (mbRequirement.containsKey(p)) {
			IBlockState state = mbRequirement.get(p);
			if (state.getBlock() == APR.apr_component) {
				APRComponentType type = state.getValue(APRComponentBlock.VARIANT);
				if (type == APRComponentType.SUPPORT || type == APRComponentType.HULL_ASSEMBLED)
					destructive = true;
			}
		}
		// misc condition here (such as damage)
		if (damage > 0)
			destructive = true;
		if (hullTemp > 1538)
			destructive = true;
		// check if particle tank is empty here
		if (particles <= 0)
			destructive = false;
		if (destructive) {
			if (hullTemp > 1538) {
				for (Entry<BlockPos,IBlockState> entry : mbRequirement.entrySet()) {
					IBlockState state = entry.getValue();
					if (state.getBlock() == APR.apr_component) {
						APRComponentType type = state.getValue(APRComponentBlock.VARIANT);
						if (type == APRComponentType.SUPPORT || type == APRComponentType.HULL_ASSEMBLED) {
							if (world.rand.nextBoolean()) {
								world.setBlockState(entry.getKey(),APR.apr_component.getDefaultState().withProperty(APRComponentBlock.VARIANT,APRComponentType.HULL));
								InventoryHelper.spawnItemStack(world,entry.getKey().getX()+0.5,entry.getKey().getY()+0.5,entry.getKey().getZ()+0.5,new ItemStack(AddonItems.blanket_mysticite));
							}
						}
					}
				}
			}
			double radius = 22+(particles-30000)/600d/5;
			world.playSound(null,p,HBMSoundHandler.rbmk_explosion,SoundCategory.BLOCKS,100,1);
			LCETorex.statFacEndo(world,p.getX()+0.5,p.getY(),p.getZ()+0.5,(float)radius);
			NukeLCA nuke = NukeLCA.statFac(world,(int)radius,p.getX()+0.5,p.getY(),p.getZ()+0.5).setExplosionType(ExplosionType.ENDOTHERMIC);
			world.spawnEntity(nuke);
			List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(p).grow(200));
			for(EntityPlayer player : players) {
				AdvancementManager.grantAchievement(player,AddonAdvancements.nukeapr);
				if(MobConfig.enableElementals)
					player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).setBoolean("radMark", true);
			}
		}
	}
}
