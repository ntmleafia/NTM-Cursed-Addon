// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.powercores.ams;

import com.hbm.api.energymk2.IEnergyReceiverMK2;
import com.hbm.api.fluidmk2.IFluidStandardReceiverMK2;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.hbm.lib.DirPos;
import com.hbm.lib.Library;
import com.hbm.tileentity.IGUIProvider;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.container_utility.LeafiaPacketReceiver;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

/** Shared state and thermal cycle for the two AMS drives. */
public abstract class AMSDriveTE extends TileEntity implements ITickable, IFluidStandardReceiverMK2, IEnergyReceiverMK2, IGUIProvider, LeafiaPacketReceiver {
    public static final int maxEfficiency = 100;
    public static final int maxHeat = 2500;
    private static final double ROOT_TWO = Math.sqrt(2);
    private static final double ROOT_TWO_PI = Math.sqrt(Math.PI * 2);

    public ItemStackHandler inventory;
    public FluidTankNTM tank;
    public long power;
    public int efficiency;
    public int heat;
    public int age;
    public int warning;
    public boolean locked;
    public boolean needsUpdate;
    protected final Random rand = new Random();
    private String customName;

    protected AMSDriveTE(int tankCapacity) {
        inventory = new ItemStackHandler(4) {
            @Override
            protected void onContentsChanged(int slot) {
                markDirty();
                super.onContentsChanged(slot);
            }
        };
        tank = new FluidTankNTM(Fluids.COOLANT, tankCapacity);
    }

    protected abstract String defaultName();

    public abstract DirPos[] getConPos();

    protected abstract void destroyDrive();

    public String getInventoryName() {
        return hasCustomInventoryName() ? customName : defaultName();
    }

    public boolean hasCustomInventoryName() {
        return customName != null && !customName.isEmpty();
    }

    public void setCustomName(String name) {
        customName = name;
    }

    public boolean isUseableByPlayer(EntityPlayer player) {
        return world.getTileEntity(pos) == this
                && player.getDistanceSq(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 128;
    }

    protected final void subscribeConnections() {
        for (var connection : getConPos()) {
            trySubscribe(world, connection);
            trySubscribe(tank.getTankType(), world, connection);
        }
    }

    protected final void tickDrive(int coolantMultiplier) {
        if (power <= 0) {
            efficiency = 0;
            warning = 1;
        } else {
            efficiency = efficiencyFor(power, heat, getMaxPower());
            power = (long) (power - Math.ceil(power * 0.025));
            warning = 0;
        }

        var fluid = tank.getTankType();
        int target, cooling, required;
        if (fluid == Fluids.CRYOGEL) {
            target = maxHeat / 2;
            cooling = 10;
            required = 5 * coolantMultiplier;
        } else if (fluid == Fluids.COOLANT) {
            target = maxHeat / 4;
            cooling = 5;
            required = 5 * coolantMultiplier;
        } else if (fluid == Fluids.WATER) {
            target = (int) (maxHeat * 0.85);
            cooling = 2;
            required = 15 * coolantMultiplier;
        } else {
            heat += efficiency;
            warning = 2;
            return;
        }

        if (tank.getFluidAmount() < required) {
            heat += efficiency;
            return;
        }
        if (heat > 0) {
            tank.drain(required, true);
            needsUpdate = true;
        }
        heat = coolHeat(heat, efficiency, target, cooling);
    }

    static int coolHeat(int heat, int efficiency, int target, int cooling) {
        if (heat <= target) {
            if (efficiency > 0) heat += efficiency;
            else if (heat > 0) heat -= Math.min(heat, cooling);
        }
        if (heat > target) heat -= Math.min(heat - target, cooling);
        return heat;
    }

    static int efficiencyFor(long power, int heat, long capacity) {
        float reciprocal = 1 / (float) power;
        float displacement = (float) (heat - maxHeat / 2) / maxHeat;
        float gaussian = (float) ((1 / Math.sqrt(reciprocal * Math.PI))
                * Math.pow(Math.E, -Math.pow(displacement, 2) / 0.10));
        float effect = (float) (gaussian * ROOT_TWO_PI / (ROOT_TWO * Math.sqrt(capacity)));
        return Math.round(effect * 100);
    }

    protected final void finishDriveTick() {
        if (tank.getFluidAmount() <= 5 || heat > maxHeat * 0.9) warning = 2;
        if (heat > maxHeat) {
            heat = maxHeat;
            locked = true;
            destroyDrive();
        }
        power = Library.chargeTEFromItems(inventory, 3, power, getMaxPower());
    }

    protected final void sendDriveState() {
        LeafiaPacket._start(this)
                .__write(0, power)
                .__write(1, locked)
                .__write(2, efficiency)
                .__write(3, heat)
                .__write(4, tank.getTankType().getID())
                .__write(5, tank.getFill())
                .__sendToClients(250);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        power = tag.getLong("power");
        if (tag.hasKey("coolant")) tank.readFromNBT(tag, "coolant");
        efficiency = tag.getInteger("efficiency");
        heat = tag.getInteger("heat");
        locked = tag.getBoolean("locked");
        if (tag.hasKey("inventory")) inventory.deserializeNBT(tag.getCompoundTag("inventory"));
        super.readFromNBT(tag);
    }

    @Override
    public @NotNull NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setLong("power", power);
        tank.writeToNBT(tag, "coolant");
        tag.setInteger("efficiency", efficiency);
        tag.setInteger("heat", heat);
        tag.setBoolean("locked", locked);
        tag.setTag("inventory", inventory.serializeNBT());
        return super.writeToNBT(tag);
    }

    public long getPowerScaled(long scale) {
        return power * scale / getMaxPower();
    }

    public int getEfficiencyScaled(int scale) {
        return efficiency * scale / maxEfficiency;
    }

    public int getHeatScaled(int scale) {
        return heat * scale / maxHeat;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared() {
        return 65536;
    }

    @Override
    public long getPower() {
        return power;
    }

    @Override
    public void setPower(long value) {
        power = value;
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
        return getReceivingTanks();
    }

    @Override
    public void onReceivePacketLocal(byte key, Object value) {
        switch (key) {
            case 0 -> power = (long) value;
            case 1 -> locked = (boolean) value;
            case 2 -> efficiency = (int) value;
            case 3 -> heat = (int) value;
            case 4 -> {
                var type = Fluids.fromID((int) value);
                if (!tank.getTankType().equals(type)) {
                    int fill = tank.getFill();
                    tank.setTankType(type);
                    tank.setFill(fill);
                }
            }
            case 5 -> tank.setFill((int) value);
        }
    }

    @Override
    public void onReceivePacketServer(byte key, Object value, EntityPlayer player) { }

    @Override
    public void onPlayerValidate(EntityPlayer player) { }
}
