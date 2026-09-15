package com.leafia.contents.effects;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.jetbrains.annotations.Nullable;

public class RenderNothing extends Render<Entity> {
	public static final IRenderFactory<Entity> FACTORY = RenderNothing::new;
	protected RenderNothing(RenderManager renderManager) {
		super(renderManager);
	}
	@Override
	public void doRender(Entity entity,double x,double y,double z,float entityYaw,float partialTicks) {}
	@Override
	public void doRenderShadowAndFire(Entity entityIn, double x, double y, double z, float yaw, float partialTicks) {}
	@Override
	protected @Nullable ResourceLocation getEntityTexture(Entity entity) {
		return null;
	}
}
