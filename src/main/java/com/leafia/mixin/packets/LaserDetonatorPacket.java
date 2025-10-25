package com.leafia.mixin.packets;


import com.hbm.render.amlfrom1710.Vec3;
import com.custom_hbm.effectNT.EffectNT;
import com.leafia.dev.optimization.bitbyte.LeafiaBuf;
import com.leafia.dev.optimization.diagnosis.RecordablePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

// this used to be a static class of ItemLaserDetonator so I put it here - Leafia
public class LaserDetonatorPacket extends RecordablePacket {
	public Vec3 startPoint;
	public Vec3 direction;
	public LaserDetonatorPacket() {
	}
	public LaserDetonatorPacket set(Vec3 startPoint,Vec3 direction) {
		this.startPoint = startPoint;
		this.direction = direction;
		return this;
	}
	@Override
	public void fromBits(LeafiaBuf buf) {
		startPoint = new Vec3(buf.readDouble(),buf.readDouble(),buf.readDouble());
		direction = new Vec3(buf.readDouble(),buf.readDouble(),buf.readDouble());
	}
	@Override
	public void toBits(LeafiaBuf buf) {
		buf.writeDouble(startPoint.xCoord);
		buf.writeDouble(startPoint.yCoord);
		buf.writeDouble(startPoint.zCoord);
		buf.writeDouble(direction.xCoord);
		buf.writeDouble(direction.yCoord);
		buf.writeDouble(direction.zCoord);
	}
	public static class Handler implements IMessageHandler<LaserDetonatorPacket,IMessage> {
		@Override
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(LaserDetonatorPacket message,MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(() -> {
				double length = message.direction.length();
				Vec3 unit = message.direction.normalize();
				Random rand = Minecraft.getMinecraft().world.rand;
				for (int i = 0; i <= 1; i++) {
					for (double p = 0; p < length/2;) {
						p+=(Math.pow(p/30d,1.5)+1)*0.2;
						double d = (length-p)*i + p*(1-i);
						NBTTagCompound particle = new NBTTagCompound();
						particle.setString("type", "rslight");
						particle.setDouble("posX",message.startPoint.xCoord+unit.xCoord*d);
						particle.setDouble("posY",message.startPoint.yCoord+unit.yCoord*d);
						particle.setDouble("posZ",message.startPoint.zCoord+unit.zCoord*d);
						particle.setDouble("mX",(rand.nextDouble()-0.5)*0.05);
						particle.setDouble("mY",(rand.nextDouble()-0.5)*0.05);
						particle.setDouble("mZ",(rand.nextDouble()-0.5)*0.05);
						particle.setFloat("red",1);
						particle.setFloat("green",0);
						particle.setFloat("blue",0);
						particle.setFloat("scale",0.5f);
						EffectNT.effectNT(particle);
					}
				}
			});
			return null;
		}
	}
}