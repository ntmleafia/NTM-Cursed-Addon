package com.leafia.overwrite_contents;

import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public class ModMixinLoader implements ILateMixinLoader {

    @Override
    public List<String> getMixinConfigs() {
        return Arrays.asList("leafia.mod.mixin.json","leafia.mod.bullshit.mixin.json");
    }
}
