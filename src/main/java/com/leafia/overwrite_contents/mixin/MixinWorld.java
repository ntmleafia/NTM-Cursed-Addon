// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin;

import com.leafia.contents.AddonItems;
import com.leafia.contents.worldgen.biomes.artificial.DigammaCrater;
import com.leafia.init.LeafiaSoundEvents;
import com.leafia.transformer.WorldServerLeafia;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(World.class)
public abstract class MixinWorld {

    @Inject(method = "onEntityRemoved", at = @At("HEAD"), require = 1)
    private void leafia$onEntityRemoved(Entity entityIn, CallbackInfo ci) {
        WorldServerLeafia.onEntityRemoved((World) (Object) this, entityIn);
    }

    @Inject(method = "playEvent(Lnet/minecraft/entity/player/EntityPlayer;ILnet/minecraft/util/math/BlockPos;I)V",at=@At("HEAD"),cancellable = true)
    void leafia$onPlayEvent(@Nullable EntityPlayer player,int type,BlockPos pos,int data,CallbackInfo ci) {
        World world = (World)(Object)this;
        int digamma = Item.getIdFromItem(AddonItems.digammaRecord);
        if (type == 1010 && DigammaCrater.isDigammaBiome(world.getBiome(pos)) && data != 0 && data != digamma) {
            ci.cancel();
            //world.playRecord(pos,LeafiaSoundEvents.digamma_record);
            world.playEvent(1010,pos,digamma);
        }
    }
}
