package com.leafia.contents.machines.powercores.ams.base;

import com.hbm.api.energymk2.IEnergyProviderMK2;
import com.hbm.api.fluidmk2.IFluidStandardReceiverMK2;
import com.hbm.entity.effect.EntityCloudFleijaRainbow;
import com.hbm.entity.logic.EntityNukeExplosionMK5;
import com.hbm.handler.ArmorUtil;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.hbm.items.ISatChip;
import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemCatalyst;
import com.hbm.items.special.ItemAMSCore;
import com.hbm.lib.DirPos;
import com.hbm.lib.ForgeDirection;
import com.hbm.lib.Library;
import com.hbm.lib.ModDamageSource;
import com.hbm.saveddata.satellites.SatelliteResonator;
import com.hbm.saveddata.satellites.SatelliteSavedData;
import com.hbm.tileentity.IGUIProvider;
import com.leafia.contents.fluids.traits.FT_DFCFuel;
import com.leafia.contents.machines.powercores.ams.base.container.AMSBaseContainer;
import com.leafia.contents.machines.powercores.ams.base.container.AMSBaseUI;
import com.leafia.contents.machines.powercores.ams.emitter.AMSEmitterTE;
import com.leafia.contents.machines.powercores.ams.stabilizer.AMSStabilizerTE;
import com.leafia.dev.LeafiaUtil;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.container_utility.LeafiaPacketReceiver;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import java.util.Random;

import java.util.List;

public class AMSBaseTE extends TileEntity implements ITickable, IFluidStandardReceiverMK2, IEnergyProviderMK2, IGUIProvider, LeafiaPacketReceiver {

	public ItemStackHandler inventory;

	public long power = 0;
	public static final long maxPower = 1000000000000000L;
	public int field = 0;
	public static final int maxField = 100;
	public int efficiency = 0;
	public static final int maxEfficiency = 100;
	public int heat = 0;
	public static final int maxHeat = 5000;
	public int age = 0;
	public int warning = 0;
	public int mode = 0;
	public boolean locked = false;
	public FluidTankNTM[] tanks;
	public int color = -1;
	public boolean needsUpdate;
	public boolean syncResonators = false;

	Random rand = new Random();

	//private static final int[] slots_top = new int[] { 0 };
	//private static final int[] slots_bottom = new int[] { 0 };
	//private static final int[] slots_side = new int[] { 0 };

	private String customName;

	public AMSBaseTE() {
		inventory = new ItemStackHandler(16){
			@Override
			protected void onContentsChanged(int slot) {
				markDirty();
				super.onContentsChanged(slot);
			}
		};
		tanks = new FluidTankNTM[4];
		needsUpdate = false;
		
		tanks[0] = new FluidTankNTM(Fluids.COOLANT,8000);
		
		tanks[1] = new FluidTankNTM(Fluids.CRYOGEL,8000);
		
		tanks[2] = new FluidTankNTM(Fluids.DEUTERIUM,8000);
		
		tanks[3] = new FluidTankNTM(Fluids.TRITIUM,8000);
	}
	
	public String getInventoryName() {
		return this.hasCustomInventoryName() ? this.customName : "tile.ams_base.name";
	}

	public boolean hasCustomInventoryName() {
		return this.customName != null && this.customName.length() > 0;
	}
	
	public void setCustomName(String name) {
		this.customName = name;
	}
	
