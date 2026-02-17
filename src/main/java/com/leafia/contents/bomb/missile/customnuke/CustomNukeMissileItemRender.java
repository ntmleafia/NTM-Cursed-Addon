package com.leafia.contents.bomb.missile.customnuke;

import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.render.item.ItemRenderMissileGeneric;
import com.hbm.render.item.TEISRBase;
import com.hbm.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.util.function.Consumer;

public class CustomNukeMissileItemRender extends TEISRBase {
	@Override
	public void renderByItem(ItemStack item) {

		Consumer<TextureManager> renderer = ItemRenderMissileGeneric.renderers.get(new ComparableStack(item).makeSingular());
		if(renderer == null) return;

		GlStateManager.pushMatrix();

		double guiScale = 1;
		double guiOffset = 0;

		/*switch(this.category) {
			case TYPE_TIER0: guiScale = 3.75D; guiOffset = 1D; break;
			case TYPE_TIER1: guiScale = 2.5D; guiOffset = 0.5D; break;
			case TYPE_TIER2: guiScale = 2D; guiOffset = 0.5D; break;
			case TYPE_TIER3: guiScale = 1.25D; guiOffset = 0D; break;
			case TYPE_STEALTH: guiScale = 1.75D; guiOffset = 1.5D; break;
			case TYPE_ABM: guiScale = 2.25D; guiOffset = 0.5D; break;
			case TYPE_NUCLEAR: guiScale = 1.375D; guiOffset = 0D; break;
			case TYPE_THERMAL: guiScale = 1.75D; guiOffset = 1D; break;
			case TYPE_ROBIN: guiScale = 1.25D; guiOffset = 2D; break;
			case TYPE_CARRIER: guiScale = 0.625D; break;
		}*/
		guiScale = 4.0D/1.4F; guiOffset = 1D;

		GlStateManager.enableLighting();
		boolean prevAlpha = RenderUtil.isAlphaEnabled();
		int prevAlphaFunc = RenderUtil.getAlphaFunc();
		float prevAlphaRef = RenderUtil.getAlphaRef();
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0F);
		GlStateManager.enableAlpha();
		switch (type) {
			case THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND -> {
				double s = 0.15;
				GlStateManager.translate(0.5, -0.25, 0.25);
				GlStateManager.scale(s, s, s);
				GlStateManager.scale(guiScale, guiScale, guiScale);
			}
			case FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND -> {
				double heldScale = 0.1;
				GlStateManager.translate(0.5, -0.25, 0.3);
				GlStateManager.scale(heldScale, heldScale, heldScale);
				GlStateManager.scale(guiScale, guiScale, guiScale);
			}
			case GROUND, FIXED, HEAD -> {
				double s2 = 0.15;
				GlStateManager.scale(s2, s2, s2);
			}
			case GUI -> {
				double s3 = 0.0625;
				GlStateManager.scale(s3, s3, s3);
				GlStateManager.translate(15 - guiOffset, 1 + guiOffset, 0);
				GlStateManager.scale(guiScale, guiScale, guiScale);
				GlStateManager.rotate(45, 0, 0, 1);
				GlStateManager.rotate(System.currentTimeMillis() / 15 % 360, 0, 1, 0);
			}
			default -> {
			}
		}

		GlStateManager.disableCull();
		renderer.accept(Minecraft.getMinecraft().renderEngine);
		GlStateManager.enableCull();
		if (!prevAlpha) GlStateManager.disableAlpha();
		GlStateManager.alphaFunc(prevAlphaFunc, prevAlphaRef);
		GlStateManager.popMatrix();
	}
}
