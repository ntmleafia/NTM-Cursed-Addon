package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.api.fluidmk2.IFluidStandardSenderMK2;
import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ModBlocks;
import com.hbm.config.MobConfig;
import com.hbm.entity.projectile.EntityZirnoxDebris;
import com.hbm.entity.projectile.EntityZirnoxDebris.DebrisType;
import com.hbm.explosion.ExplosionNukeGeneric;
import com.hbm.handler.CompatHandler;
import com.hbm.handler.MultiblockHandlerXR;
import com.hbm.handler.radiation.ChunkRadiationManager;
import com.hbm.handler.threading.PacketThreading;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemZirnoxRod;
import com.hbm.lib.DirPos;
import com.hbm.lib.ForgeDirection;
import com.hbm.lib.HBMSoundHandler;
import com.hbm.main.AdvancementManager;
import com.hbm.main.MainRegistry;
import com.hbm.packet.toclient.AuxParticlePacketNT;
import com.hbm.tileentity.IGUIProvider;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.tileentity.machine.TileEntityReactorZirnox;
import com.leafia.contents.control.fuel.nuclearfuel.LeafiaRodItem;
import com.leafia.contents.machines.reactors.zirnox.ZirnoxContainer;
import com.leafia.contents.machines.reactors.zirnox.ZirnoxGUI;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.container_utility.LeafiaPacketReceiver;
import com.leafia.init.LeafiaSoundEvents;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityReactorZIRNOX;
import com.leafia.passive.LeafiaPassiveServer;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static com.hbm.entity.projectile.EntityZirnoxDebris.DebrisType.*;

@Mixin(value = TileEntityReactorZirnox.class)
public abstract class MixinTileEntityReactorZIRNOX extends TileEntityMachineBase implements LeafiaPacketReceiver, IFluidStandardSenderMK2, IMixinTileEntityReactorZIRNOX, IGUIProvider, CompatHandler.OCComponent {
	@Shadow(remap = false)
	public FluidTankNTM water;
	@Shadow(remap = false)
	public FluidTankNTM steam;
	@Shadow(remap = false)
	public FluidTankNTM carbonDioxide;

	@Shadow(remap = false)
	protected abstract void updateConnections();

	@Shadow(remap = false)
	protected abstract void decay(int id);

	@Shadow(remap = false)
	protected abstract DirPos[] getConPos();

	@Shadow(remap = false)
	public int heat;

	@Shadow(remap = false)
	@Optional.Method(modid = "opencomputers")
	public abstract Object[] getWater(Context context,Arguments args);

	@Shadow(remap = false)
	@Optional.Method(modid = "opencomputers")
	public abstract Object[] getSteam(Context context,Arguments args);

	@Shadow(remap = false)
	@Optional.Method(modid = "opencomputers")
	public abstract Object[] getCarbonDioxide(Context context,Arguments args);

	@Inject(method = "isItemValidForSlot",at = @At(value = "HEAD"),require = 1,cancellable = true,remap = false)
	public void isItemValidForSlot(int i,ItemStack stack,CallbackInfoReturnable<Boolean> cir) {
		if (i < 24 && stack.getItem() instanceof LeafiaRodItem) {
			cir.setReturnValue(true);
			cir.cancel();
		}
	}

