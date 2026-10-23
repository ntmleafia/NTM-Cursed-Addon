// SPDX-FileCopyrightText: 2025-2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.custom_hbm.render.tileentity;

import com.custom_hbm.render.amlfrom1710.Vec3;
import com.hbm.main.ResourceManager;
import com.hbm.render.tileentity.RenderSpinnyLight;
import com.hbm.tileentity.deco.TileEntitySpinnyLight;
import com.leafia.dev.render.RenderState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.item.EnumDyeColor;
import org.lwjgl.opengl.GL11;

/** Colored rotating beacon, using the dependency's unchanged item renderer. */
public class LCERenderSpinnyLight extends RenderSpinnyLight {
    private static final EnumDyeColor[] COLORS = EnumDyeColor.values();
    private static final VertexBuffer[] CONES = new VertexBuffer[COLORS.length];
    private static final float[] RING = ring();
    private static final int[] YAW = {0, 0, 180, 0, 270, 90};

    @Override
    public boolean isGlobalRenderer(TileEntitySpinnyLight tile) {
        return true;
    }

    @Override
    public void render(TileEntitySpinnyLight tile, double x, double y, double z,
                       float partialTicks, int destroyStage, float alpha) {
        var metadata = tile.getBlockMetadata();
        var powered = (metadata & 8) != 0;
        var time = powered ? (tile.getWorld().getTotalWorldTime() - tile.timeAdded) % 10000 + partialTicks : 0F;
        var angle = time * 12 % 360;
        var color = tile.color.getColorComponentValues();
        try (var state = new RenderState()) {
            GlStateManager.pushMatrix();
            try {
                GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
                var face = metadata & 7;
                if (face == 0) GlStateManager.rotate(180F, 1, 0, 0);
                else if (face >= 2 && face <= 5) {
                    if (face != 3) GlStateManager.rotate(YAW[face], 0, 1, 0);
                    GlStateManager.rotate(90F, 1, 0, 0);
                }
                GlStateManager.translate(0D, -0.5D, 0D);
                bindTexture(ResourceManager.spinny_light_tex);
                GlStateManager.pushMatrix();
                try {
                    GlStateManager.rotate(angle, 0, 1, 0);
                    GlStateManager.shadeModel(GL11.GL_SMOOTH);
                    if (powered) OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
                    GlStateManager.color(color[0], color[1], color[2]);
                    ResourceManager.spinny_light.renderPart("light");
                } finally {
                    GlStateManager.popMatrix();
                }
                GlStateManager.color(1, 1, 1);
                ResourceManager.spinny_light.renderPart("base");
                GlStateManager.enableBlend();
                if (powered) {
                    GlStateManager.disableTexture2D();
                    GlStateManager.disableLighting();
                    GlStateManager.alphaFunc(GL11.GL_GREATER, 0);
                    GlStateManager.depthMask(false);
                    GlStateManager.disableCull();
                    GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
                } else {
                    GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.SRC_COLOR);
                }
                GlStateManager.color(color[0] * color[0], color[1] * color[1], color[2] * color[2], powered ? 1 : 0.61F);
                ResourceManager.spinny_light.renderPart("dome");
                if (powered) {
                    GlStateManager.rotate(angle, 0, 1, 0);
                    drawCone(tile.color);
                }
            } finally {
                GlStateManager.popMatrix();
            }
        }
    }

    private static float[] ring() {
        var result = new float[36];
        var point = new Vec3(0, 3, 0);
        var step = (float) (2 * Math.PI * (1F / 12));
        for (var index = 0; index < 12; index++) {
            point.rotateAroundX(step);
            result[index * 3] = (float) point.xCoord + 5;
            result[index * 3 + 1] = (float) point.yCoord + 0.1708F;
            result[index * 3 + 2] = (float) point.zCoord;
        }
        return result;
    }

    private static VertexBuffer cone(EnumDyeColor dye) {
        var existing = CONES[dye.ordinal()];
        if (existing != null) return existing;
        var tint = dye.getColorComponentValues();
        var red = dye == EnumDyeColor.RED ? 1F : tint[0] * tint[0];
        var green = dye == EnumDyeColor.RED ? 0F : tint[1] * tint[1];
        var blue = dye == EnumDyeColor.RED ? 0F : tint[2] * tint[2];
        var data = new BufferBuilder(72 * 16);
        data.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);
        for (var half = 0; half < 2; half++) {
            var sign = half == 0 ? -1 : 1;
            for (var triangle = 0; triangle < 12; triangle++) {
                data.pos(0F, 0.1708F, 0F).color(red, green, blue, 1F).endVertex();
                for (var end = 0; end < 2; end++) {
                    var index = triangle == 11 ? (end == 0 ? 11 : 0) : triangle + end;
                    var offset = index * 3;
                    data.pos(RING[offset] * sign, RING[offset + 1], RING[offset + 2]).color(0F, 0F, 0F, 0F).endVertex();
                }
            }
        }
        data.finishDrawing();
        var mesh = new VertexBuffer(DefaultVertexFormats.POSITION_COLOR);
        mesh.bufferData(data.getByteBuffer());
        CONES[dye.ordinal()] = mesh;
        return mesh;
    }

    private static void drawCone(EnumDyeColor dye) {
        var mesh = cone(dye);
        mesh.bindBuffer();
        GlStateManager.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        GlStateManager.glEnableClientState(GL11.GL_COLOR_ARRAY);
        try {
            GlStateManager.glVertexPointer(3, GL11.GL_FLOAT, 16, 0);
            GlStateManager.glColorPointer(4, GL11.GL_UNSIGNED_BYTE, 16, 12);
            mesh.drawArrays(GL11.GL_TRIANGLES);
        } finally {
            GlStateManager.glDisableClientState(GL11.GL_COLOR_ARRAY);
            GlStateManager.glDisableClientState(GL11.GL_VERTEX_ARRAY);
            mesh.unbindBuffer();
            GlStateManager.resetColor();
        }
    }
}