	public boolean isUseableByPlayer(EntityPlayer player) {
		if(world.getTileEntity(pos) != this)
		{
			return false;
		}else{
			return player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <=128;
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		power = compound.getLong("power");
		field = compound.getInteger("field");
		efficiency = compound.getInteger("efficiency");
		heat = compound.getInteger("heat");
		locked = compound.getBoolean("locked");
		if(compound.hasKey("inventory"))
			inventory.deserializeNBT(compound.getCompoundTag("inventory"));
		if (compound.hasKey("coolantA"))
			tanks[0].readFromNBT(compound,"coolantA");
		if (compound.hasKey("coolantB"))
			tanks[1].readFromNBT(compound,"coolantB");
		if (compound.hasKey("fuelA"))
			tanks[2].readFromNBT(compound,"fuelA");
		if (compound.hasKey("fuelB"))
			tanks[3].readFromNBT(compound,"fuelB");
		super.readFromNBT(compound);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setLong("power", power);
		compound.setInteger("field", field);
		compound.setInteger("efficiency", efficiency);
		compound.setInteger("heat", heat);
		compound.setBoolean("locked", locked);
		compound.setTag("inventory", inventory.serializeNBT());
		tanks[0].writeToNBT(compound,"coolantA");
		tanks[1].writeToNBT(compound,"coolantB");
		tanks[2].writeToNBT(compound,"fuelA");
		tanks[3].writeToNBT(compound,"fuelB");
		return super.writeToNBT(compound);
	}

	public DirPos[] getConPos() {
		return new DirPos[]{
				new DirPos(pos.down(),ForgeDirection.DOWN),
				new DirPos(pos.offset(EnumFacing.NORTH,2),ForgeDirection.NORTH),
				new DirPos(pos.offset(EnumFacing.SOUTH,2),ForgeDirection.SOUTH),
				new DirPos(pos.offset(EnumFacing.EAST,2),ForgeDirection.EAST),
				new DirPos(pos.offset(EnumFacing.WEST,2),ForgeDirection.WEST)
		};
	}
	
	@Override
	public void update() {
		boolean isSetUp = inventory.getStackInSlot(8).getItem() instanceof ItemCatalyst && inventory.getStackInSlot(9).getItem() instanceof ItemCatalyst &&
				inventory.getStackInSlot(10).getItem() instanceof ItemCatalyst && inventory.getStackInSlot(11).getItem() instanceof ItemCatalyst &&
				inventory.getStackInSlot(12).getItem() instanceof ItemAMSCore;
		if (!world.isRemote) {
			if(needsUpdate){
				needsUpdate = false;
			}
				
			
			/*for(int i = 0; i < tanks.length; i++){
				tanks[i].fill(new FluidStack(tankTypes[i], tanks[i].getCapacity()), true);
				needsUpdate = true;
			}*/
			
			if(!locked) {
				for (DirPos con : getConPos()) {
					tryProvide(world,con.getPos(),con.getDir());
					for (int i = 0; i < 4; i++)
						trySubscribe(tanks[i].getTankType(),world,con);
				}
				//for (int t = 0; t < 8; t+=2)
				//	tanks[t/4*2].setType(t,t+1,inventory);
				LeafiaUtil.setTypeBy(tanks[0],0,1,inventory,AMSBaseTE::isValidCoolant,tanks[1].getTankType());
				LeafiaUtil.setTypeBy(tanks[1],2,3,inventory,AMSBaseTE::isValidCoolant,tanks[0].getTankType());
				LeafiaUtil.setTypeBy(tanks[2],4,5,inventory,AMSBaseTE::isValidFuel,tanks[3].getTankType());
				LeafiaUtil.setTypeBy(tanks[3],6,7,inventory,AMSBaseTE::isValidFuel,tanks[2].getTankType());

				age++;
				if(age >= 20)
				{
					age = 0;
				}
				
				int f1 = 0, f2 = 0, f3 = 0, f4 = 0;
				int booster = 0;

				if(world.getTileEntity(pos.add(6, 0, 0)) instanceof AMSStabilizerTE) {
					AMSStabilizerTE te = (AMSStabilizerTE)world.getTileEntity(pos.add(6, 0, 0));
					if(!te.locked && AMSStabilizerTE.rotateShitfuck(te.getBlockMetadata())-10 == 4) {
						f1 = te.efficiency;
						if(te.mode == 2)
							booster++;
					}
				}
				if(world.getTileEntity(pos.add(-6, 0, 0)) instanceof AMSStabilizerTE) {
					AMSStabilizerTE te = (AMSStabilizerTE)world.getTileEntity(pos.add(-6, 0, 0));
					if(!te.locked && AMSStabilizerTE.rotateShitfuck(te.getBlockMetadata())-10 == 5) {
						f2 = te.efficiency;
						if(te.mode == 2)
							booster++;
					}
				}
				if(world.getTileEntity(pos.add(0, 0, 6)) instanceof AMSStabilizerTE) {
					AMSStabilizerTE te = (AMSStabilizerTE)world.getTileEntity(pos.add(0, 0, 6));
					if(!te.locked && AMSStabilizerTE.rotateShitfuck(te.getBlockMetadata())-10 == 2) {
						f3 = te.efficiency;
						if(te.mode == 2)
							booster++;
					}
				}
				if(world.getTileEntity(pos.add(0, 0, -6)) instanceof AMSStabilizerTE) {
					AMSStabilizerTE te = (AMSStabilizerTE)world.getTileEntity(pos.add(0, 0, -6));
					if(!te.locked && AMSStabilizerTE.rotateShitfuck(te.getBlockMetadata())-10 == 3) {
						f4 = te.efficiency;
						if(te.mode == 2)
							booster++;
					}
				}
				
				this.field = Math.round(calcField(f1, f2, f3, f4));
				
				mode = 0;
				if(field > 0)
					mode = 1;
				if(booster > 0)
					mode = 2;
				
				if(world.getTileEntity(pos.add(0, 9, 0)) instanceof AMSEmitterTE) {
					AMSEmitterTE te = (AMSEmitterTE)world.getTileEntity(pos.add(0, 9, 0));
						this.efficiency = te.efficiency;
				}
				
				this.color = -1;
				
				float powerMod = 1;
				float heatMod = 1;
				float fuelMod = 1;
				long powerBase = 0;
				int heatBase = 0;
				int fuelBase = 0;
				
				if(isSetUp && hasResonators() && efficiency > 0) {
					int a = ((ItemCatalyst)inventory.getStackInSlot(8).getItem()).getColor();
					int b = ((ItemCatalyst)inventory.getStackInSlot(9).getItem()).getColor();
					int c = ((ItemCatalyst)inventory.getStackInSlot(10).getItem()).getColor();
					int d = ((ItemCatalyst)inventory.getStackInSlot(11).getItem()).getColor();

					int e = this.calcAvgHex(a, b);
					int f = this.calcAvgHex(c, d);
					
					int g = this.calcAvgHex(e, f);
					
					this.color = g;

					
					for(int i = 8; i < 12; i++) {
						powerBase += ItemCatalyst.getPowerAbs(inventory.getStackInSlot(i));
						powerMod *= ItemCatalyst.getPowerMod(inventory.getStackInSlot(i));
						heatMod *= ItemCatalyst.getHeatMod(inventory.getStackInSlot(i));
						fuelMod *= ItemCatalyst.getFuelMod(inventory.getStackInSlot(i));
					}

					powerBase = ItemAMSCore.getPowerBase(inventory.getStackInSlot(12))*2000000L;
					heatBase = (int)(Math.sqrt(ItemAMSCore.getHeatBase(inventory.getStackInSlot(12)))*220);
					fuelBase = (int)(ItemAMSCore.getFuelBase(inventory.getStackInSlot(12))*(100/15f));
					
					powerBase *= this.efficiency;
					powerBase *= Math.pow(1.25F, booster);
					heatBase *= Math.pow(1.25F, booster);
					heatBase *= (100 - field);
					
					if(this.getFuelPower(tanks[2].getTankType()) > 0 && this.getFuelPower(tanks[3].getTankType()) > 0 &&
							tanks[2].getFluidAmount() > 0 && tanks[3].getFluidAmount() > 0) {

						power += (powerBase * powerMod * gauss(1, (heat - (maxHeat / 2)) / maxHeat)) / 1000 * getFuelPower(tanks[2].getTankType()) * getFuelPower(tanks[3].getTankType());
						heat += (heatBase * heatMod) / (float)(this.field / 100F);
						tanks[2].drain((int)(fuelBase * fuelMod), true);
						tanks[3].drain((int)(fuelBase * fuelMod), true);
						
						radiation();

						if(heat > maxHeat) {
							explode();
							heat = maxHeat;
						}
						
						if(field <= 0)
							explode();
					}
				}
				
				if(power > maxPower)
					power = maxPower;
				
				
				if(heat > 0 && tanks[0].getFluidAmount() > 0 && tanks[1].getFluidAmount() > 0) {
					heat -= (this.getCoolingStrength(tanks[0].getTankType()) * this.getCoolingStrength(tanks[1].getTankType()));

					tanks[0].drain(10, true);
					tanks[1].drain(10, true);
					
					if(heat < 0)
						heat = 0;
				}
				
			} else {
				field = 0;
				efficiency = 0;
				power = 0;
				warning = 33;
			}
			//this.sendPower(world, pos);

			/*PacketDispatcher.wrapper.sendToAllAround(new AuxElectricityPacket(pos, power), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 15));
			PacketDispatcher.wrapper.sendToAllTracking(new AuxGaugePacket(pos, hasResonators() ? 1 : 0, 4), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 15));
			PacketDispatcher.wrapper.sendToAllTracking(new AuxGaugePacket(pos, locked ? 1 : 0, 0), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 15));
			PacketDispatcher.wrapper.sendToAllTracking(new AuxGaugePacket(pos, color, 1), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 15));
			PacketDispatcher.wrapper.sendToAllTracking(new AuxGaugePacket(pos, efficiency, 2), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 15));
			PacketDispatcher.wrapper.sendToAllTracking(new AuxGaugePacket(pos, field, 3), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 15));
			PacketDispatcher.wrapper.sendToAllTracking(new FluidTankPacket(pos, new FluidTank[] {tanks[0], tanks[1], tanks[2], tanks[3]}), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 15));
			 */
			NBTTagCompound sendTanks = new NBTTagCompound();
			for (int i = 0; i < 4; i++)
				tanks[i].writeToNBT(sendTanks,"tank"+i);
			LeafiaPacket._start(this)
					.__write(0,power)
					.__write(1,locked)
					.__write(2,efficiency)
					.__write(3,heat)
					.__write(4,sendTanks)
					.__write(5,hasResonators())
					.__write(6,color)
					.__write(7,field)
					.__sendToClients(250);
		} else {
			if (!hasResonators())
				warning = 3;
			else if (!isSetUp)
				warning = 2;
			else if (efficiency <= 0)
				warning = 1;
			else
				warning = 0;
		}
	}
	
	private void radiation() {
		
		double maxSize = 5;
		double minSize = 0.5;
		double scale = minSize;
		scale += ((((double)this.tanks[2].getFluidAmount()) / ((double)this.tanks[2].getCapacity())) + (((double)this.tanks[3].getFluidAmount()) / ((double)this.tanks[3].getCapacity()))) * ((maxSize - minSize) / 2);

		scale *= 0.60;
		
		List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos.getX() - 10 + 0.5, pos.getY() - 10 + 0.5 + 6, pos.getZ() - 10 + 0.5, pos.getX() + 10 + 0.5, pos.getY() + 10 + 0.5 + 6, pos.getZ() + 10 + 0.5));
		
