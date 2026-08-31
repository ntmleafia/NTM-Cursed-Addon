package com.leafia.contents.machines.reactors.lftr.processing.separator;

import com.hbm.render.loader.WaveFrontObjectVAO;
import com.leafia.dev.render.LeafiaItemRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import static com.leafia.init.ResourceInit.getVAO;

public class SaltSeparatorRender extends TileEntitySpecialRenderer<SaltSeparatorTE> {
	public static final WaveFrontObjectVAO mdl = getVAO(new ResourceLocation("leafia","models/leafia/lftr/vacuum_distill.obj"));
	public static final ResourceLocation tex = new ResourceLocation("leafia","textures/models/leafia/lftr/vacuum_distill.png");
	public static class SaltSeparatorItemRender extends LeafiaItemRenderer {
		@Override
		protected double _sizeReference() {
			return 10;
		}
		@Override
		protected double _itemYoffset() {
			return -0.165;
		}
		@Override
		protected ResourceLocation __getTexture() {
			return tex;
		}
		@Override
		protected WaveFrontObjectVAO __getModel() {
			return mdl;
		}
	}

	@Override
	public boolean isGlobalRenderer(SaltSeparatorTE te) {
		return true;
	}

	@Override
	public void render(SaltSeparatorTE te,double x,double y,double z,float partialTicks,int destroyStage,float alpha) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x + 0.5D, y, z + 0.5D);
		GlStateManager.enableLighting();

		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		bindTexture(tex);
		mdl.renderAll();
		GlStateManager.shadeModel(GL11.GL_FLAT);

		GlStateManager.popMatrix();
	}
}