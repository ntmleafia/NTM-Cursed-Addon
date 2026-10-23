// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.dev.render;

import com.hbm.util.RenderUtil;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;

/** Restores the cached render state changed by addon effects. */
public final class RenderState extends LeafiaGls.GlStates implements AutoCloseable {
    private final int shade = RenderUtil.getShadeModel();
    private final float lightX = OpenGlHelper.lastBrightnessX;
    private final float lightY = OpenGlHelper.lastBrightnessY;

    public RenderState() {}

    @Override
    public void apply() {
        super.apply();
        GlStateManager.shadeModel(shade);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightX, lightY);
    }

    @Override
    public void close() {
        apply();
    }
}
