package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.blocks.gas.BlockGasExplosive;
import net.minecraft.entity.Entity;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = BlockGasExplosive.class)
public class MixinBlockGasExplosive {
	@Redirect(method = "combust",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;newExplosion(Lnet/minecraft/entity/Entity;DDDFZZ)Lnet/minecraft/world/Explosion;",remap = true),require = 1,remap = false)
	public Explosion onCombust(World world,Entity entity,double x,double y,double z,float strength,boolean flaming,boolean damagesTerrain) {
		return world.newExplosion(entity,x,y,z,2,true,true);
	}
}
