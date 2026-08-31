package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.custom_hbm.render.item.FancyMissingModelPerspective;
import com.hbm.main.client.NTMClientRegistry;
import com.hbm.render.item.TEISRBase;
import com.leafia.dev.render.IFMMPerspective;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.registry.IRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = NTMClientRegistry.class)
public class MixinNTMClientRegistry {
	@Inject(method = "wrapTeisrBinding",at = @At(value = "FIELD", target = "Lcom/hbm/render/item/TEISRBase;itemModel:Lnet/minecraft/client/renderer/block/model/IBakedModel;",shift = Shift.AFTER),remap = false,require = 1,cancellable = true)
	private static void leafia$onWrapTeisrBinding(@Coerce Object owned,IRegistry<ModelResourceLocation,IBakedModel> reg,CallbackInfo ci,@Local(type = TEISRBase.class, name = "teisr") TEISRBase teisr,@Local(type = ModelResourceLocation.class, name = "targetLocation") ModelResourceLocation targetLocation,@Local(type = IBakedModel.class, name = "model") IBakedModel model) {
		if (teisr instanceof IFMMPerspective fmm && fmm.useFMMPerspective(/*owned.item fuck off*/null)) {
			reg.putObject(targetLocation, new FancyMissingModelPerspective(teisr, model));
			ci.cancel();
		}
	}
}
