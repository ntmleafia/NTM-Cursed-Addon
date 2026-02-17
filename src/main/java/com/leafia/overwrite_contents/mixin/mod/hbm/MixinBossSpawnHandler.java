package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.handler.BossSpawnHandler;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = BossSpawnHandler.class,remap = false)
public abstract class MixinBossSpawnHandler {

	@Shadow
	public static void spawnMeteorAtPlayer(EntityPlayer player,boolean repell) {
	}

	@Redirect(method = "meteorUpdate",at = @At(value = "INVOKE", target = "Lcom/hbm/handler/BossSpawnHandler;spawnMeteorAtPlayer(Lnet/minecraft/entity/player/EntityPlayer;Z)V"),require = 1)
	private static void onMeteorUpdate(EntityPlayer plr,boolean repell,@Local(type = World.class) World world) {
		if (world.rand.nextInt(6) == 0)
			spawnMeteorAtPlayer(plr,repell);
	}
}
