// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.powercores.ams.emitter;

import com.hbm.render.loader.WaveFrontObjectVAO;
import com.hbm.render.util.NTMBufferBuilder;
import com.hbm.render.util.NTMImmediate;
import com.leafia.contents.machines.powercores.ams.base.AMSBaseTE;
import com.leafia.dev.render.AMSRenderSupport;
import com.leafia.dev.render.LeafiaItemRenderer;
import com.leafia.dev.render.RenderState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.Random;

import static com.leafia.AddonBase.getIntegrated;
import static com.leafia.init.ResourceInit.getVAO;

public class AMSEmitterRender extends TileEntitySpecialRenderer<AMSEmitterTE> {
    public static final WaveFrontObjectVAO mdl0 = getVAO(getIntegrated("machines/ams/ams_emitter.obj"));
    public static final WaveFrontObjectVAO mdld = getVAO(getIntegrated("machines/ams/ams_emitter_destroyed.obj"));
    public static final ResourceLocation texd = getIntegrated("machines/ams/ams_destroyed.png");
    public static final ResourceLocation tex0 = getIntegrated("machines/ams/ams_emitter.png");
    public static final ResourceLocation tex1 = getIntegrated("machines/ams/balefire/ams_emitter.png");
    Random rand = new Random();

    public static class AMSEmitterItemRender extends LeafiaItemRenderer {
        @Override protected double _sizeReference() { return 9.7; }
        @Override protected double _itemYoffset() { return -0.2; }
        @Override protected ResourceLocation __getTexture() { return tex0; }
        @Override protected WaveFrontObjectVAO __getModel() { return mdl0; }
    }

    @Override
    public void render(AMSEmitterTE emitter, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        var base = emitter.locked ? null : AMSRenderSupport.baseAt(emitter, 0, -9, 0);
        try (var state = new RenderState()) {
            GlStateManager.pushMatrix();
            try {
                GlStateManager.translate(x + 0.5D, y, z + 0.5D);
                GlStateManager.enableLighting();
                GlStateManager.disableCull();
                GlStateManager.rotate(180F, 0, 1, 0);
                bindTexture(emitter.locked ? texd : AMSRenderSupport.usesBalefire(base) ? tex1 : tex0);
                (emitter.locked ? mdld : mdl0).renderAll();
            } finally {
                GlStateManager.popMatrix();
            }
            drawBeam(emitter, base, x, y, z);
        }
    }

    public void renderTileEntityAt2(TileEntity tileEntity, double x, double y, double z, float partialTicks) {
        var emitter = (AMSEmitterTE) tileEntity;
        try (var state = new RenderState()) {
            drawBeam(emitter, emitter.locked ? null : AMSRenderSupport.baseAt(emitter, 0, -9, 0), x, y, z);
        }
    }

    private void drawBeam(AMSEmitterTE emitter, AMSBaseTE base, double x, double y, double z) {
        if (base == null || emitter.locked) return;
        GlStateManager.pushMatrix();
        try {
            GlStateManager.disableTexture2D();
            GlStateManager.disableCull();
            GlStateManager.enableBlend();
            GlStateManager.enableLighting();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            GlStateManager.translate((float) x + 0.5F, (float) y - 7, (float) z + 0.5F);
            var buffer = NTMImmediate.INSTANCE.beginPositionColor(GL11.GL_QUADS, 368);
            if (emitter.efficiency > 0) {
                var color = NTMBufferBuilder.packColor(1F, 0.5F, 0F, 1F);
                var previousX = 0D;
                var previousZ = 0D;
                for (var height = 7; height > 0; height--) {
                    var nextX = rand.nextDouble() - 0.5;
                    var nextZ = rand.nextDouble() - 0.5;
                    for (var layer = 1; layer <= 3; layer++) {
                        AMSRenderSupport.column(buffer, previousX, previousZ, nextX, nextZ,
                                height, height - 1, 0.04F * layer, color);
                    }
                    previousX = nextX;
                    previousZ = nextZ;
                }
            }
            var color = NTMBufferBuilder.packColor(1F, 1F, 0F, 1F);
            for (var layer = 1; layer <= 2; layer++) {
                AMSRenderSupport.column(buffer, 0, 0, 0, 0, 7, 0, 0.04F * layer, color);
            }
            NTMImmediate.INSTANCE.draw();
        } finally {
            GlStateManager.popMatrix();
        }
    }
}
