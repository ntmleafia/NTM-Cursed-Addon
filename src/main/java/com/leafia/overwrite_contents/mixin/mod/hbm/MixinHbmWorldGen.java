// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.lib.HbmWorldGen;
import com.hbm.world.generator.DungeonToolbox;
import com.leafia.contents.AddonBlocks.LegacyBlocks;
import com.leafia.contents.worldgen.AddonBiomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(value = HbmWorldGen.class)
public class MixinHbmWorldGen {
	@Inject(method = "generateOres",at = @At("HEAD"),require = 1,remap = false)
	public void leafia$onGenerateOres(World world,Random rand,int i,int j,CallbackInfo ci) {
		int dimID = world.provider.getDimension();
		if (dimID == 0 && rand.nextInt(16) == 0)
			DungeonToolbox.generateOre(world,rand,i,j,1,48,32,32,LegacyBlocks.ore_coal_oil);
	}
	@Inject(method = "generateStructures",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBiome(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/world/biome/Biome;",remap = true),require = 1,cancellable = true,remap = false)
	public void leafia$onGenerateStructures(World world,Random rand,int chunkMinX,int chunkMinZ,CallbackInfo ci) {
		int centerX = chunkMinX + 8;
		int centerZ = chunkMinZ + 8;
		Biome biome = world.getBiome(new BlockPos(centerX, 65, centerZ));
		if (biome == AddonBiomes.barrens || biome == AddonBiomes.ruins)
			ci.cancel();
	}
}
