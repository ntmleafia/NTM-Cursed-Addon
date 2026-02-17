package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.inventory.recipes.loader.SerializableRecipe;
import com.leafia.AddonBase;
import com.leafia.init.AddonSerializableRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SerializableRecipe.class,remap = false)
public class MixinSerializableRecipe {
	@Inject(method = "registerAllHandlers",at = @At(value = "TAIL"),require = 1)
	private static void onRegisterAllHandlers(CallbackInfo ci) {
		AddonSerializableRecipe.onRegisterAllHandlers();
	}

	@Inject(method = "initialize",at = @At(value = "TAIL"),require = 1)
	private static void onInitialize(CallbackInfo ci) {
		AddonBase.registerSerializable();
	}
}
