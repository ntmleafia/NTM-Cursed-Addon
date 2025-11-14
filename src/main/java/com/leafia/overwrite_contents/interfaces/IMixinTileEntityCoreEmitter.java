package com.leafia.overwrite_contents.interfaces;

import com.leafia.contents.machines.powercores.dfc.IDFCBase;
import com.leafia.contents.network.spk_cable.uninos.ISPKReceiver;
import net.minecraft.util.math.RayTraceResult;

public interface IMixinTileEntityCoreEmitter extends IDFCBase, ISPKReceiver {

    RayTraceResult raycast(long out);

    boolean isActive();

    void isActive(boolean active);

    RayTraceResult lastRaycast();
}
