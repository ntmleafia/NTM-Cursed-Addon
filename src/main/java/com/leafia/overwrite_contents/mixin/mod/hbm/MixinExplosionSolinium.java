// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.explosion.ExplosionSolinium;
import com.hbm.world.WorldUtil;
import com.hbm.world.biome.BiomeGenCraterBase;
import com.leafia.savedata.FalloutSavedData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ExplosionSolinium.class, remap = false)
public abstract class MixinExplosionSolinium {
    @Shadow public int radius2;
    @Shadow public int posX;
    @Shadow public int posY;
    @Shadow public int posZ;
    @Shadow public World worldObj;

    @Inject(method = "breakColumn", at = @At("HEAD"), require = 1)
    private void leafia$expireFallout(int x, int z, CallbackInfo ci) {
        var epicenter = new Vec3d(posX, 0, posZ);
        for (var fallout : FalloutSavedData.forWorld(worldObj).falloutMap) {
            if (fallout.pos.distanceTo(epicenter) <= fallout.radius) fallout.expire();
        }
    }

    @Inject(method = "breakColumn", at = @At("TAIL"), require = 1)
    private void leafia$restoreBiome(int x, int z, CallbackInfo ci) {
        if (radius2 - (x * x + z * z) <= 0) return;
        var column = new BlockPos(posX + x, posY, posZ + z);
        if (!(worldObj.getBiome(column) instanceof BiomeGenCraterBase)) return;
        WorldUtil.setBiome(worldObj, column.getX(), column.getZ(), worldObj.getBiomeProvider().getBiome(column));
        var chunk = worldObj.getChunk(column);
        WorldUtil.syncBiomeChange(worldObj, chunk.x, chunk.z);
    }
}
