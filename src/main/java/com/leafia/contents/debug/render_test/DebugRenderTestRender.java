package com.leafia.contents.debug.render_test;

import com.hbm.render.loader.WaveFrontObjectVAO;
import com.leafia.AddonBase;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import static com.leafia.AddonBase.getIntegrated;
import static com.leafia.init.ResourceInit.getVAO;

public class DebugRenderTestRender extends TileEntitySpecialRenderer<DebugRenderTestTE> {
	public static final ResourceLocation tex = getIntegrated("decoration/doors/crimdoorlarge/texture.png");
	public static final WaveFrontObjectVAO vao = getVAO(getIntegrated("decoration/doors/crimdoorsmall/crimdoorsmall.obj"));
	@Override
	public void render(DebugRenderTestTE te,double x,double y,double z,float partialTicks,int destroyStage,float alpha) {
		LeafiaGls.pushMatrix();
		LeafiaGls.enableCull();
		LeafiaGls.shadeModel(GL11.GL_SMOOTH);
		bindTexture(AddonBase.solid);
		bindTexture(tex);
		LeafiaGls.translate(x+0.5,y,z+0.5);

		vao.renderAll();

		LeafiaGls.shadeModel(GL11.GL_FLAT);
		LeafiaGls.popMatrix();
	}
}
