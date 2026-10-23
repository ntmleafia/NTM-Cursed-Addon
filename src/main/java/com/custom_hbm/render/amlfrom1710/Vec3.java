// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.custom_hbm.render.amlfrom1710;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/** Mutable simulation coordinates with Minecraft's float-rounded length. */
public final class Vec3 {
    public double xCoord;
    public double yCoord;
    public double zCoord;

    public Vec3(double x, double y, double z) {
        xCoord = x == 0 ? 0 : x;
        yCoord = y == 0 ? 0 : y;
        zCoord = z == 0 ? 0 : z;
    }

    public Vec3(Vec3d source) {
        xCoord = source.x;
        yCoord = source.y;
        zCoord = source.z;
    }

    public static Vec3 createVectorHelper(double x, double y, double z) {
        return new Vec3(x, y, z);
    }

    public double length() {
        return MathHelper.sqrt(xCoord * xCoord + yCoord * yCoord + zCoord * zCoord);
    }

    public Vec3 normalize() {
        var magnitude = length();
        if (magnitude < 1.0E-4) return new Vec3(0, 0, 0);
        return new Vec3(xCoord / magnitude, yCoord / magnitude, zCoord / magnitude);
    }

    @Override
    public String toString() {
        return "(" + xCoord + ", " + yCoord + ", " + zCoord + ")";
    }

    public void rotateAroundX(float angle) {
        rotate(0, angle);
    }

    public void rotateAroundY(float angle) {
        rotate(1, angle);
    }

    public void rotateAroundZ(float angle) {
        rotate(2, angle);
    }

    private void rotate(int axis, float angle) {
        var cosine = MathHelper.cos(angle);
        var sine = MathHelper.sin(angle);
        var first = axis == 0 ? yCoord : xCoord;
        var second = axis == 2 ? yCoord : zCoord;
        var rotatedFirst = first * cosine + second * sine;
        var rotatedSecond = second * cosine - first * sine;
        switch (axis) {
            case 0 -> { yCoord = rotatedFirst; zCoord = rotatedSecond; }
            case 1 -> { xCoord = rotatedFirst; zCoord = rotatedSecond; }
            case 2 -> { xCoord = rotatedFirst; yCoord = rotatedSecond; }
        }
    }
}
