package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.entity.logic.EntityNukeExplosionMK3;
import com.hbm.entity.logic.IChunkLoader;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityNukeExplosionMK3.class)
public abstract class MixinEntityNukeExplosionMK3 extends Entity implements IChunkLoader {
	public MixinEntityNukeExplosionMK3(World worldIn) {
		super(worldIn);
	}

	@Inject(method = "onUpdate", at = @At("HEAD"))
	private void onOnUpdate(CallbackInfo ci) {

	}
}
