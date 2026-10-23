// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.misc.modular_turbine.power_port;

import com.leafia.contents.AddonBlocks;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MTPowerPortBlock extends BlockDirectional implements ITileEntityProvider {
	public MTPowerPortBlock(Material materialIn,String s) {
		super(materialIn);
		this.setTranslationKey(s);
		this.setRegistryName(s);
		AddonBlocks.ALL_BLOCKS.add(this);
	}
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this,FACING);
	}
	@Override
	public int getMetaFromState(IBlockState state) {
		return (state.getValue(FACING)).getIndex();
	}
	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing enumfacing = EnumFacing.byIndex(meta);
		return this.getDefaultState().withProperty(FACING,enumfacing);
	}
	@Override
	public IBlockState withRotation(IBlockState state,Rotation rot) {
		return state.withProperty(FACING,rot.rotate(state.getValue(FACING)));
	}
	@Override
	public IBlockState withMirror(IBlockState state,Mirror mirrorIn) {
		return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
	}
	@Override
	public void onBlockPlacedBy(World worldIn,BlockPos pos,IBlockState state,EntityLivingBase placer,ItemStack stack) {
		worldIn.setBlockState(pos,state.withProperty(FACING,EnumFacing.getDirectionFromEntityLiving(pos,placer)));
	}
	@Override
	public @Nullable TileEntity createNewTileEntity(World worldIn,int meta) {
		return new MTPowerPortTE();
	}
}
