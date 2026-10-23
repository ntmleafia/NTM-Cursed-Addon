// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.items.tool.ItemDesignator;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemDesignator.class)
public class MixinItemDesignator {
	@Inject(method = "onItemUse",at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;isRemote:Z",shift = Shift.BEFORE),require = 1)
	public void onOnItemUse(EntityPlayer player,World world,BlockPos pos,EnumHand hand,EnumFacing facing,float hitX,float hitY,float hitZ,CallbackInfoReturnable<EnumActionResult> cir,@Local(type = ItemStack.class) ItemStack stack) {
		stack.getTagCompound().setInteger("yCoord",pos.getY());
	}
}
