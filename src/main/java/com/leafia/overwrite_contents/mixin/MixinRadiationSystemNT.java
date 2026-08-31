package com.leafia.overwrite_contents.mixin;

import com.hbm.handler.ArmorUtil;
import com.hbm.handler.radiation.RadiationSystemNT;
import com.leafia.contents.AddonItems;
import com.leafia.dev.LeafiaDebug.Tracker.Action;
import com.leafia.dev.LeafiaDebug.Tracker.LeafiaTrackerPacket;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.llib.technical.FifthString;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RadiationSystemNT.class)
public class MixinRadiationSystemNT {
	/*@Mutable @Shadow(remap = false) @Final static double RAD_MAX;

	@Inject(method = "<clinit>",at = @At("TAIL"),require = 1,remap = false)
	private static void leafia$onClinit(CallbackInfo ci) {
		RAD_MAX = 25000;
	}*/ // welp that didn't do shit
	@Inject(method = "incrementRad",at = @At(value = "HEAD"),require = 1,remap = false)
	private static void leafia$onIncrementRad(WorldServer world,BlockPos pos,double amount,double max,CallbackInfo ci) {
		for (EntityPlayer plr : world.playerEntities) {
			if (ArmorUtil.checkArmorPiece(plr,AddonItems.radglasses,3)) {
				if (new Vec3d(plr.posX,plr.posY+plr.getEyeHeight(),plr.posZ).distanceTo(new Vec3d(pos).add(0.5,0.5,0.5)) < 256) {
					LeafiaTrackerPacket packet = new LeafiaTrackerPacket();
					packet.mode = Action.SHOW_BOX;
					packet.writer = (buf)->{
						buf.writeFloat(10);
						buf.writeInt(0xAAFF11);
						buf.writeByte((byte)1);
						buf.writeFifthString(new FifthString(String.format("+%01.1fRAD",amount)));
						buf.writeVec3i(pos);
					};
					LeafiaPacket._sendToClient(packet,plr);
				}
			}
		}
	}
}
