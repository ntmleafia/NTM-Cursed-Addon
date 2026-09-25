package com.custom_hbm;

import com.leafia.init.LeafiaSoundEvents;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SideOnly(Side.CLIENT)
public class GuiEasterEgg extends GuiScreen {
	public ISound music;
	public static PositionedSoundRecord loopedMusic(SoundEvent soundIn) {
		return new PositionedSoundRecord(soundIn.getSoundName(), SoundCategory.MASTER, 1.0F, 1.0F, true, 0, ISound.AttenuationType.NONE, 0.0F, 0.0F, 0.0F);
	}

	long startTimestamp;
	public GuiEasterEgg() {
		startTimestamp = System.currentTimeMillis();
	}

	@Override
	public void onGuiClosed() {
		if (music != null)
			mc.getSoundHandler().stopSound(music);
		super.onGuiClosed();
	}

	@Override
	public void initGui() {
		if (music == null) {
			music = loopedMusic(LeafiaSoundEvents.bgm013_50);
			mc.getSoundHandler().playSound(music);
		}
	}

	@Override
	protected void keyTyped(char typedChar,int keyCode) throws IOException {
		//super.keyTyped(typedChar,keyCode); nope!
	}

	long timeRemaining;
	Random rand = new Random();
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		String msg = "remove1.7.10";
		int leng = fontRenderer.getStringWidth(msg);
		for (int i = 0; i <= width/leng; i++) {
			for (int j = 0; j <= height/10; j++) {
				int brightness = 100+rand.nextInt(156);
				fontRenderer.drawString(msg,i*leng,j*10,brightness<<16);
			}
		}
		LeafiaGls.pushMatrix();
		LeafiaGls.translate(width/2d,height/2d,0);
		LeafiaGls.scale(2);
		timeRemaining = (startTimestamp+22000)-System.currentTimeMillis();
		String countdown = Integer.toString((int)Math.ceil(timeRemaining/1000d));
		LeafiaGls.translate(-fontRenderer.getStringWidth(countdown)/2d,-4,0);
		LeafiaGls.translate(rand.nextGaussian(),rand.nextGaussian(),0);
		fontRenderer.drawString(countdown,0,0,0xFF0000,true);
		LeafiaGls.popMatrix();
		if (timeRemaining <= 0)
			mc.displayGuiScreen(null);
	}

	public static ResourceLocation bg = new ResourceLocation("textures/blocks/bedrock.png");
	@Override
	public void drawBackground(int tint)
	{
		GlStateManager.disableLighting();
		GlStateManager.disableFog();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		this.mc.getTextureManager().bindTexture(bg);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		float f = 32.0F;
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		bufferbuilder.pos(0.0D, (double)this.height, 0.0D).tex(0.0D, (double)((float)this.height / 32.0F + (float)tint)).color(64, 64, 64, 255).endVertex();
		bufferbuilder.pos((double)this.width, (double)this.height, 0.0D).tex((double)((float)this.width / 32.0F), (double)((float)this.height / 32.0F + (float)tint)).color(64, 64, 64, 255).endVertex();
		bufferbuilder.pos((double)this.width, 0.0D, 0.0D).tex((double)((float)this.width / 32.0F), (double)tint).color(64, 64, 64, 255).endVertex();
		bufferbuilder.pos(0.0D, 0.0D, 0.0D).tex(0.0D, (double)tint).color(64, 64, 64, 255).endVertex();
		tessellator.draw();
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		//if (button == dismiss)
		//	mc.displayGuiScreen(null);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}


