// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.gear.utility.fuzzy;

import com.hbm.inventory.fluid.FluidType;
import com.hbm.render.item.TEISRBase;
import com.hbm.render.misc.EnumSymbol;
import com.hbm.render.model.BakedModelTransforms;
import com.leafia.contents.AddonItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

public class FuzzyIdentifierRender extends TEISRBase {

	public static final FuzzyIdentifierRender INSTANCE = new FuzzyIdentifierRender();

	@Override
	public ModelBinding createModelBinding(Item item) {
		return ModelBinding.of(FuzzyIdentifierItem.fuzzyModel, BakedModelTransforms.defaultItemTransforms(), false);
	}

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
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		var tessellator = Tessellator.getInstance();
		var buffer = tessellator.getBuffer();
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
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

				if (fuzzy) {
					float minU = fluidIcon.getInterpolatedU(9*1.5-8);
					float minV = fluidIcon.getInterpolatedV(14*1.5-8);
					float maxU = fluidIcon.getInterpolatedU(13*1.5-8);
					float maxV = fluidIcon.getInterpolatedV(15*1.5-8);

					GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE);

					float alpha = getDisplayAlpha(0);
					buffer.pos(9*PIX, 14*PIX, -HALF_A_PIXEL).tex(minU, minV).color(r1, g1, b1, alpha).endVertex();
					buffer.pos(13 * PIX, 14 * PIX, -HALF_A_PIXEL).tex(maxU, minV).color(r1, g1, b1, alpha).endVertex();
					buffer.pos(13 * PIX, 15 * PIX, -HALF_A_PIXEL).tex(maxU, maxV).color(r1, g1, b1, alpha).endVertex();
					buffer.pos(9 * PIX, 15 * PIX, -HALF_A_PIXEL).tex(minU, maxV).color(r1, g1, b1, alpha).endVertex();
					buffer.pos(13*PIX, 14*PIX, -HALF_A_PIXEL).tex(maxU, minV).color(r1, g1, b1, alpha).endVertex();
					buffer.pos(9 * PIX, 14 * PIX, -HALF_A_PIXEL).tex(minU, minV).color(r1, g1, b1, alpha).endVertex();
					buffer.pos(9 * PIX, 15 * PIX, -HALF_A_PIXEL).tex(minU, maxV).color(r1, g1, b1, alpha).endVertex();
					buffer.pos(13 * PIX, 15 * PIX, -HALF_A_PIXEL).tex(maxU, maxV).color(r1, g1, b1, alpha).endVertex();

					alpha = getDisplayAlpha(15);
					minU = fluidIcon.getInterpolatedU(5*1.5-8);
					minV = fluidIcon.getInterpolatedV(13*1.5-8);
					maxU = fluidIcon.getInterpolatedU(13*1.5-8);
					maxV = fluidIcon.getInterpolatedV(14*1.5-8);
					buffer.pos(5*PIX, 13*PIX, -HALF_A_PIXEL).tex(minU, minV).color(r1, g1, b1, alpha).endVertex();
					buffer.pos(13 * PIX, 13 * PIX, -HALF_A_PIXEL).tex(maxU, minV).color(r1, g1, b1, alpha).endVertex();
					buffer.pos(13 * PIX, 14 * PIX, -HALF_A_PIXEL).tex(maxU, maxV).color(r1, g1, b1, alpha).endVertex();
					buffer.pos(5 * PIX, 14 * PIX, -HALF_A_PIXEL).tex(minU, maxV).color(r1, g1, b1, alpha).endVertex();
					buffer.pos(13*PIX, 13*PIX, -HALF_A_PIXEL).tex(maxU, minV).color(r1, g1, b1, alpha).endVertex();
					buffer.pos(5 * PIX, 13 * PIX, -HALF_A_PIXEL).tex(minU, minV).color(r1, g1, b1, alpha).endVertex();
					buffer.pos(5 * PIX, 14 * PIX, -HALF_A_PIXEL).tex(minU, maxV).color(r1, g1, b1, alpha).endVertex();
					buffer.pos(13 * PIX, 14 * PIX, -HALF_A_PIXEL).tex(maxU, maxV).color(r1, g1, b1, alpha).endVertex();

