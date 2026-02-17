package com.leafia.contents.machines.powercores.ams.stabilizer;

import com.hbm.api.energymk2.IEnergyReceiverMK2;
import com.hbm.api.fluidmk2.IFluidStandardReceiverMK2;
import com.hbm.explosion.ExplosionLarge;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.hbm.lib.DirPos;
import com.hbm.lib.ForgeDirection;
import com.hbm.lib.HBMSoundHandler;
import com.hbm.lib.Library;
import com.hbm.tileentity.IGUIProvider;
import com.hbm.util.ParticleUtil;
import com.leafia.contents.AddonItems;
import com.leafia.contents.machines.powercores.ams.stabilizer.container.AMSStabilizerContainer;
import com.leafia.contents.machines.powercores.ams.stabilizer.container.AMSStabilizerUI;
import com.leafia.dev.LeafiaUtil;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.container_utility.LeafiaPacketReceiver;
import com.leafia.init.LeafiaSoundEvents;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import java.util.Random;

public class AMSStabilizerTE extends TileEntity implements ITickable, IFluidStandardReceiverMK2, IEnergyReceiverMK2, IGUIProvider, LeafiaPacketReceiver {

	public static int rotateShitfuck(int meta) {
		EnumFacing bruh = EnumFacing.byIndex(meta-10);
		return bruh.rotateY().getIndex()+10;
	}

	public ItemStackHandler inventory;

	public long power = 0;
	public static final long maxPower = 10000000;
	public int efficiency = 0;
	public static final int maxEfficiency = 100;
	public int heat = 0;
	public static final int maxHeat = 2500;
	public int age = 0;
	public int warning = 0;
	public int mode = 0;
	public boolean locked = false;
	public FluidTankNTM tank;
	public boolean needsUpdate;

	Random rand = new Random();

	//private static final int[] slots_top = new int[] { 0 };
	//private static final int[] slots_bottom = new int[] { 0 };
	//private static final int[] slots_side = new int[] { 0 };

	private String customName;

	public AMSStabilizerTE() {
		inventory = new ItemStackHandler(4){
			@Override
			protected void onContentsChanged(int slot) {
				markDirty();
				super.onContentsChanged(slot);
			}
		};
		tank = new FluidTankNTM(Fluids.COOLANT,8000);
		needsUpdate = false;
	}
	
