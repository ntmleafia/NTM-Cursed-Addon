package com.leafia.mixin.mod.hbm;

import com.hbm.particle.ParticleRBMKMush;
import com.hbm.render.NTMRenderHelper;
import com.leafia.interfaces.mixin.IMixinParticleRBMKMush;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.*;

@Mixin(value = ParticleRBMKMush.class)
public abstract class MixinParticleRBMKMush extends Particle implements IMixinParticleRBMKMush {
    @Final
    @Shadow(remap = false)
    private static ResourceLocation texture;
    @Unique
    public boolean isPink = false;

    protected MixinParticleRBMKMush(World worldIn, double posXIn, double posYIn, double posZIn) {
        super(worldIn, posXIn, posYIn, posZIn);
    }

    /**
     * @author mlbv
     * @reason fix black background
     */
    @Overwrite
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        NTMRenderHelper.resetParticleInterpPos(entityIn, partialTicks);

        int segs = 30;

        // the size of one frame
        double frame = 1.0D / segs;
        // how many frames we're in
        int prog = particleAge * segs / particleMaxAge;

        GlStateManager.color(1, isPink ? 0 : 1, 1, 1);
        GlStateManager.glNormal3f(0, 1, 0);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.depthMask(false);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        RenderHelper.disableStandardItemLighting();

        Tessellator tes = Tessellator.getInstance();
        BufferBuilder buf = tes.getBuffer();

        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        float scale = this.particleScale;
        float pX = (float) ((this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX));
        float pY = (float) ((this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY)) + particleScale;
        float pZ = (float) ((this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ));

        buf.pos(pX - rotationX * scale - rotationXY * scale, pY - rotationZ * scale, pZ - rotationYZ * scale - rotationXZ * scale).tex(1, (prog + 1) * frame).endVertex();
        buf.pos(pX - rotationX * scale + rotationXY * scale, pY + rotationZ * scale, pZ - rotationYZ * scale + rotationXZ * scale).tex(1, prog * frame).endVertex();
        buf.pos(pX + rotationX * scale + rotationXY * scale, pY + rotationZ * scale, pZ + rotationYZ * scale + rotationXZ * scale).tex(0, prog * frame).endVertex();
        buf.pos(pX + rotationX * scale - rotationXY * scale, pY - rotationZ * scale, pZ + rotationYZ * scale - rotationXZ * scale).tex(0, (prog + 1) * frame).endVertex();
        tes.draw();
        GlStateManager.color(1, 1, 1, 1);

        GlStateManager.doPolygonOffset(0, 0);
        GlStateManager.enableLighting();
    }

    @Override
    public boolean isPink() {
        return isPink;
    }

    @Override
    public void setPink(boolean value) {
        isPink = value;
    }
}