		for(Entity e : list) {
			if(!(e instanceof EntityPlayer && ArmorUtil.checkForHazmat((EntityPlayer)e)))
				if(!Library.isObstructed(world, pos.getX() + 0.5, pos.getY() + 0.5 + 6, pos.getZ() + 0.5, e.posX, e.posY + e.getEyeHeight(), e.posZ)) {
					e.attackEntityFrom(ModDamageSource.ams, 1000);
					e.setFire(3);
				}
		}

		List<Entity> list2 = world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos.getX() - scale + 0.5, pos.getY() - scale + 0.5 + 6, pos.getZ() - scale + 0.5, pos.getX() + scale + 0.5, pos.getY() + scale + 0.5 + 6, pos.getZ() + scale + 0.5));
		
		for(Entity e : list2) {
			if(!(e instanceof EntityPlayer && ArmorUtil.checkForHaz2((EntityPlayer)e)))
					e.attackEntityFrom(ModDamageSource.amsCore, 10000);
		}
	}
	
	private void explode() {
		if(!world.isRemote) {
			
			for(int i = 0; i < 10; i++) {

	    		EntityCloudFleijaRainbow cloud = new EntityCloudFleijaRainbow(this.world, 100);
	    		cloud.posX = pos.getX() + rand.nextInt(201) - 100;
	    		cloud.posY = pos.getY() + rand.nextInt(201) - 100;
	    		cloud.posZ = pos.getZ() + rand.nextInt(201) - 100;
	    		this.world.spawnEntity(cloud);
			}
			
			int radius = (int)(50 + (double)(tanks[2].getFluidAmount() + tanks[3].getFluidAmount()) / 16000D * 150);
			
			world.spawnEntity(EntityNukeExplosionMK5.statFac(world, radius, pos.getX(), pos.getY(), pos.getZ()));
			
			world.setBlockToAir(pos);
		}
	}

	public static boolean isValidCoolant(FluidType type) {
		return type.equals(Fluids.WATER) || type.equals(Fluids.OIL) || type.equals(Fluids.COOLANT) || type.equals(Fluids.CRYOGEL);
	}

	private int getCoolingStrength(FluidType type) {
		if(type == null)
			return 0;
		else if(type == Fluids.WATER){
			return 5;
		} else if(type == Fluids.OIL){
			return 15;
		} else if(type == Fluids.COOLANT){
			return this.heat / 250;
		} else if(type == Fluids.CRYOGEL){
			return this.heat > heat/2 ? 25 : 5;
		} else {
			return 0;
		}
	}

	public static boolean isValidFuel(FluidType type) {
		return getFuelPower(type) > 0;
	}
	
	public static int getFuelPower(FluidType type) {
		if(type == null)
			return 0;
		else if(type == Fluids.DEUTERIUM){
			return 50;
		} else if(type == Fluids.TRITIUM){
			return 75;
		} else {
			if (type.hasTrait(FT_DFCFuel.class))
				return (int)(type.getTrait(FT_DFCFuel.class).getModifier()*49.6794871795);
			return 0;
		}
	}
	
	private float gauss(float a, float x) {
		
		//Greater values -> less difference of temperate impact
		double amplifier = 0.10;
		
		return (float) ( (1/Math.sqrt(a * Math.PI)) * Math.pow(Math.E, -1 * Math.pow(x, 2)/amplifier) );
	}
	
	/*private float calcEffect(float a, float x) {
		return (float) (gauss( 1 / a, x / maxHeat) * Math.sqrt(Math.PI * 2) / (Math.sqrt(2) * Math.sqrt(maxPower)));
	}*/
	
	private float calcField(int a, int b, int c, int d) {
		return (float)(a + b + c + d) * (a * 25 + b * 25 + c * 25 + d  * 25) / 40000;
	}
	
	private int calcAvgHex(int h1, int h2) {

		int r1 = ((h1 & 0xFF0000) >> 16);
		int g1 = ((h1 & 0x00FF00) >> 8);
		int b1 = ((h1 & 0x0000FF) >> 0);
		
		int r2 = ((h2 & 0xFF0000) >> 16);
		int g2 = ((h2 & 0x00FF00) >> 8);
		int b2 = ((h2 & 0x0000FF) >> 0);

		int r = (((r1 + r2) / 2) << 16);
		int g = (((g1 + g2) / 2) << 8);
		int b = (((b1 + b2) / 2) << 0);
		
		return r | g | b;
	}
	
	public long getPowerScaled(long i) {
		return (power * i) / maxPower;
	}
	
	public int getEfficiencyScaled(int i) {
		return (efficiency * i) / maxEfficiency;
	}
	
	public int getFieldScaled(int i) {
		return (field * i) / maxField;
	}
	
	public int getHeatScaled(int i) {
		return (heat * i) / maxHeat;
	}
	
	public boolean hasResonators() {
		//Drillgon200: Always returns true anyway
		if (world.isRemote) return syncResonators;
		// not anymore lmao
		if(!inventory.getStackInSlot(13).isEmpty() && !inventory.getStackInSlot(14).isEmpty() && !inventory.getStackInSlot(15).isEmpty() &&
				inventory.getStackInSlot(13).getItem() == ModItems.sat_chip && inventory.getStackInSlot(14).getItem() == ModItems.sat_chip && inventory.getStackInSlot(15).getItem() == ModItems.sat_chip) {
			
		    SatelliteSavedData data = (SatelliteSavedData)world.getPerWorldStorage().getOrLoadData(SatelliteSavedData.class, "satellites");
		    if(data == null) {
		        world.getPerWorldStorage().setData("satellites", new SatelliteSavedData());
		        data = (SatelliteSavedData)world.getPerWorldStorage().getOrLoadData(SatelliteSavedData.class, "satellites");
		    }
		    data.markDirty();

		    int i1 = ISatChip.getFreqS(inventory.getStackInSlot(13));
		    int i2 = ISatChip.getFreqS(inventory.getStackInSlot(14));
		    int i3 = ISatChip.getFreqS(inventory.getStackInSlot(15));
		    
		    if(data.getSatFromFreq(i1) != null && data.getSatFromFreq(i2) != null && data.getSatFromFreq(i3) != null &&
		    		data.getSatFromFreq(i1) instanceof SatelliteResonator && data.getSatFromFreq(i2) instanceof SatelliteResonator && data.getSatFromFreq(i3) instanceof SatelliteResonator &&
		    		i1 != i2 && i1 != i3 && i2 != i3)
		    	return true;
			
		}
		
		return false;
	}
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return TileEntity.INFINITE_EXTENT_AABB;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared()
	{
		return 65536.0D;
	}

	@Override
	public long getPower() {
		return power;
	}

	@Override
	public long getMaxPower() {
		return maxPower;
	}

	@Override
	public void setPower(long power) {
		this.power = power;
	}

	@Override
	public boolean isLoaded() {
		return true;
	}

	@Override
	public FluidTankNTM[] getAllTanks() {
		return tanks;
	}

	@Override
	public @NotNull FluidTankNTM[] getReceivingTanks() {
		return tanks;
	}

	@Override
	public Container provideContainer(int i,EntityPlayer entityPlayer,World world,int i1,int i2,int i3) {
		return new AMSBaseContainer(entityPlayer.inventory,this);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public GuiScreen provideGUI(int i,EntityPlayer entityPlayer,World world,int i1,int i2,int i3) {
		return new AMSBaseUI(entityPlayer.inventory,this);
	}

	@Override
	public String getPacketIdentifier() {
		return "AMS_BASE";
	}
	@Override
	public void onReceivePacketLocal(byte key,Object value) {
		switch(key) {
			case 0 -> power = (long)value;
			case 1 -> locked = (boolean)value;
			case 2 -> efficiency = (int)value;
			case 3 -> heat = (int)value;
			case 4 -> {
				NBTTagCompound tag = (NBTTagCompound)value;
				for (int i = 0; i < 4; i++)
					tanks[i].readFromNBT(tag,"tank"+i);
			}
			case 5 -> syncResonators = (boolean)value;
			case 6 -> color = (int)value;
			case 7 -> field = (int)value;
		}
	}
	@Override
	public void onReceivePacketServer(byte key,Object value,EntityPlayer plr) { }
	@Override
	public void onPlayerValidate(EntityPlayer plr) { }
}
