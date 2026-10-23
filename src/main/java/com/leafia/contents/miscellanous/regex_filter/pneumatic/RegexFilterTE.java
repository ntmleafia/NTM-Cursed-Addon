// SPDX-FileCopyrightText: 2024 Alcater <alcater.fallout4@gmail.com>
// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-FileCopyrightText: 2025 TheSlize <r.region-4@yandex.ru>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.miscellanous.regex_filter.pneumatic;

import com.hbm.tileentity.IGUIProvider;
import com.leafia.contents.miscellanous.regex_filter.pneumatic.container.IRegexFilterGUI;
import com.leafia.contents.miscellanous.regex_filter.pneumatic.container.RegexFilterContainer;
import com.leafia.contents.miscellanous.regex_filter.pneumatic.container.RegexFilterGUI;
import com.leafia.dev.LeafiaUtil;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.container_utility.LeafiaPacketReceiver;
import com.llib.group.LeafiaSet;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RegexFilterTE extends TileEntity implements LeafiaPacketReceiver, ITickable, IGUIProvider {
	public static class RegexFilter {
		public String regex = "";
		public FilterType type = FilterType.RESOURCE_ID;
		public boolean blacklist = false;
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof RegexFilter filter)
				return filter.regex.equals(regex) && filter.type == type;
			return super.equals(obj);
		}
	}
	public enum FilterType { RESOURCE_ID, ORE_DICT, CREATIVE_TAB }
	public final LeafiaSet<RegexFilter> filters = new LeafiaSet<>();

    public boolean isItemValid(ItemStack stack) {
		boolean valid = false;
		for (RegexFilter filter : filters) {
			switch(filter.type) {
				case ORE_DICT -> {
					for (int oreID : OreDictionary.getOreIDs(stack)) {
						String dict = OreDictionary.getOreName(oreID);
						if (LeafiaUtil.matchesRegex(dict,filter.regex)) {
							if (filter.blacklist)
								return false;
							valid = true;
						}
					}
				}
				case RESOURCE_ID -> {
					if (stack.getItem().getRegistryName() != null && LeafiaUtil.matchesRegex(stack.getItem().getRegistryName().toString(),filter.regex)) {
						if (filter.blacklist)
							return false;
						valid = true;
					}
				}
				case CREATIVE_TAB -> {
					CreativeTabs tab = stack.getItem().getCreativeTab();
					if (tab != null) {
						if (LeafiaUtil.matchesRegex(tab.tabLabel,filter.regex)) {
							if (filter.blacklist)
								return false;
							valid = true;
						}
					}
				}
			}
		}
		return valid;
	}
	/// stolen code goes brr
	private int findMaxInsertable(IItemHandler target, int slot, ItemStack stack, int upperBound) {
		int lo = 0;
		int hi = upperBound;

		while (lo < hi) {
			int mid = (lo + hi + 1) >>> 1;

			ItemStack test = stack.copy();
			test.setCount(mid);
			ItemStack res = target.insertItem(slot, test, true);

			if (res.isEmpty()) {
				lo = mid;
			} else {
				hi = mid - 1;
			}
		}

		return lo;
	}
	/// stolen code goes brr
	//Unloads output into chests. Capability version.
	public boolean tryInsertItemCap(IItemHandler target,ItemStack stack) {
		if(stack.isEmpty())
			return false;

		boolean movedAny = false;

		for(int i = 0; i < target.getSlots() && !stack.isEmpty(); i++) {
			ItemStack probe = stack.copy();
			probe.setCount(1);
			ItemStack simOne = target.insertItem(i, probe, true);
			if(!simOne.isEmpty()) {
				continue;
			}

			int maxTry = Math.min(stack.getCount(), target.getSlotLimit(i));
			int accepted = findMaxInsertable(target, i, stack, maxTry);

			if(accepted > 0) {
				ItemStack toInsert = stack.copy();
				toInsert.setCount(accepted);
				ItemStack rest = target.insertItem(i, toInsert, false);

				int actuallyInserted = accepted - (!rest.isEmpty() ? rest.getCount() : 0);
				if(actuallyInserted > 0) {
					stack.shrink(actuallyInserted);
					movedAny = true;
				}
			}
		}

		return movedAny;
	}
	@Override
	public void update() {
		if (!world.isRemote) {
			if (!inventory.getStackInSlot(0).isEmpty() && world.getBlockState(pos).getBlock() instanceof RegexFilterBlock) {
				EnumFacing facing = world.getBlockState(pos).getValue(BlockDirectional.FACING);
				TileEntity te = world.getTileEntity(pos.offset(facing));
				if(te != null){
					if(te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,facing.getOpposite())) {
						IItemHandler cap = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,facing.getOpposite());
						if (tryInsertItemCap(cap,inventory.getStackInSlot(0)))
							markDirty();
					}
				}
			}
		}
	}
	public final ItemStackHandler inventory = new ItemStackHandler(1) {
		@Override
		protected void onContentsChanged(int slot) {
			super.onContentsChanged(slot);
			RegexFilterTE.this.markDirty();
		}
		@Override
		public boolean isItemValid(int slot,@NotNull ItemStack stack) {
			return RegexFilterTE.this.isItemValid(stack);
		}
		@Override
		public @NotNull ItemStack insertItem(int slot,@NotNull ItemStack stack,boolean simulate) {
			if (!RegexFilterTE.this.isItemValid(stack))
				return stack;
			return super.insertItem(slot,stack,simulate);
		}
	};
	public IRegexFilterGUI gui;
	@SideOnly(Side.CLIENT)
	void updateGUIs() {
		if (gui != null)
			gui.onSyncFilters();
	}
	void loadFilterFromNBT(NBTTagList list) {
		filters.clear();
		for (NBTBase nbt : list) {
			if (nbt instanceof NBTTagCompound tag) {
				String type = tag.getString("type");
				try {
					FilterType filter = FilterType.valueOf(type);
					RegexFilter instance = new RegexFilter();
					instance.type = filter;
					instance.regex = tag.getString("value");
					instance.blacklist = tag.getBoolean("blacklist");
					filters.add(instance);
				} catch (IllegalArgumentException ignored) {}
			}
		}
	}
	NBTTagList saveFilterToNBT() {
		NBTTagList list = new NBTTagList();
		for (RegexFilter filter : filters) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("type",filter.type.name());
			tag.setString("value",filter.regex);
			tag.setBoolean("blacklist",filter.blacklist);
			list.appendTag(tag);
		}
		return list;
	}
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		inventory.deserializeNBT(compound.getCompoundTag("inventory"));
		if (compound.hasKey("filter")) {
			NBTTagList list = compound.getTagList("filter",10);
			loadFilterFromNBT(list);
		}
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setTag("inventory",inventory.serializeNBT());
		compound.setTag("filter",saveFilterToNBT());
		return super.writeToNBT(compound);
	}
	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(super.getUpdateTag());
	}
	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		readFromNBT(tag);
		if (world != null && world.isRemote)
			updateGUIs();
	}
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(pos,0,getUpdateTag());
	}
	@Override
	public void onDataPacket(NetworkManager net,SPacketUpdateTileEntity pkt) {
		handleUpdateTag(pkt.getNbtCompound());
	}
	@Override
	protected void setWorldCreate(World worldIn) {
		this.setWorld(worldIn);
	}
	EnumFacing getFacing() {
		IBlockState state = world.getBlockState(pos);
		if (state.getBlock() instanceof RegexFilterBlock)
			return state.getValue(RegexFilterBlock.FACING);
		return EnumFacing.UP;
	}
	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		if ((facing == null || facing.getOpposite().equals(getFacing())) && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return true;
		return super.hasCapability(capability,facing);
	}
	@Override
	public @Nullable <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		if ((facing == null || facing.getOpposite().equals(getFacing())) && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inventory);
		return super.getCapability(capability,facing);
	}
	@Override
	public Container provideContainer(int i,EntityPlayer entityPlayer,World world,int i1,int i2,int i3) {
		return new RegexFilterContainer(entityPlayer.inventory,this);
	}
	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen provideGUI(int i,EntityPlayer entityPlayer,World world,int i1,int i2,int i3) {
		return new RegexFilterGUI(entityPlayer.inventory,this);
	}
	@Override
	public String getPacketIdentifier() {
		return "PNEUMO_REGEX";
	}
	@Override
	public double affectionRange() {
		return 0;
	}
	public LeafiaPacket generateSyncPacket() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setTag("a",saveFilterToNBT());
		return LeafiaPacket._start(this).__write(0,compound);
	}
	public final List<EntityPlayer> listeners = new ArrayList<>();
	@Override
	public List<EntityPlayer> getListeners() {
		return listeners;
	}
	@Override
	@SideOnly(Side.CLIENT)
	public void onReceivePacketLocal(byte key,Object value) {
		if (key == 0) {
			if (value instanceof NBTTagCompound tag && tag.hasKey("a")) {
				loadFilterFromNBT(tag.getTagList("a",10));
				updateGUIs();
			}
		}
	}
	@Override
	public void onReceivePacketServer(byte key,Object value,EntityPlayer plr) {
		if (key == 0) {
			if (value instanceof NBTTagCompound tag && tag.hasKey("a")) {
				loadFilterFromNBT(tag.getTagList("a",10));
				markDirty();
				if (world != null) {
					IBlockState state = world.getBlockState(pos);
					world.notifyBlockUpdate(pos,state,state,3);
				}
				generateSyncPacket().__sendToListeners();
			}
		}
	}
	@Override
	public void onPlayerValidate(EntityPlayer plr) { }
}
