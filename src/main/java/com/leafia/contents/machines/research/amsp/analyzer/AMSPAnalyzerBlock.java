package com.leafia.contents.machines.research.amsp.analyzer;

import com.hbm.handler.MultiblockHandlerXR;
import com.hbm.lib.ForgeDirection;
import com.leafia.dev.blocks.blockbase.AddonBlockDummyable;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AMSPAnalyzerBlock extends AddonBlockDummyable {
	public AMSPAnalyzerBlock(Material materialIn,String s) {
		super(materialIn,s);
	}
	@Override
	public int[] getDimensions() {
		return new int[]{ 0,6,3,3,2,2 };
	}
	@Override
	public int getOffset() {
		return 0;
	}
	@Override
	protected void fillSpace(World world,int x,int y,int z,ForgeDirection dir,int o) {
		super.fillSpace(world,x,y,z,dir,o);
		x += dir.offsetX * o;
		z += dir.offsetZ * o;
		MultiblockHandlerXR.fillSpace(world,x,y,z,new int[]{0,6,2,2,3,-3},this,dir);
		MultiblockHandlerXR.fillSpace(world,x,y,z,new int[]{0,6,2,2,-3,3},this,dir);
	}
	@Override
	public boolean checkRequirement(World world,int x,int y,int z,ForgeDirection dir,int o) {
		x += dir.offsetX * o;
		z += dir.offsetZ * o;
		if (!MultiblockHandlerXR.checkSpace(world,x,y,z,new int[]{0,6,2,2,3,-3},x,y,z,dir))
			return false;
		if (!MultiblockHandlerXR.checkSpace(world,x,y,z,new int[]{0,6,2,2,-3,3},x,y,z,dir))
			return false;
		return super.checkRequirement(world,x,y,z,dir,o);
	}
	@Override
	public @Nullable TileEntity createNewTileEntity(World worldIn,int meta) {
		if (meta >= 12)
			return new AMSPAnalyzerTE();
		return null;
	}
}
