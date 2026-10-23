// SPDX-FileCopyrightText: 2025 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.network.spk_cable.uninos;

public interface ISPKHandler extends ISPKConnector {
    long getSPK();
    void setSPK(long power);
    long getMaxSPK();
}
