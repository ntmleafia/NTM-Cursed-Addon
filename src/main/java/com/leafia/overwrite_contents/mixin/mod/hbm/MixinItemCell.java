package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.entity.effect.EntityCloudFleija;
import com.hbm.items.special.ItemCell;
import com.leafia.overwrite_contents.interfaces.IMixinEntityCloudFleija;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.item.EntityItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemCell.class)
public abstract class MixinItemCell {

    @Inject(method = "onEntityItemUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z", ordinal = 1))
    private void beforeSpawnCloud(EntityItem entityItem, CallbackInfoReturnable<Boolean> cir, @Local EntityCloudFleija cloud) {
        ((IMixinEntityCloudFleija) cloud).setAntischrab();
    }
}

