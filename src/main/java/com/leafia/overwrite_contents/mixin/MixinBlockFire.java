// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin;

import com.leafia.dev.firestorm.IFirestormBlock;
import net.minecraft.block.BlockFire;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(BlockFire.class)
public class MixinBlockFire {

    @Inject(method = "getNeighborEncouragement", at = @At("HEAD"))
    private void leafia$igniteOnNeighborEncouragement(World worldIn, BlockPos pos,
                                                      CallbackInfoReturnable<Integer> cir) {
        IFirestormBlock.ignite(worldIn, pos);
    }

    @Inject(method = "tryCatchFire", at = @At("HEAD"), remap = false)
    private void leafia$igniteOnTryCatchFire(World worldIn, BlockPos pos, int chance, Random random, int age,
                                             EnumFacing face, CallbackInfo ci) {
        IFirestormBlock.ignite(worldIn, pos);
    }
}
