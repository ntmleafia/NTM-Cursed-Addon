package com.leafia.dev.uninos;

public interface ISPKHandler extends ISPKConnector {
    long getSPK();
    void setSPK(long power);
    long getMaxSPK();
}
