package com.leafia.contents.machines.powercores.ams.emitter;

import com.hbm.inventory.fluid.Fluids;
import com.hbm.main.ResourceManager;
import com.hbm.render.NTMRenderHelper;
import com.hbm.render.loader.WaveFrontObjectVAO;
import com.leafia.contents.machines.powercores.ams.base.AMSBaseTE;
import com.leafia.dev.LeafiaItemRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.Random;

import static com.leafia.init.ResourceInit.getIntegrated;
import static com.leafia.init.ResourceInit.getVAO;

public class AMSEmitterRender extends TileEntitySpecialRenderer<AMSEmitterTE> {
	public static final WaveFrontObjectVAO mdl0 = getVAO(getIntegrated("ams/ams_emitter.obj"));
	public static final WaveFrontObjectVAO mdld = getVAO(getIntegrated("ams/ams_emitter_destroyed.obj"));
	public static final ResourceLocation texd = getIntegrated("ams/ams_destroyed.png");
	public static final ResourceLocation tex0 = getIntegrated("ams/ams_emitter.png");
	public static final ResourceLocation tex1 = getIntegrated("ams/balefire/ams_emitter.png");
	Random rand = new Random();

	public static class AMSEmitterItemRender extends LeafiaItemRenderer {
		@Override
		protected double _sizeReference() {
			return 9.7;
		}
		@Override
		protected double _itemYoffset() {
			return -0.2;
		}
		@Override
		protected ResourceLocation __getTexture() {
			return tex0;
		}
		@Override
		protected WaveFrontObjectVAO __getModel() {
			return mdl0;
		}
	}

	@Override
	public void render(AMSEmitterTE te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {

		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5D, y, z + 0.5D);
		GlStateManager.enableLighting();
		GlStateManager.disableCull();
		GL11.glRotatef(180, 0F, 1F, 0F);


		if(te.locked)
			bindTexture(texd);
		else {
			AMSBaseTE base = null;
			if (te.getWorld().getTileEntity(te.getPos().add(0, -9, 0)) instanceof AMSBaseTE b)
				base = b;
			if (base != null && ((base.tanks[2].getTankType() == Fluids.ASCHRAB && base.tanks[3].getTankType() == Fluids.BALEFIRE) || (base.tanks[2].getTankType() == Fluids.BALEFIRE && base.tanks[3].getTankType() == Fluids.ASCHRAB)))
				bindTexture(tex1);
			else
				bindTexture(tex0);
		}

		if(te.locked)
			mdld.renderAll();
		else
			mdl0.renderAll();

		GL11.glPopMatrix();
		renderTileEntityAt2(te, x, y, z, partialTicks);
		GlStateManager.enableCull();
	}

