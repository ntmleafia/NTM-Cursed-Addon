package com.leafia.overwrite_contents.packets;

import com.hbm.entity.effect.EntityNukeTorex;
import com.leafia.dev.optimization.bitbyte.LeafiaBuf;
import com.leafia.dev.optimization.diagnosis.RecordablePacket;
import com.leafia.overwrite_contents.interfaces.IMixinEntityNukeTorex;
import com.llib.exceptions.LeafiaDevFlaw;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class TorexPacket extends RecordablePacket {
	public int entityId;
	public double x;
	public double y;
	public double z;
	public NBTTagCompound nbt;
	public UUID uuid;
	public boolean doWait;
	public TorexPacket() {
	}
	@Override
	public void fromBits(LeafiaBuf buf) {
		this.entityId = buf.readInt();
		this.uuid = new UUID(buf.readLong(), buf.readLong());
		this.x = buf.readDouble();
		this.y = buf.readDouble();
		this.z = buf.readDouble();
		this.doWait = buf.readBoolean();
		this.nbt = buf.readNBT();
	}
	@Override
	public void toBits(LeafiaBuf buf) {
		buf.writeInt(this.entityId);
		buf.writeLong(uuid.getMostSignificantBits());
		buf.writeLong(uuid.getLeastSignificantBits());
		buf.writeDouble(this.x);
		buf.writeDouble(this.y);
		buf.writeDouble(this.z);
		buf.writeBoolean(this.doWait);
		buf.writeNBT(nbt);
	}

    static Method readNBT;

    static {
        readNBT = ReflectionHelper.findMethod(
                EntityNukeTorex.class,
                "readEntityFromNBT",
                "func_70037_a",
                NBTTagCompound.class
        );
        readNBT.setAccessible(true);
    }

	public static class Handler implements IMessageHandler<TorexPacket,IMessage> {
		@Override
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(TorexPacket message, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(() -> {
				Minecraft mc = Minecraft.getMinecraft();
				EntityNukeTorex torex = new EntityNukeTorex(mc.world);
				IMixinEntityNukeTorex mixin = (IMixinEntityNukeTorex)torex;
				torex.setEntityId(message.entityId);
				torex.setUniqueId(message.uuid);
				mixin.setValid(true);
				torex.setPosition(message.x,message.y,message.z);
				mixin.setInitPosX(message.x);
				mixin.setInitPosY(message.y);
				mixin.setInitPosZ(message.z);
				EntityTracker.updateServerPosition(torex,message.x,message.y,message.z);
				//torex.readEntityFromNBT(message.nbt);
				try {
					readNBT.invoke(torex,message.nbt);
				} catch (InvocationTargetException | IllegalAccessException e) {
					throw new LeafiaDevFlaw(e);
				}
				mc.world.addWeatherEffect(torex);
				if (message.doWait) {
					//torex.calculationFinished = false;
					//torex.backupUUID = message.uuid;
					//waitingTorexes.add(torex);
				}
			});
			return null;
		}
	}
}