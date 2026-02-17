package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.api.energymk2.IEnergyProviderMK2;
import com.hbm.api.fluid.IFluidStandardReceiver;
import com.hbm.blocks.ModBlocks;
import com.hbm.explosion.ExplosionLarge;
import com.hbm.interfaces.ILaserable;
import com.hbm.inventory.control_panel.*;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.hbm.lib.ForgeDirection;
import com.hbm.tileentity.IGUIProvider;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.tileentity.machine.TileEntityCore;
import com.hbm.tileentity.machine.TileEntityCoreInjector;
import com.hbm.tileentity.machine.TileEntityCoreReceiver;
import com.hbm.util.Tuple.Pair;
import com.leafia.contents.machines.powercores.dfc.components.absorber.CoreReceiverContainer;
import com.leafia.contents.machines.powercores.dfc.components.absorber.CoreReceiverGUI;
import com.leafia.contents.machines.powercores.dfc.components.injector.CoreInjectorContainer;
import com.leafia.contents.machines.powercores.dfc.components.injector.CoreInjectorGUI;
import com.leafia.contents.machines.powercores.dfc.debris.AbsorberShrapnelEntity;
import com.leafia.contents.machines.powercores.dfc.debris.AbsorberShrapnelEntity.DebrisType;
import com.leafia.contents.network.spk_cable.uninos.ISPKReceiver;
import com.leafia.dev.LeafiaDebug;
import com.leafia.dev.NTMFNBT;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.math.FiaMatrix;
import com.leafia.init.LeafiaSoundEvents;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityCore;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityCoreReceiver;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityInjector;
import com.leafia.passive.LeafiaPassiveServer;
import com.leafia.settings.AddonConfig;
import com.llib.LeafiaLib.NumScale;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
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

import java.util.*;

@Optional.InterfaceList({@Optional.Interface(iface = "li.cil.oc.api.network.SimpleComponent", modid = "opencomputers")})
@Mixin(value = TileEntityCoreReceiver.class)
public abstract class MixinTileEntityCoreReceiver extends TileEntityMachineBase implements ITickable, IMixinTileEntityCoreReceiver, IEnergyProviderMK2, ILaserable, IFluidStandardReceiver, IGUIProvider, IControllable, SimpleComponent {
	@Shadow(remap = false) public long joules;

	@Shadow(remap = false) public long prevJoules;

	@Unique public long netRemaining = 0;

	@Shadow(remap = false) public long power;
	@Shadow(remap = false) public FluidTankNTM tank;
	@Unique public TileEntityCore core = null;

	@Unique public long fuck_you = 0;
	@Unique public long throughputLimit = 0;

	@Unique public double level = 1;

	public MixinTileEntityCoreReceiver(int scount) {
		super(scount);
	}
	@Override
	public double getLevel() {
		return level;
	}
	@Override
	public void setLevel(double value) {
		level = value;
	}
	@Override
	public TileEntityCore getCore() {
		return core;
	}

