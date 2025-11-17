package com.leafia.contents.machines.powercores.dfc.components.creativeemitter;

import com.hbm.lib.MethodHandleHelper;
import com.hbm.tileentity.machine.TileEntityCore;
import com.hbm.tileentity.machine.TileEntityCoreEmitter;
import com.leafia.AddonBase;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityCoreEmitter;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;

public class TileEntityCoreCreativeEmitter extends TileEntityCoreEmitter implements IMixinTileEntityCoreEmitter {

    private static final MethodHandle IS_ACTIVE_GETTER = MethodHandleHelper.findGetter(TileEntityCoreEmitter.class, "isActive", boolean.class);
    private static final MethodHandle IS_ACTIVE_SETTER = MethodHandleHelper.findSetter(TileEntityCoreEmitter.class, "isActive", boolean.class);
    private static final MethodHandle TARGET_POS_GETTER = MethodHandleHelper.findGetter(TileEntityCoreEmitter.class, "targetPosition", BlockPos.class);
    private static final MethodHandle TARGET_POS_SETTER = MethodHandleHelper.findSetter(TileEntityCoreEmitter.class, "targetPosition", BlockPos.class);
    private static final MethodHandle LAST_RAYCAST_GETTER = MethodHandleHelper.findGetter(TileEntityCoreEmitter.class, "lastRaycast", RayTraceResult.class);
    private static final MethodHandle LAST_RAYCAST_SETTER = MethodHandleHelper.findSetter(TileEntityCoreEmitter.class, "lastRaycast", RayTraceResult.class);
    private static final MethodHandle LAST_CORE_GETTER = MethodHandleHelper.findGetter(TileEntityCoreEmitter.class, "lastGetCore", TileEntityCore.class);
    private static final MethodHandle LAST_CORE_SETTER = MethodHandleHelper.findSetter(TileEntityCoreEmitter.class, "lastGetCore", TileEntityCore.class);
    private static final MethodHandle JOULES_GETTER = MethodHandleHelper.findGetter(TileEntityCoreEmitter.class, "joules", long.class);
    private static final MethodHandle JOULES_SETTER = MethodHandleHelper.findSetter(TileEntityCoreEmitter.class, "joules", long.class);
    private static final MethodHandle SUPER_RAYCAST = MethodHandleHelper.findSpecial(TileEntityCoreEmitter.class, TileEntityCoreEmitter.class, "raycast", MethodType.methodType(RayTraceResult.class, long.class));

    private final MethodHandle isActiveGetter;
    private final MethodHandle isActiveSetter;
    private final MethodHandle targetPosGetter;
    private final MethodHandle targetPosSetter;
    private final MethodHandle lastRaycastGetter;
    private final MethodHandle lastRaycastSetter;
    private final MethodHandle lastCoreGetter;
    private final MethodHandle lastCoreSetter;
    private final MethodHandle joulesGetter;
    private final MethodHandle joulesSetter;
    private final MethodHandle superRaycast;

    public long[] joulesT = new long[]{100_000L, 20_000_000L, 100_000_000L, 1_000_000_000L};

    int selecting = 0;
    boolean changed = false;

    public TileEntityCoreCreativeEmitter() {
        super();
        this.isActiveGetter = IS_ACTIVE_GETTER.bindTo(this);
        this.isActiveSetter = IS_ACTIVE_SETTER.bindTo(this);
        this.targetPosGetter = TARGET_POS_GETTER.bindTo(this);
        this.targetPosSetter = TARGET_POS_SETTER.bindTo(this);
        this.lastRaycastGetter = LAST_RAYCAST_GETTER.bindTo(this);
        this.lastRaycastSetter = LAST_RAYCAST_SETTER.bindTo(this);
        this.lastCoreGetter = LAST_CORE_GETTER.bindTo(this);
        this.lastCoreSetter = LAST_CORE_SETTER.bindTo(this);
        this.joulesGetter = JOULES_GETTER.bindTo(this);
        this.joulesSetter = JOULES_SETTER.bindTo(this);
        this.superRaycast = SUPER_RAYCAST.bindTo(this);
    }

    @Override
    public String getPacketIdentifier() {
        return "DFC_CBOOSTER";
    }

    @Override
    public void update() {
        if (!world.isRemote) {

            if (isOn) {
                // 50,000,000 HE = 10,000 SPK
                // 1 SPK = 5,000 HE
                long out = joulesT[selecting];
                if (out > 0) {
                    mhSuperRaycast(out);
                }
                watts = (int) (out / 1000L);
            } else {
                watts = 0;
            }

            this.markDirty();

            LeafiaPacket packet = LeafiaPacket._start(this).__write(0, isOn).__write(1, watts).__write(2, prev);
            packet.__sendToAffectedClients();
            sendChanges(null);

            LeafiaPacket._start(this).__write(31, mhGetTargetPos()).__sendToAffectedClients();
        } else if (isOn) {
            RayTraceResult result = mhSuperRaycast(0L);
            mhSetLastRaycast(result);
        }
    }

    public void sendChanges(@Nullable EntityPlayer plr) {
        LeafiaPacket packet = LeafiaPacket._start(this).__write(3, selecting).__write(4, joulesT[0]).__write(5, joulesT[1]).__write(6, joulesT[2])
                                          .__write(7, joulesT[3]);
        if (plr == null) {
            packet.__sendToAffectedClients();
        } else {
            packet.__sendToClient(plr);
        }
    }

