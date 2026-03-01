package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.custom_hbm.sound.LCEAudioWrapper;
import com.hbm.lib.ForgeDirection;
import com.hbm.lib.ModDamageSource;
import com.hbm.packet.threading.ThreadedPacket;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.tileentity.machine.TileEntityMachineArcWelder;
import com.leafia.AddonBase;
import com.leafia.dev.LeafiaUtil;
import com.leafia.dev.optimization.LeafiaParticlePacket.FiaSpark;
import com.leafia.dev.optimization.LeafiaParticlePacket.FlashParticle;
import com.leafia.init.LeafiaSoundEvents;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = TileEntityMachineArcWelder.class)
public abstract class MixinTileEntityMachineArcWelder extends TileEntityMachineBase {
	@Shadow(remap = false)
	public int progress;

	public MixinTileEntityMachineArcWelder(int scount) {
		super(scount);
	}
	@Redirect(method = "update",at = @At(value = "INVOKE", target = "Lcom/hbm/handler/threading/PacketThreading;createAllAroundThreadedPacket(Lcom/hbm/packet/threading/ThreadedPacket;Lnet/minecraftforge/fml/common/network/NetworkRegistry$TargetPoint;)V",remap = false),require = 1)
	public void leafia$removeOGParticles(ThreadedPacket message,TargetPoint target) {
		// do nothing lol
	}
	@Unique LCEAudioWrapper leafia$client_sfx = null;
	@Unique boolean leafia$client_looptimer = false; // wait 1 tick before stopping sound to make sure the sound loops
	@Unique boolean leafia$isWet = false;
	@Inject(method = "update",at = @At(value = "TAIL"))
	public void leafia$onUpdate(CallbackInfo ci) {
		if (!world.isRemote) {
			if (world.getTotalWorldTime()%50 == 0) {
				leafia$isWet = false;
				if (world.isRainingAt(pos.up(2)))
					leafia$isWet = true;
				for (int xo = -2; xo <= 2; xo++) {
					for (int yo = 0; yo <= 2; yo++) {
						for (int zo = -2; zo <= 2; zo++) {
							if (world.getBlockState(pos.add(xo,yo,zo)).getMaterial().equals(Material.WATER))
								leafia$isWet = true;
						}
					}
				}
			}
			if (progress > 0) { // while active
				if (leafia$isWet) {
					float range = 2;
					List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos.getX() - range + 0.5, pos.getY() - range + 0.5, pos.getZ() - range + 0.5, pos.getX() + range + 0.5, pos.getY() + range + 0.5, pos.getZ() + range + 0.5));
					for (Entity entity : list)
						entity.attackEntityFrom(ModDamageSource.electricity,4);
					if (world.rand.nextInt(50) == 0)
						LeafiaUtil.spreadFire(world,pos,2);
				}
			}
		} else {
			if (progress > 0 || leafia$client_looptimer) {
				if (leafia$client_sfx == null) {
					leafia$client_sfx = AddonBase.proxy.getLoopedSoundStartStop(world,LeafiaSoundEvents.arc_welder,LeafiaSoundEvents.arc_welder_start,LeafiaSoundEvents.arc_welder_stop,SoundCategory.BLOCKS,pos.getX()+0.5f,pos.getY()+0.5f,pos.getZ()+0.5f,1.4f,1);
					leafia$client_sfx.startSound();
				}
				ForgeDirection dir = ForgeDirection.getOrientation(this.getBlockMetadata()-10);
				Vec3d vec = new Vec3d(
						pos.getX() + 0.5 - dir.offsetX * 0.5,
						pos.getY() + 1.25,
						pos.getZ() + 0.5 - dir.offsetZ * 0.5
				);
				Vec3d up = new Vec3d(0,1,0);
				if(world.getTotalWorldTime() % 2 == world.rand.nextInt(2)) {
					FiaSpark spark = new FiaSpark();
					spark.color = 0xFFEE80;
					spark.count = world.rand.nextInt(3)+1;
					spark.thickness = 0.014f;
					spark.speedMin = 0.25f;
					spark.speedMax = 0.5f;
					spark.emitLocal(vec,up);
				}
				if (world.getTotalWorldTime()%2 == 0 || world.rand.nextInt(3) == 0) {
					FlashParticle flash = new FlashParticle();
					flash.scale = world.rand.nextFloat()*0.02f+0.2f;
					flash.ticksIn = 2;
					flash.ticksOut = world.rand.nextInt(2)+2;
					flash.emitLocal(vec,up);
				}
			} else if (leafia$client_sfx != null) {
				leafia$client_sfx.stopSound();
				leafia$client_sfx = null;
			}
			leafia$client_looptimer = progress > 0;
		}
	}
	@Override
	public void invalidate() {
		if (leafia$client_sfx != null) {
			leafia$client_sfx.stopSound();
			leafia$client_sfx = null;
		}
		super.invalidate();
	}
	@Override
	public void onChunkUnload() {
		if (leafia$client_sfx != null) {
			leafia$client_sfx.stopSound();
			leafia$client_sfx = null;
		}
		super.onChunkUnload();
	}
}
