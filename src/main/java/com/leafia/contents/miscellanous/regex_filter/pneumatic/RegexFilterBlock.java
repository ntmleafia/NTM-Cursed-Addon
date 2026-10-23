// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.miscellanous.regex_filter.pneumatic;

import com.hbm.main.MainRegistry;
import com.leafia.contents.AddonBlocks;
import com.leafia.dev.machine.MachineTooltip;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RegexFilterBlock extends BlockContainer {
	public static final PropertyDirection FACING = BlockDirectional.FACING;
	public RegexFilterBlock(Material materialIn,String s) {
		super(materialIn);
		this.setTranslationKey(s);
		this.setRegistryName(s);
		AddonBlocks.ALL_BLOCKS.add(this);
	}
	@Override
	public boolean onBlockActivated(World worldIn,BlockPos pos,IBlockState state,EntityPlayer playerIn,EnumHand hand,EnumFacing facing,float hitX,float hitY,float hitZ) {
		if (!worldIn.isRemote)
			playerIn.openGui(MainRegistry.instance,0,worldIn,pos.getX(),pos.getY(),pos.getZ());
		return true;
	}
	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, new IProperty[] { FACING });
	}
	@Override
	public int getMetaFromState(IBlockState state){
		return ((EnumFacing)state.getValue(FACING)).getIndex();
	}
	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing enumfacing = EnumFacing.byIndex(meta);
		return this.getDefaultState().withProperty(FACING, enumfacing);
	}
	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot){
		return state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
	}
	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn){
		return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue(FACING)));
	}
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return getDefaultState().withProperty(FACING,facing.getOpposite());
	}
	@Override
	public @Nullable TileEntity createNewTileEntity(World worldIn,int meta) {
		return new RegexFilterTE();
	}
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
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
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		if (state.getBlock() instanceof RegexFilterBlock) {
			EnumFacing.Axis axis = state.getValue(FACING).getAxis();
			if (axis == EnumFacing.Axis.X)
				return new AxisAlignedBB(0,4d/16,4d/16,1,1-4d/16,1-4d/16);
			if (axis == EnumFacing.Axis.Y)
				return new AxisAlignedBB(4d/16,0,4d/16,1-4d/16,1,1-4d/16);
			if (axis == EnumFacing.Axis.Z)
				return new AxisAlignedBB(4d/16,4d/16,0,1-4d/16,1-4d/16,1);
		}
		return super.getBoundingBox(state, source, pos);
	}
}
