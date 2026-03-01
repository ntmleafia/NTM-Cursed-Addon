package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.custom_hbm.sound.LCEAudioWrapper;
import com.hbm.lib.ForgeDirection;
import com.hbm.packet.threading.ThreadedPacket;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.tileentity.machine.TileEntityMachineSolderingStation;
import com.leafia.AddonBase;
import com.leafia.dev.optimization.LeafiaParticlePacket.FiaSpark;
import com.leafia.dev.optimization.LeafiaParticlePacket.FlashParticle;
import com.leafia.init.LeafiaSoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileEntityMachineSolderingStation.class)
public abstract class MixinTileEntityMachineSolderingStation extends TileEntityMachineBase {
	@Shadow(remap = false)
	public int progress;

	public MixinTileEntityMachineSolderingStation(int scount) {
		super(scount);
	}
	@Redirect(method = "update",at = @At(value = "INVOKE", target = "Lcom/hbm/handler/threading/PacketThreading;createAllAroundThreadedPacket(Lcom/hbm/packet/threading/ThreadedPacket;Lnet/minecraftforge/fml/common/network/NetworkRegistry$TargetPoint;)V",remap = false),require = 1)
	public void leafia$removeOGParticles(ThreadedPacket message,TargetPoint target) {
		// do nothing lol
	}
	@Unique
	LCEAudioWrapper leafia$client_sfx = null;
	@Unique boolean leafia$client_looptimer = false; // wait 1 tick before stopping sound to make sure the sound loops
	@Inject(method = "update",at = @At(value = "TAIL"))
	public void leafia$onUpdate(CallbackInfo ci) {
		if (world.isRemote) {
			if (progress > 0 || leafia$client_looptimer) {
				if (leafia$client_sfx == null) {
					leafia$client_sfx = AddonBase.proxy.getLoopedSound(LeafiaSoundEvents.crafting_tech1_part,SoundCategory.BLOCKS,pos.getX()+0.5f,pos.getY()+0.5f,pos.getZ()+0.5f,1,1);
					leafia$client_sfx.startSound();
				}
				ForgeDirection dir = ForgeDirection.getOrientation(this.getBlockMetadata()-10);
				ForgeDirection rot = dir.getRotation(ForgeDirection.UP);
				Vec3d vec = new Vec3d(
						pos.getX() + 0.5 - dir.offsetX * 0.5 + rot.offsetX * 0.5,
						pos.getY() + 1.125,
						pos.getZ() + 0.5 - dir.offsetZ * 0.5 + rot.offsetZ * 0.5
				);
				Vec3d up = new Vec3d(0,1,0);
				if(world.getTotalWorldTime() % 4 == world.rand.nextInt(4)) {
					FiaSpark spark = new FiaSpark();
					spark.color = 0xFFEE80;
					spark.count = world.rand.nextInt(3)+1;
					spark.thickness = 0.014f;
					spark.emitLocal(vec,up);
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
