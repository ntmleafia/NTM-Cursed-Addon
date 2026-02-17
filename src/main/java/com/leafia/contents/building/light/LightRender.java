package com.leafia.contents.building.light;

import com.hbm.blocks.BlockDummyable;
import com.hbm.render.loader.WaveFrontObjectVAO;
import com.leafia.contents.AddonBlocks;
import com.leafia.dev.LeafiaItemRenderer;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import static com.leafia.init.ResourceInit.getVAO;

public class LightRender extends TileEntitySpecialRenderer<LightTE> {
	public static final WaveFrontObjectVAO mdl = getVAO(new ResourceLocation("leafia","models/leafia/light.obj"));
	public static final ResourceLocation tex = new ResourceLocation("leafia","textures/solid_emissive.png");
	public static class LightItemRender extends LeafiaItemRenderer {
		@Override
		protected double _sizeReference() {
			return 3.4;
		}
		@Override
		protected double _itemYoffset() {
			return 0.2;
		}
		@Override
		public void renderCommon() {
			GL11.glScaled(0.5, 0.5, 0.5);
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
			bindTexture(__getTexture());
			LeafiaGls.disableCull();
			LeafiaGls.color(0.35f,0.35f,0.35f);
			mdl.renderPart("Base");
			LeafiaGls.color(1,1,1);
			mdl.renderPart("Bulb");
			LeafiaGls.enableCull();
			GlStateManager.shadeModel(GL11.GL_FLAT);
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
	public void render(LightTE te,double x,double y,double z,float partialTicks,int destroyStage,float alpha) {
		LeafiaGls.pushMatrix();
		LeafiaGls.translate(x+0.5,y+0.99,z+0.5);
		switch(te.getBlockMetadata() - BlockDummyable.offset) {
			case 2: LeafiaGls.rotate(180, 0F, 1F, 0F); break;
			case 4: LeafiaGls.rotate(270, 0F, 1F, 0F); break;
			case 3: LeafiaGls.rotate(0, 0F, 1F, 0F); break;
			case 5: LeafiaGls.rotate(90, 0F, 1F, 0F); break;
		}
		bindTexture(tex);
		Block block = te.getWorld().getBlockState(te.getPos()).getBlock();
		LeafiaGls.color(0.35f,0.35f,0.35f);
		LeafiaGls.disableCull();
		mdl.renderPart("Base");
		LeafiaGls.enableCull();
		LeafiaGls.color(1,1,1);
		if (block == AddonBlocks.lightLit) {
			LeafiaGls.disableLighting();
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,240.0F,240.0F);
		}
		mdl.renderPart("Bulb");
		if (block == AddonBlocks.lightLit) {
			LeafiaGls.enableLighting();
		}
		LeafiaGls.popMatrix();
	}
}
