package com.leafia.contents.network.spk_cable.uninos;

import com.hbm.lib.ForgeDirection;

public interface ISPKConnector {
    default boolean canConnectSPK(ForgeDirection dir) {
        return dir != ForgeDirection.UNKNOWN;
    }
}
