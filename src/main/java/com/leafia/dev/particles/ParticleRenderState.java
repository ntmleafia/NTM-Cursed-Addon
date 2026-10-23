// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.dev.particles;

import com.hbm.util.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;

/** Primitive state helpers for the particle render hot path. */
public final class ParticleRenderState {
    private ParticleRenderState() {
    }

    public static long blendFactors() {
        return (long) RenderUtil.getBlendSrcFactor()
                | (long) RenderUtil.getBlendDstFactor() << 16
                | (long) RenderUtil.getBlendSrcAlphaFactor() << 32
                | (long) RenderUtil.getBlendDstAlphaFactor() << 48;
    }

    public static void restoreBlend(boolean enabled, long factors) {
        GlStateManager.tryBlendFuncSeparate((int) factors & 0xffff, (int) (factors >>> 16) & 0xffff,
                (int) (factors >>> 32) & 0xffff, (int) (factors >>> 48) & 0xffff);
        if (enabled) GlStateManager.enableBlend();
        else GlStateManager.disableBlend();
    }

    public static void restoreTexture(int unit, int texture) {
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit + unit);
        if (texture >= 0) {
            GlStateManager.bindTexture(texture);
        } else {
            // Vanilla invalidates the cached name after deleting a bound texture.
            GlStateManager.bindTexture(0);
            GlStateManager.textureState[unit].textureName = texture;
        }
    }

    public static void restoreColor(float red, float green, float blue, float alpha) {
        if (red == -1 && green == -1 && blue == -1 && alpha == -1) {
            // A preceding vertex-array draw may have invalidated the current color.
            GlStateManager.resetColor();
        } else {
            GlStateManager.color(red, green, blue, alpha);
        }
    }
}
