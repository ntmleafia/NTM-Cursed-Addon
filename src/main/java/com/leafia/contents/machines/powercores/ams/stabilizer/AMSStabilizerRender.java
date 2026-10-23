// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.powercores.ams.stabilizer;

import com.hbm.render.loader.WaveFrontObjectVAO;
import com.hbm.render.util.NTMImmediate;
import com.leafia.contents.machines.powercores.ams.base.AMSBaseTE;
import com.leafia.dev.render.AMSRenderSupport;
import com.leafia.dev.render.LeafiaItemRenderer;
import com.leafia.dev.render.RenderState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import static com.leafia.AddonBase.getIntegrated;
import static com.leafia.init.ResourceInit.getVAO;

public class AMSStabilizerRender extends TileEntitySpecialRenderer<AMSStabilizerTE> {
    public static final WaveFrontObjectVAO mdl0 = getVAO(getIntegrated("machines/ams/ams_limiter.obj"));
    public static final WaveFrontObjectVAO mdld = getVAO(getIntegrated("machines/ams/ams_limiter_destroyed.obj"));
    public static final ResourceLocation texd = getIntegrated("machines/ams/ams_destroyed.png");
    public static final ResourceLocation tex0 = getIntegrated("machines/ams/ams_limiter.png");
    public static final ResourceLocation tex1 = getIntegrated("machines/ams/balefire/ams_limiter.png");

    public static class AMSStabilizerItemRender extends LeafiaItemRenderer {
        @Override protected double _sizeReference() { return 10; }
        @Override protected double _itemYoffset() { return -0.15; }
        @Override protected ResourceLocation __getTexture() { return tex0; }
        @Override protected WaveFrontObjectVAO __getModel() { return mdl0; }
    }

    @Override
    public void render(AMSStabilizerTE stabilizer, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        var facing = AMSStabilizerTE.rotateShitfuck(stabilizer.getBlockMetadata()) - 10;
        var base = stabilizer.locked ? null : AMSRenderSupport.stabilizerBase(stabilizer, facing);
        try (var state = new RenderState()) {
            GlStateManager.pushMatrix();
            try {
                position(x, y, z, facing);
                bindTexture(stabilizer.locked ? texd : AMSRenderSupport.usesBalefire(base) ? tex1 : tex0);
                (stabilizer.locked ? mdld : mdl0).renderAll();
                drawBeam(stabilizer, base);
            } finally {
                GlStateManager.popMatrix();
            }
        }
    }

    public void renderTileEntityAt2(TileEntity tileEntity, double x, double y, double z, float partialTicks) {
        var stabilizer = (AMSStabilizerTE) tileEntity;
        var facing = AMSStabilizerTE.rotateShitfuck(stabilizer.getBlockMetadata()) - 10;
        try (var state = new RenderState()) {
            GlStateManager.pushMatrix();
            try {
                position(x, y, z, facing);
                drawBeam(stabilizer, stabilizer.locked ? null : AMSRenderSupport.stabilizerBase(stabilizer, facing));
            } finally {
                GlStateManager.popMatrix();
            }
        }
    }

    private void position(double x, double y, double z, int facing) {
        GlStateManager.translate(x + 0.5D, y, z + 0.5D);
        GlStateManager.enableLighting();
        GlStateManager.disableCull();
        AMSRenderSupport.orientStabilizer(facing);
    }

    private void drawBeam(AMSStabilizerTE stabilizer, AMSBaseTE base) {
        if (base == null || stabilizer.locked) return;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
        GlStateManager.rotate(-90F, 0, 1, 0);
        GlStateManager.translate(2.5D, 5.5D, 0);
        GlStateManager.disableLighting();
        GlStateManager.disableTexture2D();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        GlStateManager.disableAlpha();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
        var buffer = NTMImmediate.INSTANCE.beginPositionColor(GL11.GL_QUADS, 32);
        AMSRenderSupport.stabilizerBeam(buffer, 0.12);
        if (stabilizer.efficiency > 0) AMSRenderSupport.stabilizerBeam(buffer, 0.24);
        NTMImmediate.INSTANCE.draw();
    }
}