	// SERVER
	@Unique public byte rods = 0;
	@Unique public byte rodsTarget = 0;
	@Unique public double hulltemp = 20;
	@Unique public double meltingPoint = 800;
	@Unique public double pressureLCE = 0;
	@Unique double lastPressureLCE = 0;
	@Unique boolean kill = false;
	@Unique public double maxPressureLCE = 30;
	@Unique public byte compression = 0;
	@Unique public int generosityTimer = 5*20;
	@Unique public double stressTimer = 300;
	@Unique public boolean valveOpen = false;
	@Unique public double avgHeat = 0;
	public MixinTileEntityReactorZIRNOX(int scount) {
		super(scount);
	}
	// CLIENT
	@Unique public int dialX = 0;
	@Unique public int dialY = 0;
	@Unique public int valveLevel = 0;
	@Unique int movedelay = 0;
	@Unique
	public int drain(FluidTankNTM tank,int amount,boolean doDrain) {
		amount = Math.min(tank.getFill(),amount);
		if (doDrain)
			tank.setFill(tank.getFill()-amount);
		return amount;
	}
	@Unique
	double getHeatInSlot(int slot,LeafiaRodItem rod) {
		return rod.getFlux(inventory.getStackInSlot(slot));
	}
	@Unique
	double handleLeafiaFuel(int slot,double cool) {
		ItemStack stack = inventory.getStackInSlot(slot);
		LeafiaRodItem rod = (LeafiaRodItem)stack.getItem();
		double detectedHeat = 0;
		for (int offset = 1; slot-offset*7 >= 0; offset += 1) {
			detectedHeat += getHeatInSlot(slot-offset*7,rod)/Math.pow(2,offset-1);
		}
		for (int offset = 1; slot+offset*7 < 24; offset += 1) {
			detectedHeat += getHeatInSlot(slot+offset*7,rod)/Math.pow(2,offset-1);
		}
		int smod = Math.floorMod(slot,7);
		if (smod <= 2) {
			for (int offset = 1; slot-offset >= 0; offset += 1) {
				detectedHeat += getHeatInSlot(slot-offset,rod)/Math.pow(2,offset-1);
			}
			for (int offset = 1; slot+offset <= 2; offset += 1) {
				detectedHeat += getHeatInSlot(slot+offset,rod)/Math.pow(2,offset-1);
			}
			if (slot-4 > 0)
				detectedHeat += getHeatInSlot(slot-4,rod)/2;
			if (slot-3 > 0)
				detectedHeat += getHeatInSlot(slot-3,rod)/2;
			if (slot+4 < 24)
				detectedHeat += getHeatInSlot(slot+4,rod)/2;
			if (slot+3 < 24)
				detectedHeat += getHeatInSlot(slot+3,rod)/2;
		} else {
			for (int offset = 1; slot-offset >= 3; offset += 1) {
				detectedHeat += getHeatInSlot(slot-offset,rod)/Math.pow(2,offset-1);
			}
			for (int offset = 1; slot+offset <= 7; offset += 1) {
				detectedHeat += getHeatInSlot(slot+offset,rod)/Math.pow(2,offset-1);
			}
			if (smod != 3) {
				if (slot - 4 > 0)
					detectedHeat += getHeatInSlot(slot - 4, rod) / 2;
				if (slot + 3 < 24)
					detectedHeat += getHeatInSlot(slot + 3, rod) / 2;
			}
			if (smod != 6) {
				if (slot - 3 > 0)
					detectedHeat += getHeatInSlot(slot - 3, rod) / 2;
				if (slot + 4 < 24)
					detectedHeat += getHeatInSlot(slot + 4, rod) / 2;
			}
		}
		rod.HeatFunction(stack,true,detectedHeat*(rods/100f),cool,20,Math.pow(pressureLCE/30,2)*1000);
		rod.decay(stack,inventory,slot);
		NBTTagCompound data = stack.getTagCompound();
		if (data != null) {
			avgHeat += (data.getDouble("heat")-20)/24;
			if (data.getBoolean("nuke")) {
				this.kill = true;
				explode();
				LeafiaPassiveServer.queueFunction(()->{
					rod.nuke(world,pos.add(0,3,0));
				});
			}
			if (data.getInteger("spillage") > 200) {
				this.kill = true;
				explode();
			}
			return data.getDouble("cooled");
		}
		return 0; // failsafe
	}
	/**
	 * @author ntmleafia
	 * @reason overhaul
	 */
	@Overwrite
	public void update() {
		if (world.isRemote) {
			if (valveOpen && (valveLevel < 6))
				valveLevel++;
			if (!valveOpen && (valveLevel > 0))
				valveLevel--;
			if (world.rand.nextInt(5) == 0)
				dialX = 1;
			else
				dialX = world.rand.nextInt(3);
			if (world.rand.nextInt(3) == 0)
				dialY = world.rand.nextInt(2);
			else
				dialY = 1;
		} else {
			movedelay = (movedelay+1)%2;
			if (movedelay == 0) {
				if (rods > rodsTarget)
					rods--;
				else if (rods < rodsTarget)
					rods++;
			}

			if (world.getTotalWorldTime() % 20 == 0) {
				this.updateConnections();
			}

			carbonDioxide.loadTank(24, 25, inventory);
			water.loadTank(26, 27, inventory);

			if (rods >= 100) {
				for (int i = 0; i < 24; i++) {

					if (!inventory.getStackInSlot(i).isEmpty()) {
						if (inventory.getStackInSlot(i).getItem() instanceof ItemZirnoxRod)
							decay(i);
						else if (inventory.getStackInSlot(i).getItem() == ModItems.meteorite_sword_bred)
							inventory.setStackInSlot(i, new ItemStack(ModItems.meteorite_sword_irradiated));
					}
				}
			}

			lastPressureLCE = pressureLCE;
			this.pressureLCE = ((carbonDioxide.getFill() * 2) + (this.hulltemp-20)*125 * ((float)this.carbonDioxide.getFill() / (float)this.carbonDioxide.getMaxFill()))/3333;
			if (valveOpen)
				drain(carbonDioxide,(int)Math.ceil(Math.pow(Math.max(this.pressureLCE-5,0),3)/30+10),true);
			stressTimer -= Math.pow(Math.max(pressureLCE-16,0)/14,0.9)*64;
			if (stressTimer <= 0) {
				stressTimer = 300; // the audios were lot longer than i thought sooo
				world.playSound(null,pos.getX()+0.5,pos.getY()+2.5,pos.getZ()+0.5,LeafiaSoundEvents.stressSounds[world.rand.nextInt(7)],SoundCategory.BLOCKS, (float) MathHelper.clampedLerp(0.25,4,Math.pow((pressureLCE-16)/14,4)),1.0F);
			}
			if ((this.pressureLCE >= this.maxPressureLCE) && (pressureLCE >= lastPressureLCE-0.2)) {
				generosityTimer--;
				if (generosityTimer <= 0) {
					explode();
					return;
				}
			} else
				generosityTimer = 5*20;
			if (hulltemp >= 1300) {
				explode();
				return;
			}
			double coolin = (float) Math.pow(carbonDioxide.getFill()/16000f,0.4);

			hulltemp += (heat*0.00001*780)*coolin  /3.5; // haha
			heat = 0;
			double feedwatr = (float) Math.pow(water.getFill()/32000f,0.4);
			double cooledSum = 0;
			avgHeat = 20;
			for (int i = 0; i < 24; i++) {
				if (kill) return;
				if (inventory.getStackInSlot(i).getItem() instanceof LeafiaRodItem)
					cooledSum += handleLeafiaFuel(i,coolin*1.5);
			}
			if (kill) return;
			double difference = Math.abs(avgHeat-this.hulltemp);
			byte sign = 1;
			if (this.hulltemp > avgHeat) sign = -1;
			this.hulltemp += Math.pow(difference,0.25)*sign + cooledSum/24;
			double steamtemp = steam.getTankType().temperature;
			double boilBase = Math.pow(Math.max(this.hulltemp-(steamtemp),0),0.25)*Math.pow(steamtemp/100,0.75)*feedwatr;
			boilBase/=5;
			int boiling = (int)(boilBase*24);
			this.hulltemp = Math.max(this.hulltemp-boilBase/*-Math.pow(this.hulltemp,0.25)*/,20);
			switch(compression) {
				case 0: drain(water,boiling/100,true); break;
				case 1: drain(water,boiling/10,true); break;
				case 2: drain(water,boiling,true); break;
				case 3: drain(water,boiling*10,true); break;
			}
			steam.fill(steam.getTankType(),boiling,true);

			for (DirPos pos : getConPos())
				this.tryProvide(steam, world, pos.getPos().getX(), pos.getPos().getY(), pos.getPos().getZ(), pos.getDir());

			NBTTagCompound compound = new NBTTagCompound();
			water.writeToNBT(compound,"water");
			carbonDioxide.writeToNBT(compound,"carbondioxide");
			steam.writeToNBT(compound,"steam");
			LeafiaPacket._start(this)
					.__write(packetKeys.HULL_TEMP.key,hulltemp)
					.__write(packetKeys.PRESSURE.key,pressureLCE)
					.__write(packetKeys.CONTROL_RODS.key,rods)
					.__write(packetKeys.COMPRESSION.key,compression)
					.__write(packetKeys.CONTROL_RODS_TARGET.key,rodsTarget)
					.__write(packetKeys.OPENVALVE.key,valveOpen)
					.__write(packetKeys.TANKS.key,compound)
					.__sendToClients(15);
		}
	}
	@Unique
	private short getCompressionLevel(FluidType fluid) {
		if (fluid == Fluids.STEAM)
			return 1;
		else if (fluid == Fluids.HOTSTEAM)
			return 10;
		else if (fluid == Fluids.SUPERHOTSTEAM)
			return 100;
		else if (fluid == Fluids.ULTRAHOTSTEAM)
			return 1000;
		return 0;
	}
	@Unique
	public void setCompression(FluidType newType) {
		short curCompression = getCompressionLevel(steam.getTankType());
		short newCompression = getCompressionLevel(newType);
		int amount = this.steam.getFill();
		steam.setTankType(newType);
		steam.setFill(amount*curCompression/newCompression);
	}
	@Unique
	private void updateCompression() {
		switch(compression) {
			case 0: setCompression(Fluids.STEAM); break;
			case 1: setCompression(Fluids.HOTSTEAM); break;
			case 2: setCompression(Fluids.SUPERHOTSTEAM); break;
			case 3: setCompression(Fluids.ULTRAHOTSTEAM); break;
		}
	}
	@Unique
	private void spawnDebris(DebrisType type) {

		EntityZirnoxDebris debris = new EntityZirnoxDebris(world, pos.getX() + 0.5D, pos.getY() + 4D, pos.getZ() + 0.5D, type);
		debris.motionX = world.rand.nextGaussian() * 0.75D;
		debris.motionZ = world.rand.nextGaussian() * 0.75D;
		debris.motionY = 0.01D + world.rand.nextDouble() * 1.25D;

		if (type == DebrisType.CONCRETE) {
			debris.motionX *= 0.25D;
			debris.motionY += world.rand.nextDouble();
			debris.motionZ *= 0.25D;
		}

		if (type == EXCHANGER) {
			debris.motionX += 0.5D;
			debris.motionY *= 0.1D;
			debris.motionZ += 0.5D;
		}

		world.spawnEntity(debris);
	}
	@Unique
	private void zirnoxDebris() {
		for(int i = 0; i < 2; i++) {
			spawnDebris(EXCHANGER);
		}
		for(int i = 0; i < 50; i++) {
			spawnDebris(CONCRETE);
			spawnDebris(BLANK);
		}
		for(int i = 0; i < 30; i++) {
			spawnDebris(SHRAPNEL);
		}
		for(int i = 0; i < 10; i++) {
			spawnDebris(ELEMENT);
			spawnDebris(GRAPHITE);
		}
	}
	@Unique
	public void explode() {
		ItemStack prevStack = null;
		for(int i = 0; i < inventory.getSlots(); i++) {
			prevStack = LeafiaRodItem.comparePriority(inventory.getStackInSlot(i),prevStack);
			inventory.setStackInSlot(i, ItemStack.EMPTY);
		}
		NBTTagCompound data = new NBTTagCompound();
		data.setString("type", "rbmkmush");
		data.setFloat("scale", 4);
		PacketThreading.createAllAroundThreadedPacket(new AuxParticlePacketNT(data, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 250));
		MainRegistry.proxy.effectNT(data);

		int meta = this.getBlockMetadata();
		for (int ox = -2; ox <= 2; ox++) {
			for (int oz = -2; oz <= 2; oz++) {
				for (int oy = 2; ox <= 5; ox++) {
					world.setBlockToAir(pos.add(ox,oy,oz));
				}
			}
		}
		world.playSound(null,pos.getX()+0.5,pos.getY()+2.5,pos.getZ()+0.5,HBMSoundHandler.rbmk_explosion,SoundCategory.BLOCKS,50.0F,1.0F);

		boolean nope = true;
		if (prevStack != null) {
			if (prevStack.getItem() instanceof LeafiaRodItem) {
				nope = false;
				LeafiaRodItem rod = (LeafiaRodItem)(prevStack.getItem());
				rod.resetDetonate();
				rod.detonateRadius = 18;
				rod.detonateVisualsOnly = true;
				rod.detonate(world,pos);
			}
		}
		if (nope) {
			ExplosionNukeGeneric.waste(world, pos.getX(), pos.getY(), pos.getZ(), 35);
			ChunkRadiationManager.proxy.incrementRad(world, pos, 3000F, 4000F);
		}

		int[] dimensions = {1, 0, 2, 2, 2, 2};

		world.setBlockState(pos, ModBlocks.zirnox_destroyed.getStateFromMeta(meta), 3);
		MultiblockHandlerXR.fillSpace(world, pos.getX(), pos.getY(), pos.getZ(), dimensions, ModBlocks.zirnox_destroyed, ForgeDirection.getOrientation(meta - BlockDummyable.offset));

		world.createExplosion(null, pos.getX()+0.5, pos.getY()+2.5, pos.getZ()+0.5, 24.0F, true);
		zirnoxDebris();

		List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos).grow(100));
		for (EntityPlayer player : players) {
			AdvancementManager.grantAchievement(player, AdvancementManager.achZIRNOXBoom);
		}

		if(MobConfig.enableElementals) {
			for(EntityPlayer player : players) {
				player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).setBoolean("radMark", true);
			}
		}
	}

	/**
	 * @author ntmleafia
	 * @reason overwrite nbts yipee
	 */
	@Overwrite
	public void readFromNBT(NBTTagCompound compound) {
		hulltemp = compound.getDouble("hulltemp");
		pressureLCE = compound.getDouble("pressure");
		rods = compound.getByte("rods");
		rodsTarget = compound.getByte("rodsD");
		if(compound.hasKey("compression"))
			compression = compound.getByte("compression");
		if(compound.hasKey("inventory"))
			inventory.deserializeNBT(compound.getCompoundTag("inventory"));
		if(compound.hasKey("water"))
			water.readFromNBT(compound,"water");
		if(compound.hasKey("carbondioxide"))
			carbonDioxide.readFromNBT(compound,"carbondioxide");
		if(compound.hasKey("steam"))
			steam.readFromNBT(compound,"steam");
		super.readFromNBT(compound);
	}

	/**
	 * @author ntmleafia
	 * @reason overwrite nbts yipee
	 */
	@Overwrite
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setDouble("hulltemp", hulltemp);
		compound.setDouble("pressure", pressureLCE);
		compound.setByte("rods", rods);
		compound.setByte("rodsD", rodsTarget);
		compound.setByte("compression", compression);
		compound.setTag("inventory", inventory.serializeNBT());
		water.writeToNBT(compound,"water");
		carbonDioxide.writeToNBT(compound,"carbondioxide");
		steam.writeToNBT(compound,"steam");
		return super.writeToNBT(compound);
	}
	@Inject(method = "<init>",at = @At(value = "TAIL"),remap = false,require = 1)
	public void onInit(CallbackInfo ci) {
		//water = new FluidTankNTM(Fluids.WATER,32000);
		//carbonDioxide = new FluidTankNTM(Fluids.CARBONDIOXIDE,16000);
		steam = new FluidTankNTM(Fluids.STEAM,8000);
	}

	/**
	 * @author ntmleafia
	 * @reason use my fucking gui
	 */
	@Overwrite(remap = false)
	public Container provideContainer(int ID,EntityPlayer player,World world,int x,int y,int z) {
		return new ZirnoxContainer(player.inventory,(TileEntityReactorZirnox)(IMixinTileEntityReactorZIRNOX)this);
	}

	/**
	 * @author ntmleafia
	 * @reason use my fucking gui
	 */
	@Overwrite(remap = false)
	@SideOnly(Side.CLIENT)
	public GuiScreen provideGUI(int ID,EntityPlayer player,World world,int x,int y,int z) {
		return new ZirnoxGUI(player.inventory,(TileEntityReactorZirnox)(IMixinTileEntityReactorZIRNOX)this);
	}

	@Override
	public byte getRods() {
		return rods;
	}

	@Override
	public byte getRodsTarget() {
		return rodsTarget;
	}

	@Override
	public double getHullTemp() {
		return hulltemp;
	}

	@Override
	public double getMeltingPoint() {
		return meltingPoint;
	}

	@Override
	public double getPressure() {
		return pressureLCE;
	}

	@Override
	public double getLastPressure() {
		return lastPressureLCE;
	}

	@Override
	public double getMaxPressure() {
		return maxPressureLCE;
	}

	@Override
	public byte getCompression() {
		return compression;
	}

	@Override
	public boolean getValveOpen() {
		return valveOpen;
	}

	@Override
	public double getAvgHeat() {
		return avgHeat;
	}

	@Override
	public int dialX() {
		return dialX;
	}

	@Override
	public int dialY() {
		return dialY;
	}

	@Override
	public int valveLevel() {
		return valveLevel;
	}

	@Override
	public String getPacketIdentifier() {
		return "zirnox";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onReceivePacketLocal(byte key, Object value) {
		if (key == packetKeys.HULL_TEMP.key)
			hulltemp = (double)value;
		if (key == packetKeys.PRESSURE.key)
			pressureLCE = (double)value;
		if (key == packetKeys.CONTROL_RODS.key)
			rods = (byte)value;
		if (key == packetKeys.CONTROL_RODS_TARGET.key)
			rodsTarget = (byte)value;
		if (key == packetKeys.TANKS.key) {
			NBTTagCompound tag = (NBTTagCompound)value;
			water.readFromNBT(tag,"water");
			carbonDioxide.readFromNBT(tag,"carbondioxide");
			steam.readFromNBT(tag,"steam");
			if (steam.getTankType().equals(Fluids.STEAM))
				compression = 0;
			else if (steam.getTankType().equals(Fluids.HOTSTEAM))
				compression = 1;
			else if (steam.getTankType().equals(Fluids.SUPERHOTSTEAM))
				compression = 2;
			else if (steam.getTankType().equals(Fluids.ULTRAHOTSTEAM))
				compression = 3;
		} if (key == packetKeys.OPENVALVE.key)
			valveOpen = (boolean)value;
	}
	@Override
	public void onReceivePacketServer(byte key, Object value, EntityPlayer plr) {
		if (key == packetKeys.CONTROL_RODS.key)
			rodsTarget = (byte)value;
		if (key == packetKeys.COMPRESSION.key) {
			compression = (byte)value;
			updateCompression();
		}
		if (key == packetKeys.OPENVALVE.key) {
			this.world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), HBMSoundHandler.rbmk_az5_cover, SoundCategory.BLOCKS, 0.5F, 0.5F);
			valveOpen = (boolean)value;
		}
	}

	@Override
	public void onPlayerValidate(EntityPlayer plr) {

	}

	@Callback(direct = true)
	@Optional.Method(modid = "opencomputers")
	public Object[] getTemp(Context context, Arguments args) {
		return new Object[]{hulltemp};
	}
	@Callback(direct = true)
	@Optional.Method(modid = "opencomputers")
	public Object[] getPressure(Context context,Arguments args) {
		return new Object[]{pressureLCE};
	}
	@Callback(direct = true)
	@Optional.Method(modid = "opencomputers")
	public Object[] getInfo(Context context, Arguments args) {
		return new Object[]{hulltemp, pressureLCE, water.getFill(), steam.getFill(), carbonDioxide.getFill(), rods};
	}
	@Callback(direct = true)
	@Optional.Method(modid = "opencomputers")
	public Object[] setControl(Context context,Arguments args) {
		rodsTarget = (byte)MathHelper.clamp(args.checkInteger(0),0,100);
		return new Object[]{rodsTarget};
	}
	@Callback(direct = true)
	@Optional.Method(modid = "opencomputers")
	public Object[] getControl(Context context,Arguments args) {
		return new Object[]{rods};
	}

	@Override
	@Optional.Method(modid = "opencomputers")
	public String[] methods() {
		return new String[]{
				"getTemp",
				"getPressure",
				"getWater",
				"getSteam",
				"getCarbonDioxide",
				"getControl",
				"getInfo",
				"setControl"
		};
	}

	@Override
	@Optional.Method(modid = "opencomputers")
	public Object[] invoke(String method, Context context, Arguments args) throws Exception {
		switch (method) {
			case ("getTemp"):
				return getTemp(context, args);
			case ("getPressure"):
				return getPressure(context, args);
			case ("getWater"):
				return getWater(context, args);
			case ("getSteam"):
				return getSteam(context, args);
			case ("getCarbonDioxide"):
				return getCarbonDioxide(context, args);
			case ("getControl"):
				return getControl(context, args);
			case ("setControl"):
				return setControl(context, args);
			case ("getInfo"):
				return getInfo(context, args);
		}
		throw new NoSuchMethodException();
	}
}
