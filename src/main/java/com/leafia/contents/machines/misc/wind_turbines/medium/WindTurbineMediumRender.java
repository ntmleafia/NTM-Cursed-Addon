package com.leafia.contents.machines.misc.wind_turbines.medium;

import com.hbm.render.loader.WaveFrontObjectVAO;
import com.leafia.AddonBase;
import com.leafia.dev.render.LeafiaItemRenderer;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import static com.leafia.AddonBase.getIntegrated;
import static com.leafia.init.ResourceInit.getVAO;

public class WindTurbineMediumRender extends TileEntitySpecialRenderer<WindTurbineMediumTE> {
	public static final ResourceLocation tex = getIntegrated("machines/wind_turbines/windturbine_upright.png");
	public static final WaveFrontObjectVAO vao = getVAO(getIntegrated("machines/wind_turbines/windturbine_upgright.obj"));
	public static class WindTurbineMediumItemRender extends LeafiaItemRenderer {
		@Override
		protected double _sizeReference() {
			return 10.9;
		}
		@Override
		protected double _itemYoffset() {
			return -0.25;
		}
		@Override
		protected ResourceLocation __getTexture() {
			return tex;
		}
		@Override
		protected WaveFrontObjectVAO __getModel() {
			return vao;
		}
	}
	@Override
	public void render(WindTurbineMediumTE te,double x,double y,double z,float partialTicks,int destroyStage,float alpha) {
		LeafiaGls.pushMatrix();
		LeafiaGls.enableCull();
		LeafiaGls.shadeModel(GL11.GL_SMOOTH);
		bindTexture(AddonBase.solid);
		bindTexture(tex);
		LeafiaGls.translate(x+0.5,y,z+0.5);

		vao.renderPart("Base");
		LeafiaGls.rotate(te.bladeAnglePrev+(te.bladeAngle-te.bladeAnglePrev)*partialTicks,0,-1,0);
		vao.renderPart("Blades");

		LeafiaGls.shadeModel(GL11.GL_FLAT);
		LeafiaGls.popMatrix();
	}
}
