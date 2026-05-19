package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemLens;
import com.leafia.contents.machines.powercores.dfc.LCEItemLens;
import com.leafia.settings.AddonConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ModItems.class, remap = false)
public class MixinModItems {

    @Redirect(method = "<clinit>", at = @At(value = "NEW", target = "Lcom/hbm/items/machine/ItemLens;"),require = 1)
    private static ItemLens redirectLens(long maxDamage, String s){
        if (AddonConfig.disableAddonDFC)
            return new ItemLens(maxDamage,s);
        else
            return new LCEItemLens(maxDamage,s);
    }
}
