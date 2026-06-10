package com.leafia.contents.machines.research.amsp.receiver;

import com.custom_hbm.sound.LCEAudioWrapper;
import com.hbm.entity.mob.glyphid.EntityGlyphid;
import com.hbm.handler.threading.PacketThreading;
import com.hbm.lib.HBMSoundHandler;
import com.leafia.AddonBase;
import com.leafia.CommandLeaf;
import com.leafia.dev.optimization.LeafiaParticlePacket.DFCNukeParticle;
import com.leafia.init.LeafiaSoundEvents;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityCore;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class AMSPReceiverTE extends TileEntity implements ITickable {
	LCEAudioWrapper alarm;
	int damage = -1;
	@Override
	public void update() {
		/*
		if (!world.isRemote) {
			damage++;
			if (damage%5 == 0) {
				PacketThreading.createSendToAllTrackingThreadedPacket(
						new CommandLeaf.ShakecamPacket(new String[]{
								"type=smooth",
								"preset=RUPTURE",
								"blurDulling*20",
								"bloomDulling*20",
								"duration/4",
								"speed*8",
								"intensity/16",
								"range=100"
						}).setPos(pos),
						new NetworkRegistry.TargetPoint(world.provider.getDimension(),pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5,250)
				);
			}
			if (damage/(20*4) == 8) {
				world.playSound(null,pos.getX() + 0.5,pos.getY() + 5.5f,pos.getZ() + 0.5,LeafiaSoundEvents.amsp_explode,SoundCategory.BLOCKS,100,1);
				world.setBlockToAir(pos);
				world.setBlockToAir(pos.up(10));
				DFCNukeParticle nuke = new DFCNukeParticle();
				nuke.radius = 150;
				nuke.emit(new Vec3d(pos.getX()+0.5,pos.getY()+5.5,pos.getZ()+0.5),new Vec3d(0,1,0),world.provider.getDimension(),150*5);
				for (int i = 0; i < 300; i++) {
					EntityGlyphid glyphid = new EntityGlyphid(world);
					glyphid.setLocationAndAngles(pos.getX()+0.5f,pos.getY()+5.5f,pos.getZ()+0.5f,0,0);
					world.spawnEntity(glyphid);
				}
				return;
			}
			if (damage%(20*4) == 0) {
				for (int i = 0; i < 5; i++)
					IMixinTileEntityCore.shockParticleEditionTrademark(world,new Vec3d(pos.getX()+0.5f,pos.getY()+5.5f,pos.getZ()+0.5f));
				world.playSound(null, pos.getX() + 0.5, pos.getY() + 5.5f, pos.getZ() + 0.5, LeafiaSoundEvents.mus_sfx_a_lithit, SoundCategory.BLOCKS, 6.66f, 1 + (float) world.rand.nextGaussian() * 0.1f);
				PacketThreading.createSendToAllTrackingThreadedPacket(
						new CommandLeaf.ShakecamPacket(new String[]{
								"type=smooth",
								"preset=RUPTURE",
								"blurDulling*20",
								"bloomDulling*20",
								"duration/4",
								"speed*8",
								"intensity/2",
								"range=100"
						}).setPos(pos),
						new NetworkRegistry.TargetPoint(world.provider.getDimension(),pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5,250)
				);
			}
		} else {
			if (alarm == null) {
				alarm = AddonBase.proxy.getLoopedSoundStartStop(world,HBMSoundHandler.alarmAMSSiren,null,null,SoundCategory.BLOCKS,pos.getX()+0.5f,pos.getY()+5.5f,pos.getZ()+0.5f,10,1);
				alarm.setCustomAttenuation((intended,distance)->
						Math.pow(MathHelper.clamp(1-(distance-3)/80,0,1),2));
				alarm.startSound();
			}
		}*/
	}
	@Override
	public void invalidate() {
		super.invalidate();
		if (alarm != null) {
			alarm.stopSound();
			alarm = null;
		}
	}
	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		if (alarm != null) {
			alarm.stopSound();
			alarm = null;
		}
	}
}
