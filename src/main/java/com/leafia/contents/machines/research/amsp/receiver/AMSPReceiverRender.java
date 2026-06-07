package com.leafia.contents.machines.research.amsp.receiver;

import com.hbm.render.loader.WaveFrontObjectVAO;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;

import static com.leafia.AddonBase.getIntegrated;
import static com.leafia.init.ResourceInit.getVAO;

public class AMSPReceiverRender extends TileEntitySpecialRenderer<AMSPReceiverTE> {
	static final WaveFrontObjectVAO vao = getVAO(getIntegrated("machines/research/ams/ams_base.obj"));
	static final ResourceLocation tex = getIntegrated("machines/research/ams/ams_base_fus.png");
	@Override
	public void render(AMSPReceiverTE te,double x,double y,double z,float partialTicks,int destroyStage,float alpha) {
		LeafiaGls.pushMatrix();
		LeafiaGls.translate(x+0.5,y,z+0.5);
		bindTexture(tex);
		vao.renderAll();
		LeafiaGls.popMatrix();
	}
}
