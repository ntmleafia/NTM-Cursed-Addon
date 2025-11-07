package com.leafia.contents.network.spk_cable.uninos;

public interface ISPKHandler extends ISPKConnector {
    long getSPK();
    void setSPK(long power);
    long getMaxSPK();
}
