// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.unsorted.ateupd;

import com.leafia.dev.blocks.blockbase.AddonBlockDummyable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class Reserved6Dummyable extends ExtremelyRetardedSolution {
	public Reserved6Dummyable(String mod,String s) {
		super(Material.ROCK,s);
		setRegistryName(mod,s);
		Reserved6TE.ateupd.add(this);
		setCreativeTab(null);
		setHardness(15f);
	}
	public abstract ItemStack getItem();
	@Override
	public void getDrops(NonNullList<ItemStack> drops,IBlockAccess world,BlockPos pos,IBlockState state,int fortune) {
		drops.add(getItem());
	}
	@Override
	public ItemStack getPickBlock(IBlockState state,RayTraceResult target,World world,BlockPos pos,EntityPlayer player) {
		return getItem();
	}
	@Override
	public @Nullable TileEntity createNewTileEntity(World worldIn,int meta) {
		return new Reserved6TE();
	}
	@Override
	public int getOffset() {
		return 0;
	}
}
