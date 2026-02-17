package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.entity.missile.EntityMissileBaseNT;
import com.hbm.entity.projectile.EntityThrowableNT;
import com.hbm.lib.internal.MethodHandleHelper;
import com.leafia.overwrite_contents.interfaces.IMixinEntityMissileBaseNT;
import com.llib.exceptions.LeafiaDevFlaw;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;

@Mixin(value = EntityThrowableNT.class)
public abstract class MixinEntityThrowableNT extends Entity {

	@Shadow(remap = false)
	protected abstract void onImpact(RayTraceResult rayTraceResult);

	private static final MethodHandle killMissile = MethodHandleHelper.findVirtual(EntityMissileBaseNT.class,"killMissile",MethodType.methodType(void.class));

	public MixinEntityThrowableNT(World worldIn) {
		super(worldIn);
	}

	@Redirect(method = "onUpdate",at = @At(value = "INVOKE", target = "Lcom/hbm/entity/projectile/EntityThrowableNT;onImpact(Lnet/minecraft/util/math/RayTraceResult;)V",remap = false),require = 1)
	public void onUpdate(EntityThrowableNT instance,RayTraceResult rayTraceResult) {
		if (this instanceof IMixinEntityMissileBaseNT mixin) {
			if (mixin.shouldDetonateInAir()) {
				EntityMissileBaseNT missile = (EntityMissileBaseNT)mixin;
				try {
					killMissile.invoke(missile);
				} catch (Throwable e) {
					throw new LeafiaDevFlaw(e);
				}
				return;
			}
		}
		onImpact(rayTraceResult);
	}
}
