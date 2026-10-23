// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.passive;

import com.leafia.dev.LeafiaDebug;
import com.leafia.dev.custompacket.LeafiaCustomPacket;
import com.leafia.dev.custompacket.LeafiaCustomPacketEncoder;
import com.leafia.dev.optimization.bitbyte.LeafiaBuf;
import com.llib.math.MathLeafia;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

public class Wind {
	public static Map<Integer,Double> dimensionWindMultiplier = new HashMap<>();
	static {
		dimensionWindMultiplier.put(0,1d);
	}

	public static double power = 0;
	public static int randomShortWindCounter = 0;
	public static int randomLongWindCounter = 0;
	public static int randomShortWindLength = 200;
	public static int randomLongWindLength = 2000;
	public static boolean strongWind = false;
	public static double startPower = 0;
	public static double endPower = 0;

	public static double angle = 0;
	public static double lastAngle = 0;
	public static int randomAngleCounter = 0;
	public static int randomAngleLength = 200;
	public static double startAngle = 0;
	public static double endAngle = 0;
	
	public static class WindSavedData extends WorldSavedData {
		public static final String key = "leafiaWind";
		public WindSavedData(String name) {
			super(name);
		}
		public static WindSavedData forWorld(World world) {
			WindSavedData data = (WindSavedData)world.getPerWorldStorage().getOrLoadData(WindSavedData.class,key);
			if (data == null) {
				world.getPerWorldStorage().setData(key,new WindSavedData(key));
				data = (WindSavedData)world.getPerWorldStorage().getOrLoadData(WindSavedData.class,key);
			}
			return data;
		}
		@Override
		public void readFromNBT(NBTTagCompound nbt) {
			Wind.readFromNBT(nbt);
		}
		@Override
		public NBTTagCompound writeToNBT(NBTTagCompound compound) {
			return Wind.writeToNBT(compound);
		}
	}
	
	public static void readFromNBT(NBTTagCompound compound) {
		power = compound.getDouble("power");
		randomShortWindCounter = compound.getInteger("shortWindCounter");
		randomShortWindLength = compound.getInteger("shortWindLength");
		randomLongWindCounter = compound.getInteger("longWindCounter");
		randomLongWindLength = compound.getInteger("longWindLength");
		strongWind = compound.getBoolean("strong");
		startPower = compound.getDouble("startPower");
		endPower = compound.getDouble("endPower");
		angle = compound.getDouble("angle");
		lastAngle = compound.getDouble("lastAngle");
		randomAngleCounter = compound.getInteger("angleCounter");
		randomAngleLength = compound.getInteger("angleLength");
		startAngle = compound.getDouble("startAngle");
		endAngle = compound.getDouble("endAngle");
	}
	
	public static NBTTagCompound writeToNBT(NBTTagCompound compound) {
		if (compound == null)
			compound = new NBTTagCompound();
		compound.setDouble("power",power);
		compound.setInteger("shortWindCounter",randomShortWindCounter);
		compound.setInteger("shortWindLength",randomShortWindLength);
		compound.setInteger("longWindCounter",randomLongWindCounter);
		compound.setInteger("longWindLength",randomLongWindLength);
		compound.setBoolean("strong",strongWind);
		compound.setDouble("startPower",startPower);
		compound.setDouble("endPower",endPower);
		compound.setDouble("angle",angle);
		compound.setDouble("lastAngle",lastAngle);
		compound.setInteger("angleCounter",randomAngleCounter);
		compound.setInteger("angleLength",randomAngleLength);
		compound.setDouble("startAngle",startAngle);
		compound.setDouble("endAngle",endAngle);
		return compound;
	}

	static final double range = 20;
	static double calculateTargetPower(Random rand) {
		double basePower = strongWind ? 100-range : 5;
		double outPower = power+rand.nextDouble()*range*2-range;
		double adjustment = basePower-power;
		outPower += adjustment/4;
		if (outPower < 0)
			outPower = 0;
		if (outPower < basePower-range)
			outPower = basePower-range;
		if (outPower > 100)
			outPower = 100;
		if (outPower > basePower+range)
			outPower = basePower+range;
		return outPower;
	}
	static Random rand = new Random();
	public static void updateWorld(World world) {
		if (world.isRemote) return;
		LeafiaCustomPacket.__start(new WindSyncPacket()).__sendToAll();
		WindSavedData.forWorld(world).markDirty();
	}
	public static void update() {
		// server only
		lastAngle = angle;
		if (rand.nextBoolean()) {
			randomAngleCounter++;
			if (randomAngleCounter > randomAngleLength) {
				randomAngleCounter = 0;
				randomAngleLength = 180+rand.nextInt(40);
				lastAngle = MathLeafia.positiveModulo(lastAngle,360);
				angle = MathLeafia.positiveModulo(angle,360);
				startAngle = angle;
				endAngle = angle+rand.nextDouble(45*2)-45;
			}
			angle = lerp(startAngle,endAngle,(double)randomAngleCounter/randomAngleLength);
		}
		randomShortWindCounter++;
		randomLongWindCounter++;
		if (randomLongWindCounter > randomLongWindLength) {
			randomLongWindCounter = 0;
			randomLongWindLength = rand.nextInt(1000)+1750;
			strongWind = rand.nextFloat() >= 0.66f;
		}
		if (randomShortWindCounter > randomShortWindLength) {
			randomShortWindCounter = 0;
			randomShortWindLength = rand.nextInt(200)+150;
			startPower = power;
			endPower = calculateTargetPower(rand);
		}
		power = lerp(startPower,endPower,(double)randomShortWindCounter/randomShortWindLength);
	}
	static double lerp(double a,double b,double t) {
		return a+(b-a)*t;
	}
	public static class WindSyncPacket implements LeafiaCustomPacketEncoder {
		@Override
		public void encode(LeafiaBuf buf) {
			buf.writeDouble(power);
			buf.writeDouble(angle);
			buf.writeDouble(lastAngle);
		}
		@Override
		public @Nullable Consumer<MessageContext> decode(LeafiaBuf buf) {
			double p = buf.readDouble();
			double a = buf.readDouble();
			double la = buf.readDouble();
			return (ctx)->{
				power = p;
				angle = a;
				lastAngle = la;
			};
		}
	}
}
