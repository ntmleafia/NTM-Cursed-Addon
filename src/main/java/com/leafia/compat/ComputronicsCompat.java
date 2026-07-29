package com.leafia.compat;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static pl.asie.computronics.reference.Capabilities.AUDIO_RECEIVER_CAPABILITY;

public final class ComputronicsCompat {
	public static boolean isAudioReceiver(World world, BlockPos pos, EnumFacing face) {
		TileEntity te = world.getTileEntity(pos.offset(face));
		return te != null && te.hasCapability(AUDIO_RECEIVER_CAPABILITY, face.getOpposite());
	}
}
