package com.leafia.contents.machines.research.amsp.analyzer;

import com.hbm.render.loader.WaveFrontObjectVAO;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;

import static com.leafia.AddonBase.getIntegrated;
import static com.leafia.init.ResourceInit.getVAO;

public class AMSPAnalyzerRender extends TileEntitySpecialRenderer<AMSPAnalyzerTE> {
	static final WaveFrontObjectVAO vao = getVAO(getIntegrated("machines/research/ams/ams_emitter.obj"));
	static final ResourceLocation tex = getIntegrated("machines/research/ams/ams_emitter_fus.png");
	@Override
	public void render(AMSPAnalyzerTE te,double x,double y,double z,float partialTicks,int destroyStage,float alpha) {
		LeafiaGls.pushMatrix();
		LeafiaGls.translate(x+0.5,y+1-7,z+0.5);
		bindTexture(tex);
		vao.renderAll();
		LeafiaGls.popMatrix();
	}
}
