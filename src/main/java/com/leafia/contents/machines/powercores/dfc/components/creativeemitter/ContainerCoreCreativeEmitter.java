package com.leafia.contents.machines.powercores.dfc.components.creativeemitter;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerCoreCreativeEmitter extends Container {

    int power;
    int watts;
    int prev;
    boolean isOn;
    FluidTank tank;
    private final TileEntityCoreCreativeEmitter nukeBoy;
    private EntityPlayerMP player;
    public ContainerCoreCreativeEmitter(EntityPlayer player, TileEntityCoreCreativeEmitter tedf) {
        InventoryPlayer invPlayer = player.inventory;
        if (player instanceof EntityPlayerMP) this.player = (EntityPlayerMP) player;
        nukeBoy = tedf;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 88 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            this.addSlotToContainer(new Slot(invPlayer, i, 8 + i * 18, 146));
        }
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        listener.sendWindowProperty(this, 3, nukeBoy.isOn ? 1 : 0);
    }

    @Override
    public void detectAndSendChanges() {
        for (IContainerListener l : listeners) {
            if (nukeBoy.isOn != isOn) {
                l.sendWindowProperty(this, 3, nukeBoy.isOn ? 1 : 0);
                isOn = nukeBoy.isOn;
            }
        }
        super.detectAndSendChanges();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data) {
        if (id == 3) nukeBoy.isOn = data > 0;
        if (id == 1) {
            if (Minecraft.getMinecraft().currentScreen instanceof GUICoreCreativeEmitter) {
                //((GUICoreCreativeEmitter)Minecraft.getMinecraft().currentScreen).syncTextField(watts);
            }
        }
        super.updateProgressBar(id, data);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return nukeBoy.isUseableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack var3 = ItemStack.EMPTY;
        Slot var4 = this.inventorySlots.get(index);

        if (var4 != null && var4.getHasStack()) {
            return ItemStack.EMPTY;
        }

        return var3;
    }
}