	public String getInventoryName() {
		return this.hasCustomInventoryName() ? this.customName : "tile.ams_limiter.name";
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

	public DirPos[] getConPos() {
		EnumFacing face = EnumFacing.byIndex(getBlockMetadata()-10);
		return new DirPos[]{
				new DirPos(pos.offset(face),ForgeDirection.getOrientation(face)),
				new DirPos(pos.offset(face.getOpposite()),ForgeDirection.getOrientation(face.getOpposite())),

				new DirPos(pos.offset(face.rotateY(),2).offset(face),ForgeDirection.getOrientation(face)),
				new DirPos(pos.offset(face.rotateY(),2).offset(face.getOpposite()),ForgeDirection.getOrientation(face.getOpposite())),
				new DirPos(pos.offset(face.rotateY(),3),ForgeDirection.getOrientation(face.rotateY())),

				new DirPos(pos.offset(face.rotateY(),-2).offset(face),ForgeDirection.getOrientation(face)),
				new DirPos(pos.offset(face.rotateY(),-2).offset(face.getOpposite()),ForgeDirection.getOrientation(face.getOpposite())),
				new DirPos(pos.offset(face.rotateY(),-3),ForgeDirection.getOrientation(face.rotateY().getOpposite()))
		};
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		power = compound.getLong("power");
		if (compound.hasKey("coolant"))
			tank.readFromNBT(compound,"coolant");
		efficiency = compound.getInteger("efficiency");
		heat = compound.getInteger("heat");
		locked = compound.getBoolean("locked");
		if(compound.hasKey("inventory"))
			inventory.deserializeNBT(compound.getCompoundTag("inventory"));
		super.readFromNBT(compound);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setLong("power", power);
		tank.writeToNBT(compound,"coolant");
		compound.setInteger("efficiency", efficiency);
		compound.setInteger("heat", heat);
		compound.setBoolean("locked", locked);
		compound.setTag("inventory", inventory.serializeNBT());
		return super.writeToNBT(compound);
	}
	
	@Override
	public void update() {
		if (!world.isRemote) {
			
			if(!locked) {
				LeafiaUtil.setTypeOnly(tank,0,1,inventory,Fluids.WATER,Fluids.COOLANT,Fluids.CRYOGEL);

				for (DirPos con : getConPos()) {
					trySubscribe(world,con);
					trySubscribe(tank.getTankType(),world,con);
				}

				if(needsUpdate){
					needsUpdate = false;
				}
				
				if(power > 0) {
					//" - (maxHeat / 2)" offsets center to 50% instead of 0%
					efficiency = Math.round(calcEffect(power, heat - (maxHeat / 2)) * 100);
					power -= Math.ceil(power * 0.025);
					warning = 0;
				} else {
					efficiency = 0;
					warning = 1;
				}
				
				if(tank.getTankType() == Fluids.CRYOGEL) {
					
					if(tank.getFluidAmount() >= 5) {
						if(heat > 0){
							tank.drain(5, true);
							needsUpdate = true;
						}

						if(heat <= maxHeat / 2)
							if(efficiency > 0)
								heat += efficiency;
							else
								for(int i = 0; i < 10; i++)
									if(heat > 0)
										heat--;
						
						for(int i = 0; i < 10; i++)
							if(heat > maxHeat / 2)
								heat--;
					} else {
						heat += efficiency;
					}
				} else if(tank.getTankType() == Fluids.COOLANT) {
					
					if(tank.getFluidAmount() >= 5) {
						if(heat > 0){
							tank.drain(5, true);
							needsUpdate = true;
						}

						if(heat <= maxHeat / 4)
							if(efficiency > 0)
								heat += efficiency;
							else
								for(int i = 0; i < 5; i++)
									if(heat > 0)
										heat--;
						
						for(int i = 0; i < 5; i++)
							if(heat > maxHeat / 4)
								heat--;
					} else {
						heat += efficiency;
					}
				} else if(tank.getTankType() == Fluids.WATER) {
					
					if(tank.getFluidAmount() >= 15) {
						if(heat > 0){
							tank.drain(15, true);
							needsUpdate = true;
						}

						if(heat <= maxHeat * 0.85)
							if(efficiency > 0)
								heat += efficiency;
							else
								for(int i = 0; i < 2; i++)
									if(heat > 0)
										heat--;
						
						for(int i = 0; i < 2; i++)
							if(heat > maxHeat * 0.85)
								heat--;
					} else {
						heat += efficiency;
					}
				} else {
					heat += efficiency;
					warning = 2;
				}
				
				mode = 0;
				if(!inventory.getStackInSlot(2).isEmpty()) {
					if(inventory.getStackInSlot(2).getItem() == AddonItems.ams_focus_limiter)
						mode = 1;
					else if(inventory.getStackInSlot(2).getItem() == AddonItems.ams_focus_booster)
						mode = 2;
					else {
						this.efficiency = 0;
						this.warning = 2;
					}
				} else {
					this.efficiency = 0;
					this.warning = 2;
				}
				
				if(tank.getFluidAmount() <= 5 || heat > maxHeat * 0.9)
					warning = 2;
				
				if(heat > maxHeat) {
					heat = maxHeat;
					locked = true;
					ExplosionLarge.spawnShock(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 24, 3);
					ExplosionLarge.spawnBurst(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 24, 3);
		            this.world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), HBMSoundHandler.oldExplosion, SoundCategory.BLOCKS, 10.0F, 1);
			        this.world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), LeafiaSoundEvents.machineDestroyed, SoundCategory.BLOCKS, 10.0F, 1.0F);
				}
	
