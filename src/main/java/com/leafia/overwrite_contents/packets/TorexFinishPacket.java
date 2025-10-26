package com.leafia.overwrite_contents.packets;


import com.hbm.entity.effect.EntityNukeTorex;
import com.leafia.dev.optimization.bitbyte.LeafiaBuf;
import com.leafia.dev.optimization.diagnosis.RecordablePacket;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

public class TorexFinishPacket extends RecordablePacket {
	private UUID uuid;
	public TorexFinishPacket() {
	}
	@Override
	public void fromBits(LeafiaBuf buf) {
		this.uuid = new UUID(buf.readLong(), buf.readLong());
	}
	@Override
	public void toBits(LeafiaBuf buf) {
		buf.writeLong(uuid.getMostSignificantBits());
		buf.writeLong(uuid.getLeastSignificantBits());
	}
	public static class Handler implements IMessageHandler<TorexFinishPacket,IMessage> {
		@Override
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(TorexFinishPacket message, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(() -> {
				/*for (EntityNukeTorex torex : waitingTorexes) {
					if (torex.getUniqueID().equals(message.uuid) || torex.backupUUID.equals(message.uuid)) {
						torex.calculationFinished = true;
						waitingTorexes.remove(torex);
						break;
					}
				}*/
			});
			return null;
		}
	}
}