package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.render.item.ItemRenderMissileGeneric;
import com.leafia.contents.bomb.missile.AddonMissileItemRender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ItemRenderMissileGeneric.class)
public class MixinItemRenderMissileGeneric {
	@Inject(method = "init",at = @At(value = "TAIL"),require = 1,remap = false)
	private static void onInit(CallbackInfo ci) {
		AddonMissileItemRender.init();
	}
}
