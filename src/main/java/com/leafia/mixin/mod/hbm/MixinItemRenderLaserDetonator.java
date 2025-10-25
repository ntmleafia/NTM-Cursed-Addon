package com.leafia.mixin.mod.hbm;

import com.hbm.main.ModEventHandlerClient;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRendererDetonatorLaser;
import com.hbm.render.item.TEISRBase;
import com.hbm.util.RenderUtil;
import com.leafia.dev.items.LeafiaGripOffsetHelper;
import com.leafia.eventbuses.LeafiaClientListener;
import com.leafia.eventbuses.LeafiaClientListener.HandlerClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import java.util.Random;

@Mixin(value = ItemRendererDetonatorLaser.class)
public class MixinItemRenderLaserDetonator extends TEISRBase {

	@Unique
	private static final LeafiaGripOffsetHelper offsets = new LeafiaGripOffsetHelper()
			.get(TransformType.GUI)
			.setScale(0.25).setPosition(-2.25,1.45,-1.25).setRotation(-40,0,0).getHelper()

			.get(TransformType.FIXED)
			.copySettings(TransformType.GUI).getHelper()

			.get(TransformType.FIRST_PERSON_RIGHT_HAND)
			.setScale(0.25).setPosition(-2.25,1,-1.25).setRotation(-10,0,5).getHelper()
			.get(TransformType.FIRST_PERSON_LEFT_HAND) // Whoops
			.setScale(0.25).setPosition(12.75,4.75,-13.75).setRotation(0,-5,-10).getHelper()
			.get(TransformType.THIRD_PERSON_RIGHT_HAND)
			.setScale(0.25).setPosition(-2.6,1.6,-0.6).setRotation(-53,7,-7).getHelper()
			.get(TransformType.THIRD_PERSON_LEFT_HAND)
			.setScale(1).setPosition(1.25,0,0).setRotation(0,-9,0).getHelper()

			.get(TransformType.GROUND)
			.setScale(0.25).setPosition(-2,0.25,-1.75).setRotation(0,0,0).getHelper();

	LeafiaGripOffsetHelper.LeafiaGripOffset ADS = new LeafiaGripOffsetHelper.LeafiaGripOffset(offsets)
			.setScale(1.01).setPosition(-3.35,1,-0.9).setRotation(-14,1,-5);

	/**
	 * @author Leafia
	 * @reason ADS view.
	 */
	@Overwrite
	public void renderByItem(ItemStack itemStackIn) {
		GlStateManager.pushMatrix();

		final boolean prevCull = RenderUtil.isCullEnabled();
		final int prevShade = RenderUtil.getShadeModel();

		if (!prevCull) GlStateManager.enableCull();
		if (prevShade != GL11.GL_SMOOTH) GlStateManager.shadeModel(GL11.GL_SMOOTH);

		Minecraft.getMinecraft().renderEngine.bindTexture(ResourceManager.detonator_laser_tex);

		switch(type) {
			case FIRST_PERSON_LEFT_HAND:
			case FIRST_PERSON_RIGHT_HAND:
				float ads = Math.signum(HandlerClient.getViewADS(Minecraft.getMinecraft().player));
				if (ads == ((type.equals(TransformType.FIRST_PERSON_RIGHT_HAND)) ? 1 : -1)) {
					offsets.apply(TransformType.FIRST_PERSON_RIGHT_HAND);
					offsets.applyCustomOffset(ADS);
				} else
					offsets.apply(type);
				break;
			default:
				offsets.apply(type);
				break;
		}

		ResourceManager.detonator_laser.renderPart("Main");
		GlStateManager.pushMatrix();
		final boolean prevLighting = RenderUtil.isLightingEnabled();
		final boolean prevCull2 = RenderUtil.isCullEnabled();
		final boolean prevTex2D = RenderUtil.isTexture2DEnabled();
		final boolean prevBlend = RenderUtil.isBlendEnabled();
		final int prevSrc = RenderUtil.getBlendSrcFactor();
		final int prevDst = RenderUtil.getBlendDstFactor();
		final int prevSrcAlpha = RenderUtil.getBlendSrcAlphaFactor();
		final int prevDstAlpha = RenderUtil.getBlendDstAlphaFactor();
		final float prevR = RenderUtil.getCurrentColorRed();
		final float prevG = RenderUtil.getCurrentColorGreen();
		final float prevB = RenderUtil.getCurrentColorBlue();
		final float prevA = RenderUtil.getCurrentColorAlpha();
		if (prevLighting) GlStateManager.disableLighting();
		if (prevCull2) GlStateManager.disableCull();
		if (!prevBlend) GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		if (prevTex2D) GlStateManager.disableTexture2D();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
		GlStateManager.color(1F, 0F, 0F, 1F);
		ResourceManager.detonator_laser.renderPart("Lights");
		GlStateManager.color(1F, 1F, 1F, 1F);
		final float px = 0.0625F;
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.5626F, px * 18, -px * 14);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