					alpha = getDisplayAlpha(30);
					minU = fluidIcon.getInterpolatedU(7*1.5-8);
					minV = fluidIcon.getInterpolatedV(12*1.5-8);
					maxU = fluidIcon.getInterpolatedU(13*1.5-8);
					maxV = fluidIcon.getInterpolatedV(13*1.5-8);
					buffer.pos(7*PIX, 12*PIX, -HALF_A_PIXEL).tex(minU, minV).color(r1, g1, b1, alpha).endVertex();
					buffer.pos(13 * PIX, 12 * PIX, -HALF_A_PIXEL).tex(maxU, minV).color(r1, g1, b1, alpha).endVertex();
					buffer.pos(13 * PIX, 13 * PIX, -HALF_A_PIXEL).tex(maxU, maxV).color(r1, g1, b1, alpha).endVertex();
					buffer.pos(7 * PIX, 13 * PIX, -HALF_A_PIXEL).tex(minU, maxV).color(r1, g1, b1, alpha).endVertex();
					buffer.pos(13*PIX, 12*PIX, -HALF_A_PIXEL).tex(maxU, minV).color(r1, g1, b1, alpha).endVertex();
					buffer.pos(7 * PIX, 12 * PIX, -HALF_A_PIXEL).tex(minU, minV).color(r1, g1, b1, alpha).endVertex();
					buffer.pos(7 * PIX, 13 * PIX, -HALF_A_PIXEL).tex(minU, maxV).color(r1, g1, b1, alpha).endVertex();
					buffer.pos(13 * PIX, 13 * PIX, -HALF_A_PIXEL).tex(maxU, maxV).color(r1, g1, b1, alpha).endVertex();

					alpha = getDisplayAlpha(45);
					minU = fluidIcon.getInterpolatedU(8*1.5-8);
					minV = fluidIcon.getInterpolatedV(11*1.5-8);
					maxU = fluidIcon.getInterpolatedU(10*1.5-8);
					maxV = fluidIcon.getInterpolatedV(12*1.5-8);
					buffer.pos(8*PIX, 11*PIX, -HALF_A_PIXEL).tex(minU, minV).color(r1, g1, b1, alpha).endVertex();
					buffer.pos(10 * PIX, 11 * PIX, -HALF_A_PIXEL).tex(maxU, minV).color(r1, g1, b1, alpha).endVertex();
					buffer.pos(10 * PIX, 12 * PIX, -HALF_A_PIXEL).tex(maxU, maxV).color(r1, g1, b1, alpha).endVertex();
					buffer.pos(8 * PIX, 12 * PIX, -HALF_A_PIXEL).tex(minU, maxV).color(r1, g1, b1, alpha).endVertex();
					buffer.pos(10*PIX, 11*PIX, -HALF_A_PIXEL).tex(maxU, minV).color(r1, g1, b1, alpha).endVertex();
					buffer.pos(8 * PIX, 11 * PIX, -HALF_A_PIXEL).tex(minU, minV).color(r1, g1, b1, alpha).endVertex();
					buffer.pos(8 * PIX, 12 * PIX, -HALF_A_PIXEL).tex(minU, maxV).color(r1, g1, b1, alpha).endVertex();
					buffer.pos(10 * PIX, 12 * PIX, -HALF_A_PIXEL).tex(maxU, maxV).color(r1, g1, b1, alpha).endVertex();
				}


				tessellator.draw();
			}
			if (fuzzy) {
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
				GlStateManager.color(1.0F, 1.0F,1.0F,1.0F);
				GlStateManager.disableTexture2D();
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
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
					buffer.pos(x*PIX, y*PIX, 0).color(r, g, b, 1).endVertex();
					buffer.pos((x+1)*PIX, y*PIX, 0).color(r, g, b, 1).endVertex();
					buffer.pos((x+1)*PIX, (y+1)*PIX, 0).color(r, g, b, 1).endVertex();
					buffer.pos(x*PIX, (y+1)*PIX, 0).color(r, g, b, 1).endVertex();
					buffer.pos((x+1)*PIX, y*PIX, -PIX).color(r, g, b, 1).endVertex();
					buffer.pos(x*PIX, y*PIX, -PIX).color(r, g, b, 1).endVertex();
					buffer.pos(x*PIX, (y+1)*PIX, -PIX).color(r, g, b, 1).endVertex();
					buffer.pos((x+1)*PIX, (y+1)*PIX, -PIX).color(r, g, b, 1).endVertex();
				}
				tessellator.draw();
				GlStateManager.enableTexture2D();
			}
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.enableLighting();
			GL11.glPopAttrib();
			GL11.glPopMatrix();
		}
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		Minecraft.getMinecraft().getRenderItem().renderItem(itemStackIn, itemModel);
	}
}
