package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemLens;
import com.leafia.contents.machines.powercores.dfc.LCEItemLens;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ModItems.class, remap = false)
public class MixinModItems {

    @Redirect(method = "<clinit>", at = @At(value = "NEW", target = "Lcom/hbm/items/machine/ItemLens;"))
    private static ItemLens redirectLens(long maxDamage, String s){
        return new LCEItemLens(maxDamage, s);
    }
}
