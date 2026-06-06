package com.leafia.overwrite_contents;

import com.leafia.settings.AddonConfig;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class MixinConfigPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String s) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    String shorten(String fullName) { // get class name from full name
        String[] array = fullName.split("\\.");
        return array[array.length-1];
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        mixinClassName = shorten(mixinClassName);
        boolean cancel = false;
        switch(mixinClassName) {
            case "MixinGuiMainMenu_WackySplashes" -> cancel = !AddonConfig.enableWackySplashes;
            case "MixinEntityRenderer_AcidRAin" -> cancel = !AddonConfig.enableAcidRainRender;
            case "MixinTileEntityReactorZIRNOX" -> cancel = AddonConfig.disableAddonZIRNOX;
            case "MixinCoreComponent","MixinCoreCore","MixinTileEntityCore","MixinTileEntityCoreEmitter",
                 "MixinTileEntityCoreInjector","MixinTileEntityCoreReceiver","MixinTileEntityCoreStabilizer" -> cancel = AddonConfig.disableAddonDFC;
        }
        if (mixinClassName.startsWith("Bullshit"))
            cancel = !AddonConfig.bullshitUnits;
        if (cancel) {
            System.out.println("DISABLING MIXIN "+mixinClassName+" ("+targetClassName+")");
            return false;
        }
        System.out.println("APPLYING MIXIN "+mixinClassName+" ("+targetClassName+")");
        return true;
    }

    @Override
    public void acceptTargets(Set<String> set, Set<String> set1) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {
    }

    @Override
    public void postApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {
    }
}