	void spawnShrapnel(DebrisType type) {
		Vec3d center = new Vec3d(pos).add(0.5, 0.5, 0.5);
		FiaMatrix mat = new FiaMatrix(center, center.add(new Vec3d(EnumFacing.byIndex(this.getBlockMetadata()).getDirectionVec())));
		AbsorberShrapnelEntity entity = new AbsorberShrapnelEntity(world, mat.getX(), mat.getY(), mat.getZ(), type);
		double forward = 0.35;
		double spread = 0.15;
		switch (type) {
			case CABLE:
				mat = mat.rotateX(90).rotateY(world.rand.nextInt(4) * 90);
				spread = 0.2;
				break;
			case CORE:
				entity.motionX = world.rand.nextGaussian() * 0.05;
				entity.motionY = world.rand.nextGaussian() * 0.05;
				entity.motionZ = world.rand.nextGaussian() * 0.05;
				world.spawnEntity(entity);
				return;
			case CORNER:
				mat = mat.rotateX(world.rand.nextInt(4) * 90 - 45).rotateY(world.rand.nextInt(2) * 90 - 45);
				break;
			case FRONT:
				spread = 0.2;
				break;
			case BEAM:
				int rand = world.rand.nextInt(12);
				if (rand < 8) mat = mat.rotateY(Math.floorDiv(rand, 2) * 90).rotateX(Math.floorMod(rand, 2) * 90 - 45);
				else mat = mat.rotateY((rand - 8) * 90 - 45);
				break;
		}
		Vec3d flyDirection = mat.frontVector.scale((world.rand.nextDouble() + 1) / 2 * forward)
				.add(mat.upVector.scale(world.rand.nextGaussian() * spread)).add(mat.rightVector.scale(world.rand.nextGaussian() * spread));
		entity.setPosition(mat.getX() + mat.frontVector.x / 3, mat.getY() + mat.frontVector.y / 3, mat.getZ() + mat.frontVector.z / 3);
		entity.motionX = flyDirection.x;
		entity.motionY = flyDirection.y;
		entity.motionZ = flyDirection.z;
		world.spawnEntity(entity);
	}
	@Override
	public void explode() {
		world.setBlockToAir(pos);
		for (int i = 0; i < 3; i++) spawnShrapnel(DebrisType.BEAM);
		for (int i = 0; i < 2; i++) spawnShrapnel(DebrisType.CORNER);
		spawnShrapnel(DebrisType.CABLE);
		spawnShrapnel(DebrisType.CORE);
		spawnShrapnel(DebrisType.FRONT);
		ExplosionLarge.spawnBurst(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 12, 3);
		this.world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), LeafiaSoundEvents.machineExplode, SoundCategory.BLOCKS, 10.0F, 1);
		world.newExplosion(null, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, 2f, true, true);
	}
	@Unique int destructionLevel = 0;

	/**
	 * @author ntmleafia
	 * @reason yipe yipe
	 */
	@Override
	@Overwrite
	public void update() {
		core = null;
		EnumFacing facing = getFront();
		throughputLimit = fuck_you;
		fuck_you = 0;
		/*
		EnumFacing facing = EnumFacing.getFront(this.getBlockMetadata());
		for(int i = 1; i <= TileEntityCoreEmitter.range; i++) {
			BlockPos offs = pos.offset(facing,i);
			TileEntity te = world.getTileEntity(offs);
			if (te instanceof TileEntityCore) {
				core = (TileEntityCore)te;
				core.absorbers.add(this);
			}
			if (!world.getBlockState(offs).getMaterial().isReplaceable()) break;
		}*/
		core = getCore(AddonConfig.dfcComponentRange);
		IMixinTileEntityCore mcore = (IMixinTileEntityCore)core;
		if (core != null)
			mcore.getDFCAbsorbers().add((TileEntityCoreReceiver) (Object) this);
		if (!world.isRemote) {
			LeafiaPacket._start(this).__write(31,targetPosition).__sendToAffectedClients();
			if (joules >= NumScale.GIGA*100L && world.getBlockState(pos).getBlock() == ModBlocks.dfc_receiver) {
				destructionLevel = Math.min(destructionLevel+2,400);
				if (destructionLevel > 300 && world.rand.nextInt(100) == 0) {
					this.explode();
					return;
				}
			} else {
				destructionLevel = Math.max(destructionLevel-1,0);
			}

			updateSPKConnections();
			if (Long.MAX_VALUE - power < joules * 5000L)
				power = Long.MAX_VALUE;
			else
				power += joules * 5000L;

			this.subscribeToAllAround(this.tank.getTankType(), this);
			for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
				this.tryProvide(this.world, this.pos.getX() + dir.offsetX, this.pos.getY() + dir.offsetY, this.pos.getZ() + dir.offsetZ, dir);
			}

			long remaining = power / 5000L;
			long totalTransfer = 0;
			if (remaining > 0) {
				List<Pair<TileEntity, EnumFacing>> targets = new ArrayList<>();
				//for (EnumFacing outFace : EnumFacing.VALUES) {
					//if (outFace.equals(facing)) continue;
				{
					EnumFacing outFace = facing.getOpposite();
                    BlockPos target = pos.offset(outFace);
                    ForgeDirection dir = ForgeDirection.getOrientation(outFace);
                    TileEntity te = world.getTileEntity(target);
                    tryLinkSPK(world, target, te, dir);
					if (te instanceof ISPKReceiver receiver && receiver.isInputPreferrable(dir))
                        targets.add(new Pair<>(te, outFace));
				}
				if (!targets.isEmpty()) {
					long transfer = remaining / (long) targets.size();
					for (Pair<TileEntity, EnumFacing> target : targets) {
                        // FIXME: can't rely on this to calculate the transfer amount, the net update is independent of this
                        totalTransfer += tryProvideSPK(target.getKey(), ForgeDirection.getOrientation(target.getValue()), transfer, false);
                    }
                    power -= Math.max(this.power-totalTransfer * 5000L,0);
				}
			}
			netRemaining = power / 5000L;

			LeafiaDebug.debugLog(world,power);

			if (joules > 0) {

				if (tank.getFluidAmount() >= 20) {
					tank.drain(20, true);
				} else {
					world.setBlockState(pos, Blocks.FLOWING_LAVA.getDefaultState());
					return;
				}
			}

			syncJoules = joules;

			joules = 0;
			LeafiaPacket._start(this).__write(2,level)/*.__write(3,power)*/.__write(4,/*totalTransfer+*/cableTransfer).__sendToAffectedClients(); // fuick fuck fuck fuck fuck
			cableTransfer = 0;
		} else {
			tickJoules[needle] = joules;
			needle = Math.floorMod(needle+1,20);
			joulesPerSec = 0;
			for (long tickJoule : tickJoules)
				joulesPerSec += Math.max(0,tickJoule-syncSpk)/20d;
			fanAngle += Math.floorMod(720/20,360);
		}
	}

	@Inject(method = "readFromNBT",at = @At("HEAD"),require = 1)
	public void onReadFromNBT(NBTTagCompound compound,CallbackInfo ci) {
		readTargetPos(compound);
		level = compound.getDouble("level");
		// big bruh
		this.power = compound.getLong("power");
		this.joules = compound.getLong("joules");
		this.tank.readFromNBT(compound, "tank");
	}

	@Inject(method = "writeToNBT",at = @At("HEAD"),require = 1)
	public void onWriteToNBT(NBTTagCompound compound,CallbackInfoReturnable<NBTTagCompound> cir) {
		writeTargetPos(compound);
		compound.setDouble("level",level);
		// big bruh
		compound.setLong("power", this.power);
		compound.setLong("joules", this.joules);
		this.tank.writeToNBT(compound, "tank");
	}

	// cleitn sht
	@Unique public int fanAngle = 0;
	@Unique public double joulesPerSec = 0;

	@Override public int fanAngle() { return fanAngle; }
	@Override public double joulesPerSec() { return joulesPerSec; }

	@Unique long[] tickJoules = new long[20];
	@Unique int needle = 0;

	@Unique public long syncJoules;
	@Unique public long syncSpk = 0;

	@Override
	public long syncJoules() {
		return syncJoules;
	}

	@Override
	public long syncSpk() { return syncSpk; }

	@Override
	public void sendToPlayer(EntityPlayer player) {
		LeafiaPacket._start(this)
				.__write(0,syncJoules)
				.__write(1,power)
				.__write(2,level)
				.__write(5,tank.getFill())
				.__sendToClient(player);
	}

	@Override
	public void onReceivePacketLocal(byte key,Object value) {
		IMixinTileEntityCoreReceiver.super.onReceivePacketLocal(key, value);
		switch(key) {
			case 0: joules = (long)value; break;
			case 1: power = (long)value; break;
			case 2: level = (double)value; break;
			case 5: tank.setFill((int)value); tank.setTankType(Fluids.CRYOGEL);
			break;
			case 4: syncSpk = (long)value; break;
		}
	}

	@Override
	public void onReceivePacketServer(byte key,Object value,EntityPlayer plr) {
		IMixinTileEntityCoreReceiver.super.onReceivePacketServer(key, value, plr);
		if (key == 0)
			level = (double)value;
	}

    public void updateSPKConnections() {
        for (ForgeDirection f : ForgeDirection.VALID_DIRECTIONS)
            if (isInputPreferrable(f)) trySubscribeSPK(world, pos, f);
    }

    @Override
    public boolean isInputPreferrable(ForgeDirection direction) {
        EnumFacing dir = direction.toEnumFacing();
        Vec3d unit = getDirection();
        double component;
        if (dir.getAxis() == EnumFacing.Axis.X) component = unit.x;
        else if (dir.getAxis() == EnumFacing.Axis.Y) component = unit.y;
        else component = unit.z;
        component *= dir.getOpposite().getAxisDirection().getOffset();
        return component > 0.707;//dir.getOpposite().ordinal() == this.getBlockMetadata();
    }

    @Override
    public long getSPK() { return joules; }

	@Override
	public long getNetRemaining() { return netRemaining; }

    @Override
    public void setSPK(long power) {
        this.joules = power;
    }
	long cableTransfer = 0;

	@Override
	public void usePower(long power) {
		IEnergyProviderMK2.super.usePower(power);
		fuck_you += power;
		netRemaining = this.power / 5000L;
	}

	@Override
	public void setPower(long power) {
		this.power = power;
		netRemaining = this.power / 5000L;
	}

	@Override
	public void setTransferredSpk(long power) {
		cableTransfer += power;
		LeafiaPassiveServer.queueFunction(()->this.power = Math.max(this.power-power*5000,0));
	}

	@Override
    public long getMaxSPK() {
        return Long.MAX_VALUE;
    }

	@Override
	public long getSPKProviderSpeed() {
		return Math.max(syncJoules-throughputLimit/5000,0);
	}

	@Unique private BlockPos targetPosition = new BlockPos(0,0,0);
	@Unique public TileEntityCore lastGetCore = null;


	@Override
	public TileEntityCore lastGetCore() {
		return lastGetCore;
	}

	@Override
	public void lastGetCore(TileEntityCore core) {
		lastGetCore = core;
	}

	@Override
	public BlockPos getTargetPosition() {
		return targetPosition;
	}
	@Override
	public void targetPosition(BlockPos pos) {
		targetPosition = pos;
	}

	@Override
	public String getPacketIdentifier() {
		return "dfc_absorber";
	}

	/**
	 * @author ntmleafia
	 * @reason uses different gui
	 */
	@Override
	@Overwrite(remap = false)
	public Container provideContainer(int i,EntityPlayer entityPlayer,World world,int i1,int i2,int i3) {
		return new CoreReceiverContainer(entityPlayer,(TileEntityCoreReceiver)(IMixinTileEntityCoreReceiver)this);
	}

	/**
	 * @author ntmleafia
	 * @reason uses different gui
	 */
	@Override
	@Overwrite(remap = false)
	@SideOnly(Side.CLIENT)
	public GuiScreen provideGUI(int i,EntityPlayer entityPlayer,World world,int i1,int i2,int i3) {
		return new CoreReceiverGUI(entityPlayer,(TileEntityCoreReceiver)(IMixinTileEntityCoreReceiver)this);
	}

	// CP //
	@Override
	public BlockPos getControlPos() {
		return getPos();
	}

	@Override
	public World getControlWorld() {
		return getWorld();
	}

	@Override
	public void receiveEvent(BlockPos from,ControlEvent e) {
		if (e.name.equals("set_absorber_level")) {
			level = e.vars.get("level").getNumber()/100d;
		}
	}
	@Override
	public Map<String,DataValue> getQueryData() {
		Map<String,DataValue> map = new HashMap<>();
		map.put("level",new DataValueFloat((float)(level*100)));
		map.put("stress",new DataValueFloat(destructionLevel*100/300f));
		map.put("received",new DataValueFloat(syncJoules));
		map.put("power",new DataValueFloat(power));
		return map;
	}

	@Override
	public List<String> getInEvents() {
		return Collections.singletonList("set_absorber_level");
	}

	@Override
	public void validate(){
		super.validate();
		ControlEventSystem.get(world).addControllable(this);
	}

	@Override
	public void invalidate(){
		super.invalidate();
		ControlEventSystem.get(world).removeControllable(this);
	}

	// OC //
	@Inject(method = "getComponentName",at = @At(value = "HEAD"),cancellable = true,remap = false)
	public void onGetComponentName(CallbackInfoReturnable<String> cir) {
		cir.setReturnValue("dfc_absorber");
		cir.cancel();
	}

	@Optional.Method(modid = "opencomputers")
	@Callback
	public Object[] incomingEnergy(Context context,Arguments args) {
		return new Object[]{syncJoules};
	}

	@Optional.Method(modid = "opencomputers")
	@Callback
	public Object[] outgoingPower(Context context, Arguments args) {
		return new Object[]{power};
	}

	@Optional.Method(modid = "opencomputers")
	@Callback
	public Object[] storedCoolant(Context context, Arguments args) {
		return new Object[]{tank.getFill()};
	}

	@Optional.Method(modid = "opencomputers")
	@Callback
	public Object[] getStress(Context context, Arguments args) {
		return new Object[]{destructionLevel*100/300f};
	}


	@Optional.Method(modid = "opencomputers")
	@Callback(doc = "setLevel(newLevel: number [0~100])->(previousLevel: number)")
	public Object[] setLevel(Context context, Arguments args) {
		double level = args.checkDouble(0);
		level = MathHelper.clamp(level,0,100);
		double prevLevel = level*100;
		this.level = level/100d;
		return new Object[]{prevLevel*100};
	}

	@Optional.Method(modid = "opencomputers")
	@Callback(doc = "getLevel()->(level: number [0-100])")
	public Object[] getLevel(Context context, Arguments args) {
		return new Object[]{level};
	}
}
