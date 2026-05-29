package com.leafia.passive;

import com.leafia.contents.gear.advisor.AdvisorItem.Warns;
import com.leafia.contents.worldgen.biomes.artificial.DigammaCrater;
import com.leafia.contents.worldgen.biomes.artificial.DigammaCrater.DigammaBackstabPacket;
import com.leafia.contents.worldgen.biomes.artificial.DigammaCrater.NullEntity;
import com.leafia.dev.custompacket.LeafiaCustomPacket;
import com.leafia.dev.optimization.diagnosis.RecordablePacket;
import com.leafia.eventbuses.LeafiaClientListener;
import com.leafia.eventbuses.LeafiaClientListener.Digamma;
import com.leafia.eventbuses.LeafiaClientListener.HandlerClient;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityCore;
import com.leafia.passive.rendering.AddonRainRender;
import com.leafia.savedata.FalloutSavedData;
import com.llib.group.LeafiaSet;
import com.llib.math.MathLeafia;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class LeafiaPassiveLocal {
	static final List<Runnable> queue = new ArrayList<>();
	// "LeaviaPassiveLoval" (my typing is awesome)
	static short t0 = 0;
	static int packetRecordTimer1s = 1_000;
	static int packetRecordTimer1m = 60;

	public static LeafiaSet<IMixinTileEntityCore> trackingCores = new LeafiaSet<>();

	public static void onTick(World world) {
		Digamma.update(world);
		for (Runnable callback : queue)
			callback.run();
		queue.clear();
		if (Minecraft.getMinecraft().isGamePaused()) {
			for (IMixinTileEntityCore core : trackingCores)
				core.setDFCExplosionClock(System.currentTimeMillis());
		}
	}
	public static int nullCounter = 0;
	public static void priorTick(World world) {
		if (!Minecraft.getMinecraft().isGamePaused()) {
			AddonRainRender.INSTANCE.update();
			FalloutSavedData.forWorld(world).tick();
			if (HandlerClient.dfcFlashTicks > 0)
				HandlerClient.dfcFlashTicks--;
		}
		RecordablePacket.previousByteUsage = RecordablePacket.bytesUsage;
		RecordablePacket.bytesUsage = 0;
		short t1 = MathLeafia.getTime32s();
		int dT = MathLeafia.getTimeDifference32s(t0,t1);
		t0 = t1;
		packetRecordTimer1s -= dT;
		if (packetRecordTimer1s < 0) {
			packetRecordTimer1s = Math.floorMod(packetRecordTimer1s,1000);
			packetRecordTimer1m--;
			RecordablePacket.previousByteUsageSec = RecordablePacket.bytesUsageSec;
			RecordablePacket.bytesUsageSec = 0;
			if (packetRecordTimer1m <= 0) {
				packetRecordTimer1m = 60;
				RecordablePacket.previousByteUsageMin = RecordablePacket.bytesUsageMin;
				RecordablePacket.bytesUsageMin = 0;
			}
		}
		Warns.preTick();
		if (!Minecraft.getMinecraft().isGamePaused()) {
			EntityPlayer player = Minecraft.getMinecraft().player;
			if (!player.isSpectator() && !player.isCreative() && DigammaCrater.isDigammaBiome(world.getBiome(new BlockPos(player.posX,player.posY,player.posZ))) && player.getHealth() > 6 && world.rand.nextInt(40000) == 0 && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0)
				LeafiaCustomPacket.__start(new DigammaBackstabPacket()).__sendToServer();
		}
		if (nullCounter < 20 && !Minecraft.getMinecraft().isGamePaused()) {
			EntityPlayer player = Minecraft.getMinecraft().player;
			if (DigammaCrater.isDigammaBiome(world.getBiome(new BlockPos(player.posX,player.posY,player.posZ)))) {
				double r = 60+world.rand.nextDouble()*80;
				double theta = world.rand.nextDouble()*2*Math.PI;
				int x = MathHelper.floor(player.posX+Math.cos(theta)*r);
				int z = MathHelper.floor(player.posZ+Math.sin(theta)*r);
				BlockPos p = new BlockPos(x,world.getHeight(x,z),z);
				if (world.getBlockState(p.down()).getMaterial().isSolid()) { // stop spawning on oceans
					if (DigammaCrater.isDigammaBiome(world.getBiome(p))) {
						NullEntity entity = new NullEntity(world);
						entity.setPosition(p.getX()+0.5,p.getY(),p.getZ()+0.5);
						world.spawnEntity(entity);
					}
				}
			}
		}
		nullCounter = 0;
	}
	public static void queueFunctionPost(Runnable callback) {
		queue.add(callback);
	}
}
