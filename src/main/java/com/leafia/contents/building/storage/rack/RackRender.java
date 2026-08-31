package com.leafia.contents.building.storage.rack;

import com.hbm.render.loader.WaveFrontObjectVAO;
import com.leafia.dev.render.LeafiaBrush;
import com.leafia.dev.render.LeafiaBrush.BrushMode;
import com.leafia.dev.render.LeafiaItemRenderer;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import static com.leafia.AddonBase.getIntegrated;
import static com.leafia.init.ResourceInit.getVAO;

public class RackRender extends TileEntitySpecialRenderer<RackTE> {
	static final String basePath = "decoration/sellacity/storagerack/";
	static final WaveFrontObjectVAO mdl = getVAO(getIntegrated(basePath+"rackfinal.obj"));
	static final ResourceLocation tex = getIntegrated(basePath+"rack.png");
	static final ResourceLocation pallet = new ResourceLocation("textures/blocks/planks_spruce.png");
	public static class RackItemRender extends LeafiaItemRenderer {
		@Override
		protected double _sizeReference() {
			return 5.5;
		}
		@Override
		protected double _itemYoffset() {
			return -0.11;
		}
		@Override
		protected ResourceLocation __getTexture() {
			return null;
		}
		@Override
		protected WaveFrontObjectVAO __getModel() {
			return null;
		}
		@Override
		public void renderCommon() {
			GL11.glScaled(0.5, 0.5, 0.5);
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
			bindTexture(tex);
			mdl.renderPart("rack");
			mdl.renderPart("poles");
			mdl.renderPart("support");
			GlStateManager.shadeModel(GL11.GL_FLAT);
		}
	}
	void renderBlock(RackTE te,IBlockState state) {
		bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		BlockRendererDispatcher blocker = Minecraft.getMinecraft().getBlockRendererDispatcher();
		BlockModelRenderer modeler = blocker.getBlockModelRenderer();
		IBakedModel baked = blocker.getModelForState(state);
		LeafiaBrush brush = LeafiaBrush.instance;
		LeafiaGls.pushMatrix();
		LeafiaGls.translate(1,0,0);
		LeafiaGls.rotate(180,0,1,0);
		LeafiaGls.translate(-te.getPos().getX(),-te.getPos().getY(),-te.getPos().getZ());
		brush.startDrawing(BrushMode.QUADS,DefaultVertexFormats.BLOCK);
		modeler.renderModel(te.getWorld(),baked,state,te.getPos(),brush.buf,true);
		brush.draw();
		LeafiaGls.popMatrix();
	}
	IBlockState getState(RackTE te,int index) {
		return te.crates[index].getStateFromMeta(te.metas[index]);
	}
	@Override
	public void render(RackTE te,double x,double y,double z,float partialTicks,int destroyStage,float alpha) {
		LeafiaGls.pushMatrix();
		LeafiaGls.translate(x+0.5,y,z+0.5);
		switch(te.getBlockMetadata()-10) {
			case 2 -> LeafiaGls.rotate(180,0F,1F,0F);
			case 3 -> LeafiaGls.rotate(0,0F,1F,0F);
			case 4 -> LeafiaGls.rotate(270,0F,1F,0F);
			case 5 -> LeafiaGls.rotate(90,0F,1F,0F);
		}
		LeafiaGls.translate(0,0,-0.5);
		bindTexture(tex);
		mdl.renderPart("rack");
		mdl.renderPart("poles");
		mdl.renderPart("support");
		boolean palletLower = false;
		boolean palletUpper = false;
		if (te.crates[0] != null) {
			palletLower = true;
			LeafiaGls.pushMatrix();
			LeafiaGls.translate(-1.125,0.375,0.375);
			renderBlock(te,getState(te,0));
			LeafiaGls.popMatrix();
		}
		if (te.crates[1] != null) {
			palletLower = true;
			LeafiaGls.pushMatrix();
			LeafiaGls.translate(0.125,0.375,0.375);
			renderBlock(te,getState(te,1));
			LeafiaGls.popMatrix();
		}
		if (te.crates[2] != null) {
			palletUpper = true;
			LeafiaGls.pushMatrix();
			LeafiaGls.translate(-1.125,1.75,0.375);
			renderBlock(te,getState(te,2));
			LeafiaGls.popMatrix();
		}
		if (te.crates[3] != null) {
			palletUpper = true;
			LeafiaGls.pushMatrix();
			LeafiaGls.translate(0.125,1.75,0.375);
			renderBlock(te,getState(te,3));
			LeafiaGls.popMatrix();
		}
		bindTexture(pallet);
		if (palletLower)
			mdl.renderPart("palletLower");
		if (palletUpper)
			mdl.renderPart("palletUpper");
		LeafiaGls.popMatrix();
	}
}