				power = Library.chargeTEFromItems(inventory, 3, power, maxPower);
				
			} else {
				//fire particles n stuff
				int meta = rotateShitfuck(this.getBlockMetadata())-10;
				
				double pos2 = rand.nextDouble() * 2.5;
				double off = 0.25;

				if(meta == 2)
					ParticleUtil.spawnGasFlame(world, pos.getX() + 0.5 + off, pos.getY() + 5.5, pos.getZ() + 0.5 - pos2, 0.0, 0.0, 0.0);
				if(meta == 3)
					ParticleUtil.spawnGasFlame(world, pos.getX() + 0.5 - off, pos.getY() + 5.5, pos.getZ() + 0.5 + pos2, 0.0, 0.0, 0.0);
				if(meta == 4)
					ParticleUtil.spawnGasFlame(world, pos.getX() + 0.5 - pos2, pos.getY() + 5.5, pos.getZ() + 0.5 - off, 0.0, 0.0, 0.0);
				if(meta == 5)
					ParticleUtil.spawnGasFlame(world, pos.getX() + 0.5 + pos2, pos.getY() + 5.5, pos.getZ() + 0.5 + off, 0.0, 0.0, 0.0);

				// TODO: particle bullshit
				efficiency = 0;
				power = 0;
				warning = 3;
			}
			
			//tankType = ModForgeFluids.cryogel;
			//tank.drain(tank.getCapacity(), true);
			//tank.fill(new FluidStack(ModForgeFluids.cryogel, tank.getCapacity()), true);

			/*PacketDispatcher.wrapper.sendToAllAround(new AuxElectricityPacket(pos, power), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 15));
			PacketDispatcher.wrapper.sendToAllTracking(new AuxGaugePacket(pos, locked ? 1 : 0, 0), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 15));
			PacketDispatcher.wrapper.sendToAllTracking(new AuxGaugePacket(pos, efficiency, 1), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 15));
			PacketDispatcher.wrapper.sendToAllAround(new FluidTankPacket(pos, new FluidTank[]{tank}), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 15));
			 */
			LeafiaPacket._start(this)
					.__write(0,power)
					.__write(1,locked)
					.__write(2,efficiency)
					.__write(3,heat)
					.__write(4,tank.getTankType().getID())
					.__write(5,tank.getFill())
					.__sendToClients(250);
		}
	}

	private float gauss(float a, float x) {
		
		//Greater values -> less difference of temperate impact
		double amplifier = 0.10;
		
		return (float) ( (1/Math.sqrt(a * Math.PI)) * Math.pow(Math.E, -1 * Math.pow(x, 2)/amplifier) );
	}
	
	private float calcEffect(float a, float x) {
		return (float) (gauss( 1 / a, x / maxHeat) * Math.sqrt(Math.PI * 2) / (Math.sqrt(2) * Math.sqrt(maxPower)));
	}
	
	public long getPowerScaled(long i) {
		return (power * i) / maxPower;
	}
	
	public int getEfficiencyScaled(int i) {
		return (efficiency * i) / maxEfficiency;
	}
	
	public int getHeatScaled(int i) {
		return (heat * i) / maxHeat;
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
	
	public boolean isValidFluid(FluidType fluid){
		if(fluid != null && (fluid == Fluids.WATER || fluid == Fluids.COOLANT || fluid == Fluids.CRYOGEL))
			return true;
		return false;
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
	public @NotNull FluidTankNTM[] getReceivingTanks() {
		return new FluidTankNTM[]{tank};
	}

	@Override
	public FluidTankNTM[] getAllTanks() {
		return new FluidTankNTM[]{tank};
	}

	@Override
	public Container provideContainer(int i,EntityPlayer entityPlayer,World world,int i1,int i2,int i3) {
		return new AMSStabilizerContainer(entityPlayer.inventory,this);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public GuiScreen provideGUI(int i,EntityPlayer entityPlayer,World world,int i1,int i2,int i3) {
		return new AMSStabilizerUI(entityPlayer.inventory,this);
	}

	@Override
	public String getPacketIdentifier() {
		return "AMS_LMTR";
	}
	@Override
	public void onReceivePacketLocal(byte key,Object value) {
		switch(key) {
			case 0 -> power = (long)value;
			case 1 -> locked = (boolean)value;
			case 2 -> efficiency = (int)value;
			case 3 -> heat = (int)value;
			case 4 -> {
				FluidType type = Fluids.fromID((int)value);
				if (!tank.getTankType().equals(type)) {
					int fill = tank.getFill();
					tank.setTankType(type);
					tank.setFill(fill);
				}
			}
			case 5 -> tank.setFill((int)value);
		}
	}
	@Override
	public void onReceivePacketServer(byte key,Object value,EntityPlayer plr) { }
	@Override
	public void onPlayerValidate(EntityPlayer plr) { }
}
