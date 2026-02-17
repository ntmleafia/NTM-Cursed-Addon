package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.lib.HbmWorldGen;
import com.hbm.world.generator.DungeonToolbox;
import com.leafia.contents.AddonBlocks.LegacyBlocks;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(value = HbmWorldGen.class)
public class MixinHbmWorldGen {
	@Inject(method = "generateOres",at = @At("HEAD"),require = 1,remap = false)
	public void onGenerateOres(World world,Random rand,int i,int j,CallbackInfo ci) {
		int dimID = world.provider.getDimension();
		if (dimID == 0 && rand.nextInt(16) == 0)
			DungeonToolbox.generateOre(world,rand,i,j,1,48,32,32,LegacyBlocks.ore_coal_oil);
	}
}
