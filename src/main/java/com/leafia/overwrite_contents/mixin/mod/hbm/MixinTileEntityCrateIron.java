// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.custom_hbm.util.LCETuple.Pair;
import com.hbm.tileentity.machine.TileEntityCrateIron;
import com.hbm.tileentity.machine.storage.TileEntityCrateBase;
import com.leafia.contents.nonmachines.storage.items.CrateLabelContainer;
import com.leafia.contents.nonmachines.storage.items.CrateLabelGUI;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityCrate;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TileEntityCrateIron.class)
public abstract class MixinTileEntityCrateIron extends TileEntityCrateBase implements IMixinTileEntityCrate {
	public MixinTileEntityCrateIron(int scount) {
		super(scount);
	}
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
	@Unique
	private String leafia$tryReadString(NBTTagCompound compound,String key) {
		if (compound.hasKey(key))
			return compound.getString(key);
		return "";
	}
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
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
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setString("leafia_label_upper",leafia$label_upper);
		compound.setString("leafia_label_middle",leafia$label_middle);
		compound.setString("leafia_label_lower",leafia$label_lower);
		for (int i = 0; i < 4; i++)
			compound.setString("leafia_label_"+i,leafia$labels_vert[i]);
		if (leafia$icon != null) {
			compound.setString("leafia_icon_item",leafia$icon.getRegistryName().toString());
			compound.setInteger("leafia_icon_meta",leafia$meta);
		}
		return super.writeToNBT(compound);
	}
}
