package com.leafia.contents.machines.heat.hpboiler;

import com.hbm.render.loader.WaveFrontObjectVAO;
import com.leafia.dev.render.LeafiaItemRenderer;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import static com.leafia.AddonBase.getIntegrated;
import static com.leafia.init.ResourceInit.getVAO;

public class HPBoilerRender extends TileEntitySpecialRenderer<HPBoilerTE> {
	static WaveFrontObjectVAO mdl = getVAO(getIntegrated("fluids/ntmf/ngf_hpboiler/boiler.obj"));
	static ResourceLocation tex = getIntegrated("fluids/ntmf/ngf_hpboiler/boiler.png");
	public static class HPBoilerItemRender extends LeafiaItemRenderer {
		@Override
		protected double _sizeReference() {
			return 6.8;
		}
		@Override
		protected double _itemYoffset() {
			return -0.12;
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
	public void render(HPBoilerTE te,double x,double y,double z,float partialTicks,int destroyStage,float alpha) {
		LeafiaGls.pushMatrix();
		LeafiaGls.translate(x+0.5,y,z+0.5);
		switch(te.getBlockMetadata() - 10) {
			case 2:
				GL11.glRotatef(180, 0F, 1F, 0F); break;
			case 3:
				GL11.glRotatef(0, 0F, 1F, 0F); break;
			case 4:
				GL11.glRotatef(270, 0F, 1F, 0F); break;
			case 5:
				GL11.glRotatef(90, 0F, 1F, 0F); break;
		}
		bindTexture(tex);
		mdl.renderAll();
		LeafiaGls.popMatrix();
	}
}
