package com.leafia.overwrite_contents.other;

import com.hbm.entity.effect.EntityNukeTorex;
import com.hbm.handler.threading.PacketThreading;
import com.hbm.packet.PacketDispatcher;
import com.leafia.overwrite_contents.interfaces.IMixinEntityNukeTorex;
import com.leafia.overwrite_contents.packets.TorexPacket;
import com.llib.exceptions.LeafiaDevFlaw;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TorexSpawner {
	static Method writeNBT;
	static {
		writeNBT = ObfuscationReflectionHelper.findMethod(
				EntityNukeTorex.class,
				"func_70014_b", // writeEntityToNBT
				null,
				NBTTagCompound.class
		);
		writeNBT.setAccessible(true);
	}
	public static void spawnTorex(World world,EntityNukeTorex torex) {
		IMixinEntityNukeTorex mixin = (IMixinEntityNukeTorex)torex;
		/*if (torex.boundEntity == null) {
			if (bindMe != null) {
				if (bindMe.isEntityAlive() && bindMe.ticksExisted < 10) {
					if ((bindMe.dimension == torex.dimension) && (Math.sqrt(bindMe.getPosition().distanceSq(torex.getPosition())) < 1.5)) {
						torex.boundEntity = bindMe;
						bindMe = null;
					}
				} else bindMe = null;
			}
		}*/
		mixin.setInitPosX(torex.posX);
		mixin.setInitPosY(torex.posY);
		mixin.setInitPosZ(torex.posZ);
		mixin.setValid(true);
		world.weatherEffects.add(torex);
		TorexPacket packet = new TorexPacket();
		packet.entityId = torex.getEntityId();
		packet.uuid = torex.getUniqueID();
		packet.x = mixin.getInitPosX();
		packet.y = mixin.getInitPosY();
		packet.z = mixin.getInitPosZ();
		//torex.calculationFinished = torex.boundEntity == null;
		//packet.doWait = torex.boundEntity != null;
		NBTTagCompound nbt = new NBTTagCompound();
		//torex.writeEntityToNBT(nbt);
		try {
			writeNBT.invoke(torex,nbt);
		} catch (InvocationTargetException|IllegalAccessException e) {
			throw new LeafiaDevFlaw(e);
		}
		packet.nbt = nbt;
		double amp = torex.getScale()*100;
		PacketThreading.createSendToAllTrackingThreadedPacket(packet,new NetworkRegistry.TargetPoint(
						torex.dimension,
						packet.x,
						packet.y,
						packet.z,
						200+amp+Math.pow(amp,0.8)*8
				)
		);
		//PacketThreading.createSendToDimensionThreadedPacket(packet,torex.dimension);
	}
}
