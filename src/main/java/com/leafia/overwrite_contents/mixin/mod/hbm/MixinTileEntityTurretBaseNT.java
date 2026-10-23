// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.lib.Library;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.tileentity.turret.TileEntityTurretBaseNT;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityTurretBaseNT;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = TileEntityTurretBaseNT.class)
public abstract class MixinTileEntityTurretBaseNT extends TileEntityMachineBase implements IMixinTileEntityTurretBaseNT {
	public MixinTileEntityTurretBaseNT(int scount) {
		super(scount);
	}
	@Shadow(remap = false) public abstract Vec3d getTurretPos();
	@Shadow(remap = false) public abstract Vec3d getEntityPos(Entity e);
	@Shadow(remap = false) public abstract boolean entityAcceptableTarget(Entity e);

	@Shadow(remap = false) public Entity target;

	@Unique
	@Override
	public boolean leafia$detectFriendlyFire(Entity e) {
		Vec3d pos = this.getTurretPos();
		Vec3d ent = this.getEntityPos(e);
		RayTraceResult res = Library.rayTraceIncludeEntities(world,new Vec3d(pos.x, pos.y, pos.z), new Vec3d(ent.x, ent.y, ent.z), this.target);
		if (res == null)
			return false;
		else {
			if (res.typeOfHit == RayTraceResult.Type.MISS)
				return false;
			if (res.typeOfHit == RayTraceResult.Type.BLOCK)
				return false;
			if (res.typeOfHit == RayTraceResult.Type.ENTITY) {
				Entity hit = res.entityHit;
				return !this.entityAcceptableTarget(hit);
			}
			return false;
		}
	}
}
