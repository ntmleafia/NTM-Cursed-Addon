package com.leafia.dev.hazards.types;

import com.leafia.settings.AddonConfig;

final class HazardTypeHelper {
    private HazardTypeHelper() {
    }

    static double accountConfig(double value) {
        return AddonConfig.enableHealthMod ? value : 1.0;
    }
}
