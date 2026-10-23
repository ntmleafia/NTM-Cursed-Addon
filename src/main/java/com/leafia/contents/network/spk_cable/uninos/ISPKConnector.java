// SPDX-FileCopyrightText: 2025 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.network.spk_cable.uninos;

import com.hbm.lib.ForgeDirection;

public interface ISPKConnector {
    default boolean canConnectSPK(ForgeDirection dir) {
        return dir != ForgeDirection.UNKNOWN;
    }
}
