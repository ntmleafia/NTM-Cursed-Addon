package com.leafia.dev.rad_correction;

import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

public class RadCorrection {
	public static boolean isUnderground(Entity entity) {
		BlockPos pos = new BlockPos(entity.posX,entity.posY,entity.posZ);
		int height = entity.world.getHeight(pos.getX(),pos.getZ());
		while (entity.world.getBlockState(pos).getMaterial() instanceof MaterialLiquid)
			pos = pos.up();
		if (pos.getY() <= height-8)
			return true;
		return false;
	}
}
