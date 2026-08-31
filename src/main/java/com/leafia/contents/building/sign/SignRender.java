package com.leafia.contents.building.sign;

import com.hbm.render.loader.WaveFrontObjectVAO;
import com.leafia.dev.render.LeafiaItemRenderer;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class SignRender extends TileEntitySpecialRenderer<SignTE>  {
	public static class SignItemRender extends LeafiaItemRenderer {
		@Override
		protected double _sizeReference() {
			return 3;
		}
		@Override
		protected double _itemYoffset() {
			return 0;
		}
		@Override
		protected ResourceLocation __getTexture() { return null; }
		@Override
		protected WaveFrontObjectVAO __getModel() { return null; }

		@Override
		public void renderByItem(ItemStack stack) {
			if (type.equals(TransformType.GUI)) {
				if (stack.getItem() instanceof ItemBlock block) {
					if (block.getBlock() instanceof SignBlock sign) {
						LeafiaGls.pushMatrix();
						LeafiaGls.translate(0,0,-0.01);
						LeafiaGls.rotate(90,1,0,0);
						LeafiaGls.rotate(90,0,0,1);
						LeafiaGls.translate(0.5+1/16f,-1,0);
						LeafiaGls.translate(0.5,0.5,0);
						LeafiaGls.scale(0.9);
						LeafiaGls.translate(-0.5,-0.5,0);
						LeafiaGls.scale(1/7f);
						FontRenderer font = Minecraft.getMinecraft().fontRenderer;
						String s = sign.letter;
						boolean unicode = font.getUnicodeFlag();
						font.setUnicodeFlag(false);
						int width = font.getStringWidth(s);
						LeafiaGls.translate(-width/2f,0,0);
						LeafiaGls.translate(-0.25,0,0);
						font.drawString(s,0,0,0xFFFFFF);
						LeafiaGls.translate(0.5,0,0);
						font.drawString(s,0,0,0xFFFFFF);
						font.setUnicodeFlag(unicode);
						LeafiaGls.popMatrix();
					}
				}
				return;
			}
			super.renderByItem(stack);
		}

		@Override
		public void renderCommon(ItemStack stack) {
			if (stack.getItem() instanceof ItemBlock block) {
				if (block.getBlock() instanceof SignBlock sign) {
					GL11.glScaled(0.5, 0.5, 0.5);
					LeafiaGls.scale(1/7f);
					FontRenderer font = Minecraft.getMinecraft().fontRenderer;
					String s = sign.letter;
					boolean unicode = font.getUnicodeFlag();
					font.setUnicodeFlag(false);
					int width = font.getStringWidth(s);
					LeafiaGls.translate(-width/2f,0,0);
					LeafiaGls.translate(-0.25,0,0);
					font.drawString(s,0,0,0xFFFFFF);
					LeafiaGls.translate(0.5,0,0);
					font.drawString(s,0,0,0xFFFFFF);
					font.setUnicodeFlag(unicode);
				}
			}
		}
	}
	@Override
	public void render(SignTE te,double x,double y,double z,float partialTicks,int destroyStage,float alpha) {
		LeafiaGls.pushMatrix();
		LeafiaGls.enableLighting();
		FontRenderer font = Minecraft.getMinecraft().fontRenderer;
		LeafiaGls.translate(x+0.5,y+0.5,z+0.5);
		IBlockState state = getWorld().getBlockState(te.getPos());
		if (state.getBlock() instanceof SignBlock sign) {
			LeafiaGls.disableCull();
			LeafiaGls.rotate(-state.getValue(SignBlock.FACING).getHorizontalAngle()+180,0,1,0);
			LeafiaGls.translate(0,0.5,0.495);
			LeafiaGls.scale(1/7f);
			LeafiaGls.scale(-1,-1,1);
			String s = sign.letter;
			boolean unicode = font.getUnicodeFlag();
			font.setUnicodeFlag(false);
			int width = font.getStringWidth(s);
			LeafiaGls.translate(-width/2f,0,0);
			LeafiaGls.translate(-0.25,0,0);
			font.drawString(s,0,0,te.color.getColorValue());
			LeafiaGls.translate(0.5,0,0);
			font.drawString(s,0,0,te.color.getColorValue());
			font.setUnicodeFlag(unicode);
			LeafiaGls.enableCull();
		}
		LeafiaGls.popMatrix();
	}
}
