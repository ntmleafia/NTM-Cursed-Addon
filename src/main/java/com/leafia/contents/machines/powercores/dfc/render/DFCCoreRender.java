package com.leafia.contents.machines.powercores.dfc.render;


import com.hbm.main.ResourceManager;
import com.hbm.render.NTMRenderHelper;
import com.hbm.render.RenderSparks;
import com.hbm.render.amlfrom1710.Vec3;
import com.hbm.render.loader.HFRWavefrontObject;
import com.hbm.render.loader.WaveFrontObjectVAO;
import com.hbm.render.misc.BeamPronter;
import com.hbm.render.misc.BeamPronter.EnumBeamType;
import com.hbm.render.misc.BeamPronter.EnumWaveType;
import com.hbm.tileentity.machine.TileEntityCore;
import com.leafia.AddonBase;
import com.leafia.dev.math.FiaMatrix;
import com.leafia.dev.math.FiaMatrix.RotationOrder;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityCore;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityCore.Cores;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityCore.DFCShock;
import com.leafia.transformer.LeafiaGls;
import com.llib.math.LeafiaColor;
import com.llib.math.MathLeafia;
import com.llib.technical.LeafiaEase;
import com.llib.technical.LeafiaEase.Direction;
import com.llib.technical.LeafiaEase.Ease;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.util.ConcurrentModificationException;
import java.util.Random;

import static com.leafia.init.ResourceInit.getVAO;

public class DFCCoreRender extends TileEntitySpecialRenderer<TileEntityCore> {
	public static WaveFrontObjectVAO[] deformSphere = new WaveFrontObjectVAO[10];
	static {
		for (int i = 0; i < 10; i++)
			deformSphere[i] = getVAO(new ResourceLocation("leafia", "models/leafia/deformed_sphere/deform"+i+".obj"));
	}
	public static WaveFrontObjectVAO instability_ring = getVAO(new ResourceLocation("leafia","models/leafia/ecr_instability_ring.obj"));

	@Override
	public boolean isGlobalRenderer(TileEntityCore te) {
		return true;
	}

	@Override
	public void render(TileEntityCore core, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		IMixinTileEntityCore mixin = (IMixinTileEntityCore)core;
		if (mixin.getDFCTemperature() < 100) {
			renderStandby(core, x, y, z);
		} else {

			GL11.glPushMatrix();
			GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
			//GL11.glRotatef(-Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
			//GL11.glRotatef(Minecraft.getMinecraft().getRenderManager().playerViewX - 90, 1.0F, 0.0F, 0.0F);
			GL11.glTranslated(-0.5, -0.5, -0.5);

			renderOrb(core, 0, 0, 0, partialTicks);
			GL11.glPopMatrix();
			if (mixin.getDFCClientType() == Cores.glitch) {
				GlStateManager.matrixMode(GL11.GL_TEXTURE);
				GlStateManager.loadIdentity();

				EntityPlayer player = Minecraft.getMinecraft().player;
				Vec3d playerPos = new Vec3d(player.posX,player.posY,player.posZ);
				Vec3d corePos = new Vec3d(core.getPos()).add(0.5,0.5,0.5);

				double distance = corePos.distanceTo(playerPos);
				float modifier = (float)Math.pow(MathHelper.clamp(1 - (distance - 3) / 125, 0, 1), 3);
				float modifier2 = (float)Math.pow(MathHelper.clamp(1 - (distance - 3) / 125, 0, 1), 12);

				GL11.glTranslatef((float)core.getWorld().rand.nextGaussian()*100*modifier2,(float)core.getWorld().rand.nextGaussian()*100*modifier2,(float)core.getWorld().rand.nextGaussian()*100*modifier2);

				GlStateManager.matrixMode(GL11.GL_MODELVIEW);
				GL11.glTranslatef((core.getWorld().rand.nextFloat()-0.5f)*modifier,(core.getWorld().rand.nextFloat()-0.5f)*modifier,(core.getWorld().rand.nextFloat()-0.5f)*modifier);
			}
		}
		if (mixin.getDFCJammerPos() != null) {
			GL11.glPushMatrix();
			GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
			LeafiaColor colorBlast = new LeafiaColor(1, 0.5, 0);
			LeafiaColor colorJammer = new LeafiaColor(0, 0.75, 1);
			Vec3d vec = new Vec3d(mixin.getDFCJammerPos().subtract(core.getPos()));
			BeamPronter.prontBeam(
					vec,
					EnumWaveType.RANDOM, EnumBeamType.SOLID,
					colorBlast.toInARGB(), colorBlast.toInARGB(),
					(int) (core.getWorld().getTotalWorldTime() % 1000),
					(int) vec.length(), 0.5f, 1, 0.2f
			);
			BeamPronter.prontBeam(
					vec,
					EnumWaveType.RANDOM, EnumBeamType.SOLID,
					colorJammer.toInARGB(), colorJammer.toInARGB(),
					(int) ((core.getWorld().getTotalWorldTime() + 500) % 1000),
					(int) vec.length(), 0.5f, 1, 0.2f
			);
			GL11.glPopMatrix();
		}
		try {
			for (DFCShock shock : mixin.getDfcShocks()) {
				Vec3d lastPos = null;
				LeafiaGls.pushMatrix();
				GL11.glTranslated(x-core.getPos().getX(),y-core.getPos().getY(),z-core.getPos().getZ());
				if (core.getWorld().rand.nextInt(4) >= 1) {
					for (Vec3d pos : shock.poses) {
						if (lastPos != null) {
							if (pos.distanceTo(lastPos) < 0.1) continue;
							LeafiaGls.pushMatrix();
							LeafiaGls.translate(lastPos);
							Vec3d vec3 = pos.subtract(lastPos);
							BeamPronter.prontBeam(
									vec3,
									EnumWaveType.STRAIGHT,EnumBeamType.SOLID,
									0x5B1D00,0x7F7F7F,
									0,1,0,2,0.25f
							);
							LeafiaGls.popMatrix();
						}
						lastPos = pos;
					}
				}
				LeafiaGls.popMatrix();
			}
		} catch (ConcurrentModificationException ignored) {} // fuck you java array iterations
	}

