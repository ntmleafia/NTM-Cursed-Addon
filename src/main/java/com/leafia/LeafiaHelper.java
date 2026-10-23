// SPDX-FileCopyrightText: 2025-2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia;

import com.hbm.lib.internal.MethodHandleHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.lang.invoke.MethodHandle;

public class LeafiaHelper {
    private static final MethodHandle registryNameSetterHandle = MethodHandleHelper.findSetter(IForgeRegistryEntry.Impl.class, "registryName", ResourceLocation.class);

    public static void setRegistryName(IForgeRegistryEntry.Impl<?> entry, String namespace, String path) {
        try {
            registryNameSetterHandle.invokeExact(entry, new ResourceLocation(namespace, path));
        } catch (Throwable t) {
            throw new RuntimeException("Failed to set registry name for " + entry.getClass().getName(), t);
        }
    }

    public static AxisAlignedBB getAABBRadius(Vec3d center,double radius) {
        return new AxisAlignedBB(center.x-radius,center.y-radius,center.z-radius,center.x+radius,center.y+radius,center.z+radius);
    }
    public static Vec3d getBlockPosCenter(BlockPos pos) {
        return new Vec3d(pos).add(0.5,0.5,0.5);
    }
}
