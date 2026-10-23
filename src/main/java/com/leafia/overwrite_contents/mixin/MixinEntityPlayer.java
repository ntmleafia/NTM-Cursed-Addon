// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin;

import com.leafia.transformer.WorldServerLeafia;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer {

    @Inject(method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/item/EntityItem;", at = @At("RETURN"))
    private void leafia$trackDroppedItem(ItemStack droppedItem, boolean dropAround, boolean traceItem,
                                         CallbackInfoReturnable<EntityItem> cir) {
        EntityItem entityItem = cir.getReturnValue();
        if (entityItem != null) {
            WorldServerLeafia.onItemDroppedByPlayer((EntityPlayer) (Object) this, entityItem);
        }
    }
}
