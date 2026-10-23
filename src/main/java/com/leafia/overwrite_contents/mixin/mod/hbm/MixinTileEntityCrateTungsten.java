// SPDX-FileCopyrightText: 2025-2026 Leafia
// SPDX-FileCopyrightText: 2025-2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.custom_hbm.util.LCETuple.Pair;
import com.hbm.interfaces.ILaserable;
import com.hbm.inventory.recipes.DFCRecipes;
import com.hbm.items.ModItems;
import com.hbm.items.weapon.ItemCrucible;
import com.hbm.lib.ForgeDirection;
import com.hbm.tileentity.IBufPacketReceiver;
import com.hbm.tileentity.IGUIProvider;
import com.hbm.tileentity.machine.TileEntityCrate;
import com.hbm.tileentity.machine.TileEntityCrateTungsten;
import com.leafia.contents.network.spk_cable.uninos.ISPKReceiver;
import com.leafia.contents.nonmachines.storage.items.CrateLabelContainer;
import com.leafia.contents.nonmachines.storage.items.CrateLabelGUI;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityCrate;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TileEntityCrateTungsten.class)
public abstract class MixinTileEntityCrateTungsten extends TileEntityCrate implements IMixinTileEntityCrate, IBufPacketReceiver, ITickable, ILaserable, IGUIProvider, ISPKReceiver {

    public MixinTileEntityCrateTungsten(int scount,String name,int crateColumns,int crateRows,int crateX,int crateY,int playerInventoryX,int playerInventoryY,int hotbarY,int guiWidth,int guiHeight,int inventoryLabelX,int titleColor,int inventoryLabelColor,ResourceLocation texture) {
        super(scount,name,crateColumns,crateRows,crateX,crateY,playerInventoryX,playerInventoryY,hotbarY,guiWidth,guiHeight,inventoryLabelX,titleColor,inventoryLabelColor,texture);
    }

    @Shadow(remap = false)
    public long joules;

    //@Shadow(remap = false)
    //public abstract void addEnergy(World world, BlockPos pos, long energy, EnumFacing dir);

