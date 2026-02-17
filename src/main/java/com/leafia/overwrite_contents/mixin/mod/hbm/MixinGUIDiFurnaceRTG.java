package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.inventory.gui.GUIDiFurnaceRTG;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = GUIDiFurnaceRTG.class)
public class MixinGUIDiFurnaceRTG {
	@Redirect(method = "<clinit>",at = @At(value = "NEW", target = "(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/util/ResourceLocation;"),require = 1,remap = false)
	private static ResourceLocation onClInit(String modid,String path) {
		return new ResourceLocation("leafia","textures/gui/processing/gui_rtg_difurnace.png");
	}
}
