// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.heat.rtheater;

import com.hbm.api.tile.IHeatSource;
import com.hbm.inventory.control_panel.ControlEventSystem;
import com.hbm.inventory.control_panel.IControllable;
import com.hbm.inventory.control_panel.types.DataValue;
import com.hbm.inventory.control_panel.types.DataValueFloat;
import com.hbm.tileentity.IGUIProvider;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.util.RTGUtil;
import com.leafia.contents.control.fuel.nuclearfuel.LeafiaRodItem;
import com.leafia.contents.machines.heat.rtheater.container.HeaterRTGContainer;
import com.leafia.contents.machines.heat.rtheater.container.HeaterRTGUI;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class HeaterRTGTE extends TileEntityMachineBase implements IHeatSource, ITickable, IGUIProvider, IControllable {
    private static final int[] RTG_SLOTS = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14};

    public static final int maxHeatEnergy = 750_000;
    public int heatGen;
    public int heatEnergy;
    AxisAlignedBB bb;

    public HeaterRTGTE() {
        super(16);
    }

    @Override
    public void update() {
        if (world.isRemote) return;
        heatEnergy = (int) (heatEnergy * 0.999);
        tryPullHeat();
        heatGen = 10 * RTGUtil.updateRTGs(inventory, RTG_SLOTS);
        var stack = inventory.getStackInSlot(15);
        if (stack.getItem() instanceof LeafiaRodItem rod && !tickRod(stack, rod)) return;
        heatEnergy = Math.min(maxHeatEnergy, heatEnergy + heatGen);
        networkPackNT(25);
    }

    private boolean tickRod(ItemStack stack, LeafiaRodItem rod) {
        var nbt = stack.getTagCompound();
        double fuelHeat = nbt == null ? 20 : nbt.getDouble("heat");
        if (nbt != null && nbt.getBoolean("nuke")) {
            clearInventory();
            world.setBlockToAir(pos);
            rod.nuke(world, pos);
            return false;
        }
        if (nbt != null && nbt.getInteger("spillage") > 100) {
            ItemStack selected = null;
            for (int slot = 0; slot < inventory.getSlots(); slot++) {
                selected = LeafiaRodItem.comparePriority(inventory.getStackInSlot(slot), selected);
                inventory.setStackInSlot(slot, ItemStack.EMPTY);
            }
            world.setBlockToAir(pos);
            var detonator = (LeafiaRodItem) selected.getItem();
            detonator.resetDetonate();
            detonator.detonateRadius = 2;
            detonator.detonate(world, pos);
            return false;
        }
        heatGen += (int) Math.floor(Math.pow(fuelHeat / 250, 0.54) * 15) * 10 * 15;
        rod.HeatFunction(stack, true, rod.getFlux(stack) * 2, 0, 0, 0);
        rod.decay(stack, inventory, 15);
        return true;
    }

    private void clearInventory() {
        for (int slot = 0; slot < inventory.getSlots(); slot++) inventory.setStackInSlot(slot, ItemStack.EMPTY);
    }

    protected void tryPullHeat() {
        if (world.getTileEntity(pos.down()) instanceof IHeatSource source) {
            int available = source.getHeatStored();
            heatEnergy = (int) (heatEnergy + available * 0.85);
            source.useUpHeat(available);
        }
    }

    @Override
    public int getHeatStored() {
        return heatEnergy;
    }

    @Override
    public void useUpHeat(int heat) {
        heatEnergy = Math.max(0, heatEnergy - heat);
    }

    @Override
    public String getDefaultName() {
        return "container.heaterRadioThermal";
    }

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        buf.writeInt(heatGen).writeInt(heatEnergy);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        heatGen = buf.readInt();
        heatEnergy = buf.readInt();
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        heatEnergy = tag.getInteger("heatEnergy");
    }

    @Override
    public @NotNull NBTTagCompound writeToNBT(NBTTagCompound tag) {
        var result = super.writeToNBT(tag);
        result.setInteger("heatEnergy", heatEnergy);
        return result;
    }

    @Override
    public @NotNull AxisAlignedBB getRenderBoundingBox() {
        if (bb == null) bb = new AxisAlignedBB(pos).grow(1, 0, 1).expand(0, 1, 0);
        return bb;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared() {
        return 65536;
    }

    @Override
    public Container provideContainer(int id, EntityPlayer player, World world, int x, int y, int z) {
        return new HeaterRTGContainer(player.inventory, this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiScreen provideGUI(int id, EntityPlayer player, World world, int x, int y, int z) {
        return new HeaterRTGUI(player.inventory, this);
    }

    public boolean hasHeatGen() {
        return heatGen > 0;
    }

    public boolean hasHeat() {
        return heatEnergy > 0;
    }

    public int getHeatGenScaled(int scale) {
        return heatGen == 0 ? 0 : (int) (Math.log(heatGen) * scale / Math.log(90000));
    }

    public int getHeatScaled(int scale) {
        return heatEnergy * scale / maxHeatEnergy;
    }

    @Override
    public Map<String, DataValue> getQueryData() {
        var values = new HashMap<String, DataValue>(4);
        values.put("heat", new DataValueFloat(heatEnergy));
        values.put("maxHeat", new DataValueFloat(maxHeatEnergy));
        return values;
    }

    @Override
    public BlockPos getControlPos() {
        return pos;
    }

    @Override
    public World getControlWorld() {
        return world;
    }

    @Override
    public void validate() {
        super.validate();
        ControlEventSystem.get(world).addControllable(this);
    }

    @Override
    public void invalidate() {
        ControlEventSystem.get(world).removeControllable(this);
        super.invalidate();
    }
}
