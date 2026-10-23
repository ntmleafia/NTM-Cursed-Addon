// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.debug.render_test;

import com.leafia.dev.blocks.blockbase.AddonBlockBase;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DebugRenderTestBlock extends AddonBlockBase implements ITileEntityProvider {
	public DebugRenderTestBlock(Material m,String s) {
		super(m,s);
	}
	@Override
	public @Nullable TileEntity createNewTileEntity(World worldIn,int meta) {
		return new DebugRenderTestTE();
	}
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.INVISIBLE;
	}
	@Override
	public boolean isFullBlock(IBlockState state) {
		return false;
	}
	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}
	@Override
	public boolean isNormalCube(IBlockState state,IBlockAccess world,BlockPos pos) {
		return false;
	}
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
}
