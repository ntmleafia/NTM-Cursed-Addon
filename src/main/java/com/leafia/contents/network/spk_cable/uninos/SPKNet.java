// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2025 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.network.spk_cable.uninos;

import com.hbm.uninos.INetworkProvider;
import com.hbm.uninos.NodeNet;
import com.llib.exceptions.LeafiaDevFlaw;

/**
 * SPK network identity and accounting; transfer is performed by the active SPK transport.
 *
 * @author mlbv
 */
public class SPKNet extends NodeNet<ISPKReceiver, ISPKProvider, SPKNode, SPKNet> {
    public static final INetworkProvider<SPKNet> THE_NETWORK_PROVIDER = SPKNet::new;

    public long energyTracker = 0L;
    protected static int timeout = 3_000;

    @Override
    public void resetTrackers() {
        this.energyTracker = 0;
    }

    @Deprecated
    @Override
    public void update() {

        if (providerEntries.isEmpty()) return;
        if (receiverEntries.isEmpty()) return;

        throw new LeafiaDevFlaw("Deprecated update method of SPKNet called");
    }
}
