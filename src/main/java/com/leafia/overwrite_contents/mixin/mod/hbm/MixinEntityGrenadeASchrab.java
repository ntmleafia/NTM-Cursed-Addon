// SPDX-FileCopyrightText: 2025 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.entity.effect.EntityCloudFleija;
import com.leafia.overwrite_contents.interfaces.IMixinEntityCloudFleija;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//@Mixin(EntityGrenadeASchrab.class)
public abstract class MixinEntityGrenadeASchrab {

    /*@Inject(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z", ordinal = 1), require = 1)
    private void beforeSpawnCloud(CallbackInfo ci, @Local EntityCloudFleija cloud) {
        ((IMixinEntityCloudFleija) cloud).setAntischrab();
    }*/
}
