package com.leafia.contents.machines.processing.mixingvat;

import com.hbm.blocks.BlockDummyable;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.render.loader.WaveFrontObjectVAO;
import com.leafia.dev.LeafiaItemRenderer;
import com.leafia.transformer.LeafiaGls;
import com.llib.math.LeafiaColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import static com.leafia.init.ResourceInit.getIntegrated;
import static com.leafia.init.ResourceInit.getVAO;

public class MixingVatRenderNeo extends TileEntitySpecialRenderer<MixingVatTE> {
	public static final WaveFrontObjectVAO mdl = getVAO(getIntegrated("ngf_vat/vat.obj"));
	public static final ResourceLocation tex = getIntegrated("ngf_vat/vat.png");
	public static final ResourceLocation blade = getIntegrated("ngf_vat/blade.png");
	public static class MixingVatItemRenderNeo extends LeafiaItemRenderer {
		@Override
		public double _sizeReference() {
			return 5;
		}
		@Override
		public double _itemYoffset() {
			return -0.07;
		}
		@Override
		protected ResourceLocation __getTexture() {
			return tex;
		}
		@Override
		protected WaveFrontObjectVAO __getModel() {
			return mdl;
		}
		@Override
		public void renderCommon() {
			GL11.glScaled(0.5, 0.5, 0.5);
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
			bindTexture(__getTexture());
			__getModel().renderPart("Body");
			__getModel().renderPart("Frame");
			bindTexture(blade);
			__getModel().renderPart("Spinner");
			GlStateManager.shadeModel(GL11.GL_FLAT);
		}
	}
	public static final ResourceLocation rsc = getIntegrated("ngf_vat/fluid.png");
	@Override
	public void render(MixingVatTE te,double x,double y,double z,float partialTicks,int destroyStage,float alpha) {
		LeafiaGls.pushMatrix();
		LeafiaGls.translate(x+0.5,y,z+0.5);
		switch(te.getBlockMetadata() - BlockDummyable.offset) {
			case 2: LeafiaGls.rotate(180, 0F, 1F, 0F); break;
			case 4: LeafiaGls.rotate(270, 0F, 1F, 0F); break;
			case 3: LeafiaGls.rotate(0, 0F, 1F, 0F); break;
			case 5: LeafiaGls.rotate(90, 0F, 1F, 0F); break;
		}
		LeafiaGls.translate(0.5,0,-1);

		bindTexture(tex);
		LeafiaGls.shadeModel(GL11.GL_SMOOTH);
		mdl.renderPart("Body");
		LeafiaGls.shadeModel(GL11.GL_FLAT);
		IBlockState above = te.getWorld().getBlockState(te.getPos().up(2));
		if (above.isFullCube() && above.getMaterial() != Material.AIR)
			mdl.renderPart("Frame");

		bindTexture(blade);
		LeafiaGls.pushMatrix();
		float rot = te.prevRot+(te.mixerRot-te.prevRot)*partialTicks;
		LeafiaGls.translate(0,0,-0.5);
		LeafiaGls.rotate(-rot,0,1,0);
		LeafiaGls.translate(0,0,0.5);
		mdl.renderPart("Spinner");
		LeafiaGls.popMatrix();

		LeafiaGls.pushMatrix();
		GL11.glAlphaFunc(GL11.GL_ALWAYS, 0);
		LeafiaGls.enableBlend();
		LeafiaGls.blendFunc(SourceFactor.SRC_ALPHA,DestFactor.ONE_MINUS_SRC_ALPHA);
		bindTexture(rsc);
		float level = 1;
		FluidType ntmf = Fluids.NONE;
		LeafiaColor color;
		if (!te.nuclearMode) {
			color = new LeafiaColor();
		} else {
			level = Math.max(te.tankNc0.getFluidAmount()/(float)te.tankNc0.getCapacity(),te.tankNc1.getFluidAmount()/(float)te.tankNc1.getCapacity());
			ntmf = te.inputTypeNc;
			color = new LeafiaColor(ntmf.getColor());
		}
		LeafiaGls.color(color.getRed(),color.getGreen(),color.getBlue());
		LeafiaGls.translate(0,1.12171*(1-level),0);
		LeafiaGls.scale(1,level,1);
		mdl.renderPart("Fluid");
		GL11.glAlphaFunc(GL11.GL_GREATER, 0);
		LeafiaGls.disableBlend();
		LeafiaGls.popMatrix();

		LeafiaGls.color(1,1,1);
		LeafiaGls.popMatrix();
	}
}