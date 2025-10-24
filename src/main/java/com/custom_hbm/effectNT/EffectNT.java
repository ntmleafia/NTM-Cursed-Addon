package com.custom_hbm.effectNT;

import com.leafia.unsorted.ParticleRedstoneLight;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.Random;

public class EffectNT {
	public static void effectNT(NBTTagCompound data) {
		World world = Minecraft.getMinecraft().world;
		if(world == null)
			return;
		EntityPlayer player = Minecraft.getMinecraft().player;
		Random rand = world.rand;
		String type = data.getString("type");
		double x = data.getDouble("posX");
		double y = data.getDouble("posY");
		double z = data.getDouble("posZ");

		if("rslight".equals(type)) {
			float size = data.getFloat("scale");
			ParticleRedstoneLight fx = new ParticleRedstoneLight(
					world,x,y,z,(size == 0) ? 1 : size,
					data.getDouble("mX"),
					data.getDouble("mY"),
					data.getDouble("mZ"),
					data.getFloat("red"),
					data.getFloat("green"),
					data.getFloat("blue")
			);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			return;
		}

	}
}
