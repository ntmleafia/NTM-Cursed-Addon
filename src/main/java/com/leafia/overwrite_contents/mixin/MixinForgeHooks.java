// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin;

import com.leafia.shit.AssHooks;
import net.minecraft.advancements.Advancement;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(value = ForgeHooks.class, remap = false)
public class MixinForgeHooks {

    @Inject(
            method = "loadAdvancements(Ljava/util/Map;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/common/crafting/CraftingHelper;init()V",
                    shift = At.Shift.AFTER
            )
    )
    private static void leafia$loadAddonAdvancements(
            Map<ResourceLocation, Advancement.Builder> map,
            CallbackInfoReturnable<Boolean> cir
    ) {
        AssHooks.loadAdvancements(map);
    }
}
