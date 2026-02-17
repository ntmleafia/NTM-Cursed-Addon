package com.leafia.contents.machines.powercores.ams.stabilizer;

import com.hbm.inventory.fluid.Fluids;
import com.hbm.render.NTMRenderHelper;
import com.hbm.render.loader.WaveFrontObjectVAO;
import com.leafia.contents.machines.powercores.ams.base.AMSBaseTE;
import com.leafia.dev.LeafiaItemRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import static com.leafia.init.ResourceInit.getIntegrated;
import static com.leafia.init.ResourceInit.getVAO;

public class AMSStabilizerRender extends TileEntitySpecialRenderer<AMSStabilizerTE> {
	public static final WaveFrontObjectVAO mdl0 = getVAO(getIntegrated("ams/ams_limiter.obj"));
	public static final WaveFrontObjectVAO mdld = getVAO(getIntegrated("ams/ams_limiter_destroyed.obj"));
	public static final ResourceLocation texd = getIntegrated("ams/ams_destroyed.png");
	public static final ResourceLocation tex0 = getIntegrated("ams/ams_limiter.png");
	public static final ResourceLocation tex1 = getIntegrated("ams/balefire/ams_limiter.png");
	public static class AMSStabilizerItemRender extends LeafiaItemRenderer {
		@Override
		protected double _sizeReference() {
			return 10;
		}
		@Override
		protected double _itemYoffset() {
			return -0.15;
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
	public void render(AMSStabilizerTE te,double x,double y,double z,float partialTicks,int destroyStage,float alpha) {
		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5D, y, z + 0.5D);
		GlStateManager.enableLighting();
		GlStateManager.disableCull();
		GL11.glRotatef(180, 0F, 1F, 0F);
		GL11.glRotatef(-90, 0F, 1F, 0F);
		switch(AMSStabilizerTE.rotateShitfuck(te.getBlockMetadata())-10)
		{
			case 2:
				GL11.glRotatef(90, 0F, 1F, 0F); break;
			case 4:
				GL11.glRotatef(180, 0F, 1F, 0F); break;
			case 3:
				GL11.glRotatef(270, 0F, 1F, 0F); break;
			case 5:
				GL11.glRotatef(0, 0F, 1F, 0F); break;
		}
		if(te.locked)
			bindTexture(texd);
		else {
			int meta = AMSStabilizerTE.rotateShitfuck(te.getBlockMetadata())-10;
			AMSBaseTE base = null;
			if(meta == 2 && te.getWorld().getTileEntity(te.getPos().add(0, 0, -6)) instanceof AMSBaseTE)
				base = (AMSBaseTE)te.getWorld().getTileEntity(te.getPos().add(0, 0, -6));
			if(meta == 3 && te.getWorld().getTileEntity(te.getPos().add(0, 0, 6)) instanceof AMSBaseTE)
				base = (AMSBaseTE)te.getWorld().getTileEntity(te.getPos().add(0, 0, 6));
			if(meta == 4 && te.getWorld().getTileEntity(te.getPos().add(-6, 0, 0)) instanceof AMSBaseTE)
				base = (AMSBaseTE)te.getWorld().getTileEntity(te.getPos().add(-6, 0, 0));
			if(meta == 5 && te.getWorld().getTileEntity(te.getPos().add(6, 0, 0)) instanceof AMSBaseTE)
				base = (AMSBaseTE)te.getWorld().getTileEntity(te.getPos().add(6, 0, 0));
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
		// i now understand why bob hates the AMS so much.
		// Nothing stops me though, it's too late.
		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5D, y, z + 0.5D);
		GlStateManager.enableLighting();
		GlStateManager.disableCull();
		GL11.glRotatef(180, 0F, 1F, 0F);
		GL11.glRotatef(-90, 0F, 1F, 0F);

		switch(AMSStabilizerTE.rotateShitfuck(tileEntity.getBlockMetadata())-10)
		{
			case 2:
				GL11.glRotatef(90, 0F, 1F, 0F); break;
			case 4:
				GL11.glRotatef(180, 0F, 1F, 0F); break;
			case 3:
				GL11.glRotatef(270, 0F, 1F, 0F); break;
			case 5:
				GL11.glRotatef(0, 0F, 1F, 0F); break;
		}

		// bindTexture(ResourceManager.universal);

		AMSStabilizerTE limiter = (AMSStabilizerTE)tileEntity;

		int meta = AMSStabilizerTE.rotateShitfuck(tileEntity.getBlockMetadata())-10;
		boolean flag = false;
		double maxSize = 5;
		double minSize = 0.5;
		if(meta == 2 && tileEntity.getWorld().getTileEntity(tileEntity.getPos().add(0, 0, -6)) instanceof AMSBaseTE && !limiter.locked) {
			flag = true;
			AMSBaseTE base = (AMSBaseTE)tileEntity.getWorld().getTileEntity(tileEntity.getPos().add(0, 0, -6));
			maxSize += ((((double)base.tanks[2].getFill()) / ((double)base.tanks[2].getMaxFill())) + (((double)base.tanks[3].getFill()) / ((double)base.tanks[3].getMaxFill()))) * ((maxSize - minSize) / 2);
		}
		if(meta == 3 && tileEntity.getWorld().getTileEntity(tileEntity.getPos().add(0, 0, 6)) instanceof AMSBaseTE && !limiter.locked) {
			flag = true;
			AMSBaseTE base = (AMSBaseTE)tileEntity.getWorld().getTileEntity(tileEntity.getPos().add(0, 0, 6));
			maxSize += ((((double)base.tanks[2].getFill()) / ((double)base.tanks[2].getMaxFill())) + (((double)base.tanks[3].getFill()) / ((double)base.tanks[3].getMaxFill()))) * ((maxSize - minSize) / 2);
		}
		if(meta == 4 && tileEntity.getWorld().getTileEntity(tileEntity.getPos().add(-6, 0, 0)) instanceof AMSBaseTE && !limiter.locked) {
			flag = true;
			AMSBaseTE base = (AMSBaseTE)tileEntity.getWorld().getTileEntity(tileEntity.getPos().add(-6, 0, 0));
			maxSize += ((((double)base.tanks[2].getFill()) / ((double)base.tanks[2].getMaxFill())) + (((double)base.tanks[3].getFill()) / ((double)base.tanks[3].getMaxFill()))) * ((maxSize - minSize) / 2);
		}
		if(meta == 5 && tileEntity.getWorld().getTileEntity(tileEntity.getPos().add(6, 0, 0)) instanceof AMSBaseTE && !limiter.locked) {
			flag = true;
			AMSBaseTE base = (AMSBaseTE)tileEntity.getWorld().getTileEntity(tileEntity.getPos().add(6, 0, 0));
			maxSize += ((((double)base.tanks[2].getFill()) / ((double)base.tanks[2].getMaxFill())) + (((double)base.tanks[3].getFill()) / ((double)base.tanks[3].getMaxFill()))) * ((maxSize - minSize) / 2);
		}

		if(flag) {
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);

			GL11.glRotatef(-90, 0F, 1F, 0F);

			double posX = 0;
			double posY = 0;
			double posZ = 0;
			double length = 4;
			double radius = 0.12;
			GL11.glTranslated(2.5, 5.5, 0);

			net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
			GlStateManager.disableTexture2D();
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
			GlStateManager.disableAlpha();
			GlStateManager.disableCull();
			GlStateManager.depthMask(false);

			NTMRenderHelper.startDrawingColored(GL11.GL_QUADS);

			NTMRenderHelper.addVertexColor(posX + length, posY - radius, posZ - radius, 0.408F - 0.175F, 0.686F - 0.175F, 0.686F - 0.175F, 0f);
			NTMRenderHelper.addVertexColor(posX + length, posY - radius, posZ + radius, 0.408F - 0.175F, 0.686F - 0.175F, 0.686F - 0.175F, 0f);
			NTMRenderHelper.addVertexColor(posX, posY - radius, posZ + radius, 0.408F - 0.175F, 0.686F - 0.175F, 0.686F - 0.175F, 1);
			NTMRenderHelper.addVertexColor(posX, posY - radius, posZ - radius, 0.408F - 0.175F, 0.686F - 0.175F, 0.686F - 0.175F, 1);

			NTMRenderHelper.addVertexColor(posX + length, posY + radius, posZ + radius, 0.408F - 0.175F, 0.686F - 0.175F, 0.686F - 0.175F, 0f);
			NTMRenderHelper.addVertexColor(posX + length, posY + radius, posZ - radius, 0.408F - 0.175F, 0.686F - 0.175F, 0.686F - 0.175F, 0f);
			NTMRenderHelper.addVertexColor(posX, posY + radius, posZ - radius, 0.408F - 0.175F, 0.686F - 0.175F, 0.686F - 0.175F, 1);
			NTMRenderHelper.addVertexColor(posX, posY + radius, posZ + radius, 0.408F - 0.175F, 0.686F - 0.175F, 0.686F - 0.175F, 1);

			NTMRenderHelper.addVertexColor(posX + length, posY - radius, posZ - radius, 0.408F - 0.175F, 0.686F - 0.175F, 0.686F - 0.175F, 0f);
			NTMRenderHelper.addVertexColor(posX + length, posY + radius, posZ - radius, 0.408F - 0.175F, 0.686F - 0.175F, 0.686F - 0.175F, 0f);
			NTMRenderHelper.addVertexColor(posX, posY + radius, posZ - radius, 0.408F - 0.175F, 0.686F - 0.175F, 0.686F - 0.175F, 1);
			NTMRenderHelper.addVertexColor(posX, posY - radius, posZ - radius, 0.408F - 0.175F, 0.686F - 0.175F, 0.686F - 0.175F, 1);

			NTMRenderHelper.addVertexColor(posX + length, posY - radius, posZ + radius, 0.408F - 0.175F, 0.686F - 0.175F, 0.686F - 0.175F, 0f);
			NTMRenderHelper.addVertexColor(posX + length, posY + radius, posZ + radius, 0.408F - 0.175F, 0.686F - 0.175F, 0.686F - 0.175F, 0f);
			NTMRenderHelper.addVertexColor(posX, posY + radius, posZ + radius, 0.408F - 0.175F, 0.686F - 0.175F, 0.686F - 0.175F, 1);
			NTMRenderHelper.addVertexColor(posX, posY - radius, posZ + radius, 0.408F - 0.175F, 0.686F - 0.175F, 0.686F - 0.175F, 1);

			if(limiter.efficiency > 0) {

				radius *= 2;
				net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
				GlStateManager.disableTexture2D();
				GlStateManager.shadeModel(GL11.GL_SMOOTH);
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
				GlStateManager.disableAlpha();
				GlStateManager.disableCull();
				GlStateManager.depthMask(false);

				NTMRenderHelper.addVertexColor(posX + length, posY - radius, posZ - radius, 0.408F - 0.175F, 0.686F - 0.175F, 0.686F - 0.175F, 0f);
				NTMRenderHelper.addVertexColor(posX + length, posY - radius, posZ + radius, 0.408F - 0.175F, 0.686F - 0.175F, 0.686F - 0.175F, 0f);
				NTMRenderHelper.addVertexColor(posX, posY - radius, posZ + radius, 0.408F - 0.175F, 0.686F - 0.175F, 0.686F - 0.175F, 1);
				NTMRenderHelper.addVertexColor(posX, posY - radius, posZ - radius, 0.408F - 0.175F, 0.686F - 0.175F, 0.686F - 0.175F, 1);

				NTMRenderHelper.addVertexColor(posX + length, posY + radius, posZ + radius, 0.408F - 0.175F, 0.686F - 0.175F, 0.686F - 0.175F, 0f);
				NTMRenderHelper.addVertexColor(posX + length, posY + radius, posZ - radius, 0.408F - 0.175F, 0.686F - 0.175F, 0.686F - 0.175F, 0f);
				NTMRenderHelper.addVertexColor(posX, posY + radius, posZ - radius, 0.408F - 0.175F, 0.686F - 0.175F, 0.686F - 0.175F, 1);
				NTMRenderHelper.addVertexColor(posX, posY + radius, posZ + radius, 0.408F - 0.175F, 0.686F - 0.175F, 0.686F - 0.175F, 1);

				NTMRenderHelper.addVertexColor(posX + length, posY - radius, posZ - radius, 0.408F - 0.175F, 0.686F - 0.175F, 0.686F - 0.175F, 0f);
				NTMRenderHelper.addVertexColor(posX + length, posY + radius, posZ - radius, 0.408F - 0.175F, 0.686F - 0.175F, 0.686F - 0.175F, 0f);
				NTMRenderHelper.addVertexColor(posX, posY + radius, posZ - radius, 0.408F - 0.175F, 0.686F - 0.175F, 0.686F - 0.175F, 1);
				NTMRenderHelper.addVertexColor(posX, posY - radius, posZ - radius, 0.408F - 0.175F, 0.686F - 0.175F, 0.686F - 0.175F, 1);

				NTMRenderHelper.addVertexColor(posX + length, posY - radius, posZ + radius, 0.408F - 0.175F, 0.686F - 0.175F, 0.686F - 0.175F, 0f);
				NTMRenderHelper.addVertexColor(posX + length, posY + radius, posZ + radius, 0.408F - 0.175F, 0.686F - 0.175F, 0.686F - 0.175F, 0f);
				NTMRenderHelper.addVertexColor(posX, posY + radius, posZ + radius, 0.408F - 0.175F, 0.686F - 0.175F, 0.686F - 0.175F, 1);
				NTMRenderHelper.addVertexColor(posX, posY - radius, posZ + radius, 0.408F - 0.175F, 0.686F - 0.175F, 0.686F - 0.175F, 1);
			}

			NTMRenderHelper.draw();

			GlStateManager.disableBlend();
			GlStateManager.enableAlpha();
			GlStateManager.shadeModel(GL11.GL_FLAT);
			net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
			GlStateManager.depthMask(true);
			GlStateManager.enableCull();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.enableTexture2D();
		}

		GL11.glPopMatrix();
	}
}
