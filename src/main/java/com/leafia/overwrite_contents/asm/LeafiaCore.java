// SPDX-FileCopyrightText: 2025-2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.asm;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.jetbrains.annotations.Nullable;
import zone.rong.mixinbooter.IEarlyMixinLoader;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.TransformerExclusions({"com.leafia.overwrite_contents.asm","com.leafia.settings"})
@IFMLLoadingPlugin.SortingIndex(1001) //mlbv: run it early but keep it after the 1000 srg transformer
public class LeafiaCore implements IFMLLoadingPlugin, IEarlyMixinLoader {

    @Override
    public String @Nullable [] getASMTransformerClass() {
        return new String[]{KillMethodTransformer.class.getName(), NestedModTransformer.class.getName()};
    }

    @Override
    public @Nullable String getModContainerClass() {
        return "com.leafia.overwrite_contents.asm.CoreModContainer";
    }

    @Override
    public @Nullable String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
    }

    @Override
    public @Nullable String getAccessTransformerClass() {
        return null;
    }

    @Override
    public List<String> getMixinConfigs() {
        return Arrays.asList("leafia.default.mixin.json", "leafia.optional.mixin.json");
    }
}
