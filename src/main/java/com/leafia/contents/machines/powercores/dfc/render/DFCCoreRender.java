// SPDX-FileCopyrightText: 2025-2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.powercores.dfc.render;


import com.hbm.main.ResourceManager;
import com.hbm.render.NTMRenderHelper;
import com.hbm.render.RenderSparks;
import com.custom_hbm.render.amlfrom1710.Vec3;
import com.hbm.render.loader.HFRWavefrontObject;
import com.hbm.render.loader.WaveFrontObjectVAO;
import com.hbm.render.misc.BeamPronter;
import com.hbm.render.misc.BeamPronter.EnumBeamType;
import com.hbm.render.misc.BeamPronter.EnumWaveType;
import com.hbm.tileentity.machine.TileEntityCore;
import com.leafia.AddonBase;
import com.leafia.dev.math.FiaMatrix;
import com.leafia.dev.render.RenderState;
import com.leafia.dev.render.SeededFlashMesh;
import com.leafia.dev.math.FiaMatrix.RotationOrder;
import com.leafia.eventbuses.LeafiaClientListener.HandlerClient;
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


import static com.leafia.init.ResourceInit.getVAO;

public class DFCCoreRender extends TileEntitySpecialRenderer<TileEntityCore> {
	private static final ResourceLocation NOISE = new ResourceLocation("leafia", "textures/models/leafia/null-noisemap.png");
    private static final ResourceLocation EMISSIVE = new ResourceLocation("leafia", "textures/solid_emissive.png");

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
        try (var renderState = new RenderState()) {
		IMixinTileEntityCore mixin = (IMixinTileEntityCore)core;
		if (mixin.getDFCTemperature() < 100) {
			renderStandby(core, x, y, z);
		} else {

			GlStateManager.pushMatrix();
			GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
			//GlStateManager.rotate(-Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
			//GlStateManager.rotate(Minecraft.getMinecraft().getRenderManager().playerViewX - 90, 1.0F, 0.0F, 0.0F);
			GlStateManager.translate(-0.5, -0.5, -0.5);

			renderOrb(core, 0, 0, 0, partialTicks);
			GlStateManager.popMatrix();
			if (mixin.getDFCClientType() == Cores.glitch) {
				//GlStateManager.matrixMode(GL11.GL_TEXTURE);
				//GlStateManager.loadIdentity();

				EntityPlayer player = Minecraft.getMinecraft().player;
				Vec3d playerPos = new Vec3d(player.posX,player.posY,player.posZ);
				Vec3d corePos = new Vec3d(core.getPos()).add(0.5,0.5,0.5);

				double distance = corePos.distanceTo(playerPos);
				float modifier = (float)Math.pow(MathHelper.clamp(1 - (distance - 3) / 125, 0, 1), 3);
				float modifier2 = (float)Math.pow(MathHelper.clamp(1 - (distance - 3) / 125, 0, 1), 12);

				float mul = 0.05f;
				HandlerClient.rx = (float)core.getWorld().rand.nextGaussian()*mul*modifier2;
				HandlerClient.ry = (float)core.getWorld().rand.nextGaussian()*mul*modifier2;
				HandlerClient.rz = (float)core.getWorld().rand.nextGaussian()*mul*modifier2;

				GlStateManager.matrixMode(GL11.GL_MODELVIEW);
				GlStateManager.translate((core.getWorld().rand.nextFloat()-0.5f)*modifier,(core.getWorld().rand.nextFloat()-0.5f)*modifier,(core.getWorld().rand.nextFloat()-0.5f)*modifier);
			}
		}
		if (mixin.getDFCJammerPos() != null) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
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
			GlStateManager.popMatrix();
		}
		try {
			for (DFCShock shock : mixin.getDfcShocks()) {
				Vec3d lastPos = null;
				LeafiaGls.pushMatrix();
				try {
					GlStateManager.translate(x-core.getPos().getX(),y-core.getPos().getY(),z-core.getPos().getZ());
					if (core.getWorld().rand.nextInt(4) >= 1) {
						for (Vec3d pos : shock.poses) {
							if (lastPos != null) {
								if (pos.distanceTo(lastPos) < 0.1) continue;
								LeafiaGls.pushMatrix();
								try {
									LeafiaGls.translate(lastPos);
									Vec3d vec3 = pos.subtract(lastPos);
									BeamPronter.prontBeam(
											vec3,
											EnumWaveType.STRAIGHT,EnumBeamType.SOLID,
											0x5B1D00,0x7F7F7F,
											0,1,0,2,0.25f
									);
								} finally {
									LeafiaGls.popMatrix();
								}
							}
							lastPos = pos;
						}
					}
				} finally {
					LeafiaGls.popMatrix();
				}
			}
		} catch (ConcurrentModificationException ignored) {} // fuck you java array iterations
	        }
    }

	public void renderStandby(TileEntityCore core, double x, double y, double z) {
        try (var state = new RenderState()) {
            GlStateManager.pushMatrix();
            try {
                GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
                GlStateManager.disableLighting();
                GlStateManager.enableCull();
                NTMRenderHelper.bindTexture(AddonBase.solid);
                GlStateManager.scale(0.25F, 0.25F, 0.25F);
                var temperature = ((IMixinTileEntityCore) core).getDFCTemperature();
                var brightness = (float) Math.pow(temperature / 100D, 1.5);
                var normalized = (float) temperature / 100F;
                var red = 0.1F + brightness * 0.9F;
                GlStateManager.color(red, red, red);
                ResourceManager.sphere_uv.renderAll();
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
                GlStateManager.scale(1.25F, 1.25F, 1.25F);
                GlStateManager.color(red, 0.2F + (normalized * 0.25F + brightness * 0.75F) * 0.8F,
                        0.4F + normalized * 0.6F);
                ResourceManager.sphere_uv.renderAll();
                GlStateManager.disableBlend();
                GlStateManager.enableLighting();
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
                if (core.getWorld().rand.nextInt(50) == 0) {
                    for (var spark = 0; spark < 6; spark++) {
                        var wide = (spark & 1) != 0;
                        var seed = (int) System.currentTimeMillis() / (wide ? 50 : 100) + (spark / 2) * 10000;
                        RenderSparks.renderSpark(seed, 0, 0, 0, wide ? 3F : 1.5F, 5, 10, 0x00FFFF, 0xFFFFFF);
                    }
                }
            } finally {
                GlStateManager.popMatrix();
            }
        }
    }

	public void renderDraconic(FiaMatrix face,double scale,double distance,float partialTicks) {
		int segments = 6;
		double startRadius = scale*0.5;
		double endRadius = 0.3;
		int resolution = 8;
		bindTexture(NOISE);
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
		GlStateManager.translate(0, movement, 0);

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
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(x+0.5,y+0.5,z+0.5);
		GlStateManager.pushMatrix();

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

		GlStateManager.scale(scale,scale,scale);

		GlStateManager.enableCull();
		bindTexture(EMISSIVE); // shader fix
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

		GlStateManager.rotate((float) (rot), 1, 1, 1);
		GlStateManager.scale(0.5F,0.5F,0.5F);
		ResourceManager.sphere_ruv.renderAll();
		GlStateManager.scale(2F, 2F, 2F);

		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);


		if (mixin.getDFCClientType() == Cores.ams_core_eyeofharmony || mixin.getDFCClientType() == Cores.glitch) {
			GlStateManager.scale(0.5F,0.5F,0.5F);
			//GlStateManager.pushMatrix();
			//GlStateManager.rotate(core.getWorld().rand.nextFloat()*360,1,0,0);
			//GlStateManager.rotate(core.getWorld().rand.nextFloat()*360,0,1,0);
			//GlStateManager.rotate(core.getWorld().rand.nextFloat()*360,0,0,1);
		}
		for (int i = 6; i <= 10; i++) {
			GlStateManager.pushMatrix();
			GlStateManager.scale(i * 0.1F, i * 0.1F, i * 0.1F);
			if (mixin.getDFCClientType() == Cores.ams_core_eyeofharmony || mixin.getDFCClientType() == Cores.glitch)
				deformSphere[core.getWorld().rand.nextInt(10)].renderAll();
			else
				ResourceManager.sphere_ruv.renderAll();
			GlStateManager.popMatrix();
		}
		if (mixin.getDFCClientType() == Cores.ams_core_eyeofharmony || mixin.getDFCClientType() == Cores.glitch) {
			//GlStateManager.popMatrix();
			GlStateManager.scale(2F,2F,2F);
		}
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		if (mixin.getDFCClientType() == Cores.ams_core_sing) {
			GlStateManager.pushMatrix();
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.color(0.11f, 0.11f, 0.11f);
			GlStateManager.scale(-scale * 1.15f, -scale * 1.15f, -scale * 1.15f);
			ResourceManager.sphere_ruv.renderAll();
			GlStateManager.popMatrix();

			GlStateManager.blendFunc(SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.color(0.529f, 0.722f, 1, 0.5f);
			double tr = MathHelper.positiveModulo(System.currentTimeMillis() / 1000d, 3000) / 2 * Math.PI;
			double add = 0.4; // 0.6
			GlStateManager.rotate((float) (tr / 3 * (180 / Math.PI)), 0, 1, 2);
			for (int d = 0; d < 16; d++) {
				GlStateManager.pushMatrix();
				double t = tr + add * d;
				GlStateManager.rotate((float) (-MathLeafia.smoothLinear(Math.abs(MathHelper.positiveModulo(t / 3 / Math.PI, 2) - 1), 0.5) * 180 * 3), 0, 0, 1);
				GlStateManager.rotate((float) (Math.sin(t / 3) * 135), 0, 1, 0);
				GlStateManager.translate(0, 0, -scale * 1.4 / 2);
				GlStateManager.scale(-0.25f, -0.25f, -0.25f);
				ResourceManager.sphere_ruv.renderAll();
				GlStateManager.popMatrix();
			}
		} else if (mixin.getDFCClientType() == Cores.ams_core_eyeofharmony) {
			LeafiaGls.disableCull();
			LeafiaGls.pushMatrix();
			LeafiaGls.rotate(mixin.getDFCAngle()+partialTicks*mixin.getDFCLightRotateSpeed(),1,1,1);
			renderFlash(0.024f+core.getWorld().rand.nextFloat()*0.001f,20,1,r,g,b);
			LeafiaGls.enableCull();
			LeafiaGls.popMatrix();
		}

		GlStateManager.popMatrix();

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
		GlStateManager.popMatrix();
	}
	private void renderFlash(float scale, double intensity, double alpha, float red, float green, float blue) {
        SeededFlashMesh.draw(scale, intensity, alpha, red, green, blue);
    }
}
