package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.entity.effect.EntityNukeTorex;
import com.hbm.render.entity.effect.RenderTorex;
import com.leafia.overwrite_contents.interfaces.IMixinEntityNukeTorex;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = RenderTorex.class)
public abstract class MixinRenderTorex extends Render<EntityNukeTorex> {
	protected MixinRenderTorex(RenderManager renderManager) {
		super(renderManager);
	}
	@Redirect(method = "doRender(Lcom/hbm/entity/effect/EntityNukeTorex;DDDFF)V",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;translate(DDD)V"))
	public void onDoRender(double x,double y,double z,@Local EntityNukeTorex torex,@Local float partialTicks) {
		Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
		double d3 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks;
		double d4 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks;
		double d5 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks;
		IMixinEntityNukeTorex mixin = (IMixinEntityNukeTorex)torex;
		//GlStateManager.translate(mixin.getInitPosX()-d3,mixin.getInitPosY()-d4,mixin.getInitPosZ()-d5); ?????
	}
}
