// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-FileCopyrightText: 2025 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.init.hazards.types;

import com.leafia.settings.AddonConfig;

public final class HazardTypeHelper {
    private HazardTypeHelper() {
    }

    public static double accountConfig(double value) {
        return AddonConfig.enableHealthMod ? value : 1.0;
    }
}
