package com.leafia.contents.building.broof;

import com.hbm.render.loader.WaveFrontObjectVAO;
import com.leafia.dev.LeafiaItemRenderer;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import static com.leafia.init.ResourceInit.getIntegrated;
import static com.leafia.init.ResourceInit.getVAO;

public class BroofRender extends TileEntitySpecialRenderer<BroofTE> {
	public static final WaveFrontObjectVAO mdl = getVAO(getIntegrated("broof/broof.obj"));
	public static final ResourceLocation tex = getIntegrated("broof/broof.png");
	public static class BroofItemRender extends LeafiaItemRenderer {
		@Override
		protected double _sizeReference() {
			return 5.9;
		}
		@Override
		protected double _itemYoffset() {
			return 0.03;
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
	public void render(BroofTE te,double x,double y,double z,float partialTicks,int destroyStage,float alpha) {
		LeafiaGls.pushMatrix();
		LeafiaGls.translate(x+0.5,y,z+0.5);
		bindTexture(tex);
		LeafiaGls.enableBlend();
		LeafiaGls.blendFunc(SourceFactor.SRC_ALPHA,DestFactor.ONE_MINUS_SRC_ALPHA);
		GL11.glAlphaFunc(GL11.GL_ALWAYS, 0);
		mdl.renderAll();
		GL11.glAlphaFunc(GL11.GL_GREATER, 0);
		LeafiaGls.disableBlend();
		LeafiaGls.popMatrix();
	}
}