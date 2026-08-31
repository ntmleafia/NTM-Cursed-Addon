package com.leafia.contents.machines.elevators;

import com.hbm.render.loader.WaveFrontObjectVAO;
import com.leafia.dev.render.LeafiaItemRenderer;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import static com.leafia.contents.machines.elevators.car.ElevatorRender.model;
import static com.leafia.contents.machines.elevators.car.ElevatorRender.resource;

public class EvBufferRender extends TileEntitySpecialRenderer<EvBufferTE> {
	static WaveFrontObjectVAO mdl = model("buffer");
	static ResourceLocation tex = resource("buffer");
	static double globalScale = 6/8.375;
	public static class EvBufferItemRender extends LeafiaItemRenderer {
		@Override
		protected double _sizeReference() {
			return 11.2;
		}
		@Override
		protected double _itemYoffset() {
			return -0.22;
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
	public void render(EvBufferTE te,double x,double y,double z,float partialTicks,int destroyStage,float alpha) {
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
		LeafiaGls.scale(globalScale);
		double shrinkLength = 0;
		if (te.elevator != null) {
			double targetY = te.elevator.posY-0.25;
			shrinkLength = Math.max(((te.getPos().getY()+8.375*globalScale)-targetY)/globalScale,0);
		}
		bindTexture(tex);
		mdl.renderPart("Base");
		float length = (float)Math.max(4-shrinkLength,-0.375*globalScale);
		float scale = (length+0.375f)/4.375f;
		LeafiaGls.pushMatrix();
		LeafiaGls.translate(0,length-4,0);
		mdl.renderPart("Move");
		LeafiaGls.popMatrix();
		LeafiaGls.pushMatrix();
		LeafiaGls.translate(0,3.625f*(1-scale),0);
		LeafiaGls.scale(1,scale,1);
		mdl.renderPart("Scale");
		LeafiaGls.popMatrix();
		LeafiaGls.popMatrix();
	}
}
