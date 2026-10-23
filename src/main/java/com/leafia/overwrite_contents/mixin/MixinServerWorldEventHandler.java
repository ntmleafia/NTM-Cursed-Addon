// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin;

import com.leafia.transformer.WorldServerLeafia;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldEventHandler;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorldEventHandler.class)
public class MixinServerWorldEventHandler {

    @Shadow
    @Final
    private WorldServer world;

    @Inject(method = "sendBlockBreakProgress", at = @At("HEAD"))
    private void leafia$onSendBlockBreakProgress(int breakerId, BlockPos pos, int progress, CallbackInfo ci) {
        WorldServerLeafia.player_onBreakBlockProgress(world, breakerId, pos, progress);
    }
}
