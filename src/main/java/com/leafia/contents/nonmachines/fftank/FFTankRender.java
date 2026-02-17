package com.leafia.contents.nonmachines.fftank;

import com.hbm.blocks.BlockDummyable;
import com.hbm.render.loader.WaveFrontObjectVAO;
import com.leafia.dev.LeafiaItemRenderer;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import static com.leafia.init.ResourceInit.getIntegrated;
import static com.leafia.init.ResourceInit.getVAO;

public class FFTankRender extends TileEntitySpecialRenderer<FFTankTE> {
	public static WaveFrontObjectVAO mdl = getVAO(getIntegrated("ngf_fftank/fftank.obj"));
	public static ResourceLocation tex = getIntegrated("ngf_fftank/fftank.png");
	public static class FFTankItemRender extends LeafiaItemRenderer {
		@Override
		protected double _sizeReference() {
			return 6.2;
		}
		@Override
		protected double _itemYoffset() {
			return -0.11;
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
	public void render(FFTankTE te,double x,double y,double z,float partialTicks,int destroyStage,float alpha) {
		LeafiaGls.pushMatrix();
		int shade = LeafiaGls.activeShadeModel;
		LeafiaGls.shadeModel(GL11.GL_SMOOTH);
		LeafiaGls.translate(x+0.5,y,z+0.5);
		switch(te.getBlockMetadata()-BlockDummyable.offset) {
			case 2: LeafiaGls.rotate(180,0F,1F,0F); break;
			case 4: LeafiaGls.rotate(270,0F,1F,0F); break;
			case 3: LeafiaGls.rotate(0,0F,1F,0F); break;
			case 5: LeafiaGls.rotate(90,0F,1F,0F); break;
		}
		bindTexture(tex);
		mdl.renderPart("Body");
		IBlockState above = te.getWorld().getBlockState(te.getPos().up(3));
		LeafiaGls.shadeModel(shade);
		if (above.isFullCube() && above.getMaterial() != Material.AIR)
			mdl.renderPart("Frame");
		LeafiaGls.popMatrix();
	}
}
