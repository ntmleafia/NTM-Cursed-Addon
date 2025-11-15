package com.leafia.dev;

import com.hbm.render.NTMRenderHelper;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.render.loader.WaveFrontObjectVAO;
import com.leafia.dev.items.LeafiaGripOffsetHelper;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public abstract class LeafiaItemRenderer extends ItemRenderBase {
	static boolean debug = false;
	double scale = _sizeReference();
	double offset = _itemYoffset();
	protected abstract double _sizeReference();
	protected abstract double _itemYoffset();
	protected abstract ResourceLocation __getTexture();
	protected abstract WaveFrontObjectVAO __getModel();
	boolean buttonPressed = false;
	LeafiaGripOffsetHelper genericGrip = new LeafiaGripOffsetHelper()
			.get(TransformType.GUI)
			.setPosition(-2.05,0,-1.25).setRotation(-39,65,-54).getHelper()

			.get(TransformType.FIRST_PERSON_RIGHT_HAND)
			.setPosition(-4.25,4.5,0).setRotation(-115,0,0).getHelper()

			.get(TransformType.FIRST_PERSON_LEFT_HAND)
			.setPosition(5,0,0).getHelper()

			.get(TransformType.THIRD_PERSON_RIGHT_HAND)
			.setPosition(-1.25,0.85,-2).getHelper()

			.get(TransformType.FIXED)
			.setScale(0.25).setPosition(-2,2,-1.25).setRotation(-90,0,0).getHelper();
	@Override
	public void renderByItem(ItemStack itemStackIn) {
		if (debug) {
			if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
				if (!buttonPressed)
					offset += Keyboard.isKeyDown(Keyboard.KEY_TAB) ? 0.005 : 0.05;
				buttonPressed = true;
			} else if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
				if (!buttonPressed)
					offset -= Keyboard.isKeyDown(Keyboard.KEY_TAB) ? 0.005 : 0.05;
				buttonPressed = true;
			} else if (Keyboard.isKeyDown(Keyboard.KEY_ADD)) {
				if (!buttonPressed)
					scale -= Keyboard.isKeyDown(Keyboard.KEY_TAB) ? 0.01 : 0.1;
				buttonPressed = true;
			} else if (Keyboard.isKeyDown(Keyboard.KEY_SUBTRACT)) {
				if (!buttonPressed)
					scale += Keyboard.isKeyDown(Keyboard.KEY_TAB) ? 0.01 : 0.1;
				buttonPressed = true;
			} else if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
				if (!buttonPressed) {
					EntityPlayer player = Minecraft.getMinecraft().player;
					Minecraft.getMinecraft().ingameGUI.getChatGUI().clearChatMessages(false);
					player.sendMessage(new TextComponentString("-- Current Configuration --").setStyle(new Style().setColor(TextFormatting.AQUA)));
					player.sendMessage(new TextComponentString(String.format(" Scale: %01.2f",scale)));
					player.sendMessage(new TextComponentString(String.format(" Offset: %01.2f",offset)));
				}
				buttonPressed = true;
			} else
				buttonPressed = false;
		}
		LeafiaGls.pushMatrix();
		if (type.equals(TransformType.GUI)) {
			LeafiaGls.enableLighting();
			LeafiaGls.translate(offset,0,-0.01);
		}
		genericGrip.apply(type);
		GL11.glScaled(10/scale, 10/scale, 10/scale);
		renderCommon(itemStackIn);
		if (type.equals(TransformType.GUI))
			LeafiaGls.disableLighting();
		LeafiaGls.popMatrix();
	}
	//			public void renderInventory() {
//                /*GL11.glRotated(-65,1,0,0);
//                GL11.glRotated(25,0,0,1);
//                GL11.glTranslated(0,-_itemYoffset(),0);*/
//			}
	public void renderCommon() {
		GL11.glScaled(0.5, 0.5, 0.5);
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		bindTexture(__getTexture());
		__getModel().renderAll();
		GlStateManager.shadeModel(GL11.GL_FLAT);
	}
	public static void bindTexture(ResourceLocation loc) {
		NTMRenderHelper.bindTexture(loc);
	}
}
