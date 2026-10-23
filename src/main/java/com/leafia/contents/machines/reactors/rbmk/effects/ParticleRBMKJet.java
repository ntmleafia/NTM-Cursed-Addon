// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.reactors.rbmk.effects;

import com.hbm.render.NTMRenderHelper;
import com.hbm.util.RenderUtil;
import com.leafia.dev.particles.ParticleRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

/** Upright, camera-facing steam strip using twenty horizontal animation frames. */
public class ParticleRBMKJet extends Particle {
    private static final ResourceLocation TEXTURE = new ResourceLocation("hbm", "textures/particle/rbmk_jet_steam.png");

    public ParticleRBMKJet(World world, double x, double y, double z, int maxAge) {
        super(world, x, y, z);
        particleMaxAge = maxAge;
        particleScale = rand.nextFloat() + 1F;
    }

    @Override
    public int getFXLayer() {
        return 3;
    }

    protected ResourceLocation getTexture() {
        return TEXTURE;
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity camera, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        var minecraft = Minecraft.getMinecraft();
        NTMRenderHelper.resetParticleInterpPos(camera, partialTicks);
        var lighting = RenderUtil.isLightingEnabled();
        var blend = RenderUtil.isBlendEnabled();
        var blendFactors = ParticleRenderState.blendFactors();
        var alphaFunc = RenderUtil.getAlphaFunc();
        var alphaRef = RenderUtil.getAlphaRef();
        var depthMask = RenderUtil.isDepthMaskEnabled();
        var textureUnit = RenderUtil.getActiveTextureUnitIndex();
        var textureName = GlStateManager.textureState[textureUnit].textureName;
        var red = RenderUtil.getCurrentColorRed();
        var green = RenderUtil.getCurrentColorGreen();
        var blue = RenderUtil.getCurrentColorBlue();
        var alpha = RenderUtil.getCurrentColorAlpha();
        var lightX = OpenGlHelper.lastBrightnessX;
        var lightY = OpenGlHelper.lastBrightnessY;
        var tessellator = Tessellator.getInstance();
        var output = tessellator.getBuffer();
        var building = false;
        GlStateManager.pushMatrix();
        try {
            minecraft.getTextureManager().bindTexture(getTexture());
            GlStateManager.color(1, 1, 1, 1);
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0);
            GlStateManager.depthMask(false);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);

            particleAge = Math.min(particleAge, particleMaxAge);
            var frame = particleAge * 20 / particleMaxAge;
            var minU = frame * (1F / 20F);
            var maxU = minU + 1F / 20F;
            // The stored alpha belongs to the preceding render, as in the steam effect.
            GlStateManager.color(1, 1, 1, particleAlpha);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
            particleAlpha = 1;
            if (particleAge < 20)
                particleAlpha = particleAge / 20F;
            if (particleAge > particleMaxAge - 20)
                particleAlpha = (particleMaxAge - particleAge) / 20F;
            particleAlpha *= 0.5F;

            var x = (float) (prevPosX + (posX - prevPosX) * (double) partialTicks - interpPosX);
            var y = (float) (prevPosY + (posY - prevPosY) * (double) partialTicks - interpPosY);
            var z = (float) (prevPosZ + (posZ - prevPosZ) * (double) partialTicks - interpPosZ);
            GlStateManager.translate(x + rotationX, y + rotationZ, z + rotationYZ);
            GlStateManager.rotate(-minecraft.getRenderManager().playerViewY, 0, 1, 0);
            output.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            building = true;
            for (var corner = 0; corner < 4; corner++) {
                var right = corner >= 2;
                var top = corner == 1 || corner == 2;
                output.pos(right ? particleScale - 1 : -particleScale - 1, top ? particleScale * 4 : 0, 0)
                        .tex(right ? minU : maxU, top ? 0 : 1).endVertex();
            }
            building = false;
            tessellator.draw();
        } finally {
            if (building) output.finishDrawing();
            GlStateManager.popMatrix();
            ParticleRenderState.restoreTexture(textureUnit, textureName);
            ParticleRenderState.restoreBlend(blend, blendFactors);
            GlStateManager.alphaFunc(alphaFunc, alphaRef);
            GlStateManager.depthMask(depthMask);
            if (lighting) GlStateManager.enableLighting();
            else GlStateManager.disableLighting();
            ParticleRenderState.restoreColor(red, green, blue, alpha);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightX, lightY);
        }
    }
}
