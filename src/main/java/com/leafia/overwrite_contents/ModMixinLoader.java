package com.leafia.overwrite_contents;

import zone.rong.mixinbooter.Context;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class ModMixinLoader implements ILateMixinLoader {

    private static final Map<String, String> MOD_GATED_CONFIGS = new HashMap<>();

    static {
        MOD_GATED_CONFIGS.put("leafia.mod.computronics.mixin.json", "computronics");
        MOD_GATED_CONFIGS.put("leafia.mod.opencomputers.mixin.json", "opencomputers");
    }

    @Override
    public List<String> getMixinConfigs() {
        return Arrays.asList(
                "leafia.mod.mixin.json",
                "leafia.mod.bullshit.mixin.json",
                "leafia.mod.computronics.mixin.json",
                "leafia.mod.opencomputers.mixin.json"
        );
    }

    @Override
    public boolean shouldMixinConfigQueue(Context context) {
        String requiredMod = MOD_GATED_CONFIGS.get(context.mixinConfig());
        return requiredMod == null || context.isModPresent(requiredMod);
    }
}
