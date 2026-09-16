package com.leafia.contents.machines.reactors.apr.blocks.core;

import com.hbm.render.NTMRenderHelper;
import com.hbm.render.loader.WaveFrontObjectVAO;
import com.hbm.render.util.SmallBlockPronter;
import com.leafia.AddonBase;
import com.leafia.dev.render.LeafiaItemRenderer;
import com.leafia.transformer.LeafiaGls;
import com.llib.exceptions.messages.TextWarningLeafia;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static com.leafia.AddonBase.getIntegrated;
import static com.leafia.init.ResourceInit.getVAO;

public class APRCoreRender extends TileEntitySpecialRenderer<APRCoreTE> {
	public static final WaveFrontObjectVAO vao = getVAO(getIntegrated("machines/reactors/apr/apr_core_better.obj"));
	public static final ResourceLocation tex = getIntegrated("machines/reactors/apr/apr_core.png");
	public static final Map<String,TextureAtlasSprite> sprs = new HashMap<>();
	public static class APRCoreItemRender extends LeafiaItemRenderer {
		@Override
		protected double _sizeReference() {
			return 7.8;
		}
		@Override
		protected double _itemYoffset() {
			return 0.09;
		}
		@Override
		protected ResourceLocation __getTexture() {
			return tex;
		}
		@Override
		protected WaveFrontObjectVAO __getModel() {
			return vao;
		}
		@Override
		public void renderCommon() {
			GL11.glScaled(0.5, 0.5, 0.5);
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
			bindTexture(tex);
			vao.renderPart("Core");
			LeafiaGls.pushMatrix();
			LeafiaGls.rotate(15,0,1,0);
			for (int i = 0; i < 12; i++) {
				LeafiaGls.rotate(30,0,1,0);
				vao.renderPart("Funnel");
			}
			LeafiaGls.popMatrix();
			GlStateManager.shadeModel(GL11.GL_FLAT);
		}
	}
	public static double ctrl(double v) {
		return Math.pow((v-10)/90d,1.25);
	}
	@Override
	public void render(APRCoreTE te,double x,double y,double z,float partialTicks,int destroyStage,float alpha) {
		LeafiaGls.pushMatrix();
		LeafiaGls.translate(x+0.5,y,z+0.5);
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		bindTexture(tex);
		vao.renderPart("Core");
		float funnelAngle = (float)(60*(ctrl(te.lastControl)+(ctrl(te.control)-ctrl(te.lastControl))*partialTicks));
		LeafiaGls.pushMatrix();
		LeafiaGls.rotate(15,0,1,0);
		for (int i = 0; i < 12; i++) {
			LeafiaGls.rotate(30,0,1,0);
			LeafiaGls.pushMatrix();
			LeafiaGls.translate(0,-0.25,-1.44889);
			LeafiaGls.rotate(-funnelAngle,1,0,0);
			LeafiaGls.translate(0,0.25,1.44889);
			vao.renderPart("Funnel");
			LeafiaGls.popMatrix();
		}
		LeafiaGls.popMatrix();
		LeafiaGls.popMatrix();
		GlStateManager.shadeModel(GL11.GL_FLAT);
		if (!te.assembled) {
			LeafiaGls.pushMatrix();
			LeafiaGls.translate(x,y,z);
			SmallBlockPronter.startDrawing();
			NTMRenderHelper.bindBlockTexture();
			NTMRenderHelper.startDrawingTexturedQuads();
			for (Entry<BlockPos,IBlockState> entry : te.mbRequirement.entrySet()) {
				if (entry.getValue().getBlock() instanceof BlockAir) continue;
				String spr = entry.getValue().getBlock().getRegistryName().toString()+":"+entry.getValue().getBlock().getMetaFromState(entry.getValue());
				if (!sprs.containsKey(spr)) {
					Minecraft.getMinecraft().player.sendMessage(new TextWarningLeafia("Couldn't find preview sprite for "+spr+"!"));
					continue;
				}
				SmallBlockPronter.renderSimpleBlockAt(sprs.get(spr),entry.getKey().getX()-te.getPos().getX(),entry.getKey().getY()-te.getPos().getY(),entry.getKey().getZ()-te.getPos().getZ());
			}
			NTMRenderHelper.draw();
			LeafiaGls.disableBlend();
			LeafiaGls.enableAlpha();
			LeafiaGls.enableLighting();
			LeafiaGls.popMatrix();
		}
	}
}
