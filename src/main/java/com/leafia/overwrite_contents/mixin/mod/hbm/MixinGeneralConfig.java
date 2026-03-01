package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.config.GeneralConfig;
import com.leafia.settings.AddonConfig.ConfigOverrides;
import net.minecraftforge.common.config.Configuration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GeneralConfig.class)
public class MixinGeneralConfig {
	@Inject(method = "loadFromConfig",at = @At(value = "TAIL"),require = 1,remap = false)
	private static void leafia$onLoadFromConfig(Configuration config,CallbackInfo ci) {
		ConfigOverrides.applyGeneralConfig();
	}
}
