package com.leafia.contents.machines.processing.solblaster;

import com.hbm.blocks.ITooltipProvider;
import com.hbm.handler.MultiblockHandlerXR;
import com.hbm.lib.ForgeDirection;
import com.hbm.tileentity.TileEntityProxyCombo;
import com.leafia.contents.machines.processing.solblaster.recipes.SolBlasterRecipes;
import com.leafia.dev.blocks.blockbase.AddonBlockDummyable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SolBlasterBlock extends AddonBlockDummyable implements ITooltipProvider {
	public SolBlasterBlock(Material materialIn,String s) {
		super(materialIn,s);
	}
	@Override
	public int[] getDimensions() {
		return new int[]{2,0,1,1,1,1};
	}
	@Override
	public int getOffset() {
		return 1;
	}
	@Override
	public @Nullable TileEntity createNewTileEntity(World worldIn,int meta) {
		if (meta >= 12)
			return new SolBlasterTE();
		else if (meta >= 6)
			return new TileEntityProxyCombo(true,false,false);
		return null;
	}
	@Override
	protected void fillSpace(World world,int x,int y,int z,ForgeDirection dir,int o) {
		super.fillSpace(world,x,y,z,dir,o);
		x += dir.offsetX * o;
		z += dir.offsetZ * o;
		MultiblockHandlerXR.fillSpace(world,x,y,z,new int[]{4,-3,0,0,0,0},this,dir);
		makeExtra(world,x+1,y,z+1);
		makeExtra(world,x+1,y,z-1);
		makeExtra(world,x-1,y,z+1);
		makeExtra(world,x-1,y,z-1);
	}
	@Override
	public boolean checkRequirement(World world,int x,int y,int z,ForgeDirection dir,int o) {
		x += dir.offsetX * o;
		z += dir.offsetZ * o;
		if (!MultiblockHandlerXR.checkSpace(world,x,y,z,new int[]{4,-3,0,0,0,0},x,y,z,dir))
			return false;
		return MultiblockHandlerXR.checkSpace(world,x,y,z,getDimensions(),x,y,z,dir);
	}
	@Override
	public boolean onBlockActivated(World worldIn,BlockPos pos,IBlockState state,EntityPlayer playerIn,EnumHand hand,EnumFacing facing,float hitX,float hitY,float hitZ) {
		return standardOpenBehavior(worldIn,pos,playerIn,0);
	}
	@Override
	public void breakBlock(@NotNull World world,@NotNull BlockPos pos,IBlockState state) {
		super.breakBlock(world,pos,state);
		world.updateComparatorOutputLevel(pos,this);
	}
	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {
		return true;
	}
	@Override
	public int getComparatorInputOverride(IBlockState blockState,World worldIn,BlockPos pos) {
		BlockPos core = findCore(worldIn,pos);
		if (core != null) {
			TileEntity te = worldIn.getTileEntity(core);
			if (te instanceof SolBlasterTE sol) {
				int power = 0;
				for (int i = 5; i < 20; i++) {
					if (SolBlasterRecipes.isValidInput(sol.inventory.getStackInSlot(i)))
						power++;
				}
				return power;
			}
		}
		return 0;
	}
	@Override
	public void neighborChanged(@NotNull IBlockState state,World world,@NotNull BlockPos pos,@NotNull Block blockIn,@NotNull BlockPos fromPos) {
		BlockPos core = findCore(world,pos);
		if (core != null) {
			TileEntity te = world.getTileEntity(core);
			if (te instanceof SolBlasterTE sol)
				sol.updateRedstonePower(pos);
		}
		super.neighborChanged(state,world,pos,blockIn,fromPos);
	}
}
