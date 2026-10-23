// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.custom_hbm.render.amlfrom1710;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

/** Stateful vertex colors for the lock-on and world highlight overlays. */
@SideOnly(Side.CLIENT)
public class CompositeBrush {

    public static final CompositeBrush instance = new CompositeBrush();

    private int color;
    private boolean colored;

    public void startDrawing(int mode, VertexFormat format) {
        Tessellator.getInstance().getBuffer().begin(mode, format);
    }

    public void startDrawingQuadsColor() {
        startDrawing(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
    }

    /** The high byte specifies transparency: zero is opaque. */
    public void setColorHex(int value) {
        color = value;
        colored = true;
    }

    public void addVertex(Vec3d position) {
        addVertex(position.x, position.y, position.z);
    }

    public void addVertex(double x, double y, double z) {
        var buffer = Tessellator.getInstance().getBuffer();
        buffer.pos(x, y, z);
        if (colored) {
            buffer.color((color >>> 16) & 255, (color >>> 8) & 255, color & 255, 255 - (color >>> 24));
        }
        buffer.endVertex();
    }

    public void draw() {
        colored = false;
        Tessellator.getInstance().draw();
    }
}
