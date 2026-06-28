package com.leafia.overwrite_contents.mixin;

import com.leafia.contents.worldgen.biomes.artificial.DigammaCrater;
import com.leafia.eventbuses.LeafiaClientListener.HandlerClient;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Inject(method = "dispatchKeypresses", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ScreenShotHelper;saveScreenshot(Ljava/io/File;IILnet/minecraft/client/shader/Framebuffer;)Lnet/minecraft/util/text/ITextComponent;"), cancellable = true)
    private void leafia$interceptScreenshot(CallbackInfo ci) {
        DigammaCrater.NULL_LIST.clear();
        HandlerClient.screenshot = true;
        ci.cancel();
    }
}
