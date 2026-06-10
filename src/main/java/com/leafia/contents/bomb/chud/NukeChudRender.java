package com.leafia.contents.bomb.chud;

import com.hbm.main.ResourceManager;
import com.hbm.render.loader.WaveFrontObjectVAO;
import com.leafia.dev.LeafiaItemRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import static com.leafia.init.ResourceInit.getVAO;

// static tesr baked models can suck my butt
public class NukeChudRender extends TileEntitySpecialRenderer<NukeChudTE> {
	static final WaveFrontObjectVAO vao = getVAO(new ResourceLocation("hbm","models/bombs/fatman.obj"));
	static final ResourceLocation rsc = new ResourceLocation("leafia","textures/models/fatchud.png");
	public static class NukeChudItemRender extends LeafiaItemRenderer {
		@Override
		protected double _sizeReference() {
			return 3.5;
		}
		@Override
		protected double _itemYoffset() {
			return 0;
		}
		@Override
		protected ResourceLocation __getTexture() {
			return rsc;
		}
		@Override
		protected WaveFrontObjectVAO __getModel() {
			return vao;
		}
	}
	@Override
	public void render(NukeChudTE te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5D, y, z + 0.5D);
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
		switch(te.getBlockMetadata())
		{
		case 3:
			GL11.glRotatef(90, 0F, 1F, 0F); break;
		case 5:
			GL11.glRotatef(180, 0F, 1F, 0F); break;
		case 2:
			GL11.glRotatef(270, 0F, 1F, 0F); break;
		case 4:
			GL11.glRotatef(0, 0F, 1F, 0F); break;
		}

		GL11.glShadeModel(GL11.GL_SMOOTH);
		bindTexture(rsc);
        vao.renderAll();
        GL11.glShadeModel(GL11.GL_FLAT);

        GlStateManager.enableCull();

        GL11.glPopMatrix();
	}
}
