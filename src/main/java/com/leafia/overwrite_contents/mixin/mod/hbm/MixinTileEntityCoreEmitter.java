package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.custom_hbm.sound.LCEAudioWrapper;
import com.hbm.api.energymk2.IEnergyReceiverMK2;
import com.hbm.api.fluid.IFluidStandardReceiver;
import com.hbm.api.fluidmk2.IFluidStandardSenderMK2;
import com.hbm.interfaces.ILaserable;
import com.hbm.inventory.control_panel.*;
import com.hbm.inventory.control_panel.types.*;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.hbm.lib.ForgeDirection;
import com.hbm.lib.ModDamageSource;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.tileentity.machine.TileEntityCore;
import com.hbm.tileentity.machine.TileEntityCoreEmitter;
import com.hbm.tileentity.machine.TileEntityCoreReceiver;
import com.leafia.AddonBase;
import com.leafia.contents.AddonBlocks;
import com.leafia.contents.AddonFluids;
import com.leafia.contents.network.spk_cable.uninos.ISPKReceiver;
import com.leafia.dev.LeafiaUtil;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.init.LeafiaSoundEvents;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityCore;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityCoreEmitter;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityCoreReceiver;
import com.leafia.settings.AddonConfig;
import com.llib.LeafiaLib;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.*;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Optional.InterfaceList({@Optional.Interface(iface = "li.cil.oc.api.network.SimpleComponent", modid = "opencomputers")})
@Mixin(value = TileEntityCoreEmitter.class, remap = false)
public abstract class MixinTileEntityCoreEmitter extends TileEntityMachineBase implements ITickable, IMixinTileEntityCoreEmitter, IEnergyReceiverMK2, ILaserable, IFluidStandardReceiver, IControllable, SimpleComponent, IFluidStandardSenderMK2 {
    public MixinTileEntityCoreEmitter(int scount) {
        super(scount);
    }
    @Final
    @Shadow
    public static long maxPower;
    @Shadow
    public long power;
    @Shadow
    public int watts;
    @Shadow
    public int beam;
    @Shadow
    public long joules;
    @Shadow
    public boolean isOn;
    @Shadow
    public FluidTankNTM tank;
    @Unique
    public FluidTankNTM leafia$output = new FluidTankNTM(AddonFluids.PYROGEL,64000);
    @Shadow
    public long prev;
    @Unique
    private BlockPos leafia$targetPosition = new BlockPos(0,0,0);
    @Unique
    private RayTraceResult leafia$lastRaycast;
    @Unique
    private TileEntityCore leafia$lastGetCore;

    @Unique
    private boolean leafia$isActive;

    @Unique
    LCEAudioWrapper leafia$sound;
    @Unique boolean leafia$isPlaying = false;
    @Unique void leafia$playSound() {
        if (leafia$sound == null) {
            leafia$sound = AddonBase.proxy.getLoopedSoundStartStop(
                    world,
                    LeafiaSoundEvents.laser1loop,
                    LeafiaSoundEvents.laser1start,
                    LeafiaSoundEvents.laser1stop,
                    SoundCategory.BLOCKS,
                    pos.getX()+0.5f,pos.getY()+0.5f,pos.getZ()+0.5f,
                    1,1
            ).setCustomAttenuation((intended,distance)->Math.pow(Math.max(0,1-distance/50),6)/4);
        }
        if (!leafia$isPlaying)
            leafia$sound.startSound();
        leafia$isPlaying = true;
    }
    @Unique void leafia$stopSound() {
        if (leafia$sound == null) return;
        if (leafia$isPlaying)
            leafia$sound.stopSound();
        leafia$isPlaying = false;
    }

