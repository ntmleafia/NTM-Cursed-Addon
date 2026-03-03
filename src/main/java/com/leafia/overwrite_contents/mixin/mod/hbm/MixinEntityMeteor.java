package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.config.GeneralConfig;
import com.hbm.entity.projectile.EntityMeteor;
import com.hbm.handler.threading.PacketThreading;
import com.hbm.world.Meteorite;
import com.leafia.CommandLeaf;
import com.leafia.contents.debug.explosion_test.DebugBoomBlock;
import com.leafia.contents.miscellanous.diverter.DiverterBlock;
import com.leafia.dev.custompacket.LeafiaCustomPacket;
import com.leafia.overwrite_contents.interfaces.IMixinEntityMeteor;
import com.leafia.settings.AddonConfig;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(value = EntityMeteor.class)
public abstract class MixinEntityMeteor extends Entity implements IMixinEntityMeteor {
	@Shadow(remap = false)
	public boolean safe;

	public MixinEntityMeteor(World worldIn) {
		super(worldIn);
	}
	@Unique double leafia$divertAngle = world.rand.nextDouble()*Math.PI*2;
	@Inject(method = "onUpdate",at = @At(value = "HEAD"),cancellable = true)
	private void leafia$divert(CallbackInfo ci) {
		if (!world.isRemote) {
			boolean moved = false;
			Vec3d oldPos = new Vec3d(posX,posY,posZ);
			while (true) {
				int cx = (int) (posX/16);
				int cz = (int) (posZ/16);
				if (DiverterBlock.isProtected(world,cx,cz)) {
					double divertX = Math.cos(leafia$divertAngle);
					double divertZ = Math.sin(leafia$divertAngle);
					moved = true;
					setPositionAndUpdate(
							posX+divertX*AddonConfig.meteorDiverterProtectionRadius*16,
							posY,
							posZ+divertZ*AddonConfig.meteorDiverterProtectionRadius*16
					);
				} else
					break;
			}
			if (moved) {
				// i will destroy you into pieces whoever wrote the shitty entity
				// rendering that keeps making shit go back and forth
				this.setDead();
				EntityMeteor fuck = new EntityMeteor(world);
				fuck.safe = this.safe;
				fuck.setPosition(posX,posY,posZ);
				fuck.motionX = motionX;
				fuck.motionY = motionY;
				fuck.motionZ = motionZ;
				world.spawnEntity(fuck);
				ci.cancel();
				return;
			} //seriously fuck it
			/*
			MeteorSyncPacket sync = new MeteorSyncPacket();
			sync.entity = this;
			sync.x = posX;
			sync.z = posZ;
			LeafiaCustomPacket.__start(sync).__sendToAllAround(
					world.provider.getDimension(),
					oldPos,2000
			);*/
			if (world.getTotalWorldTime()%5 == 0) {
				PacketThreading.createSendToAllTrackingThreadedPacket(
						new CommandLeaf.ShakecamPacket(new String[]{
								"type=smooth",
								"preset=RUPTURE",
								"blurDulling*2",
								"duration/4",
								"speed/2",
								"intensity/4",
								"range=200"
						}).setPos(new BlockPos(posX,posY,posZ)),
						new NetworkRegistry.TargetPoint(world.provider.getDimension(),posX,posY,posZ,250)
				);
			}
		}
	}
	@Redirect(method = "onUpdate",at = @At(value = "INVOKE", target = "Lcom/hbm/world/Meteorite;generate(Lnet/minecraft/world/World;Ljava/util/Random;IIIZZZ)V",remap = false),require = 1)
	private void leafia$onSpawnMeteor(Meteorite instance,World world,Random rand,int x,int y,int z,boolean safe,boolean special,boolean damaging) {
		if (!safe && AddonConfig.enableMeteorCraters)
			y-=2;
		instance.generate(world,rand,x,y,z,safe,special,damaging);
	}
	@Redirect(method = "onUpdate",at = @At(value = "FIELD", target = "Lcom/hbm/config/GeneralConfig;enableMeteorTails:Z",ordinal = 0,remap = false),require = 1)
	private boolean leafia$onOnUpdate() {
		if (!AddonConfig.enableMeteorCraters) return GeneralConfig.enableMeteorTails;
		if (GeneralConfig.enableMeteorTails) {
			DebugBoomBlock.createMeteorExplosionEffect(world,posX,posY,posZ);
			DebugBoomBlock.createMeteorCrater(world,new BlockPos(posX,posY,posZ));
		}
		return false;
	}
}
