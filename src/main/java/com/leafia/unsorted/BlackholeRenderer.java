package com.leafia.unsorted;

import com.hbm.render.NTMRenderHelper;
import com.hbm.render.loader.WaveFrontObjectVAO;
import com.leafia.AddonBase;
import com.leafia.dev.render.LeafiaBrush;
import com.leafia.dev.render.LeafiaBrush.BrushMode;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import static com.leafia.AddonBase.getIntegrated;
import static com.leafia.init.ResourceInit.getVAO;

@SideOnly(Side.CLIENT)
public class BlackholeRenderer {
	static WaveFrontObjectVAO mdl = getVAO(getIntegrated("miscellaneous/blackhole/blackhole.obj"));
	static ResourceLocation disk01 = getIntegrated("miscellaneous/blackhole/disk_01.png");
	static ResourceLocation disk02 = getIntegrated("miscellaneous/blackhole/disk_02.png");
	static float getRotation(float speed) {
		return (float)MathHelper.positiveModulo(System.currentTimeMillis()/1000d*360,360/speed)*speed;
	}
	public static void draw(boolean large) {
		{
			LeafiaGls._push();
			LeafiaGls.disableAlpha();
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,240F,240F);
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
			LeafiaGls.enableCull();
			{
				NTMRenderHelper.bindTexture(AddonBase.solid);
				LeafiaGls.color(0,0,0);
				mdl.renderPart("Core");
				LeafiaGls.color(1,1,1);
			}
			LeafiaGls.enableBlend();
			LeafiaGls.blendFunc(SourceFactor.SRC_ALPHA,DestFactor.ONE);
			{
				NTMRenderHelper.bindTexture(AddonBase.solid_e);
				LeafiaGls.color(1,0.35f,0.127f);
				{
					LeafiaGls.pushMatrix();
					LeafiaGls.rotate(getRotation(0.75f),0,1,0);
					mdl.renderPart("Lens");
					LeafiaGls.pushMatrix();
					LeafiaGls.scale(0.875);
					mdl.renderPart("Lens");
					LeafiaGls.popMatrix();
					LeafiaGls.pushMatrix();
					LeafiaGls.scale(0.75);
					mdl.renderPart("Lens");
					LeafiaGls.popMatrix();
					LeafiaGls.popMatrix();
				}
				LeafiaGls.color(1,1,1);
			}
			LeafiaGls.disableCull();
			if (large) {
				LeafiaBrush brush = LeafiaBrush.instance;
				NTMRenderHelper.bindTexture(AddonBase.solid_e);
				for (int y = -1; y <= 1; y+=2) {
					brush.startDrawing(BrushMode.TRIANGLE_FAN,DefaultVertexFormats.POSITION_TEX_COLOR);
					brush.addVertexWithUVAndColor(0,0,0,0,0,1,1,1,0.35f);
					for (int i = 0; i <= 8; i++) {
						double rad = Math.PI*2/8*i;
						brush.addVertexWithUVAndColor(0.4*Math.cos(rad),y*8,0.4*Math.sin(rad),0,0,1,1,1,0);
					}
					brush.draw();
				}
			}
			if (large) {
				LeafiaGls.pushMatrix();
				LeafiaGls.rotate(getRotation(0.25f),0,1,0);
				LeafiaGls.color(0.75f,0.75f,0.75f);
				NTMRenderHelper.bindTexture(disk02);
				mdl.renderPart("DiskOuter");
				LeafiaGls.color(1,1,1);
				LeafiaGls.popMatrix();
			}
			LeafiaGls.enableCull();
			{
				LeafiaGls.pushMatrix();
				//LeafiaGls.scale(1.25,1,1.25);
				LeafiaGls.rotate(getRotation(0.5f),0,1,0);
				if (large)
					LeafiaGls.color(0.75f,0.75f,0.75f);
				NTMRenderHelper.bindTexture(disk01);
				mdl.renderPart("DiskInner");
				LeafiaGls.color(1,1,1);
				LeafiaGls.popMatrix();
			}
			LeafiaGls.enableCull();
			LeafiaGls._pop();
		}
	}
}