    @Unique
    public int leafia$heatTimer;
    /**
     * @author ntmleafia
     * @reason i preferred Extended algorithm
     */
    @Overwrite(remap = false)
    public void addEnergy(World sourceWorld, BlockPos pos, long energy, EnumFacing direction) {
        leafia$heatTimer = 5;
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            var input = inventory.getStackInSlot(slot);
            if (input.isEmpty()) continue;
            var output = FurnaceRecipes.instance().getSmeltingResult(input);
            long flux = (long) (DFCRecipes.getRequiredFlux(input) * 0.9D);
            if (flux >= 0 && energy > flux &&
                    0.001D > input.getCount() * world.rand.nextDouble() * ((double) flux / (double) energy)) {
                var fusion = DFCRecipes.getOutput(input);
                if (fusion != null && fusion != ItemStack.EMPTY) output = fusion;
            }
            if (input.getItem() == ModItems.crucible && ItemCrucible.getCharges(input) < 3 && energy > 10000000) {
                ItemCrucible.charge(input);
            }
            if (output == null || output.isEmpty()) continue;
            int count = input.getCount();
            if (output.getCount() * count > output.getMaxStackSize()) continue;
            inventory.setStackInSlot(slot, output.copy());
            var replacement = inventory.getStackInSlot(slot);
            replacement.setCount(replacement.getCount() * count);
        }
        joules = energy;
    }

    @Inject(method = "update()V", at = @At(value = "FIELD", target = "Lcom/hbm/tileentity/machine/TileEntityCrateTungsten;heatTimer:I", ordinal = 0, shift = At.Shift.BEFORE, remap = false),require = 1)
    public void leafia$onUpdate(CallbackInfo ci) {
        for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            trySubscribeSPK(world, pos.getX() + dir.offsetX, pos.getY() + dir.offsetY, pos.getZ() + dir.offsetZ, dir);
        }
    }

    /*
    // Smelting fix..?
    @Redirect(method = "addEnergy",at = @At(value = "INVOKE", target = "Lcom/hbm/inventory/recipes/DFCRecipes;getOutput(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;"),require = 1,remap = false)
    public ItemStack onGetOutput(ItemStack key,@Local(name = "result") ItemStack result) {
        ItemStack output = DFCRecipes.getOutput(key);
        if (output == null)
            return result;
        return output;
    }*/

    @Override
    public long transferSPK(long power, boolean simulate) {
        if (!simulate) {
            addEnergy(null, null, power, null);
            joules = power;
        }
        return 0L;
    }

    @Override
    public long getSPK() {
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


    // LABELABLE CRATE ZONE
    @Unique ItemStackHandler leafia$iconHandler = new ItemStackHandler(1) {
        @Override
        public @NotNull ItemStack insertItem(int slot,@NotNull ItemStack stack,boolean simulate) {
            if (!stack.isEmpty()) {
                leafia$icon = stack.getItem();
                leafia$meta = stack.getMetadata();
                setStackInSlot(0,new ItemStack(leafia$icon,1,leafia$meta));
            }
            return stack;
        }
        @Override
        public @NotNull ItemStack extractItem(int slot,int amount,boolean simulate) {
            leafia$icon = null;
            leafia$meta = -1;
            setStackInSlot(0,ItemStack.EMPTY);
            return ItemStack.EMPTY;
        }
    };
    @Unique Item leafia$icon = null;
    @Unique int leafia$meta = -1;
    @Override
    public ItemStackHandler leafia$getIconHandler() {
        return leafia$iconHandler;
    }
    @Unique String[] leafia$labels_vert = new String[]{"","","",""};
    @Unique String leafia$label_upper = "";
    @Unique String leafia$label_middle = "";
    @Unique String leafia$label_lower = "";
    @Override public String[] leafia$verticalLabels() { return leafia$labels_vert; }
    @Override public String leafia$upperLabel() { return leafia$label_upper; }
    @Override public String leafia$middleLabel() { return leafia$label_middle; }
    @Override public String leafia$lowerLabel() { return leafia$label_lower; }
    @Override public void leafia$setUpperLabel(String label) { leafia$label_upper = label; }
    @Override public void leafia$setMiddleLabel(String label) { leafia$label_middle = label; }
    @Override public void leafia$setLowerLabel(String label) { leafia$label_lower = label; }
    @Override public Pair<Item,Integer> leafia$icon() { return new Pair<>(leafia$icon,leafia$meta); }
    @Unique
    void leafia$setLabelFromData(byte key,Object value) {
        if (key < 4)
            leafia$labels_vert[key] = (String)value;
        else if (key == 5)
            leafia$label_upper = (String)value;
        else if (key == 6)
            leafia$label_middle = (String)value;
        else if (key == 7)
            leafia$label_lower = (String)value;
        else if (key == 8) {
            if (value == null)
                leafia$icon = null;
            else
                leafia$icon = Item.getByNameOrId((String)value);
        } else if (key == 9) {
            leafia$meta = (int)value;
            if (leafia$icon != null)
                leafia$iconHandler.setStackInSlot(0,new ItemStack(leafia$icon,1,leafia$meta));
            else
                leafia$iconHandler.setStackInSlot(0,ItemStack.EMPTY);
        }
    }
    @Override
    public LeafiaPacket generateSyncPacket() {
        LeafiaPacket packet = LeafiaPacket._start(this);
        for (int i = 0; i < 4; i++)
            packet.__write(i,leafia$labels_vert[i]);
        packet.__write(5,leafia$label_upper);
        packet.__write(6,leafia$label_middle);
        packet.__write(7,leafia$label_lower);
        packet.__write(8,leafia$icon != null ? leafia$icon.getRegistryName().toString() : null);
        packet.__write(9,leafia$meta);
        return packet;
    }
    @Override
    public void onReceivePacketServer(byte key,Object value,EntityPlayer plr) {
        leafia$setLabelFromData(key,value);
    }
    @Override
    public void onReceivePacketLocal(byte key,Object value) {
        leafia$setLabelFromData(key,value);
    }
    @Override
    public void onPlayerValidate(EntityPlayer plr) {
        generateSyncPacket().__sendToClient(plr);
    }
    @SideOnly(Side.CLIENT)
    @Inject(method = "provideGUI",at = @At(value = "HEAD"),require = 1,remap = false,cancellable = true)
    public void leafia$onProvideGUI(int ID,EntityPlayer player,World world,int x,int y,int z,CallbackInfoReturnable<GuiScreen> cir) {
        if (ID >= 1121 && ID < 1121+4) {
            cir.setReturnValue(new CrateLabelGUI(player.inventory,this,world.getBlockState(pos),EnumFacing.byHorizontalIndex(ID-1121)));
            cir.cancel();
        }
    }
    @Unique
    private String leafia$tryReadString(NBTTagCompound compound,String key) {
        if (compound.hasKey(key))
            return compound.getString(key);
        return "";
    }
    @Inject(method = "readFromNBT",at = @At(value = "HEAD"),require = 1)
    public void leafia$onReadFromNBT(NBTTagCompound compound,CallbackInfo ci) {
        leafia$label_upper = leafia$tryReadString(compound,"leafia_label_upper");
        leafia$label_middle = leafia$tryReadString(compound,"leafia_label_middle");
        leafia$label_lower = leafia$tryReadString(compound,"leafia_label_lower");
        for (int i = 0; i < 4; i++)
            leafia$labels_vert[i] = leafia$tryReadString(compound,"leafia_label_"+i);
        if (compound.hasKey("leafia_icon_item")) {
            leafia$icon = Item.getByNameOrId(compound.getString("leafia_icon_item"));
            leafia$meta = compound.getInteger("leafia_icon_meta");
        }
    }
    @Inject(method = "writeToNBT",at = @At(value = "HEAD"),require = 1)
    public void leafia$onWriteToNBT(NBTTagCompound compound,CallbackInfoReturnable<NBTTagCompound> cir) {
        compound.setString("leafia_label_upper",leafia$label_upper);
        compound.setString("leafia_label_middle",leafia$label_middle);
        compound.setString("leafia_label_lower",leafia$label_lower);
        for (int i = 0; i < 4; i++)
            compound.setString("leafia_label_"+i,leafia$labels_vert[i]);
        if (leafia$icon != null) {
            compound.setString("leafia_icon_item",leafia$icon.getRegistryName().toString());
            compound.setInteger("leafia_icon_meta",leafia$meta);
        }
    }
}


