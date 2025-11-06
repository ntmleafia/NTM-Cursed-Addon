package com.leafia.dev.uninos;

import com.hbm.lib.ForgeDirection;

public interface ISPKConnector {
    default boolean canConnectSPK(ForgeDirection dir) {
        return dir != ForgeDirection.UNKNOWN;
    }
}
