package com.leafia.contents.machines.research.amsp.receiver;

import com.hbm.handler.MultiblockHandlerXR;
import com.hbm.lib.ForgeDirection;
import com.leafia.dev.blocks.blockbase.AddonBlockDummyable;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AMSPReceiverBlock extends AddonBlockDummyable {
	public AMSPReceiverBlock(Material materialIn,String s) {
		super(materialIn,s);
	}
	@Override
	public int[] getDimensions() {
		return new int[]{ 2,0,3,3,3,3 };
	}
	@Override
	public int getOffset() {
		return 3;
	}
	@Override
	public @Nullable TileEntity createNewTileEntity(World worldIn,int meta) {
		if (meta >= 12)
			return new AMSPReceiverTE();
		return null;
	}
}
