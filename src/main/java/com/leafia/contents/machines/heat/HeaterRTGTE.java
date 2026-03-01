package com.leafia.contents.machines.heat;

import com.hbm.api.tile.IHeatSource;
import com.hbm.tileentity.IGUIProvider;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.util.RTGUtil;
import com.leafia.contents.control.fuel.nuclearfuel.LeafiaRodItem;
import com.leafia.contents.machines.heat.container.HeaterRTGContainer;
import com.leafia.contents.machines.heat.container.HeaterRTGUI;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class HeaterRTGTE extends TileEntityMachineBase implements IHeatSource, ITickable, IGUIProvider {

    public int heatGen;
    public int heatEnergy;
    public static final int maxHeatEnergy = 750_000;

    public HeaterRTGTE() {
        super(16);
    }

    @Override
    public void update() {
        
        if(!world.isRemote) {
            
            this.heatEnergy *= 0.999;
            
            this.tryPullHeat();

            this.heatGen = RTGUtil.updateRTGs(inventory, new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14}) * 10;

            if (inventory.getStackInSlot(15).getItem() instanceof LeafiaRodItem) {
                ItemStack stack = inventory.getStackInSlot(15);
                LeafiaRodItem rod = (LeafiaRodItem)stack.getItem();
                NBTTagCompound nbt = stack.getTagCompound();
                double fuelHeat = 20;
                if (nbt != null) {
                    fuelHeat = nbt.getDouble("heat");
                    //if (fuelHeat >= 2000) {
                    if (nbt.getBoolean("nuke")) {
                        for (int i = 0; i < inventory.getSlots(); i++)
                            inventory.setStackInSlot(i, ItemStack.EMPTY);
                        world.setBlockToAir(pos);
                        rod.nuke(world,pos);
                        return;
                    }
                    if (nbt.getInteger("spillage") > 20*5) {
                        ItemStack prevStack = null;
                        for (int i = 0; i < inventory.getSlots(); i++) {
                            prevStack = LeafiaRodItem.comparePriority(inventory.getStackInSlot(i), prevStack);
                            inventory.setStackInSlot(i, ItemStack.EMPTY);
                        }
                        world.setBlockToAir(pos);
                        LeafiaRodItem detonate = (LeafiaRodItem)prevStack.getItem();
                        detonate.resetDetonate();
                        detonate.detonateRadius = 2;
                        detonate.detonate(world, pos);
                        return;
                    }
                    //}
                }
                heatGen += (int)Math.floor(Math.pow(fuelHeat/250,0.54)*15)*10 * 5;
                rod.HeatFunction(stack,true,rod.getFlux(stack)*2,0,0,0);
                rod.decay(stack,inventory,15);
            }
            this.heatEnergy += heatGen;
            if(heatEnergy > maxHeatEnergy) this.heatEnergy = maxHeatEnergy;

            networkPackNT(25);
        }
    }

    @Override
    public String getDefaultName() {
        return "container.heaterRadioThermal";
    }

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        buf.writeInt(this.heatGen);
        buf.writeInt(this.heatEnergy);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        this.heatGen = buf.readInt();
        this.heatEnergy = buf.readInt();
    }
    
    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.heatEnergy = nbt.getInteger("heatEnergy");
    }
    
    @Override
    public @NotNull NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("heatEnergy", heatEnergy);
        return nbt;
    }
    
    protected void tryPullHeat() {
        TileEntity con = world.getTileEntity(pos.add(0, -1, 0));
        
        if(con instanceof IHeatSource) {
            IHeatSource source = (IHeatSource) con;
            this.heatEnergy += source.getHeatStored() * 0.85;
            source.useUpHeat(source.getHeatStored());
        }
    }

    @Override
    public int getHeatStored() {
        return this.heatEnergy;
    }

    @Override
    public void useUpHeat(int heat) {
        this.heatEnergy = Math.max(0, this.heatEnergy - heat);
    }
    
    AxisAlignedBB bb = null;
    @Override
    @Nonnull
    public AxisAlignedBB getRenderBoundingBox() {

        if (bb == null) {
            bb = new AxisAlignedBB(pos.getX() - 1, pos.getY(), pos.getZ() - 1, pos.getX() + 2, pos.getY() + 2, pos.getZ() + 2);
        }

        return bb;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared() {
        return 65536.0D;
    }

    @Override
    public Container provideContainer(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return new HeaterRTGContainer(player.inventory, this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiScreen provideGUI(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return new HeaterRTGUI(player.inventory, this);
    }

    public boolean hasHeatGen(){
        return heatGen > 0;
    }

    public boolean hasHeat(){
        return heatEnergy > 0;
    }

    public int getHeatGenScaled(int i){
        if(heatGen == 0) return 0;
        return (int) (Math.log(heatGen) * i / Math.log(90000));
    }

    public int getHeatScaled(int i){
        return (heatEnergy * i) / maxHeatEnergy;
    }
}
