// SPDX-FileCopyrightText: 2025-2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.gear.advisor;

import com.hbm.blocks.gas.*;
import com.hbm.main.ClientProxy;
import com.hbm.main.MainRegistry;
import com.hbm.tileentity.IGUIProvider;
import com.hbm.util.ArmorRegistry;
import com.hbm.util.ArmorRegistry.HazardClass;
import com.hbm.util.I18nUtil;
import com.leafia.contents.gear.advisor.container.AdvisorUI;
import com.leafia.contents.gear.advisor.container.AdvisorUI.HistoryElement;
import com.leafia.dev.gui.DummyContainer;
import com.leafia.contents.gear.advisor.container.IAdvisorUI;
import com.leafia.dev.custompacket.LeafiaCustomPacket;
import com.leafia.dev.custompacket.LeafiaCustomPacketEncoder;
import com.leafia.dev.items.itembase.AddonItemBase;
import com.leafia.dev.math.FiaMatrix;
import com.leafia.dev.optimization.bitbyte.LeafiaBuf;
import com.leafia.init.LeafiaSoundEvents;
import com.llib.exceptions.LeafiaDevFlaw;
import com.llib.technical.FifthString;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class AdvisorItem extends AddonItemBase {//implements IGUIProvider {
	public static void setCover(EntityPlayer player,boolean open) {
		if (open) {
			ItemStack stack = null;
			if (player.getHeldItemMainhand().getItem() instanceof AdvisorItem)
				stack = player.getHeldItemMainhand();
			else if (player.getHeldItemMainhand().getItem() instanceof AdvisorItem)
				stack = player.getHeldItemMainhand();
			if (stack != null) {
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setBoolean("open",true);
				stack.setTagCompound(nbt);
			}
			player.inventoryContainer.detectAndSendChanges();
		} else {
			for (ItemStack stack : player.inventory.mainInventory) {
				if (stack.getItem() instanceof AdvisorItem)
					stack.setTagCompound(null);
			}
			for (ItemStack stack : player.inventory.offHandInventory) {
				if (stack.getItem() instanceof AdvisorItem)
					stack.setTagCompound(null);
			}
			// don't ask why
			for (ItemStack stack : player.inventory.armorInventory) {
				if (stack.getItem() instanceof AdvisorItem)
					stack.setTagCompound(null);
			}
			player.inventoryContainer.detectAndSendChanges();
		}
	}
	/*
	@Override
	public Container provideContainer(int i,EntityPlayer entityPlayer,World world,int i1,int i2,int i3) {
		setCover(entityPlayer,true);
		return new DummyContainer();
	}
	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen provideGUI(int i,EntityPlayer entityPlayer,World world,int i1,int i2,int i3) {
		return new AdvisorUI();
	}*/ // this sucks
	@SideOnly(Side.CLIENT)
	void openFuckingUI() {
		Minecraft.getMinecraft().displayGuiScreen(new AdvisorUI());
	}
	@Override
	public @NotNull ActionResult<ItemStack> onItemRightClick(World world,EntityPlayer player,EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (hand != EnumHand.MAIN_HAND) return new ActionResult<>(EnumActionResult.FAIL,stack);
		//if (!world.isRemote) player.openGui(MainRegistry.instance, 0, world, 0, 0, 0);
		if (!world.isRemote)
			setCover(player,true);
		else
			openFuckingUI();
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}
	public static void addWarningHistory(EntityPlayer player,String id) {
		NBTTagCompound tag = null;
		if (player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).hasKey("leafia_advisor"))
			tag = player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getCompoundTag("leafia_advisor");
		if (tag == null) tag = new NBTTagCompound();
		NBTTagList list;
		if (tag.hasKey("history"))
			list = tag.getTagList("history",8);
		else
			list = new NBTTagList();
		list.appendTag(new NBTTagString(id));
		tag.setTag("history",list);
		player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).setTag("leafia_advisor",tag);
		AdvisorPacket packet = new AdvisorPacket(AdvisorSignalType.SYNC_LOCAL.id);
		packet.warnings = new ArrayList<>();
		for (NBTBase nbtBase : list) {
			if (nbtBase instanceof NBTTagString str)
				packet.warnings.add(str.getString());
			else
				packet.warnings.add(nbtBase.toString());
		}
		LeafiaCustomPacket.__start(packet).__sendToClient(player);
	}
	public static void removeWarningHistory(EntityPlayer player,int index) {
		NBTTagCompound tag = null;
		if (player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).hasKey("leafia_advisor"))
			tag = player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getCompoundTag("leafia_advisor");
		if (tag == null) tag = new NBTTagCompound();
		NBTTagList list;
		if (tag.hasKey("history"))
			list = tag.getTagList("history",8);
		else
			list = new NBTTagList();
		if (index >= 0 && index < list.tagCount())
			list.removeTag(index);
		tag.setTag("history",list);
		player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).setTag("leafia_advisor",tag);
		AdvisorPacket packet = new AdvisorPacket(AdvisorSignalType.SYNC_LOCAL.id);
		packet.warnings = new ArrayList<>();
		for (NBTBase nbtBase : list) {
			if (nbtBase instanceof NBTTagString str)
				packet.warnings.add(str.getString());
			else
				packet.warnings.add(nbtBase.toString());
		}
		LeafiaCustomPacket.__start(packet).__sendToClient(player);
	}
	public static List<String> getWarningHistory(EntityPlayer player,List<String> target) {
		NBTTagCompound tag = null;
		if (player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).hasKey("leafia_advisor"))
			tag = player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getCompoundTag("leafia_advisor");
		if (tag == null) tag = new NBTTagCompound();
		if (tag.hasKey("history")) {
			NBTTagList list = tag.getTagList("history",8);
			for (NBTBase nbtBase : list) {
				if (nbtBase instanceof NBTTagString str)
					target.add(str.getString());
				else
					target.add(nbtBase.toString());
			}
		}
		return target;
	}
	public enum AdvisorSignalType {
		CLOSE(0), // to server
		WARN(1), // to server
		SYNC_REQUEST(2), // to server
		SYNC_LOCAL(3), // to local
		DISMISS(4), // to server
		;
		public final byte id;
		AdvisorSignalType(int id) {
			this.id = (byte)id;
		}
		public static AdvisorSignalType fromId(byte id) {
			for (AdvisorSignalType value : values()) {
				if (value.id == id)
					return value;
			}
			throw new LeafiaDevFlaw("what");
		}
	}
	public static class AdvisorPacket implements LeafiaCustomPacketEncoder {
		byte signalType;
		String warningKey;
		List<String> warnings;
		public int dismissIndex;
		public AdvisorPacket() { }
		public AdvisorPacket(int signalType) {
			this.signalType = (byte)signalType;
		}

		@Override
		public void encode(LeafiaBuf buf) {
			buf.writeByte(signalType);
			switch(AdvisorSignalType.fromId(signalType)) {
				case WARN -> buf.writeFifthString(new FifthString(warningKey));
				case SYNC_LOCAL -> {
					buf.writeInt(warnings.size());
					for (String warning : warnings)
						buf.writeFifthString(new FifthString(warning));
				}
				case DISMISS -> buf.writeInt(dismissIndex);
			}
		}
		@SideOnly(Side.CLIENT)
		void processLocal(LeafiaBuf buf) {
			IAdvisorUI ui = AdvisorUI.instance;
			if (ui != null) {
				switch(AdvisorSignalType.fromId(signalType)) {
					case SYNC_LOCAL -> {
						if (ui instanceof AdvisorUI main) {
							int length = buf.readInt();
							main.warnings.clear();
							for (int i = length-1; i >= 0; i--)
								main.warnings.add(0,new HistoryElement(buf.readFifthString().toString(),i));
							main.updateRects();
							main.loading = false;
						}
					}
				}
			}
		}
		@Override
		public @Nullable Consumer<MessageContext> decode(LeafiaBuf buf) {
			signalType = buf.readByte();
			return (ctx)->{
				if (ctx.netHandler instanceof NetHandlerPlayServer) {
					switch(AdvisorSignalType.fromId(signalType)) {
						case CLOSE -> setCover(ctx.getServerHandler().player,false);
						case WARN -> {
							String key = buf.readFifthString().toString();
							addWarningHistory(ctx.getServerHandler().player,key);
						}
						case SYNC_REQUEST -> {
							AdvisorPacket packet = new AdvisorPacket(AdvisorSignalType.SYNC_LOCAL.id);
							packet.warnings = getWarningHistory(ctx.getServerHandler().player,new ArrayList<>());
							LeafiaCustomPacket.__start(packet).__sendToClient(ctx.getServerHandler().player);
						}
						case DISMISS -> removeWarningHistory(ctx.getServerHandler().player,buf.readInt());
					}
				} else if (ctx.netHandler instanceof NetHandlerPlayClient)
					processLocal(buf);
			};
		}
	}
	public static class AdvisorWarningPacket implements LeafiaCustomPacketEncoder {
		public int warningId;
		public AdvisorWarningPacket() { }
		public AdvisorWarningPacket(int warningId) {
			this.warningId = warningId;
		}
		@Override
		public void encode(LeafiaBuf buf) {
			buf.writeInt(warningId);
		}
		@Override
		public @Nullable Consumer<MessageContext> decode(LeafiaBuf buf) {
			int warningId = buf.readInt();
			return (ctx)->{
				if (warningId == 0) // PYROPHORIC
					Warns.pyro = true;
				else if (warningId == 1) // SKIN DAMAGE II
					Warns.skinDmg2 = true;
				else if (warningId == 2) // SKIN DAMAGE III
					Warns.skinDmg3 = true;
				else if (warningId == 3) // TOXIC
					Warns.toxic = true;
				else if (warningId == 4) // TOXIC (STRONG)
					Warns.toxicStrong = true;
			};
		}
	}
	final static int len = 10000;
	public static void showMessage(ITextComponent msg,int millisec,int id) {
		id*=10;
		MainRegistry.proxy.displayTooltipLegacy(msg.getFormattedText(),millisec,1121+id);
	}
	public static void showMessage(String msg,int millisec,int id) {
		id*=10;
		for (String s : msg.split("\\$")) {
			MainRegistry.proxy.displayTooltipLegacy(TextFormatting.GOLD+s,millisec,1121+id);
			id++;
		}
	}
	@SideOnly(Side.CLIENT)
	public static void warnPlayer(boolean playSound,String key) {
		AdvisorPacket packet = new AdvisorPacket(AdvisorSignalType.WARN.id);
		packet.warningKey = key;
		LeafiaCustomPacket.__start(packet).__sendToServer();
		if (playSound)
			Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(LeafiaSoundEvents.advisor_warning,1));
	}
	public AdvisorItem(String s) {
		super(s);
	}
	public static class Warns {
		static int gas = 0;
		static boolean pyro = false;
		static int pyroCooldown = 0;
		static boolean skinDmg2 = false;
		static boolean skinDmg3 = false;
		static boolean toxic = false;
		static boolean toxicStrong = false;
		static int toxicCooldown = 0;
		static int lava = 0;
		public static void preTick() {
			gas = decrement(gas);
			pyro = false;
			skinDmg2 = false;
			skinDmg3 = false;
			lava = decrement(lava);
			toxic = false;
			toxicStrong = false;
			toxicCooldown = decrement(toxicCooldown);
		}
		static int decrement(int v) { return Math.max(v-1,0); }
	}
	public static final String msgRoot = "item.advisor.message.";
	static String msg(String key) {
		return I18nUtil.resolveKey(msgRoot+key+".message");
	}
	static final int gasCooldown = 20*60;
	public void gasAlert(World world,BlockPos pos,EntityPlayer player) {
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (block instanceof BlockGasCoal) {
			if (!ArmorRegistry.hasProtection(player,EntityEquipmentSlot.HEAD,HazardClass.PARTICLE_COARSE)) {
				if (Warns.gas <= 0)
					warnPlayer(true,"coal");
				showMessage(msg("coal"),len,0);
				Warns.gas = gasCooldown;
			}
		}
		if (block instanceof BlockGasAsbestos) {
			if (!ArmorRegistry.hasProtection(player,EntityEquipmentSlot.HEAD,HazardClass.PARTICLE_FINE)) {
				if (Warns.gas <= 0)
					warnPlayer(true,"asbestos");
				showMessage(msg("asbestos"),len,1);
				Warns.gas = gasCooldown;
			}
		}
		if (block instanceof BlockGasRadon || block instanceof BlockGasRadonDense || block instanceof BlockGasRadonTomb || block instanceof BlockGasMeltdown) {
			if (!ArmorRegistry.hasProtection(player,EntityEquipmentSlot.HEAD,HazardClass.PARTICLE_FINE)) {
				if (Warns.gas <= 0)
					warnPlayer(true,"radon");
				showMessage(msg("radon"),len,7);
				Warns.gas = gasCooldown;
			}
		}
	}
	@Override
	public void onUpdate(ItemStack stack,World world,Entity entity,int itemSlot,boolean isSelected) {
		if (world.isRemote && MainRegistry.proxy instanceof ClientProxy && entity instanceof EntityPlayer player) {
			//showMessage("Selected: "+isSelected,1000,0);
			BlockPos pos = new BlockPos(entity.posX,entity.posY,entity.posZ);
			{
				// BLOCK ALERT
				gasAlert(world,pos,player);
				gasAlert(world,pos.up(),player);
			}
			if (Warns.pyro) {
				if (Warns.pyroCooldown <= 0)
					warnPlayer(true,"pyro");
				Warns.pyroCooldown = gasCooldown;
				showMessage(msg("pyro"),len,2);
			}
			if (Warns.skinDmg2) {
				warnPlayer(true,"skindmg2");
				showMessage(msg("skindmg2"),len,3);
			}
			if (Warns.skinDmg3) {
				warnPlayer(true,"skindmg3");
				showMessage(msg("skindmg3"),len,3);
			}
			if (Warns.toxic && !Warns.toxicStrong) {
				if (Warns.toxicCooldown <= 0)
					warnPlayer(true,"toxic1");
				Warns.toxicCooldown = gasCooldown;
				showMessage(msg("toxic1"),len,5);
			}
			if (Warns.toxicStrong) {
				if (Warns.toxicCooldown <= 0)
					warnPlayer(true,"toxic2");
				Warns.toxicCooldown = gasCooldown;
				showMessage(msg("toxic2"),len,6);
			}
			{
				// BEHIND CHECK
				FiaMatrix facing = new FiaMatrix(new Vec3d(player.posX+0.5,player.posY+0.5,player.posZ+0.5)).rotateY(-player.rotationYawHead);
				Vec3d relativeVelocity = facing.toObjectSpace(facing.add(new Vec3d(player.motionX,0,player.motionZ))).position.normalize();
				// i must've been smoking my tail when I was coding this but the positive Z is forward for some reason lmao
				if (relativeVelocity.z < -0.707) {
					boolean lavaWarn = false;
					for (int i = 1; i <= 4; i++) {
						BlockPos p = new BlockPos(facing.translate(0,0,-i).position);
						if (!isAir(world,p)) break;
						for (int j = 0; j < 10; j++) {
							BlockPos p2 = p.down(j);
							if (world.getBlockState(p2).getMaterial().equals(Material.LAVA)) {
								lavaWarn = true;
								break;
							}
							if (!isAir(world,p2))
								break;
						}
						if (lavaWarn)
							break;
					}
					if (lavaWarn) {
						if (Warns.lava <= 0)
							warnPlayer(true,"lava");
						Warns.lava = 200;
						showMessage(msg("lava"),4000,4);
					}
				}
			}
		}
	}
	public boolean isAir(World world,BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		AxisAlignedBB bb = state.getBlock().getCollisionBoundingBox(state,world,pos);
		return bb == Block.NULL_AABB;
	}
	@Override
	public void addInformation(ItemStack stack,@Nullable World worldIn,List<String> tooltip,ITooltipFlag flagIn) {
		tooltip.addAll(Arrays.asList(I18nUtil.resolveKey("item.advisor.desc").split("\\$")));
		super.addInformation(stack,worldIn,tooltip,flagIn);
	}
}
