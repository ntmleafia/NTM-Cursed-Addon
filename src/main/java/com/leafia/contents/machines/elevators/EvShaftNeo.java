package com.leafia.contents.machines.elevators;

import com.leafia.dev.blocks.blockbase.AddonBlockBase;
import com.leafia.dev.math.FiaBB;
import com.leafia.dev.math.FiaMatrix;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EvShaftNeo extends AddonBlockBase {
	public EvShaftNeo(Material m,String s) {
		super(m,s);
		setDefaultState(getDefaultState().withProperty(BlockDirectional.FACING,EnumFacing.NORTH));
	}
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this,BlockDirectional.FACING);
	}
	@Override
	public int getMetaFromState(IBlockState state) {
		return Math.max(state.getValue(BlockDirectional.FACING).getHorizontalIndex(),0);
	}
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(BlockDirectional.FACING,EnumFacing.byHorizontalIndex(meta%4));
	}

	@Override
	public void addCollisionBoxToList(IBlockState state,World worldIn,BlockPos pos,AxisAlignedBB entityBox,List<AxisAlignedBB> collidingBoxes,@Nullable Entity entityIn,boolean isActualState) { }

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT_MIPPED;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isBlockNormalCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isNormalCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isNormalCube(IBlockState state,IBlockAccess world,BlockPos pos) {
		return false;
	}

	@Override
	public boolean isFullBlock(IBlockState state) {
		return false;
	}

	@Override
	public IBlockState getStateForPlacement(World world,BlockPos pos,EnumFacing facing,float hitX,float hitY,float hitZ,int meta,EntityLivingBase placer,EnumHand hand) {
		return getDefaultState().withProperty(BlockDirectional.FACING,placer.getHorizontalFacing().getOpposite());
	}
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state,IBlockAccess source,BlockPos pos) {
		int ang = state.getValue(BlockDirectional.FACING).getHorizontalIndex()*90;
		FiaBB bb = new FiaBB(new FiaMatrix(new Vec3d(0.5,0,0.5)).rotateY(-ang).translate(0,0,-0.5+2/16d).rotateY(180),-0.5,0,0.5,1,1/16d);
		return bb.toAABB();
	}
}
