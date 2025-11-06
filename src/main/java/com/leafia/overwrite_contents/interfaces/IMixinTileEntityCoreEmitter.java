package com.leafia.overwrite_contents.interfaces;

import net.minecraft.util.math.RayTraceResult;

public interface IMixinTileEntityCoreEmitter extends IDFCBase {

    RayTraceResult raycast(long out);

    boolean isActive();

    void isActive(boolean active);
}
