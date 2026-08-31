package com.leafia.contents.network.ff_duct.utility;

import com.hbm.render.loader.WaveFrontObjectVAO;
import com.leafia.contents.AddonBlocks;
import com.leafia.contents.network.ff_duct.utility.pump.FFPumpTE;
import com.leafia.dev.render.LeafiaItemRenderer;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import static com.leafia.init.ResourceInit.getVAO;

public class FFDuctUtilityRender extends TileEntitySpecialRenderer<FFDuctUtilityTEBase> {
	public static final WaveFrontObjectVAO converter = getVAO(new ResourceLocation("leafia", "models/leafia/lftr/converter.obj"));
	public static final WaveFrontObjectVAO pump = getVAO(new ResourceLocation("leafia", "models/leafia/lftr/pump.obj"));
	public static final ResourceLocation ff = new ResourceLocation("leafia", "textures/models/leafia/lftr/pipe_silver.png");
	public static final ResourceLocation ntmf = new ResourceLocation("hbm", "textures/blocks/pipe_silver.png");
	public static final ResourceLocation box = new ResourceLocation("leafia", "textures/models/leafia/lftr/pipe_converter.png");
	public static final ResourceLocation arrow = new ResourceLocation("leafia", "textures/models/leafia/lftr/pipe_converter_arrow.png");
	public static class FFDuctUtilityItemRender extends LeafiaItemRenderer {
		@Override
		protected double _sizeReference() {
			return 1.5;
		}
		@Override
		protected double _itemYoffset() {
			return 0.175;
		}
		@Override protected ResourceLocation __getTexture() { return null; }
		@Override protected WaveFrontObjectVAO __getModel() { return null; }
		@Override
		public void renderCommon(ItemStack stack) {
			GL11.glScaled(0.5, 0.5, 0.5);
			if (stack.getItem().equals(Item.getItemFromBlock(AddonBlocks.ff_pump))) {
				bindTexture(ff);
				pump.renderPart("pZ");
				bindTexture(box);
				pump.renderPart("Cylinder");
				bindTexture(arrow);
				pump.renderPart("Cylinder");
			} else {
				bindTexture(ff);
				converter.renderPart("pZ");
				bindTexture(ntmf);
				converter.renderPart("nZ");
				bindTexture(box);
				converter.renderPart("Cube");
				bindTexture(arrow);
				converter.renderPart("Cube");
			}
		}
	}
	@Override
	public void render(FFDuctUtilityTEBase te,double x,double y,double z,float partialTicks,int destroyStage,float alpha) {
		LeafiaGls.pushMatrix();
		LeafiaGls.enableLighting();
		LeafiaGls.enableCull();
		LeafiaGls.translate(x+0.5,y+0.5,z+0.5);
		IBlockState state = getWorld().getBlockState(te.getPos());
		if (state.getBlock() instanceof FFDuctUtilityBase) {
			switch(state.getValue(FFDuctUtilityBase.FACING)) {
				//case NORTH: LeafiaGls.rotate(0,0,1,0); break;
				case SOUTH: LeafiaGls.rotate(180,0,1,0); break;
				case EAST: LeafiaGls.rotate(-90,0,1,0); break;
				case WEST: LeafiaGls.rotate(90,0,1,0); break;
				case UP: LeafiaGls.rotate(90,1,0,0); break;
				case DOWN: LeafiaGls.rotate(-90,1,0,0); break;
			}
			int code = te.getType().getColor();
			float max = 240/255f;
			float red = (code>>>16&0xFF)/255f;
			float green = (code>>>8&0xFF)/255f;
			float blue = (code&0xFF)/255f;
			LeafiaGls.color(red*max,green*max,blue*max);
			float lightColor = 0.65f;
			float darkColor = 0.4f;
			if (te instanceof FFPumpTE) {
				bindTexture(ff);
				pump.renderPart("pZ");
				bindTexture(box);
				if ((red+green+blue)/3f > 0.5f)
					LeafiaGls.color(darkColor,darkColor,darkColor);
				else
					LeafiaGls.color(lightColor,lightColor,lightColor);
				pump.renderPart("Cylinder");
				LeafiaGls.color(1,1,1);
				bindTexture(arrow);
				pump.renderPart("Cylinder");
			} else {
				bindTexture(ff);
				converter.renderPart("pZ");
				bindTexture(ntmf);
				converter.renderPart("nZ");
				bindTexture(box);
				if ((red+green+blue)/3f > 0.5f)
					LeafiaGls.color(darkColor,darkColor,darkColor);
				else
					LeafiaGls.color(lightColor,lightColor,lightColor);
				converter.renderPart("Cube");
				LeafiaGls.color(1,1,1);
				bindTexture(arrow);
				converter.renderPart("Cube");
			}
		}
		LeafiaGls.popMatrix();
	}
}