	public void renderStandby(TileEntityCore core, double x, double y, double z) {
		IMixinTileEntityCore mixin = (IMixinTileEntityCore)core;
		
		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
		GlStateManager.disableLighting();
		GlStateManager.enableCull();
		//GlStateManager.disableTexture2D();
		NTMRenderHelper.bindTexture(AddonBase.solid);

		GL11.glScalef(0.25F, 0.25F, 0.25F);
		float brightness = (float) Math.pow(mixin.getDFCTemperature() / 100d, 1.5);
		GlStateManager.color(0.1F + brightness * .9f, 0.1F + brightness * .9f, 0.1F + brightness * .9f);
		ResourceManager.sphere_uv.renderAll();

		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
		GL11.glScalef(1.25F, 1.25F, 1.25F);
		GlStateManager.color(0.1F + brightness * .9f, 0.2F + (((float) mixin.getDFCTemperature() / 100f) * 0.25f + brightness * 0.75f) * .8f, 0.4F + ((float) mixin.getDFCTemperature() / 100f) * .6f);
		ResourceManager.sphere_uv.renderAll();
		GlStateManager.disableBlend();

		//GlStateManager.enableTexture2D();
		GlStateManager.enableLighting();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);

		if (core.getWorld().rand.nextInt(50) == 0) {
			for (int i = 0; i < 3; i++) {
				RenderSparks.renderSpark((int) System.currentTimeMillis() / 100 + i * 10000, 0, 0, 0, 1.5F, 5, 10, 0x00FFFF, 0xFFFFFF);
				RenderSparks.renderSpark((int) System.currentTimeMillis() / 50 + i * 10000, 0, 0, 0, 3F, 5, 10, 0x00FFFF, 0xFFFFFF);
			}
		}
		GlStateManager.color(1F, 1F, 1F);
		GL11.glPopMatrix();
	}

	public void renderDraconic(FiaMatrix face,double scale,double distance,float partialTicks) {
		int segments = 6;
		double startRadius = scale*0.5;
		double endRadius = 0.3;
		int resolution = 8;
		bindTexture(new ResourceLocation("leafia","textures/models/leafia/null-noisemap.png"));
		//CompositeBrush brush = CompositeBrush.instance;
		//brush.startDrawingQuads();

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buf = tessellator.getBuffer();

		buf.begin(GL11.GL_QUADS,DefaultVertexFormats.POSITION_TEX_COLOR);
		GlStateManager.shadeModel(GL11.GL_SMOOTH);

		GlStateManager.matrixMode(GL11.GL_TEXTURE);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();

		float movement = (Minecraft.getMinecraft().player.ticksExisted + partialTicks) * 0.005F * 10;
		GL11.glTranslatef(0, movement, 0);

		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		for (int i = 0; i < segments; i++) {
			double ratio0 = i/(double)segments;
			double ratio1 = (i+1)/(double)segments;
			FiaMatrix mat0 = face.translate(0,0,-distance*ratio0);
			FiaMatrix mat1 = face.translate(0,0,-distance*ratio1);
			double ratio0r = Math.pow(ratio0,0.5);
			double ratio1r = Math.pow(ratio1,0.5);
			double radius0 = startRadius+(endRadius-startRadius)*ratio0r;
			double radius1 = startRadius+(endRadius-startRadius)*ratio1r;
			for (int j = 0; j < resolution; j++) {
				float ratioAngle0 = j/(float)resolution;
				float ratioAngle1 = (j+1)/(float)resolution;
				float angle0 = ratioAngle0*360;
				float angle1 = ratioAngle1*360;
				FiaMatrix rot00 = mat0.rotate(RotationOrder.XYZ,0,0,angle0).translate(0,radius0,0);
				FiaMatrix rot01 = mat0.rotate(RotationOrder.XYZ,0,0,angle1).translate(0,radius0,0);
				FiaMatrix rot10 = mat1.rotate(RotationOrder.XYZ,0,0,angle0).translate(0,radius1,0);
				FiaMatrix rot11 = mat1.rotate(RotationOrder.XYZ,0,0,angle1).translate(0,radius1,0);
				//brush.addVertexWithUV(rot10.getX(),rot10.getY(),rot10.getZ(),0,1);
				//brush.addVertexWithUV(rot11.getX(),rot11.getY(),rot11.getZ(),1,1);
				//brush.addVertexWithUV(rot01.getX(),rot01.getY(),rot01.getZ(),1,0);
				//brush.addVertexWithUV(rot00.getX(),rot00.getY(),rot00.getZ(),0,0);
				buf.pos(rot10.getX(),rot10.getY(),rot10.getZ()).tex(0,1).color(0.173f,0.549f,0.788f,(float)ratio1).endVertex();
				buf.pos(rot11.getX(),rot11.getY(),rot11.getZ()).tex(1,1).color(0.173f,0.549f,0.788f,(float)ratio1).endVertex();
				buf.pos(rot01.getX(),rot01.getY(),rot01.getZ()).tex(1,0).color(0.173f,0.549f,0.788f,(float)ratio0).endVertex();
				buf.pos(rot00.getX(),rot00.getY(),rot00.getZ()).tex(0,0).color(0.173f,0.549f,0.788f,(float)ratio0).endVertex();
			}
		}
		//brush.draw();
		tessellator.draw();
		GlStateManager.shadeModel(GL11.GL_FLAT);
		GlStateManager.matrixMode(GL11.GL_TEXTURE);
		GlStateManager.loadIdentity();
		GlStateManager.popMatrix();

		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
	}

	public void renderOrb(TileEntityCore core, double x, double y, double z, float partialTicks) {
		IMixinTileEntityCore mixin = (IMixinTileEntityCore)core;
		
		GL11.glPushMatrix();
		GL11.glTranslated(x+0.5,y+0.5,z+0.5);
		GL11.glPushMatrix();

		int color = core.color;
		float r = (color>>16&255)/255F;
		float g = (color>>8&255)/255F;
		float b = (color&255)/255F;

		int tot = core.tanks[0].getCapacity()+core.tanks[1].getCapacity();
		int fill = core.tanks[0].getFluidAmount()+core.tanks[1].getFluidAmount();

		float scale = (float) Math.log(mixin.getDFCTemperature()/50+1) /* * ((float) fill / (float) tot)*/+0.5F;
		double rot = 0;
		if (mixin.getDFCCollapsing() > 0.97) {
			double percent = (mixin.getDFCCollapsing()-0.97)/0.03;
			LeafiaEase ease = new LeafiaEase(Ease.EXPO,Direction.I);
			scale *= (float) ease.get(ease.get(percent),1,0);
			rot = ease.get(percent)*1000;
		}
		GlStateManager.disableCull();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,240F,240F);
		GlStateManager.disableLighting();
		int colorC = mixin.getDFCColorCatalyst();
		float rC = (colorC>>16&255)/255F;
		float gC = (colorC>>8&255)/255F;
		float bC = (colorC&255)/255F;

		GL11.glScalef(scale,scale,scale);

		GlStateManager.enableCull();
		bindTexture(new ResourceLocation("leafia","textures/solid_emissive.png")); // shader fix
		//GlStateManager.disableTexture2D();


		if (mixin.getDFCRingAlpha() > 0) {
			GlStateManager.pushMatrix();
			GlStateManager.disableCull();
			GlStateManager.scale(0.025f,0.025f,0.025f);
			GlStateManager.color(rC,gC,bC,mixin.getDFCRingAlpha());
			GlStateManager.pushMatrix();
			GlStateManager.rotate(mixin.getDFCRingAngle()+mixin.getDFCRingSpinSpeed()*partialTicks,0,1,0);
			instability_ring.renderPart("InstabilityInnerRing");
			GlStateManager.popMatrix();
			GlStateManager.rotate(mixin.getDFCRingAngle()+mixin.getDFCRingSpinSpeed()*partialTicks,0,-1,0);
			instability_ring.renderPart("InstabilityRing");
			GlStateManager.enableCull();
			GlStateManager.popMatrix();
		}
		GlStateManager.color(r,g,b,1.0F);
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

		GL11.glRotated(rot,1,1,1);
		GL11.glScalef(0.5F,0.5F,0.5F);
		ResourceManager.sphere_ruv.renderAll();
		GL11.glScalef(2F, 2F, 2F);

		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);


		if (mixin.getDFCClientType() == Cores.ams_core_eyeofharmony || mixin.getDFCClientType() == Cores.glitch) {
			GL11.glScalef(0.5F,0.5F,0.5F);
			//GL11.glPushMatrix();
			//GL11.glRotatef(core.getWorld().rand.nextFloat()*360,1,0,0);
			//GL11.glRotatef(core.getWorld().rand.nextFloat()*360,0,1,0);
			//GL11.glRotatef(core.getWorld().rand.nextFloat()*360,0,0,1);
		}
		for (int i = 6; i <= 10; i++) {
			GL11.glPushMatrix();
			GL11.glScalef(i * 0.1F, i * 0.1F, i * 0.1F);
			if (mixin.getDFCClientType() == Cores.ams_core_eyeofharmony || mixin.getDFCClientType() == Cores.glitch)
				deformSphere[core.getWorld().rand.nextInt(10)].renderAll();
			else
				ResourceManager.sphere_ruv.renderAll();
			GL11.glPopMatrix();
		}
		if (mixin.getDFCClientType() == Cores.ams_core_eyeofharmony || mixin.getDFCClientType() == Cores.glitch) {
			//GL11.glPopMatrix();
			GL11.glScalef(2F,2F,2F);
		}
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		if (mixin.getDFCClientType() == Cores.ams_core_sing) {
			GL11.glPushMatrix();
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.color(0.11f, 0.11f, 0.11f);
			GL11.glScalef(-scale * 1.15f, -scale * 1.15f, -scale * 1.15f);
			ResourceManager.sphere_ruv.renderAll();
			GL11.glPopMatrix();

			GlStateManager.blendFunc(SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.color(0.529f, 0.722f, 1, 0.5f);
			double tr = MathHelper.positiveModulo(System.currentTimeMillis() / 1000d, 3000) / 2 * Math.PI;
			double add = 0.4; // 0.6
			GL11.glRotated(tr / 3 * (180 / Math.PI), 0, 1, 2);
			for (int d = 0; d < 16; d++) {
				GL11.glPushMatrix();
				double t = tr + add * d;
				GL11.glRotated(-MathLeafia.smoothLinear(Math.abs(MathHelper.positiveModulo(t / 3 / Math.PI, 2) - 1), 0.5) * 180 * 3, 0, 0, 1);
				GL11.glRotated(Math.sin(t / 3) * 135, 0, 1, 0);
				GL11.glTranslated(0, 0, -scale * 1.4 / 2);
				GL11.glScalef(-0.25f, -0.25f, -0.25f);
				ResourceManager.sphere_ruv.renderAll();
				GL11.glPopMatrix();
			}
		} else if (mixin.getDFCClientType() == Cores.ams_core_eyeofharmony) {
			LeafiaGls.disableCull();
			LeafiaGls.pushMatrix();
			LeafiaGls.rotate(mixin.getDFCAngle()+partialTicks*mixin.getDFCLightRotateSpeed(),1,1,1);
			renderFlash(0.024f+core.getWorld().rand.nextFloat()*0.001f,20,1,r,g,b);
			LeafiaGls.enableCull();
			LeafiaGls.popMatrix();
		}

		GL11.glPopMatrix();

		if (mixin.getDFCClientType() == Cores.ams_core_wormhole) {
			LeafiaGls.disableCull();
			GlStateManager.color(rC,gC,bC,1);
			GlStateManager.disableAlpha();
			GlStateManager.depthMask(false);

			for (BlockPos pos : mixin.getDFCPrevComponentPositions()) {
				Vec3d relative = new Vec3d(pos.subtract(core.getPos()));
				FiaMatrix mat = new FiaMatrix(new Vec3d(0,0,0),relative);
				renderDraconic(mat.translate(0,0,-2),scale,relative.length()-2.5,partialTicks);
			}
			LeafiaGls.enableCull();
			GlStateManager.enableAlpha();
			GlStateManager.depthMask(true);
		}
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		GlStateManager.disableBlend();
		GlStateManager.enableLighting();
		//GlStateManager.enableTexture2D();
		GL11.glPopMatrix();
	}
	private void renderFlash(float scale, double intensity, double alpha, float r, float g, float b) {

		GL11.glScalef(0.2F, 0.2F, 0.2F);

		double inverse = 1.0D - intensity;

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buf = tessellator.getBuffer();
		RenderHelper.disableStandardItemLighting();

		Random random = new Random(432L);
		//GlStateManager.disableTexture2D();
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
		GlStateManager.disableAlpha();
		GlStateManager.enableCull();
		GlStateManager.depthMask(false);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);

		GL11.glPushMatrix();

		for(int i = 0; i < 300; i++) {

			GL11.glRotatef(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(random.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);

			float vert1 = (random.nextFloat() * 20.0F + 5.0F + 1 * 10.0F) * (float)(intensity * scale);
			float vert2 = (random.nextFloat() * 2.0F + 1.0F + 1 * 2.0F) * (float)(intensity * scale);

			buf.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
			buf.pos(0D, 0D, 0D).color(1.0F, 1.0F, 1.0F, (float)alpha/*(float) inverse*/).endVertex();
			buf.pos(-0.866D * vert2, vert1, -0.5D * vert2).color(r,g,b, 0.0F).endVertex();
			buf.pos(0.866D * vert2, vert1, -0.5D * vert2).color(r,g,b, 0.0F).endVertex();
			buf.pos(0.0D, vert1, 1.0D * vert2).color(r,g,b, 0.0F).endVertex();
			buf.pos(-0.866D * vert2, vert1, -0.5D * vert2).color(r,g,b, 0.0F).endVertex();
			tessellator.draw();
		}

		GL11.glPopMatrix();

		GlStateManager.depthMask(true);
		GlStateManager.disableCull();
		GlStateManager.disableBlend();
		GlStateManager.shadeModel(GL11.GL_FLAT);
		GlStateManager.color(1, 1, 1, 1);
		//GlStateManager.enableTexture2D();
		GlStateManager.enableAlpha();
		RenderHelper.enableStandardItemLighting();
	}
}
