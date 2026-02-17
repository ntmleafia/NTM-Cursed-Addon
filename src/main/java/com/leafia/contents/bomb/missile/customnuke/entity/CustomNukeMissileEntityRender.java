package com.leafia.contents.bomb.missile.customnuke.entity;

import com.hbm.render.NTMRenderHelper;
import com.hbm.util.RenderUtil;
import com.leafia.contents.bomb.missile.AddonMissileItemRender;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.opengl.GL11;

public class CustomNukeMissileEntityRender extends Render<CustomNukeMissileEntity> {

	public static final IRenderFactory<CustomNukeMissileEntity> FACTORY = CustomNukeMissileEntityRender::new;

	protected CustomNukeMissileEntityRender(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(CustomNukeMissileEntity missile, double x, double y, double z, float entityYaw, float partialTicks) {
		GL11.glPushMatrix();
		boolean prevLighting = RenderUtil.isLightingEnabled();
		GlStateManager.enableLighting();
		double[] renderPos = NTMRenderHelper.getRenderPosFromMissile(missile, partialTicks);
		x = renderPos[0];
		y = renderPos[1];
		z = renderPos[2];
		GL11.glTranslated(x, y, z);
        GL11.glRotatef(missile.prevRotationYaw + (missile.rotationYaw - missile.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(missile.prevRotationPitch + (missile.rotationPitch - missile.prevRotationPitch) * partialTicks, 0.0F, 0.0F, 1.0F);
        //GL11.glScalef(2F, 2F, 2F);

        GL11.glDisable(GL11.GL_CULL_FACE);
        bindTexture(AddonMissileItemRender.neonc_tex);
        AddonMissileItemRender.neonc_mdl.renderAll();
        GL11.glEnable(GL11.GL_CULL_FACE);
		if (!prevLighting) GlStateManager.disableLighting();
		GL11.glPopMatrix();
	}
	
	@Override
	protected ResourceLocation getEntityTexture(CustomNukeMissileEntity entity) {
		return AddonMissileItemRender.neonc_tex;
	}
}
