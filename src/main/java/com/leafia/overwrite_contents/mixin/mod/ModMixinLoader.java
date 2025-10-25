package com.leafia.overwrite_contents.mixin.mod;

import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public class ModMixinLoader implements ILateMixinLoader {

    @Override
    public List<String> getMixinConfigs() {
        return Collections.singletonList("leafia.overwrite_contents.mod.mixin.json");
    }
}
