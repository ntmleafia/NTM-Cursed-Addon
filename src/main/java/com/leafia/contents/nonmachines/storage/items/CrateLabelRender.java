// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.nonmachines.storage.items;

import com.hbm.interfaces.Spaghetti;
import com.hbm.tileentity.machine.TileEntityCrate;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityCrate;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.Arrays;
import java.util.List;

public class CrateLabelRender extends TileEntitySpecialRenderer<TileEntityCrate> {

	protected static final float surfaceOffset = 0.501F;

	@Spaghetti("Old code goes brrrrrrrr(-uh)")
	void renderTxt(double x, double y, double z,float rx,float ry,float rz,float yoffset,int alignment,float scale,String text,boolean vertical) {
		GL11.glPushMatrix();
		if (vertical)
			yoffset = -yoffset;
		GL11.glTranslated(x + 0.5F, y + 0.5F, z + 0.5F);
		GL11.glRotatef(rx, 1F, 0F, 0F);
		GL11.glRotatef(ry, 0F, 1F, 0F);
		GL11.glRotatef(rz, 0F, 0F, 1F);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GlStateManager.depthMask(false);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GlStateManager.color(1, 1, 1, 1);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
		GL11.glTranslated(surfaceOffset, 0, 0);
		GL11.glTranslated(0F,yoffset,0F);

		FontRenderer font = Minecraft.getMinecraft().fontRenderer;

		int width = font.getStringWidth(text);
		int height = font.FONT_HEIGHT-1;

		float pix = 1F/7F/16F;

		float f3 = pix*scale;
		if (!vertical)
			GL11.glScalef(f3, -f3, f3);
		else
			GL11.glScalef(-f3, f3, -f3);
		GL11.glNormal3f(0.0F, 0.0F, -1.0F);
		GL11.glRotatef(90, 0, 1, 0);
		font.drawString(text, -width / 2, -height / 2 * alignment, 0xFFFFFF);

		GlStateManager.depthMask(true);
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
	}

	@Override
	public void render(TileEntityCrate te,double x,double y,double z,float partialTicks,int destroyStage,float alpha) {
		super.render(te, x, y, z, partialTicks, destroyStage, alpha);
		if (te instanceof IMixinTileEntityCrate mixin) {
			String labelUpper = mixin.leafia$upperLabel();
			String labelMiddle = mixin.leafia$middleLabel();
			Item labelIcon = mixin.leafia$icon().getA();
			int labelMeta = mixin.leafia$icon().getB();
			String labelLower = mixin.leafia$lowerLabel();
			List<String> labelVertical = Arrays.asList(mixin.leafia$verticalLabels());
			float middleScale = 3F;
			if (labelMiddle.length() == 3)
				middleScale = 4F;
			if (labelMiddle.length() < 3)
				middleScale = 5F;
			float vRot = 0F;
			float offs = 0.5F-(1F/16F*0.75F)/2F;
			int orientation = 0;
			for (int rot = 0;rot < 358;rot+=90) {
				renderTxt(x, y, z, 0F, rot, 0F, offs * 0.8F, 0, 1.25F, labelUpper,false);
				if (labelIcon == null)
					renderTxt(x, y, z, 0F, rot, 0F, 0F, 1, middleScale, labelMiddle,false);
				else {
					ItemStack stack = new ItemStack(labelIcon,1,labelMeta);
					LeafiaGls.pushMatrix();
					IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(stack,te.getWorld(),null);
					Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
					GL11.glTranslated(x + 0.5F, y + 0.5F, z + 0.5F);
					GL11.glRotatef(rot, 0F, 1F, 0F);
					GL11.glTranslated(-surfaceOffset, 0, 0);
					GL11.glRotatef(90, 0, 1, 0);
					GL11.glScaled(0.5,0.5,0.5);
					model = ForgeHooksClient.handleCameraTransforms(model,TransformType.FIXED,false);
					Minecraft.getMinecraft().getRenderItem().renderItem(stack,model);
					LeafiaGls.popMatrix();
				}
				renderTxt(x, y, z, 0F, rot, 0F, -offs * 0.8F, 2, 1.25F, labelLower,false);

				renderTxt(x, y, z, 0F, rot, 90F, -offs * 0.8F, 2, 1.25F, labelVertical.get(orientation),true);
				renderTxt(x, y, z, 0F, rot, -90F, -offs * 0.8F, 2, 1.25F, labelVertical.get(orientation),true);
				orientation++;
			}
		}
		//renderTxt(x, y, z, vRot, 0F, 90F, 0F, 1, middleScale, labelMiddle,true);
		//renderTxt(x, y, z, vRot, 0F, -90F, 0F, 1, middleScale, labelMiddle,true);
	}
}
