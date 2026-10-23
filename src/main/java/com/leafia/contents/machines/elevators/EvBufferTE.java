// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.elevators;

import com.leafia.contents.machines.elevators.car.ElevatorEntity;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;

public class EvBufferTE extends TileEntity implements ITickable {
	public ElevatorEntity elevator;
	AxisAlignedBB scanAABB;
	@Override
	public void update() {
		if (world.isRemote) {
			if (scanAABB == null) {
				scanAABB = new AxisAlignedBB(
						pos.getX(),
						pos.getY(),
						pos.getZ(),
						pos.getX()+1,
						pos.getY()+50,
						pos.getZ()+1
				);
			}
			for (Entity entity : world.getEntitiesWithinAABBExcludingEntity(null,scanAABB)) {
				if (entity instanceof ElevatorEntity ev) {
					elevator = ev;
					break;
				}
			}
		}
	}
	AxisAlignedBB aabb;
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		if (aabb == null) {
			aabb = new AxisAlignedBB(
					pos.getX()-0.5,
					pos.getY(),
					pos.getZ()-0.5,
					pos.getX()+1.5,
					pos.getY()+6,
					pos.getZ()+1.5
			);
		}
		return aabb;
	}
}
