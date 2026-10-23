// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.powercores.ams.base;

import com.hbm.main.ResourceManager;
import com.hbm.render.loader.WaveFrontObjectVAO;
import com.leafia.AddonBase;
import com.leafia.dev.render.AMSRenderSupport;
import com.leafia.dev.render.LeafiaItemRenderer;
import com.leafia.dev.render.RenderState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import static com.leafia.AddonBase.getIntegrated;
import static com.leafia.init.ResourceInit.getVAO;

public class AMSBaseRender extends TileEntitySpecialRenderer<AMSBaseTE> {
    public static final WaveFrontObjectVAO mdl0 = getVAO(getIntegrated("machines/ams/ams_base.obj"));
    public static final ResourceLocation tex0 = getIntegrated("machines/ams/ams_base.png");
    public static final ResourceLocation tex1 = getIntegrated("machines/ams/balefire/ams_base.png");
    private static final float[] SHELLS = {0.5F, 0.6F, 0.7F, 0.8F, 0.9F, 1F};

    public static class AMSBaseItemRender extends LeafiaItemRenderer {
        @Override protected double _sizeReference() { return 5.7; }
        @Override protected double _itemYoffset() { return -0.03; }
        @Override protected ResourceLocation __getTexture() { return tex0; }
        @Override protected WaveFrontObjectVAO __getModel() { return mdl0; }
    }

    @Override
    public void render(AMSBaseTE base, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        try (var state = new RenderState()) {
            GlStateManager.pushMatrix();
            try {
                GlStateManager.translate(x + 0.5D, y, z + 0.5D);
                GlStateManager.enableLighting();
                GlStateManager.disableCull();
                GlStateManager.rotate(180F, 0, 1, 0);
                bindTexture(AMSRenderSupport.usesBalefire(base) ? tex1 : tex0);
                mdl0.renderAll();
            } finally {
                GlStateManager.popMatrix();
            }
            if (base.color > -1) {
                shadow(base, x, y, z);
                colored(base, x, y, z);
            }
        }
    }

    public void renderTileEntityAt2(TileEntity tileEntity, double x, double y, double z, float partialTicks) {
        try (var state = new RenderState()) {
            shadow((AMSBaseTE) tileEntity, x, y, z);
            colored((AMSBaseTE) tileEntity, x, y, z);
        }
    }

    public void renderTileEntityAt3(TileEntity tileEntity, double x, double y, double z, float partialTicks) {
        try (var state = new RenderState()) {
            colored((AMSBaseTE) tileEntity, x, y, z);
        }
    }

    private void positionOrb(AMSBaseTE base, double x, double y, double z) {
        GlStateManager.translate(x + 0.5D, y + 5.5D, z + 0.5D);
        GlStateManager.disableLighting();
        GlStateManager.enableCull();
        GlStateManager.rotate(180F, 0, 1, 0);
        var size = 0.5 + (base.tanks[2].getFluidAmount() / (double) base.tanks[2].getCapacity()
                + base.tanks[3].getFluidAmount() / (double) base.tanks[3].getCapacity()) * 2.25;
        GlStateManager.scale(size, size, size);
        bindTexture(AddonBase.solid_e);
    }

    private void shadow(AMSBaseTE base, double x, double y, double z) {
        GlStateManager.pushMatrix();
        try {
            positionOrb(base, x, y, z);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
            GlStateManager.rotate((int) ((System.nanoTime() / 100000000) % 360) * 3, 0, 1, 0);
            GlStateManager.scale(1.1F, 1.1F, 1.1F);
            // The original integer component divisions produced a black shell.
            GlStateManager.color(0, 0, 0);
            ResourceManager.sphere_ruv.renderAll();
        } finally {
            GlStateManager.popMatrix();
        }
    }

    private void colored(AMSBaseTE base, double x, double y, double z) {
        GlStateManager.pushMatrix();
        try {
            positionOrb(base, x, y, z);
            GlStateManager.color((base.color >> 16 & 255) / 255F, (base.color >> 8 & 255) / 255F, (base.color & 255) / 255F);
            var rotation = (int) ((System.nanoTime() / 100000000) % 360) * 3;
            GlStateManager.rotate(rotation, 0, 1, 0);
            for (var index = 0; index < SHELLS.length; index++) {
                if (index == 1) {
                    GlStateManager.enableBlend();
                    GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
                }
                if (index > 0) GlStateManager.rotate((index & 1) == 0 ? rotation * 2 : -rotation * 2, 0, 1, 0);
                var size = SHELLS[index];
                GlStateManager.scale(size, size, size);
                ResourceManager.sphere_ruv.renderAll();
                if (size != 1) GlStateManager.scale(1F / size, 1F / size, 1F / size);
            }
            GlStateManager.disableBlend();
        } finally {
            GlStateManager.popMatrix();
        }
    }
}