	public void renderTileEntityAt2(TileEntity tileEntity,double x,double y,double z,float f)
	{
		float radius = 0.04F;
		int distance = 1;
		int layers = 3;

		GL11.glPushMatrix();
		GlStateManager.disableTexture2D();
		GlStateManager.disableCull();
		GlStateManager.enableBlend();
		GlStateManager.enableLighting();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GL11.glTranslatef((float) x + 0.5F, (float) y - 7, (float) z + 0.5F);

		AMSEmitterTE emitter = (AMSEmitterTE)tileEntity;

		if(emitter.getWorld().getTileEntity(emitter.getPos().add(0, -9, 0)) instanceof AMSBaseTE && !emitter.locked) {

			NTMRenderHelper.startDrawingColored(GL11.GL_QUADS);

			if(emitter.efficiency > 0) {

				double lastPosX = 0;
				double lastPosZ = 0;



				for(int i = 7; i > 0; i -= distance) {

					double posX = rand.nextDouble() - 0.5;
					double posZ = rand.nextDouble() - 0.5;

					for(int j = 1; j <= layers; j++) {

						NTMRenderHelper.addVertexColor(lastPosX + (radius * j), i, lastPosZ + (radius * j), 1, 0.5F, 0, 1f);
						NTMRenderHelper.addVertexColor(lastPosX + (radius * j), i, lastPosZ - (radius * j), 1, 0.5F, 0, 1f);
						NTMRenderHelper.addVertexColor(posX + (radius * j), i - distance, posZ - (radius * j), 1, 0.5F, 0, 1f);
						NTMRenderHelper.addVertexColor(posX + (radius * j), i - distance, posZ + (radius * j), 1, 0.5F, 0, 1f);

						NTMRenderHelper.addVertexColor(lastPosX - (radius * j), i, lastPosZ + (radius * j), 1, 0.5F, 0, 1f);
						NTMRenderHelper.addVertexColor(lastPosX - (radius * j), i, lastPosZ - (radius * j), 1, 0.5F, 0, 1f);
						NTMRenderHelper.addVertexColor(posX - (radius * j), i - distance, posZ - (radius * j), 1, 0.5F, 0, 1f);
						NTMRenderHelper.addVertexColor(posX - (radius * j), i - distance, posZ + (radius * j), 1, 0.5F, 0, 1f);

						NTMRenderHelper.addVertexColor(lastPosX + (radius * j), i, lastPosZ + (radius * j), 1, 0.5F, 0, 1f);
						NTMRenderHelper.addVertexColor(lastPosX - (radius * j), i, lastPosZ + (radius * j), 1, 0.5F, 0, 1f);
						NTMRenderHelper.addVertexColor(posX - (radius * j), i - distance, posZ + (radius * j), 1, 0.5F, 0, 1f);
						NTMRenderHelper.addVertexColor(posX + (radius * j), i - distance, posZ + (radius * j), 1, 0.5F, 0, 1f);

						NTMRenderHelper.addVertexColor(lastPosX + (radius * j), i, lastPosZ - (radius * j), 1, 0.5F, 0, 1f);
						NTMRenderHelper.addVertexColor(lastPosX - (radius * j), i, lastPosZ - (radius * j), 1, 0.5F, 0, 1f);
						NTMRenderHelper.addVertexColor(posX - (radius * j), i - distance, posZ - (radius * j), 1, 0.5F, 0, 1f);
						NTMRenderHelper.addVertexColor(posX + (radius * j), i - distance, posZ - (radius * j), 1, 0.5F, 0, 1f);
					}

					lastPosX = posX;
					lastPosZ = posZ;
				}
			}

			for(int j = 1; j <= 2; j++) {
				NTMRenderHelper.addVertexColor(0 + (radius * j), 7, 0 + (radius * j), 1, 1, 0, 1f);
				NTMRenderHelper.addVertexColor(0 + (radius * j), 7, 0 - (radius * j), 1, 1, 0, 1f);
				NTMRenderHelper.addVertexColor(0 + (radius * j), 0, 0 - (radius * j), 1, 1, 0, 1f);
				NTMRenderHelper.addVertexColor(0 + (radius * j), 0, 0 + (radius * j), 1, 1, 0, 1f);

				NTMRenderHelper.addVertexColor(0 - (radius * j), 7, 0 + (radius * j), 1, 1, 0, 1f);
				NTMRenderHelper.addVertexColor(0 - (radius * j), 7, 0 - (radius * j), 1, 1, 0, 1f);
				NTMRenderHelper.addVertexColor(0 - (radius * j), 0, 0 - (radius * j), 1, 1, 0, 1f);
				NTMRenderHelper.addVertexColor(0 - (radius * j), 0, 0 + (radius * j), 1, 1, 0, 1f);

				NTMRenderHelper.addVertexColor(0 + (radius * j), 7, 0 + (radius * j), 1, 1, 0, 1f);
				NTMRenderHelper.addVertexColor(0 - (radius * j), 7, 0 + (radius * j), 1, 1, 0, 1f);
				NTMRenderHelper.addVertexColor(0 - (radius * j), 0, 0 + (radius * j), 1, 1, 0, 1f);
				NTMRenderHelper.addVertexColor(0 + (radius * j), 0, 0 + (radius * j), 1, 1, 0, 1f);

				NTMRenderHelper.addVertexColor(0 + (radius * j), 7, 0 - (radius * j), 1, 1, 0, 1f);
				NTMRenderHelper.addVertexColor(0 - (radius * j), 7, 0 - (radius * j), 1, 1, 0, 1f);
				NTMRenderHelper.addVertexColor(0 - (radius * j), 0, 0 - (radius * j), 1, 1, 0, 1f);
				NTMRenderHelper.addVertexColor(0 + (radius * j), 0, 0 - (radius * j), 1, 1, 0, 1f);
			}
			NTMRenderHelper.draw();
		}


		GlStateManager.disableBlend();
		GlStateManager.enableTexture2D();
		GL11.glPopMatrix();
	}
}
