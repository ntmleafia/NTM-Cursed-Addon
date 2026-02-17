package com.leafia.contents.gear.utility;

import com.custom_hbm.render.LCERenderHelper;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.render.misc.EnumSymbol;
import com.leafia.contents.AddonItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

public class FuzzyIdentifierRender extends TileEntityItemStackRenderer {

	public static final FuzzyIdentifierRender INSTANCE = new FuzzyIdentifierRender();
	
	public TransformType type;
	public IBakedModel itemModelFuzzy;

	float getDisplayAlpha(long offset) {
		return 1-(float)Math.pow(Math.floorMod(System.currentTimeMillis()-offset,100)/200f,2)*2;
	}

	@SideOnly(Side.CLIENT)
	public static TextureAtlasSprite getTextureFromFluid(Fluid f){
		if(f == null) {
			return null;
		}
		return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(f.getStill().toString());
	}

	@Override
	public void renderByItem(ItemStack itemStackIn) {
		if(itemStackIn.getItem() != AddonItems.fuzzy_identifier)
			return;
		final double HALF_A_PIXEL = 0.03125;
		final double PIX = 0.0625;
		FluidType fluid = FuzzyIdentifierItem.getType(itemStackIn);
		TextureAtlasSprite fluidIcon = getTextureFromFluid(fluid.getFF());
		LCERenderHelper.bindBlockTexture();
		boolean fuzzy = true;
		{
			GL11.glPushMatrix();
			GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
			GlStateManager.disableLighting();
			GL11.glTranslated(0, 0, 0.5+HALF_A_PIXEL);

			int hex = fluid.getColor();
			float r1 = ((hex>>>16)&0xFF)/255f;
			float g1 = ((hex>>>8)&0xFF)/255f;
			float b1 = ((hex)&0xFF)/255f;

			if(fluidIcon != null){
				//FFUtils.setColorFromFluid(fluid);
				LCERenderHelper.startDrawingColoredTexturedQuads();

				if (fuzzy) {
					float minU = fluidIcon.getInterpolatedU(9*1.5-8);
					float minV = fluidIcon.getInterpolatedV(14*1.5-8);
					float maxU = fluidIcon.getInterpolatedU(13*1.5-8);
					float maxV = fluidIcon.getInterpolatedV(15*1.5-8);

					GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE);

					float alpha = getDisplayAlpha(0);
					LCERenderHelper.addVertexColorWithUV(9*PIX, 14*PIX, -HALF_A_PIXEL, minU, minV, r1, g1, b1, alpha);
					LCERenderHelper.addVertexColorWithUV(13 * PIX, 14 * PIX, -HALF_A_PIXEL, maxU, minV, r1, g1, b1, alpha);
					LCERenderHelper.addVertexColorWithUV(13 * PIX, 15 * PIX, -HALF_A_PIXEL, maxU, maxV, r1, g1, b1, alpha);
					LCERenderHelper.addVertexColorWithUV(9 * PIX, 15 * PIX, -HALF_A_PIXEL, minU, maxV, r1, g1, b1, alpha);
					LCERenderHelper.addVertexColorWithUV(13*PIX, 14*PIX, -HALF_A_PIXEL, maxU, minV, r1, g1, b1, alpha);
					LCERenderHelper.addVertexColorWithUV(9 * PIX, 14 * PIX, -HALF_A_PIXEL, minU, minV, r1, g1, b1, alpha);
					LCERenderHelper.addVertexColorWithUV(9 * PIX, 15 * PIX, -HALF_A_PIXEL, minU, maxV, r1, g1, b1, alpha);
					LCERenderHelper.addVertexColorWithUV(13 * PIX, 15 * PIX, -HALF_A_PIXEL, maxU, maxV, r1, g1, b1, alpha);

					alpha = getDisplayAlpha(15);
					minU = fluidIcon.getInterpolatedU(5*1.5-8);
					minV = fluidIcon.getInterpolatedV(13*1.5-8);
					maxU = fluidIcon.getInterpolatedU(13*1.5-8);
					maxV = fluidIcon.getInterpolatedV(14*1.5-8);
					LCERenderHelper.addVertexColorWithUV(5*PIX, 13*PIX, -HALF_A_PIXEL, minU, minV, r1, g1, b1, alpha);
					LCERenderHelper.addVertexColorWithUV(13 * PIX, 13 * PIX, -HALF_A_PIXEL, maxU, minV, r1, g1, b1, alpha);
					LCERenderHelper.addVertexColorWithUV(13 * PIX, 14 * PIX, -HALF_A_PIXEL, maxU, maxV, r1, g1, b1, alpha);
					LCERenderHelper.addVertexColorWithUV(5 * PIX, 14 * PIX, -HALF_A_PIXEL, minU, maxV, r1, g1, b1, alpha);
					LCERenderHelper.addVertexColorWithUV(13*PIX, 13*PIX, -HALF_A_PIXEL, maxU, minV, r1, g1, b1, alpha);
					LCERenderHelper.addVertexColorWithUV(5 * PIX, 13 * PIX, -HALF_A_PIXEL, minU, minV, r1, g1, b1, alpha);
					LCERenderHelper.addVertexColorWithUV(5 * PIX, 14 * PIX, -HALF_A_PIXEL, minU, maxV, r1, g1, b1, alpha);
					LCERenderHelper.addVertexColorWithUV(13 * PIX, 14 * PIX, -HALF_A_PIXEL, maxU, maxV, r1, g1, b1, alpha);

					alpha = getDisplayAlpha(30);
					minU = fluidIcon.getInterpolatedU(7*1.5-8);
					minV = fluidIcon.getInterpolatedV(12*1.5-8);
					maxU = fluidIcon.getInterpolatedU(13*1.5-8);
					maxV = fluidIcon.getInterpolatedV(13*1.5-8);
					LCERenderHelper.addVertexColorWithUV(7*PIX, 12*PIX, -HALF_A_PIXEL, minU, minV, r1, g1, b1, alpha);
					LCERenderHelper.addVertexColorWithUV(13 * PIX, 12 * PIX, -HALF_A_PIXEL, maxU, minV, r1, g1, b1, alpha);
					LCERenderHelper.addVertexColorWithUV(13 * PIX, 13 * PIX, -HALF_A_PIXEL, maxU, maxV, r1, g1, b1, alpha);
					LCERenderHelper.addVertexColorWithUV(7 * PIX, 13 * PIX, -HALF_A_PIXEL, minU, maxV, r1, g1, b1, alpha);
					LCERenderHelper.addVertexColorWithUV(13*PIX, 12*PIX, -HALF_A_PIXEL, maxU, minV, r1, g1, b1, alpha);
					LCERenderHelper.addVertexColorWithUV(7 * PIX, 12 * PIX, -HALF_A_PIXEL, minU, minV, r1, g1, b1, alpha);
					LCERenderHelper.addVertexColorWithUV(7 * PIX, 13 * PIX, -HALF_A_PIXEL, minU, maxV, r1, g1, b1, alpha);
					LCERenderHelper.addVertexColorWithUV(13 * PIX, 13 * PIX, -HALF_A_PIXEL, maxU, maxV, r1, g1, b1, alpha);

					alpha = getDisplayAlpha(45);
					minU = fluidIcon.getInterpolatedU(8*1.5-8);
					minV = fluidIcon.getInterpolatedV(11*1.5-8);
					maxU = fluidIcon.getInterpolatedU(10*1.5-8);
					maxV = fluidIcon.getInterpolatedV(12*1.5-8);
					LCERenderHelper.addVertexColorWithUV(8*PIX, 11*PIX, -HALF_A_PIXEL, minU, minV, r1, g1, b1, alpha);
					LCERenderHelper.addVertexColorWithUV(10 * PIX, 11 * PIX, -HALF_A_PIXEL, maxU, minV, r1, g1, b1, alpha);
					LCERenderHelper.addVertexColorWithUV(10 * PIX, 12 * PIX, -HALF_A_PIXEL, maxU, maxV, r1, g1, b1, alpha);
					LCERenderHelper.addVertexColorWithUV(8 * PIX, 12 * PIX, -HALF_A_PIXEL, minU, maxV, r1, g1, b1, alpha);
					LCERenderHelper.addVertexColorWithUV(10*PIX, 11*PIX, -HALF_A_PIXEL, maxU, minV, r1, g1, b1, alpha);
					LCERenderHelper.addVertexColorWithUV(8 * PIX, 11 * PIX, -HALF_A_PIXEL, minU, minV, r1, g1, b1, alpha);
					LCERenderHelper.addVertexColorWithUV(8 * PIX, 12 * PIX, -HALF_A_PIXEL, minU, maxV, r1, g1, b1, alpha);
					LCERenderHelper.addVertexColorWithUV(10 * PIX, 12 * PIX, -HALF_A_PIXEL, maxU, maxV, r1, g1, b1, alpha);
				}


				LCERenderHelper.draw();
			}
			if (fuzzy) {
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
				GlStateManager.color(1.0F, 1.0F,1.0F,1.0F);
				GlStateManager.disableTexture2D();
				LCERenderHelper.startDrawingColoredQuads();
				for (int type = 0; type < 4; type++) {
					int x = 3;
					int y = 10;
					float r = 0;
					float g = 0;
					float b = 0;
					switch(type) {
						case 0:
							x++;
							r = fluid.flammability/4f;
							break;
						case 1:
							y--;
							b = fluid.poison/4f;
							break;
						case 2:
							x+=2;
							y--;
							r = fluid.reactivity/4f;
							g = fluid.reactivity/4f;
							break;
						case 3:
							x++;
							y-=2;
							if (fluid.symbol != EnumSymbol.NONE) {
								r = 1;
								g = 1;
								b = 1;
							}
							break;
					}
					r = (float)Math.pow(r,0.65);
					g = (float)Math.pow(g,0.65);
					b = (float)Math.pow(b,0.65);
					LCERenderHelper.addVertexColor(x*PIX,y*PIX,0,r,g,b,1);
					LCERenderHelper.addVertexColor((x+1)*PIX,y*PIX,0,r,g,b,1);
					LCERenderHelper.addVertexColor((x+1)*PIX,(y+1)*PIX,0,r,g,b,1);
					LCERenderHelper.addVertexColor(x*PIX,(y+1)*PIX,0,r,g,b,1);
					LCERenderHelper.addVertexColor((x+1)*PIX,y*PIX,-PIX,r,g,b,1);
					LCERenderHelper.addVertexColor(x*PIX,y*PIX,-PIX,r,g,b,1);
					LCERenderHelper.addVertexColor(x*PIX,(y+1)*PIX,-PIX,r,g,b,1);
					LCERenderHelper.addVertexColor((x+1)*PIX,(y+1)*PIX,-PIX,r,g,b,1);
				}
				LCERenderHelper.draw();
				GlStateManager.enableTexture2D();
			}
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.enableLighting();
			GL11.glPopAttrib();
			GL11.glPopMatrix();
		}
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		Minecraft.getMinecraft().getRenderItem().renderItem(itemStackIn,itemModelFuzzy);
	}
}
