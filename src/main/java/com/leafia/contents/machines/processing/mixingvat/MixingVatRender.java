package com.leafia.contents.machines.processing.mixingvat;

import com.hbm.blocks.BlockDummyable;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.render.loader.WaveFrontObjectVAO;
import com.leafia.dev.LeafiaItemRenderer;
import com.leafia.transformer.LeafiaGls;
import com.llib.math.LeafiaColor;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import static com.leafia.init.ResourceInit.getVAO;

public class MixingVatRender extends TileEntitySpecialRenderer<MixingVatTE> {
	public static final WaveFrontObjectVAO mdl = getVAO(new ResourceLocation("leafia","models/xenoulexi/mixingvat.obj"));
	public static final ResourceLocation tex = new ResourceLocation("leafia","textures/models/xenoulexi/mixingvat.png");
	public static class MixingVatItemRender extends LeafiaItemRenderer {
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
	}
	public static final ResourceLocation rsc = new ResourceLocation("leafia","textures/models/xenoulexi/mixingvat_fluid.png");
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
		mdl.renderPart("Base");

		LeafiaGls.pushMatrix();
		float rot = te.prevRot+(te.mixerRot-te.prevRot)*partialTicks;
		LeafiaGls.translate(0,0,-0.5);
		LeafiaGls.rotate(-rot,0,1,0);
		LeafiaGls.translate(0,0,0.5);
		mdl.renderPart("MixingBlade");
		LeafiaGls.popMatrix();

		mdl.renderPart("VatGlass");
		LeafiaGls.shadeModel(GL11.GL_SMOOTH);
		mdl.renderPart("Vat");
		mdl.renderPart("Tanks");
		bindTexture(rsc);

		LeafiaGls.pushMatrix();
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
		LeafiaGls.translate(0,-0.65+0.65*level,0);
		mdl.renderPart("VatLiquid");
		LeafiaGls.popMatrix();

		LeafiaGls.color(1,1,1);
		LeafiaGls.shadeModel(GL11.GL_FLAT);
		LeafiaGls.popMatrix();
	}
}