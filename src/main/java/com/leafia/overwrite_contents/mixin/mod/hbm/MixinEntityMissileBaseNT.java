package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.entity.missile.EntityMissileBaseNT;
import com.hbm.entity.projectile.EntityThrowableInterp;
import com.hbm.explosion.ExplosionLarge;
import com.leafia.overwrite_contents.interfaces.IMixinEntityMissileBaseNT;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = EntityMissileBaseNT.class)
public abstract class MixinEntityMissileBaseNT extends EntityThrowableInterp implements IMixinEntityMissileBaseNT {
	@Shadow(remap = false)
	public int targetX;

	@Shadow(remap = false)
	public int targetZ;

	@Shadow(remap = false)
	public abstract void onMissileImpact(RayTraceResult rayTraceResult);

	@Shadow(remap = false)
	public abstract List<ItemStack> getDebris();

	@Shadow(remap = false)
	public abstract ItemStack getDebrisRareDrop();

	public MixinEntityMissileBaseNT(World world) {
		super(world);
	}
	@Inject(method = "onUpdate",at = @At(value = "INVOKE", target = "Lcom/hbm/entity/missile/EntityMissileBaseNT;loadNeighboringChunks(II)V",remap = false),require = 1)
	public void onOnUpdate(CallbackInfo ci) {
		if (!shouldDetonateInAir()) return;
		if (Math.abs(targetX-posX) < 20 && Math.abs(targetZ-posZ) < 20 && posY-world.getHeight((int)posX,(int)posZ) < 50) {
			onMissileImpact(IMixinEntityMissileBaseNT.generateFakeRayTraceResult((EntityMissileBaseNT)(IMixinEntityMissileBaseNT)this));
		}
	}

	/**
	 * @author ntmleafia
	 * @reason AB missile interaction
	 */
	@Overwrite(remap = false)
	protected void killMissile() {
		if (!this.isDead) {
			this.setDead();
			if (interactABMissile()) {
				ExplosionLarge.explode(world,thrower,posX,posY,posZ,5,true,false,true);
				ExplosionLarge.spawnShrapnelShower(world,posX,posY,posZ,motionX,motionY,motionZ,15,0.075);
				ExplosionLarge.spawnMissileDebris(world,posX,posY,posZ,motionX,motionY,motionZ,0.25,getDebris(),getDebrisRareDrop());
			}
		}
	}
}