		int sub = 32;
		double width = px * 8;
		double len = width / sub;
		double time = System.currentTimeMillis() / -100D;
		double amplitude = 0.075;
		float red = 1.0F, green = 1.0F, blue = 0.0F; // Yellow color (0xFFFF00)

		for (int i = 0; i < sub; i++) {
			double h0 = Math.sin(i * 0.5 + time) * amplitude;
			double h1 = Math.sin((i + 1) * 0.5 + time) * amplitude;

			buffer.pos(0, -px * 0.25 + h1, len * (i + 1)).color(red, green, blue, 1.0F).endVertex();
			buffer.pos(0, px * 0.25 + h1, len * (i + 1)).color(red, green, blue, 1.0F).endVertex();
			buffer.pos(0, px * 0.25 + h0, len * i).color(red, green, blue, 1.0F).endVertex();
			buffer.pos(0, -px * 0.25 + h0, len * i).color(red, green, blue, 1.0F).endVertex();
		}

		tessellator.draw();
		GlStateManager.popMatrix();
		final boolean texWasEnabledForHud = RenderUtil.isTexture2DEnabled();
		if (!texWasEnabledForHud) GlStateManager.enableTexture2D();

		GlStateManager.pushMatrix();
		String s;
		Random rand = new Random(System.currentTimeMillis() / 500);
		FontRenderer font = Minecraft.getMinecraft().fontRenderer;
		float f3 = 0.01F;
		GlStateManager.translate(0.5625F, 1.3125F, 0.875F);
		GlStateManager.scale(f3, -f3, f3);
		GlStateManager.rotate(90F, 0F, 1F, 0F);
		GlStateManager.glNormal3f(0.0F, 0.0F, -1.0F * f3);

		GlStateManager.translate(3F, -2F, 0.2F);

		for (int i = 0; i < 3; i++) {
			s = (rand.nextInt(900000) + 100000) + "";
			font.drawString(s, 0, 0, 0xff0000);
			GlStateManager.translate(0F, 12.5F, 0F);
		}
		GlStateManager.popMatrix();

		if (!texWasEnabledForHud) GlStateManager.disableTexture2D();
		GlStateManager.color(prevR, prevG, prevB, prevA);
		GlStateManager.tryBlendFuncSeparate(prevSrc, prevDst, prevSrcAlpha, prevDstAlpha);
		if (!prevBlend) GlStateManager.disableBlend();
		if (prevCull2) GlStateManager.enableCull();
		if (prevTex2D) GlStateManager.enableTexture2D();
		if (prevLighting) GlStateManager.enableLighting();

		GlStateManager.popMatrix();
		if (prevShade != GL11.GL_SMOOTH) GlStateManager.shadeModel(prevShade);
		if (!prevCull) GlStateManager.disableCull();

		GlStateManager.popMatrix();
	}
}
