package com.leafia.init.hazards.types;

import com.leafia.settings.AddonConfig;

public final class HazardTypeHelper {
    private HazardTypeHelper() {
    }

    public static double accountConfig(double value) {
        return AddonConfig.enableHealthMod ? value : 1.0;
    }
}
