package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.explosion.ExplosionNukeRayParallelized;
import com.leafia.overwrite_contents.interfaces.IMixinExplosionRay;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExplosionNukeRayParallelized.class)
public class MixinExplosionNukeRayParallelized implements IMixinExplosionRay {
	@Unique
	int lowestHeight = Integer.MAX_VALUE;
	@Override
	public int leafia$getLowestHeight() {
		return lowestHeight;
	}
	// i give up
}
