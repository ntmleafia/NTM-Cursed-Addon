// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.building.storage.rack;

import com.hbm.tileentity.IGUIProvider;
import com.hbm.tileentity.IPersistentNBT;
import com.hbm.tileentity.machine.storage.TileEntityCrateBase;
import com.leafia.dev.LeafiaUtil;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.container_utility.LeafiaPacketReceiver;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityCrateBase;
import com.leafia.passive.LeafiaPassiveServer;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class RackTE extends TileEntity implements IGUIProvider, LeafiaPacketReceiver {
	/// has to be blocks that provides TileEntityCrateBase otherwise it'll do nothing
	public static final Set<String> supportedTEBlocks = new HashSet<>();
	static {
		supportedTEBlocks.add("hbm:crate_iron");
		supportedTEBlocks.add("hbm:crate_steel");
		supportedTEBlocks.add("hbm:crate_tungsten");
		supportedTEBlocks.add("hbm:crate_desh");
		supportedTEBlocks.add("hbm:crate_template");
	}
	final TileEntityCrateBase[] tes = new TileEntityCrateBase[4];
	public final Block[] crates = new Block[4];
	public final int[] metas = new int[4];
	public boolean storeCrate(ItemStack item,int index,boolean sendUpdate) {
		if (item.getItem() instanceof ItemBlock ib) {
			Block block = ib.getBlock();
			IBlockState state = block.getStateFromMeta(item.getMetadata());
			if (state.getRenderType() == EnumBlockRenderType.MODEL) {
				crates[index] = block;
				metas[index] = item.getMetadata();
				if (supportedTEBlocks.contains(block.getRegistryName().toString()) && block instanceof ITileEntityProvider provider) {
					TileEntity te = provider.createNewTileEntity(world,item.getMetadata());
					if (te instanceof TileEntityCrateBase crate) {
						((IMixinTileEntityCrateBase)crate).leafia$setOnRack();
						crate.setPos(pos);
						crate.setWorld(world);
						if (item.hasTagCompound() && crate instanceof IPersistentNBT pers)
							pers.readNBT(item.getTagCompound());
						crate.validate();
						tes[index] = crate;
					}
				}
				if (!world.isRemote && sendUpdate)
					LeafiaPacket._start(this).__write(index,generateSyncCrateSignal(index)).__sendToAffectedClients();
				return true;
			}
		}
		return false;
	}
	public ItemStack removeCrate(int index,boolean sendUpdate) {
		Block block = crates[index];
		TileEntityCrateBase crate = tes[index];
		int meta = metas[index];
		crates[index] = null;
		tes[index] = null;
		metas[index] = 0;
		if (sendUpdate)
			LeafiaPacket._start(this).__write(index,null).__sendToAffectedClients();
		if (block != null) {
			ItemStack stack = new ItemStack(block,1,meta);
			if (crate != null) {
				crate.invalidate();
				if (crate instanceof IPersistentNBT pers) {
					NBTTagCompound tag = new NBTTagCompound();
					pers.writeNBT(tag);
					if (!tag.isEmpty())
						stack.setTagCompound(tag);
				}
			}
			return stack;
		}
		return ItemStack.EMPTY;
	}
	public NBTTagCompound generateSyncCrateSignal(int index) {
		if (crates[index] != null) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("rsc",crates[index].getRegistryName().toString());
			tag.setInteger("meta",metas[index]);
			return tag;
		}
		return null;
	}
	@Override
	public void invalidate() {
		for (TileEntityCrateBase crate : tes) {
			if (crate != null)
				crate.invalidate();
		}
		super.invalidate();
	}
	AxisAlignedBB bb = null;
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		if (bb == null) {
			bb = new AxisAlignedBB(
					getPos().getX()-1,
					getPos().getY(),
					getPos().getZ()-1,
					getPos().getX()+2,
					getPos().getY()+2,
					getPos().getZ()+2
			);
		}
		return bb;
	}
	public Container provideContainer(int id,EntityPlayer player,World world,int x,int y,int z) {
		if (id >= 0 && id < 4) {
			if (tes[id] != null && tes[id] instanceof IGUIProvider provider)
				return provider.provideContainer(0,player,world,x,y,z);
		}
		return null;
	}
	@SideOnly(Side.CLIENT)
	public GuiScreen provideGUI(int id,EntityPlayer player,World world,int x,int y,int z) {
		if (id >= 0 && id < 4) {
			if (tes[id] != null && tes[id] instanceof IGUIProvider provider)
				return provider.provideGUI(0,player,world,x,y,z);
		}
		return null;
	}

	@Override
	public double affectionRange() {
		return 512;
	}
	@Override
	public String getPacketIdentifier() {
		return "SLC_RACK";
	}
	@Override
	public void onReceivePacketLocal(byte key,Object value) {
		if (key >= 0 && key < 4) {
			if (value != null) {
				NBTTagCompound tag = (NBTTagCompound)value;
				storeCrate(new ItemStack(
						Objects.requireNonNull(Block.getBlockFromName(tag.getString("rsc"))),
						1,
						tag.getInteger("meta")
				),key,false);
			} else
				removeCrate(key,false);
		}
	}
	@Override
	public void onReceivePacketServer(byte key,Object value,EntityPlayer plr) { }
	@Override
	public void onPlayerValidate(EntityPlayer plr) {
		LeafiaPacket packet = LeafiaPacket._start(this);
		for (int i = 0; i < 4; i++)
			packet.__write(i,generateSyncCrateSignal(i));
		packet.__sendToClient(plr);
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		for (int i = 0; i < 4; i++) {
			if (crates[i] != null) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setString("rsc",crates[i].getRegistryName().toString());
				tag.setInteger("meta",metas[i]);
				if (tes[i] != null)
					tag.setTag("inventory",tes[i].inventory.serializeNBT());
				compound.setTag("crate"+i,tag);
			}
		}
		return super.writeToNBT(compound);
	}
	@Override
	protected void setWorldCreate(World worldIn) {
		world = worldIn;
	}
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if (!isInvalid()) {
			for (int i = 0; i < 4; i++) {
				if (compound.hasKey("crate"+i)) {
					NBTTagCompound tag = compound.getCompoundTag("crate"+i);
					Block block = Block.getBlockFromName(tag.getString("rsc"));
					if (block != null) {
						storeCrate(new ItemStack(block,1,tag.getInteger("meta")),i,false);
						if (tes[i] != null && tag.hasKey("inventory"))
							tes[i].inventory.deserializeNBT(tag.getCompoundTag("inventory"));
					}
				}
			}
			LeafiaPacket packet = LeafiaPacket._start(this);
			for (int i = 0; i < 4; i++)
				packet.__write(i,generateSyncCrateSignal(i));
			packet.__sendToAffectedClients();
		}
	}
}
