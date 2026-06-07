package com.leafia.contents.machines.research.amsp.receiver;

import com.hbm.handler.MultiblockHandlerXR;
import com.hbm.lib.ForgeDirection;
import com.leafia.dev.blocks.blockbase.AddonBlockDummyable;
import com.leafia.dev.machine.MachineTooltip;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AMSPReceiverBlock extends AddonBlockDummyable {
	public AMSPReceiverBlock(Material materialIn,String s) {
		super(materialIn,s);
	}
	@Override
	public void addInformation(ItemStack stack,@Nullable World worldIn,List<String> tooltip,ITooltipFlag flagIn) {
		MachineTooltip.addWIP(tooltip);
		super.addInformation(stack,worldIn,tooltip,flagIn);
	}
	@Override
	public int[] getDimensions() {
		return new int[]{ 1,0,3,3,3,3 };
	}
	@Override
	public int getOffset() {
		return 3;
	}
	@Override
	protected void fillSpace(World world,int x,int y,int z,ForgeDirection dir,int o) {
		super.fillSpace(world,x,y,z,dir,o);
		x += dir.offsetX * o;
		z += dir.offsetZ * o;
		MultiblockHandlerXR.fillSpace(world,x,y,z,new int[]{2,-2,3,-2,3,3},this,dir);
		MultiblockHandlerXR.fillSpace(world,x,y,z,new int[]{2,-2,-2,3,3,3},this,dir);
		MultiblockHandlerXR.fillSpace(world,x,y,z,new int[]{2,-2,1,1,3,-2},this,dir);
		MultiblockHandlerXR.fillSpace(world,x,y,z,new int[]{2,-2,1,1,-2,3},this,dir);
	}
	@Override
	public boolean checkRequirement(World world,int x,int y,int z,ForgeDirection dir,int o) {
		x += dir.offsetX * o;
		z += dir.offsetZ * o;
		if (!MultiblockHandlerXR.checkSpace(world,x,y,z,new int[]{2,-2,3,-2,3,3},x,y,z,dir))
			return false;
		if (!MultiblockHandlerXR.checkSpace(world,x,y,z,new int[]{2,-2,-2,3,3,3},x,y,z,dir))
			return false;
		if (!MultiblockHandlerXR.checkSpace(world,x,y,z,new int[]{2,-2,1,1,3,-2},x,y,z,dir))
			return false;
		if (!MultiblockHandlerXR.checkSpace(world,x,y,z,new int[]{2,-2,1,1,-2,3},x,y,z,dir))
			return false;
		return super.checkRequirement(world,x,y,z,dir,o);
	}
	@Override
	public @Nullable TileEntity createNewTileEntity(World worldIn,int meta) {
		if (meta >= 12)
			return new AMSPReceiverTE();
		return null;
	}
}
