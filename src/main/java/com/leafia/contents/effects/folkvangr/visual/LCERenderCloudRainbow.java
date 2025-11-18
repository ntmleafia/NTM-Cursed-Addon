package com.leafia.contents.effects.folkvangr.visual;

import com.hbm.entity.effect.EntityCloudFleija;
import com.hbm.entity.effect.EntityCloudFleijaRainbow;
import com.hbm.lib.RefStrings;
import com.hbm.render.loader.WaveFrontObjectVAO;
import com.leafia.AddonBase;
import com.leafia.overwrite_contents.interfaces.IMixinEntityCloudFleija;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.opengl.GL11;

import static com.leafia.init.ResourceInit.getVAO;

public class LCERenderCloudRainbow extends Render<EntityCloudFleijaRainbow> {

	private static final ResourceLocation blastTexture = AddonBase.solid_e;

	public float scale = 0;
	public float ring = 0;

	public static final IRenderFactory<EntityCloudFleijaRainbow> FACTORY = LCERenderCloudRainbow::new;

	protected LCERenderCloudRainbow(RenderManager renderManager) {
		super(renderManager);
		scale = 0;
	}
	
	@Override
	public void doRender(EntityCloudFleijaRainbow cloud, double x, double y, double z, float entityYaw,
			float partialTicks) {
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_CURRENT_BIT);
        GL11.glTranslated(x, y, z);
        GlStateManager.disableLighting();
        GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_TEXTURE_2D);

		float s = (float)(cloud.scale+(/*cloud.remoteTicks+*/partialTicks)*((IMixinEntityCloudFleija)cloud).getTickrate());
        GL11.glScalef(s,s,s);

		GL11.glColor3ub((byte)cloud.world.rand.nextInt(0x100), (byte)cloud.world.rand.nextInt(0x100), (byte)cloud.world.rand.nextInt(0x100));

        GL11.glScalef(0.5F, 0.5F, 0.5F);
        LCERenderCloudFleija.cloudFleija.renderAll();
        GL11.glScalef(1/0.5F, 1/0.5F, 1/0.5F);

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		
        for(float i = 0.6F; i <= 1F; i += 0.1F) {

    		GL11.glColor3ub((byte)cloud.world.rand.nextInt(0x100), (byte)cloud.world.rand.nextInt(0x100), (byte)cloud.world.rand.nextInt(0x100));
    		
            GL11.glScalef(i, i, i);
	        LCERenderCloudFleija.cloudFleija.renderAll();
            GL11.glScalef(1/i, 1/i, 1/i);
        }
        
		GL11.glDisable(GL11.GL_BLEND);
        GlStateManager.enableLighting();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_CULL_FACE);
        
        GL11.glPopAttrib();
        GL11.glPopMatrix();
	}

	@Override
	public void doRenderShadowAndFire(Entity entityIn, double x, double y, double z, float yaw, float partialTicks) {
	}
	
	@Override
	protected ResourceLocation getEntityTexture(EntityCloudFleijaRainbow entity) {
		return null;
	}

}
