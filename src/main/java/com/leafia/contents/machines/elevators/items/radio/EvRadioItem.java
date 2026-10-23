// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.elevators.items.radio;

import com.hbm.util.EnumUtil;
import com.leafia.contents.AddonItems;
import com.leafia.contents.machines.elevators.items.radio.container.EvRadioUI;
import com.leafia.dev.custompacket.LeafiaCustomPacket;
import com.leafia.dev.custompacket.LeafiaCustomPacketEncoder;
import com.leafia.dev.items.itembase.AddonItemBaked;
import com.leafia.dev.optimization.bitbyte.LeafiaBuf;
import com.leafia.init.LeafiaSoundEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class EvRadioItem extends AddonItemBaked {
	public enum ElevatorMusic {
		LOCAL_FORECAST(LeafiaSoundEvents.local_forecast,"Local Forecast","Kevin Macleod"),
		ELEVATOR_JAM(LeafiaSoundEvents.elevator_jam_loop,LeafiaSoundEvents.elevator_jam_end,"Elevator Jam","LSPLASH"),
		;
		public final SoundEvent loop;
		public final SoundEvent end;
		public final String name;
		public final String artist;
		ElevatorMusic(SoundEvent music,String name,String artist) {
			loop = music;
			end = null;
			this.name = name;
			this.artist = artist;
		}
		ElevatorMusic(SoundEvent loop,SoundEvent end,String name,String artist) {
			this.loop = loop;
			this.end = end;
			this.name = name;
			this.artist = artist;
		}
	}
	public EvRadioItem(String s) {
		super(s,"leafia/elevators/ev_radio");
	}
	@SideOnly(Side.CLIENT)
	void openFuckingUI() {
		Minecraft.getMinecraft().displayGuiScreen(new EvRadioUI());
	}
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn,EntityPlayer playerIn,EnumHand handIn) {
		if (worldIn.isRemote)
			openFuckingUI();
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
	}
	public static class EvRadioSyncRequestPacket implements LeafiaCustomPacketEncoder {
		@Override
		public void encode(LeafiaBuf buf) {

		}
		@Override
		public @Nullable Consumer<MessageContext> decode(LeafiaBuf buf) {
			return (ctx)->{
				EntityPlayer player = ctx.getServerHandler().player;
				ItemStack stack = player.getHeldItemMainhand();
				if (stack.getItem() == AddonItems.ev_radio) {
					NBTTagCompound compound = stack.getTagCompound();
					int upOrdinal = -1;
					int downOrdinal = -1;
					int idleOrdinal = -1;
					try {
						if (compound != null && compound.hasKey("up"))
							upOrdinal = ElevatorMusic.valueOf(compound.getString("up")).ordinal();
					} catch (IllegalArgumentException ignored) {}
					try {
						if (compound != null && compound.hasKey("down"))
							downOrdinal = ElevatorMusic.valueOf(compound.getString("down")).ordinal();
					} catch (IllegalArgumentException ignored) {}
					try {
						if (compound != null && compound.hasKey("idle"))
							idleOrdinal = ElevatorMusic.valueOf(compound.getString("idle")).ordinal();
					} catch (IllegalArgumentException ignored) {}
					EvRadioUIPacket packet = new EvRadioUIPacket();
					packet.upOrdinal = upOrdinal;
					packet.downOrdinal = downOrdinal;
					packet.idleOrdinal = idleOrdinal;
					LeafiaCustomPacket.__start(packet).__sendToClient(player);
				}
			};
		}
	}
	public static class EvRadioUIPacket implements LeafiaCustomPacketEncoder {
		public int upOrdinal = -1;
		public int downOrdinal = -1;
		public int idleOrdinal = -1;
		@Override
		public void encode(LeafiaBuf buf) {
			buf.writeByte(upOrdinal);
			buf.writeByte(downOrdinal);
			buf.writeByte(idleOrdinal);
		}
		@SideOnly(Side.CLIENT)
		void handleLocal(int upOrdinal,int downOrdinal,int idleOrdinal) {
			EvRadioUI ui = EvRadioUI.instance;
			if (ui != null) {
				ui.upOrdinal = upOrdinal;
				ui.downOrdinal = downOrdinal;
				ui.idleOrdinal = idleOrdinal;
				ui.loading = false;
			}
		}
		@Override
		public @Nullable Consumer<MessageContext> decode(LeafiaBuf buf) {
			int upOrdinal = buf.readByte();
			int downOrdinal = buf.readByte();
			int idleOrdinal = buf.readByte();
			return (ctx)->{
				if (ctx.side == Side.CLIENT)
					handleLocal(upOrdinal,downOrdinal,idleOrdinal);
				else {
					EntityPlayer player = ctx.getServerHandler().player;
					ItemStack stack = player.getHeldItemMainhand();
					if (stack.getItem() == AddonItems.ev_radio) {
						NBTTagCompound compound = new NBTTagCompound();
						if (upOrdinal != -1)
							compound.setString("up",EnumUtil.grabEnumSafely(ElevatorMusic.values(),upOrdinal).name());
						if (downOrdinal != -1)
							compound.setString("down",EnumUtil.grabEnumSafely(ElevatorMusic.values(),downOrdinal).name());
						if (idleOrdinal != -1)
							compound.setString("idle",EnumUtil.grabEnumSafely(ElevatorMusic.values(),idleOrdinal).name());
						stack.setTagCompound(compound);
						player.inventoryContainer.detectAndSendChanges();
					}
				}
			};
		}
	}
}
