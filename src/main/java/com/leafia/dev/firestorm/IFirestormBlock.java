package com.leafia.dev.firestorm;

import com.hbm.blocks.BlockDummyable;
import com.leafia.settings.AddonConfig;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IFirestormBlock {
	/// also used via ASM
	static void ignite(World world,BlockPos pos) {
		if (!AddonConfig.enableFirestorm) return;
		if (world.getBlockState(pos).getBlock() instanceof IFirestormBlock firestormBlock) {
			if (firestormBlock.canCatchFire(world,pos))
				firestormBlock.catchFire(world,pos);
		}
	}
	default void catchFire(World world,BlockPos pos) {
		if (this instanceof BlockDummyable dummyable) {
			pos = dummyable.findCore(world,pos);
		}
		if (pos == null) return;
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof IFirestormTE firestormTE)
			firestormTE.catchFire();
	}
	default boolean canCatchFire(World world,BlockPos pos) {
		if (this instanceof BlockDummyable dummyable) {
			pos = dummyable.findCore(world,pos);
		}
		if (pos == null) return false;
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof IFirestormTE firestormTE)
			return firestormTE.canCatchFire();
		return false;
	}
	default boolean isDestroyed(World world,BlockPos pos) {
		if (this instanceof BlockDummyable dummyable) {
			pos = dummyable.findCore(world,pos);
		}
		if (pos == null) return false;
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof IFirestormTE firestormTE)
			return firestormTE.isDestroyed();
		return false;
	}
}
