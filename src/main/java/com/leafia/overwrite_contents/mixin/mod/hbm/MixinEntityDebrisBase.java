package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.entity.projectile.EntityDebrisBase;
import com.leafia.overwrite_contents.interfaces.IMixinDebrisBase;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityDebrisBase.class)
public abstract class MixinEntityDebrisBase extends Entity implements IMixinDebrisBase {
	public MixinEntityDebrisBase(World worldIn) {
		super(worldIn);
	}
	@Redirect(method = "onUpdate",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;rayTraceBlocks(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;ZZZ)Lnet/minecraft/util/math/RayTraceResult;"))
	public RayTraceResult onRayTraceBlocks(World instance,Vec3d pos,Vec3d next,boolean st,boolean ig,boolean re) {
		if (this.destroysBlocks())
			return world.rayTraceBlocks(pos,next,st,ig,re);
		return null;
	}
	@Override
	public boolean destroysBlocks() {
		return true;
	}
}
