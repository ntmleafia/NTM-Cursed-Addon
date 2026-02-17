package com.leafia.contents.machines.reactors.pwr.blocks.components.element;

import com.hbm.tileentity.TileEntityProxyInventory;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class PWRProxyTE extends TileEntityProxyInventory {
	@Override
	public TileEntity getTE() {
		for (int i = pos.getY()+1; i < 256; i++) {
			BlockPos newPos = new BlockPos(pos.getX(),i,pos.getZ());
			IBlockState state = world.getBlockState(newPos);
			if (state.getBlock() == getBlockType()) {
				if (!state.getValue(PWRElementBlock.stacked))
					return world.getTileEntity(newPos);
			} else break;
		}
		return null;
	}
}
