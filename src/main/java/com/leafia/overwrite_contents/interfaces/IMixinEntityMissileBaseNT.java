package com.leafia.overwrite_contents.interfaces;

import com.hbm.entity.missile.EntityMissileBaseNT;
import com.leafia.database.AirDetonationMissiles;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public interface IMixinEntityMissileBaseNT {
	/// nuclear missiles should use this
	default boolean shouldDetonateInAir() {
		return AirDetonationMissiles.defaultAirDetonationMissiles.contains(((EntityMissileBaseNT)this).getMissileItemForInfo().getItem());
	}
	/**
	 * usually shouldn't be overridden
	 * @return true if it should drop debris, false otherwise
	 */
	default boolean interactABMissile() {
		if (shouldDetonateInAir())
			return true;
		explodeAB();
		return false;
	}
	/// override this method for custom interaction
	default void explodeAB() {
		EntityMissileBaseNT missile = (EntityMissileBaseNT)this;
		missile.onMissileImpact(generateFakeRayTraceResult(missile));
	}
	static RayTraceResult generateFakeRayTraceResult(EntityMissileBaseNT missile) {
		return new RayTraceResult(new Vec3d(missile.posX,missile.posY,missile.posZ),EnumFacing.UP,new BlockPos(missile.posX,missile.posY,missile.posZ));
	}
}