    /**
     * @author mlbv
     * @reason seriously who cares?
     */
    @Override
    @Overwrite
    public void update() {
        if (!world.isRemote) {
            LeafiaPacket._start(this).__write(31,leafia$targetPosition).__sendToAffectedClients();

            for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                this.trySubscribe(this.world, this.pos.getX() + dir.offsetX, this.pos.getY() + dir.offsetY, this.pos.getZ() + dir.offsetZ, dir);
            }

            this.subscribeToAllAround(this.tank.getTankType(), this);
            this.updateSPKConnections();

            watts = MathHelper.clamp(watts, 1, 100);
            long demand = maxPower * Math.min(watts, 100) / 2000;
            leafia$isActive = false;

            beam = 0;

            if (joules > 0 || prev > 0) {

                if (tank.getTankType() == Fluids.CRYOGEL && tank.getFill() >= 20) {
                    tank.setFill(tank.getFill()-20);
                    leafia$output.setFill(Math.min(leafia$output.getMaxFill(),leafia$output.getFill()+20));
                } else if (tank.getTankType() == Fluids.PERFLUOROMETHYL_COLD && tank.getFill() >= 2000) {
                    tank.setFill(tank.getFill()-2000);
                    leafia$output.setFill(Math.min(leafia$output.getMaxFill(),leafia$output.getFill()+2000));
                } else {
                    world.setBlockState(pos, AddonBlocks.fluid_osmiridium.getDefaultState());
                    return;
                }
            }

            if (isOn) {
                //i.e. 50,000,000 HE = 10,000 SPK
                //1 SPK = 5,000HE

                if (power >= demand) {
                    power -= demand;
                    long add = watts * 100;
                    if (add > Long.MAX_VALUE - joules)
                        joules = Long.MAX_VALUE;
                    else
                        joules += add;
                }
                prev = joules;

                if (joules > 0) {

                    long out = joules;

					/*
					EnumFacing dir = EnumFacing.getFront(this.getBlockMetadata());
					for(int i = 1; i <= range; i++) {

						beam = i;

						int x = pos.getX() + dir.getFrontOffsetX() * i;
						int y = pos.getY() + dir.getFrontOffsetY() * i;
						int z = pos.getZ() + dir.getFrontOffsetZ() * i;

						BlockPos pos1 = new BlockPos(x, y, z);


					}*/
                    leafia$isActive = true;
                    leafia$raycast(out);

                    joules = 0;
                }
            } else {
                joules = 0;
                prev = 0;
            }

            this.markDirty();

            for (EnumFacing face : EnumFacing.values())
                tryProvide(leafia$output,world,pos.offset(face),ForgeDirection.getOrientation(face));

            NBTTagCompound compound = new NBTTagCompound();
            tank.writeToNBT(compound,"t");
            leafia$output.writeToNBT(compound,"o");
            LeafiaPacket packet = LeafiaPacket._start(this)
                                              .__write(0, isOn)
                                              .__write(1, watts)
                                              .__write(2, prev)
                                              .__write(3,leafia$isActive)
                                              .__write(4, compound)
                                              .__write(5,power);
            //if (watts != prevWatts)
            //	packet.__write(1,watts);
            packet.__sendToAffectedClients();
			/*
			//PacketDispatcher.wrapper.sendToAllTracking(new AuxGaugePacket(pos, beam, 0), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 250));
			if(watts != prevWatts) PacketDispatcher.wrapper.sendToAllTracking(new AuxGaugePacket(pos, watts, 1), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 250));
			PacketDispatcher.wrapper.sendToAllTracking(new AuxLongPacket(pos, prev, 0), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 250));
			prevWatts = watts;
			*/
            //this.networkPack(data, 250);
        } else {
            if (isOn) {
                leafia$playSound();
                leafia$lastRaycast = leafia$raycast(0);
            } else
                leafia$stopSound();
        }
    }

    /**
     * @author ntmleafia
     * @reason fuck you
     */
    @Overwrite
    public @NotNull FluidTankNTM[] getAllTanks() {
        return new FluidTankNTM[]{ tank,leafia$output };
    }

    @Override
    public @NotNull FluidTankNTM[] getSendingTanks() {
        return new FluidTankNTM[]{ leafia$output };
    }

    @Unique
    private void updateSPKConnections() {
        for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if (isInputPreferrable(dir))
                this.trySubscribeSPK(this.world, this.pos.getX() + dir.offsetX, this.pos.getY() + dir.offsetY, this.pos.getZ() + dir.offsetZ, dir);
        }
    }

    //mlbv: intentionally marked this final. overriding may need bridge methods when you need to call super.raycast due to compile-time visibility problems
    //actually im wrong!
    @Override
    public RayTraceResult leafia$raycast(long out) {
        return LeafiaLib.leafiaRayTraceBlocksCustom(world, new Vec3d(pos).add(0.5, 0.5, 0.5), new Vec3d(pos).add(0.5, 0.5, 0.5).add(leafia$getDirection().scale(AddonConfig.dfcComponentRange)), (process,config,current) -> {
            if (!world.isRemote) {
                Vec3d centerVec = current.posIntended.add(new Vec3d(config.pivotAxisFace.getDirectionVec()).scale(0.5)
                                                                                                           .add(config.secondaryVector.scale(0.5)));
                List<Entity> list = world.getEntitiesWithinAABB(Entity.class, LeafiaUtil.createAABB(
                        centerVec.subtract(0.5, 0.5, 0.5), centerVec.add(0.5, 0.5, 0.5)
                ));
                for (Entity e : list) {
                    e.attackEntityFrom(ModDamageSource.amsCore, joules * 0.000001F);
                    e.setFire(10);
                }
            }
            if (current.posSnapped.equals(pos)) return process.CONTINUE();

            RayTraceResult miss = new RayTraceResult(RayTraceResult.Type.MISS, current.posIntended, config.pivotAxisFace, current.posSnapped);
            if (!current.block.canCollideCheck(current.state, true))
                return process.CONTINUE(miss);

            RayTraceResult result = current.state.collisionRayTrace(world, current.posSnapped, current.posIntended.subtract(config.unitDir.scale(2)), current.posIntended.add(config.unitDir.scale(2)));
            if (result == null)
                return process.CONTINUE(miss);

            Vec3d vec = result.hitVec;
            TileEntity te = world.getTileEntity(current.posSnapped);
            if (te instanceof ISPKReceiver receiver) {
                if (receiver.isInputPreferrable(ForgeDirection.getOrientation(config.pivotAxisFace))) {
                    if (!world.isRemote) receiver.transferSPK(out*100*watts/10000,false);
                    return process.RETURN(result);
                } else if (te instanceof TileEntityCoreReceiver) {
                    ((IMixinTileEntityCoreReceiver)te).leafia$explode();
                }
            }

            if (te instanceof TileEntityCore) {
                //out = Math.max(0, ((TileEntityCore)te).burn(out));
                IMixinTileEntityCore mixin = (IMixinTileEntityCore) te;
                if (!world.isRemote) mixin.setDFCIncomingSpk(mixin.getDFCIncomingSpk() + out / 1000_000d);
                //continue;
                //break;
                result.hitVec = new Vec3d(te.getPos()); // align to the center
                mixin.getDFCComponentPositions().add(pos);
                return process.RETURN(result);
            }

            IBlockState state = current.state;

            if (current.block != Blocks.AIR) { //(!state.getRenderType().equals(EnumBlockRenderType.INVISIBLE)) {
                if (!world.isRemote) {
                    if (state.getMaterial().isLiquid()) {
                        world.playSound(null, vec.x, vec.y, vec.z, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        world.setBlockToAir(result.getBlockPos());
                        return process.RETURN(result);
                    }
                    @SuppressWarnings("deprecation")
                    float hardness = state.getBlock().getExplosionResistance(null);
                    if (hardness < 10000 && world.rand.nextDouble() / 20 < (out * 0.00000001F) / hardness) {
                        world.playSound(null, vec.x, vec.y, vec.z, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        world.getBlockState(result.getBlockPos()).getBlock().onBlockExploded(world, result.getBlockPos(), new Explosion(world, null, result.getBlockPos().getX(), result.getBlockPos().getY(), result.getBlockPos().getZ(), 5, false, false));
                        //world.destroyBlock(pos1, false);
                    }
                }
                return process.RETURN(result);
            } else
                return process.CONTINUE(result);
        });
    }

    @Inject(method = "readFromNBT",at = @At("HEAD"),remap = true,require = 1)
    public void onReadFromNBT(NBTTagCompound compound,CallbackInfo ci) {
        leafia$readTargetPos(compound);
        //bandaid shitfix
        this.power = compound.getLong("power");
        this.watts = compound.getInteger("watts");
        this.joules = compound.getLong("joules");
        this.prev = compound.getLong("prev");
        this.isOn = compound.getBoolean("isOn");
        this.tank.readFromNBT(compound, "tank");
        if (compound.hasKey("output"))
            this.leafia$output.readFromNBT(compound,"output");
    }

    @Inject(method = "writeToNBT",at = @At("HEAD"),remap = true,require = 1)
    public void onWriteToNBT(NBTTagCompound compound,CallbackInfoReturnable<NBTTagCompound> cir) {
        leafia$writeTargetPos(compound);
        //bandaid shitfix
        compound.setLong("power", this.power);
        compound.setInteger("watts", this.watts);
        compound.setLong("joules", this.joules);
        compound.setLong("prev", this.prev);
        compound.setBoolean("isOn", this.isOn);
        this.tank.writeToNBT(compound, "tank");
        this.leafia$output.writeToNBT(compound, "output");
    }

    // networking
    @Override
    public String getPacketIdentifier() {
        return "DFC_BOOSTER";
    }
    @Override
    public void onReceivePacketLocal(byte key, Object value) {
        IMixinTileEntityCoreEmitter.super.onReceivePacketLocal(key, value);
        switch (key) {
            case 0:
                isOn = (boolean) value;
                break;
            case 1:
                watts = (int) value;
                break;
            case 2:
                prev = (long) value;
                break;
            case 3:
                leafia$isActive = (boolean) value;
                break;
            case 4:
                NBTTagCompound tag = (NBTTagCompound)value;
                tank.readFromNBT(tag,"t");
                leafia$output.readFromNBT(tag,"o");
                break;
            case 5:
                power = (long)value;
                break;
        }
    }

    @Override
    public TileEntityCore leafia$lastGetCore() {
        return leafia$lastGetCore;
    }

    @Override
    public void leafia$lastGetCore(TileEntityCore core) {
        leafia$lastGetCore = core;
    }

    @Override
    public boolean isInputPreferrable(ForgeDirection side) {
        return side.getOpposite().ordinal() != getBlockMetadata();
    }

    @Override
    public long getSPK(){
        return joules;
    }

    @Override
    public void setSPK(long power) {
        joules = power;
    }

    @Override
    public long getMaxSPK() {
        return Long.MAX_VALUE;
    }

    @Override
    public BlockPos leafia$getTargetPosition() {
        return leafia$targetPosition;
    }

    @Override
    public void leafia$targetPosition(BlockPos pos) {
        leafia$targetPosition = pos;
    }

    @Override
    public boolean leafia$isActive() {
        return leafia$isActive;
    }

    @Override
    public void leafia$isActive(boolean active) {
        leafia$isActive = active;
    }

    @Override
    public RayTraceResult leafia$lastRaycast() {
        return leafia$lastRaycast;
    }

    @Override
    public FluidTankNTM leafia$getOutputTank() {
        return leafia$output;
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
        if (e.name.equals("set_booster_active")) {
            isOn = e.vars.get("active").getNumber() >= 1f;
        } else if (e.name.equals("set_booster_level")) {
            watts = Math.round(e.vars.get("level").getNumber());
        }
    }
    @Override
    public Map<String,DataValue> getQueryData() {
        Map<String,DataValue> map = new HashMap<>();
        map.put("active",new DataValueFloat(isOn ? 1 : 0));
        map.put("level",new DataValueFloat(watts));
        map.put("emitted",new DataValueFloat(prev));
        return map;
    }

    @Override
    public List<String> getInEvents() {
        return Arrays.asList("set_booster_level","set_booster_active");
    }

    @Override
    public void validate(){
        super.validate();
        ControlEventSystem.get(world).addControllable(this);
    }

    @Override
    public void invalidate(){
        super.invalidate();
        if (leafia$sound != null) {
            leafia$sound.stopSound();
            leafia$sound = null;
        }
        ControlEventSystem.get(world).removeControllable(this);
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        if (leafia$sound != null) {
            leafia$sound.stopSound();
            leafia$sound = null;
        }
    }

    // OC //
    @Optional.Method(modid = "opencomputers")
    @Callback(doc = "setLevel(newLevel: number)->(previousLevel: number)")
    public Object[] setLevel(Context context,Arguments args) {
        Object[] prev = new Object[]{watts};
        watts = MathHelper.clamp(args.checkInteger(0), 1, 100);
        return prev;
    }

    @Optional.Method(modid = "opencomputers")
    @Callback(doc = "getLevel()->(level: number)")
    public Object[] getLevel(Context context, Arguments args) {
        return new Object[]{watts};
    }

    @Optional.Method(modid = "opencomputers")
    @Callback
    public Object[] getEmitted(Context context, Arguments args) {
        return new Object[]{prev};
    }

    @Optional.Method(modid = "opencomputers")
    @Callback(doc = "getActive()->(active: boolean)")
    public Object[] getActive(Context context, Arguments args) {
        return new Object[]{isOn};
    }

    @Optional.Method(modid = "opencomputers")
    @Callback(doc = "setActive(active: boolean)->(previously: boolean)")
    public Object[] setActive(Context context, Arguments args) {
        boolean wasOn = isOn;
        isOn = args.checkBoolean(0);
        return new Object[]{wasOn};
    }

    @Optional.Method(modid = "opencomputers")
    @Callback(doc = "getPower(); returns the current power level - long")
    public Object[] getPower(Context context, Arguments args) {
        return new Object[]{power};
    }

    @Optional.Method(modid = "opencomputers")
    @Callback(doc = "getMaxPower(); returns the maximum power level - long")
    public Object[] getMaxPower(Context context, Arguments args) {
        return new Object[]{getMaxPower()};
    }

    @Optional.Method(modid = "opencomputers")
    @Callback(doc = "getChargePercent(); returns the charge in percent - double")
    public Object[] getChargePercent(Context context, Arguments args) {
        return new Object[]{100D * getPower() / (double) getMaxPower()};
    }

    @Optional.Method(modid = "opencomputers")
    @Callback
    public Object[] storedCoolnt(Context context, Arguments args) {
        return new Object[]{tank.getFill()};
    }

    /**
     * @author ntmleafia
     * @reason me when the lasers
     */
    @Overwrite
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }
}
