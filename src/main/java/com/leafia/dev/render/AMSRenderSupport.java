// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.dev.render;

import com.hbm.inventory.fluid.Fluids;
import com.hbm.render.util.NTMBufferBuilder;
import com.leafia.contents.machines.powercores.ams.base.AMSBaseTE;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.tileentity.TileEntity;

/** Shared AMS attachment lookup and square-section beam topology. */
public final class AMSRenderSupport {
    private static final int[] COLUMN = {0, 2, 2, 0, 1, 3, 3, 1, 0, 1, 1, 0, 2, 3, 3, 2};
    private static final int[] HORIZONTAL = {0, 2, 2, 0, 3, 1, 1, 3, 0, 1, 1, 0, 2, 3, 3, 2};

    private AMSRenderSupport() {}

    public static boolean usesBalefire(AMSBaseTE base) {
        if (base == null) return false;
        var first = base.tanks[2].getTankType();
        var second = base.tanks[3].getTankType();
        return first == Fluids.ASCHRAB && second == Fluids.BALEFIRE
                || first == Fluids.BALEFIRE && second == Fluids.ASCHRAB;
    }

    public static AMSBaseTE baseAt(TileEntity tile, int x, int y, int z) {
        return tile.getWorld().getTileEntity(tile.getPos().add(x, y, z)) instanceof AMSBaseTE base ? base : null;
    }

    public static AMSBaseTE stabilizerBase(TileEntity tile, int facing) {
        return switch (facing) {
            case 2 -> baseAt(tile, 0, 0, -6);
            case 3 -> baseAt(tile, 0, 0, 6);
            case 4 -> baseAt(tile, -6, 0, 0);
            case 5 -> baseAt(tile, 6, 0, 0);
            default -> null;
        };
    }

    public static void orientStabilizer(int facing) {
        GlStateManager.rotate(180F, 0, 1, 0);
        GlStateManager.rotate(-90F, 0, 1, 0);
        var yaw = switch (facing) {
            case 2 -> 90;
            case 4 -> 180;
            case 3 -> 270;
            default -> 0;
        };
        GlStateManager.rotate(yaw, 0, 1, 0);
    }

    public static void column(NTMBufferBuilder buffer, double firstX, double firstZ, double secondX, double secondZ,
                               double top, double bottom, float radius, int color) {
        for (var index = 0; index < COLUMN.length; index++) {
            var corner = COLUMN[index];
            var first = (index & 3) < 2;
            buffer.appendPositionColor((float) ((first ? firstX : secondX) + ((corner & 1) == 0 ? radius : -radius)),
                    (float) (first ? top : bottom),
                    (float) ((first ? firstZ : secondZ) + ((corner & 2) == 0 ? radius : -radius)), color);
        }
    }

    public static void stabilizerBeam(NTMBufferBuilder buffer, double radius) {
        var opaque = NTMBufferBuilder.packColor(0.408F - 0.175F, 0.686F - 0.175F, 0.686F - 0.175F, 1F);
        var transparent = NTMBufferBuilder.packColor(0.408F - 0.175F, 0.686F - 0.175F, 0.686F - 0.175F, 0F);
        for (var index = 0; index < HORIZONTAL.length; index++) {
            var corner = HORIZONTAL[index];
            var far = (index & 3) < 2;
            var color = far ? transparent : opaque;
            buffer.appendPositionColor(far ? 4F : 0F, (float) ((corner & 1) == 0 ? -radius : radius),
                    (float) ((corner & 2) == 0 ? -radius : radius), color);
        }
    }
}