    @Override
    public LeafiaPacket syncClients(LeafiaPacket packet) {
        packet.__write(3, selecting).__write(4, joulesT[0]).__write(5, joulesT[1]).__write(6, joulesT[2]).__write(7, joulesT[3]);
        return IMixinTileEntityCoreEmitter.super.syncClients(packet);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setBoolean("isOn", isOn);
        compound.setLong("0", joulesT[0]);
        compound.setLong("1", joulesT[1]);
        compound.setLong("2", joulesT[2]);
        compound.setLong("3", joulesT[3]);
        compound.setInteger("selecting", selecting);
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        isOn = compound.getBoolean("isOn");
        joulesT[0] = compound.getLong("0");
        joulesT[1] = compound.getLong("1");
        joulesT[2] = compound.getLong("2");
        joulesT[3] = compound.getLong("3");
        selecting = compound.getInteger("selecting");
        super.readFromNBT(compound);
    }

    @Override
    public Container provideContainer(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return new ContainerCoreCreativeEmitter(player, this);
    }

    @SideOnly(Side.CLIENT)
    public GuiScreen provideGUI(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return new GUICoreCreativeEmitter(player, this);
    }

    @Override
    public void onReceivePacketLocal(byte key, Object value) {
        if (key == 0) {
            mhSetIsActive((boolean) value);
        }
        if (key == 3) {
            selecting = (int) value;
        } else if (key >= 4 && key <= 7) {
            joulesT[key - 4] = (long) value;
            changed = true;
        }
        IMixinTileEntityCoreEmitter.super.onReceivePacketLocal(key, value);
    }

    @Override
    public void onReceivePacketServer(byte key, Object value, EntityPlayer plr) {
        if (key == 0) {
            selecting = (int) value;
        } else if (key < 5) {
            joulesT[key - 1] = (long) value;
        }
        sendChanges(null);
        IMixinTileEntityCoreEmitter.super.onReceivePacketServer(key, value, plr);
    }

    @Override
    public void onPlayerValidate(EntityPlayer plr) {
        IMixinTileEntityCoreEmitter.super.onPlayerValidate(plr);
        sendChanges(plr);
    }

    @Override
    public TileEntityCore lastGetCore() {
        return mhGetLastCore();
    }

    @Override
    public void lastGetCore(TileEntityCore core) {
        mhSetLastCore(core);
    }

    @Override
    public BlockPos getTargetPosition() {
        return mhGetTargetPos();
    }

    @Override
    public void targetPosition(BlockPos pos) {
        mhSetTargetPos(pos);
    }

    @Override
    public RayTraceResult raycast(long out) {
        return mhSuperRaycast(out);
    }

    @Override
    public boolean isActive() {
        return mhGetIsActive();
    }

    @Override
    public void isActive(boolean active) {
        mhSetIsActive(active);
    }

    @Override
    public RayTraceResult lastRaycast() {
        return mhGetLastRaycast();
    }

    @Override
    public long getSPK() {
        return mhGetJoules();
    }

    @Override
    public void setSPK(long power) {
        mhSetJoules(power);
    }

    @Override
    public long getMaxSPK() {
        return Long.MAX_VALUE;
    }

    @Override
    public Object getModInstanceForGui() {
        return AddonBase.instance;
    }

    private boolean mhGetIsActive() {
        try {
            return (boolean) isActiveGetter.invokeExact();
        } catch (Throwable t) {
            throw new RuntimeException("Failed to get isActive", t);
        }
    }

    private void mhSetIsActive(boolean value) {
        try {
            isActiveSetter.invokeExact(value);
        } catch (Throwable t) {
            throw new RuntimeException("Failed to set isActive", t);
        }
    }

    private BlockPos mhGetTargetPos() {
        try {
            return (BlockPos) targetPosGetter.invokeExact();
        } catch (Throwable t) {
            throw new RuntimeException("Failed to get targetPosition", t);
        }
    }

    private void mhSetTargetPos(BlockPos pos) {
        try {
            targetPosSetter.invokeExact(pos);
        } catch (Throwable t) {
            throw new RuntimeException("Failed to set targetPosition", t);
        }
    }

    private RayTraceResult mhGetLastRaycast() {
        try {
            return (RayTraceResult) lastRaycastGetter.invokeExact();
        } catch (Throwable t) {
            throw new RuntimeException("Failed to get lastRaycast", t);
        }
    }

    private void mhSetLastRaycast(RayTraceResult result) {
        try {
            lastRaycastSetter.invokeExact(result);
        } catch (Throwable t) {
            throw new RuntimeException("Failed to set lastRaycast", t);
        }
    }

    private TileEntityCore mhGetLastCore() {
        try {
            return (TileEntityCore) lastCoreGetter.invokeExact();
        } catch (Throwable t) {
            throw new RuntimeException("Failed to get lastGetCore", t);
        }
    }

    private void mhSetLastCore(TileEntityCore core) {
        try {
            lastCoreSetter.invokeExact(core);
        } catch (Throwable t) {
            throw new RuntimeException("Failed to set lastGetCore", t);
        }
    }

    private long mhGetJoules() {
        try {
            return (long) joulesGetter.invokeExact();
        } catch (Throwable t) {
            throw new RuntimeException("Failed to get joules", t);
        }
    }

    private void mhSetJoules(long value) {
        try {
            joulesSetter.invokeExact(value);
        } catch (Throwable t) {
            throw new RuntimeException("Failed to set joules", t);
        }
    }

    private RayTraceResult mhSuperRaycast(long out) {
        try {
            return (RayTraceResult) superRaycast.invokeExact(out);
        } catch (Throwable t) {
            throw new RuntimeException("Failed to call TileEntityCoreEmitter.raycast(long)", t);
        }
    }
}
