package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.handler.EntityEffectHandler;
import com.hbm.util.ContaminationUtil;
import com.hbm.util.ContaminationUtil.ContaminationType;
import com.hbm.util.ContaminationUtil.HazardType;
import com.leafia.dev.rad_correction.RadCorrection;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = EntityEffectHandler.class)
public class MixinEntityEffectHandler {
	@Redirect(method = "onUpdate",at = @At(value = "INVOKE", target = "Lcom/hbm/util/ContaminationUtil;contaminate(Lnet/minecraft/entity/EntityLivingBase;Lcom/hbm/util/ContaminationUtil$HazardType;Lcom/hbm/util/ContaminationUtil$ContaminationType;D)Z",ordinal = 0),require = 1,remap = false)
	private static boolean leafia$correctCraterRad(EntityLivingBase player,HazardType hazardType,ContaminationType contaminationType,double value) {
		if (RadCorrection.isUnderground(player))
			return false;
		return ContaminationUtil.contaminate(player,hazardType,contaminationType,value);
	}
	@Redirect(method = "onUpdate",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBiome(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/world/biome/Biome;",ordinal = 1,remap = true),require = 1,remap = false)
	private static Biome leafia$correctBiomeDetection(World world,BlockPos pos,@Local(type = EntityPlayer.class, name = "player") EntityPlayer player) {
		if (RadCorrection.isUnderground(player))
			return world.getBiomeProvider().getBiome(pos);
		return world.getBiome(pos);
	}
}
