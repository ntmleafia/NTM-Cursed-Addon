// SPDX-FileCopyrightText: 2025-2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.building.light;

import com.hbm.main.MainRegistry;
import com.leafia.AddonBase;
import com.leafia.contents.AddonBlocks;
import com.leafia.dev.blocks.blockbase.AddonBlockBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class LightEmitter extends BlockAir {
	public LightEmitter(String s) {
		setLightLevel(1);
		this.setTranslationKey(s);
		this.setRegistryName(AddonBase.MODID, s);
		this.setHarvestLevel("pickaxe", 0);
		this.setCreativeTab(MainRegistry.controlTab);
		AddonBlocks.ALL_BLOCKS.add(this);
	}

	public static final int lightRange = 8;
	public void update(World world,BlockPos pos) {
		BlockPos originalPos = pos;
		boolean invalid = true;
		for (int i = 0; i < lightRange; i++) {
			pos = pos.up();
			IBlockState state = world.getBlockState(pos);
			if (state.getBlock() instanceof LightBlock light) {
				BlockPos core = light.findCore(world,pos);
				if (core != null) {
					if (world.getBlockState(core).getBlock() == AddonBlocks.lightLit) {
						invalid = false;
						break;
					} else
						break;
				} else
					break;
			}
		}
		if (invalid)
			world.setBlockState(originalPos,Blocks.AIR.getDefaultState());
	}

	@Override
	public void onBlockAdded(World worldIn,BlockPos pos,IBlockState state) {
		super.onBlockAdded(worldIn,pos,state);
		update(worldIn,pos);
	}

	@Override
	public void neighborChanged(IBlockState state,World worldIn,BlockPos pos,Block blockIn,BlockPos fromPos) {
		super.neighborChanged(state,worldIn,pos,blockIn,fromPos);
		update(worldIn,pos);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.INVISIBLE;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}

	@Override
	public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid){
		return false;
	}

	@Override
	public boolean isCollidable(){
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState,IBlockAccess worldIn,BlockPos pos){
		return NULL_AABB;
	}

	@Override
	public Item getItemDropped(IBlockState state,Random rand,int fortune){
		return Items.AIR;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side){
		return false;
	}

	@Override
	public boolean isBlockNormalCube(IBlockState state){
		return false;
	}

	@Override
	public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos){
		return true;
	}
}
