package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.explosion.ExplosionNukeRayBatched;
import com.leafia.overwrite_contents.interfaces.IMixinExplosionRay;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ExplosionNukeRayBatched.class)
public class MixinExplosionNukeRayBatched implements IMixinExplosionRay {
	@Unique int lowestHeight = Integer.MAX_VALUE;
	@Override
	public int leafia$getLowestHeight() {
		return lowestHeight;
	}
	@Inject(method = "processChunkBlocks",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockToAir(Lnet/minecraft/util/math/BlockPos;)Z"),require = 1,remap = false)
	public void leafia$onProcessChunkBlocks(long start,int time,CallbackInfo ci,@Local(name = "pos") MutableBlockPos pos) {
		if (pos.getY() < lowestHeight)
			lowestHeight = pos.getY();
	}
}
