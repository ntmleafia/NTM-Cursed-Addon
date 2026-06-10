package com.leafia.contents.bomb.chud;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;

public class NukeChudTE extends TileEntity {
	@Override
	public double getMaxRenderDistanceSquared() {
		return 65536;
	}
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}
}
