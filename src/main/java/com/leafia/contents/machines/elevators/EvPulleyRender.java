package com.leafia.contents.machines.elevators;

import com.hbm.render.NTMRenderHelper;
import com.hbm.render.loader.WaveFrontObjectVAO;
import com.leafia.dev.render.LeafiaItemRenderer;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import static com.leafia.contents.machines.elevators.car.ElevatorRender.*;

public class EvPulleyRender extends TileEntitySpecialRenderer<EvPulleyTE> {
	static WaveFrontObjectVAO mdl = model("pulley");
	static ResourceLocation concrete = new ResourceLocation("hbm:textures/blocks/concrete_colored_ext.machine.png");
	static ResourceLocation plug = resource("elevator_cable");
	static ResourceLocation motor = resource("motor");
	static ResourceLocation rope = resource("rope");
	public static class EvPulleyItemRender extends LeafiaItemRenderer {
		@Override
		protected double _sizeReference() {
			return 11;
		}
		@Override
		protected double _itemYoffset() {
			return 0.1;
		}
		@Override
		protected ResourceLocation __getTexture() {
			return null;
		}
		@Override
		protected WaveFrontObjectVAO __getModel() {
			return mdl;
		}
		@Override
		public void renderCommon(ItemStack stack) {
			NTMRenderHelper.bindTexture(concrete);
			mdl.renderPart("Floor");
			NTMRenderHelper.bindTexture(plug);
			mdl.renderPart("Inlet");

			NTMRenderHelper.bindTexture(motor);
			mdl.renderPart("Motor");
			NTMRenderHelper.bindTexture(support);
			mdl.renderPart("Support");
			mdl.renderPart("Pulley");
		}
	}
	@Override
	public void render(EvPulleyTE te,double x,double y,double z,float partialTicks,int destroyStage,float alpha) {
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
		bindTexture(concrete);
		mdl.renderPart("Floor");
		bindTexture(plug);
		mdl.renderPart("Inlet");

		bindTexture(motor);
		mdl.renderPart("Motor");
		bindTexture(support);
		mdl.renderPart("Support");
		mdl.renderPart("Pulley");
		//bindTexture(rope);
		LeafiaGls.disableTexture2D();
		LeafiaGls.color(0,0,0);
		mdl.renderPart("Rope");
		LeafiaGls.enableTexture2D();
		LeafiaGls.color(1,1,1);
		LeafiaGls.popMatrix();
	}
}
