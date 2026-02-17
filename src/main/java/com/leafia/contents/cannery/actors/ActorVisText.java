package com.leafia.contents.cannery.actors;

import com.hbm.wiaj.JarScript;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class ActorVisText extends ActorVisBase {
	final String[] texts;
	final float scale;
	final int forceUnicode;
	public ActorVisText(JarScript script,int x,int y,int z,int color,float scale,String... texts) {
		super(script,x,y,z,color);
		forceUnicode = -1;
		this.texts = texts;
		this.scale = scale;
	}
	public ActorVisText(JarScript script,int x,int y,int z,int color,float scale,boolean setUnicode,String... texts) {
		super(script,x,y,z,color);
		this.texts = texts;
		forceUnicode = setUnicode ? 1 : 0;
		this.scale = scale;
	}
	@Override
	public void renderContents() {
		LeafiaGls.pushMatrix();
		LeafiaGls.translate(0.5f,0.5f,0.5f);
		LeafiaGls.rotate((float)script.yaw(),0,-1,0);
		LeafiaGls.rotate((float)script.pitch(),-1,0,0);
		FontRenderer font = Minecraft.getMinecraft().fontRenderer;
		boolean unicode = font.getUnicodeFlag();
		if (forceUnicode >= 0)
			font.setUnicodeFlag(forceUnicode == 1);
		LeafiaGls.scale(1/16d/2*scale);
		LeafiaGls.rotate(180,0,0,1);
		int lineHeight = 9;
		for (int i = 0; i < texts.length; i++) {
			LeafiaGls.pushMatrix();
			String s = texts[i];
			double offset = (i - (texts.length - 1) / 2d) * lineHeight;
			LeafiaGls.translate(-font.getStringWidth(s) / 2d,-7 / 2d + offset,0);
			font.drawString(s,0,0,color);
			LeafiaGls.popMatrix();
		}
		if (forceUnicode >= 0)
			font.setUnicodeFlag(unicode);
		LeafiaGls.popMatrix();
	}
}
