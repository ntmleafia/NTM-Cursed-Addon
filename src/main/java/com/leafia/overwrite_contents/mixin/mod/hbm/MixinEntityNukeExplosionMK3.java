// SPDX-FileCopyrightText: 2025-2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.config.CompatibilityConfig;
import com.hbm.entity.logic.EntityNukeExplosionMK3;
import com.hbm.entity.logic.EntityNukeExplosionMK3.ATEntry;
import com.hbm.entity.logic.IChunkLoader;
import com.hbm.entity.projectile.EntityRBMKDebris;
import com.hbm.entity.projectile.EntityZirnoxDebris;
import com.leafia.contents.effects.folkvangr.EntityNukeFolkvangr;
import com.leafia.database.FolkvangrJammers;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = EntityNukeExplosionMK3.class)
public abstract class MixinEntityNukeExplosionMK3 extends Entity implements IChunkLoader {
	@Shadow(remap = false) public int extType;
	@Shadow(remap = false) public boolean did;
	@Shadow(remap = false) public boolean waste;

	@Shadow public abstract void setDead();

	@Shadow public abstract void onUpdate();

	@Shadow(remap = false)
	public int age;

	public MixinEntityNukeExplosionMK3(World worldIn) {
		super(worldIn);
	}

	@Inject(method = "onUpdate", at = @At("HEAD"), cancellable = true, require = 1)
	private void onOnUpdate(CallbackInfo ci) {
		if (!this.world.isRemote) {
			if (CompatibilityConfig.isWarDim(this.world)) {
				if (!this.did) {
					if (!waste) {
						if (extType == 0) {
							super.onUpdate();
							EntityNukeFolkvangr folkvangr = new EntityNukeFolkvangr(world, this.getPositionVector(), null);
							world.spawnEntity(folkvangr);
							this.setDead();
							ci.cancel();
						}
					}
				}
				if (extType == 1) {
					double radius = age;
					List<Entity> entities = world.getEntitiesWithinAABB(Entity.class,new AxisAlignedBB(posX-radius,posY-radius,posZ-radius,posX+radius,posY+radius,posZ+radius));

					for (Entity entity : entities) {

						Vec3d vec = new Vec3d(posX-entity.posX,posY-entity.posY,posZ-entity.posZ);

						double dist = vec.length();

						if (dist > radius)
							continue;

						if (entity instanceof EntityRBMKDebris rbmk) {
							if (rbmk.getType() == EntityRBMKDebris.DebrisType.FUEL)
								entity.setDead();
						} else if (entity instanceof EntityZirnoxDebris zirnox) {
							if (zirnox.getType() == EntityZirnoxDebris.DebrisType.ELEMENT)
								entity.setDead();
						}
					}
				}
			}
		}
	}

	@Inject(method = "isJammed",at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setDead()V"),require = 1)
	private static void onIsJammed(World world,Entity entity,CallbackInfoReturnable<Boolean> cir,@Local(type = ATEntry.class) ATEntry jammer) {
		FolkvangrJammers.lastDetectedJammer = new BlockPos(jammer.x, jammer.y, jammer.z);
	}
}
