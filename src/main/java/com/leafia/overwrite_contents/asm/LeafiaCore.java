package com.leafia.overwrite_contents.asm;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.TransformerExclusions({"com.leafia.overwrite_contents.asm","com.leafia.settings"})
@IFMLLoadingPlugin.SortingIndex(1001) //mlbv: run it early but keep it after the 1000 srg transformer
public class LeafiaCore implements IFMLLoadingPlugin {

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{KillMethodTransformer.class.getName(),TransformerCoreLeafia.class.getName()};
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
}
