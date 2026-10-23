// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.entity.projectile.EntityChemical;
import com.hbm.entity.projectile.EntityThrowableNT;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.leafia.contents.AddonBlocks;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityChemical.class)
public abstract class MixinEntityChemical extends EntityThrowableNT {
	@Shadow(remap = false)
	public abstract FluidType getType();

	public MixinEntityChemical(World world,EntityLivingBase thrower) {
		super(world,thrower);
	}
	@Inject(method = "onImpact",at = @At(value = "TAIL"),require = 1,remap = false)
	void leafia$onOnImpact(RayTraceResult mop,CallbackInfo ci) {
		if (getType() == Fluids.CONCRETE) {
			if (mop.typeOfHit == Type.BLOCK) {
				BlockPos bp = new BlockPos(posX,posY,posZ);
				if (world.getBlockState(bp).getMaterial().isReplaceable() && world.getBlockState(bp).getBlock().isReplaceable(world,bp)) {
					world.setBlockState(bp,AddonBlocks.fluid_concrete.getDefaultState());
					setDead();
				}
			}
		}
	}
}
