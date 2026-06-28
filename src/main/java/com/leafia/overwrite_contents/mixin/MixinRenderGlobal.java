package com.leafia.overwrite_contents.mixin;

import com.leafia.contents.worldgen.biomes.artificial.DigammaCrater;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.MinecraftForgeClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderGlobal.class)
public class MixinRenderGlobal {
    @Inject(method = "renderEntities", at = @At("HEAD"))
    public void leafia$renderNull(Entity renderViewEntity, ICamera camera, float partialTicks, CallbackInfo ci) {
        for (int i = 0; i < DigammaCrater.NULL_LIST.size(); ++i) {
            DigammaCrater.NullEntity nullEntity = DigammaCrater.NULL_LIST.get(i);
            if (nullEntity.shouldRenderInPass(MinecraftForgeClient.getRenderPass())) {
                Minecraft.getMinecraft().getRenderManager().renderEntityStatic(nullEntity, partialTicks, false);
            }
        }
    }
}
