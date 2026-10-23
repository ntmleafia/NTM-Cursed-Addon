// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.dev.particles;

import com.hbm.render.NTMRenderHelper;
import com.hbm.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

/** Standalone textured particle with a shared quad and blend-state policy. */
public abstract class AddonBillboardParticle extends Particle {
    private final ResourceLocation texture;
    private final boolean additive;

    protected AddonBillboardParticle(World world, double x, double y, double z,
                                     ResourceLocation texture, boolean additive) {
        super(world, x, y, z);
        this.texture = texture;
        this.additive = additive;
    }

    protected float renderSize(float partialTicks) {
        return particleScale;
    }

    @Override
    public int getFXLayer() {
        return 3;
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity camera, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        NTMRenderHelper.resetParticleInterpPos(camera, partialTicks);
        var lighting = RenderUtil.isLightingEnabled();
        var blend = RenderUtil.isBlendEnabled();
        var blendFactors = ParticleRenderState.blendFactors();
        var alphaFunc = RenderUtil.getAlphaFunc();
        var alphaRef = RenderUtil.getAlphaRef();
        var depthMask = RenderUtil.isDepthMaskEnabled();
        var textureUnit = RenderUtil.getActiveTextureUnitIndex();
        var textureName = GlStateManager.textureState[textureUnit].textureName;
        var tessellator = Tessellator.getInstance();
        var output = tessellator.getBuffer();
        var building = false;
        try {
            Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
            GlStateManager.depthMask(false);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
                    additive ? GlStateManager.DestFactor.ONE : GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            var brightness = getBrightnessForRender(partialTicks);
            var size = renderSize(partialTicks);
            output.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
            building = true;
            ParticleQuad.emit(output,
                    (float) (prevPosX + (posX - prevPosX) * (double) partialTicks - interpPosX),
                    (float) (prevPosY + (posY - prevPosY) * (double) partialTicks - interpPosY),
                    (float) (prevPosZ + (posZ - prevPosZ) * (double) partialTicks - interpPosZ),
                    rotationX, rotationYZ, rotationZ, rotationXY, rotationXZ, size,
                    0, 1, 0, 1, particleRed, particleGreen, particleBlue, particleAlpha, brightness);
            building = false;
            tessellator.draw();
        } finally {
            if (building) output.finishDrawing();
            ParticleRenderState.restoreTexture(textureUnit, textureName);
            ParticleRenderState.restoreBlend(blend, blendFactors);
            GlStateManager.alphaFunc(alphaFunc, alphaRef);
            GlStateManager.depthMask(depthMask);
            if (lighting) GlStateManager.enableLighting();
            else GlStateManager.disableLighting();
        }
    }
}
