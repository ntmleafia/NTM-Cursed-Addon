// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin;

import com.leafia.dev.machine.MachineTooltip;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(ItemStack.class)
public class MixinItemStack {
    @SideOnly(Side.CLIENT)
    @Redirect(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;addInformation(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Ljava/util/List;Lnet/minecraft/client/util/ITooltipFlag;)V"))
    private void leafia$addMachineTooltip(Item item, ItemStack stack, World world, List<String> tooltip,
                                          ITooltipFlag advanced) {
        MachineTooltip.addInfoASM(item, tooltip);
        item.addInformation(stack, world, tooltip, advanced);
    }
}
