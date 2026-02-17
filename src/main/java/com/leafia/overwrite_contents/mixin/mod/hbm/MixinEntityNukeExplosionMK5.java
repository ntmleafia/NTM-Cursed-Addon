package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.entity.logic.EntityExplosionChunkloading;
import com.hbm.entity.logic.EntityNukeExplosionMK5;
import com.leafia.overwrite_contents.interfaces.IMixinEntityFalloutRain;
import com.leafia.overwrite_contents.interfaces.IMixinEntityNukeExploisonMK5;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityNukeExplosionMK5.class)
public abstract class MixinEntityNukeExplosionMK5 extends EntityExplosionChunkloading implements IMixinEntityNukeExploisonMK5 {
	public MixinEntityNukeExplosionMK5(World world) {
		super(world);
	}
	@Unique boolean digammaFallout = false;
	@Override
	public void setDigammaFallout() {
		digammaFallout = true;
	}
	@Inject(method = "readEntityFromNBT",at = @At(value = "HEAD"),require = 1)
	public void onReadEntityFromNBT(NBTTagCompound nbt,CallbackInfo ci) {
		digammaFallout = nbt.getBoolean("digammaFallout");
	}
	@Inject(method = "writeEntityToNBT",at = @At(value = "HEAD"),require = 1)
	public void onWriteEntityFromNBT(NBTTagCompound nbt,CallbackInfo ci) {
		nbt.setBoolean("digammaFallout",digammaFallout);
	}
	@Redirect(method = "onUpdate",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"),require = 1)
	public boolean onUpdate(World world,Entity entity) {
		if (entity instanceof IMixinEntityFalloutRain rain && digammaFallout)
			rain.setDigammaFallout();
		return world.spawnEntity(entity);
	}
}
