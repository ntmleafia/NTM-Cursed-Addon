package com.leafia.dev.blocks.blockbase;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ModBlocks;
import com.hbm.items.IDynamicModels;
import com.leafia.contents.AddonBlocks;
import com.leafia.dev.math.FiaMatrix;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving.SpawnPlacementType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public abstract class AddonBlockDummyable extends BlockDummyable {
	public AddonBlockDummyable(Material materialIn,String s) {
		super(materialIn,s);
		ModBlocks.ALL_BLOCKS.remove(this);
		AddonBlocks.ALL_BLOCKS.add(this);
		IDynamicModels.INSTANCES.remove(this);
	}
	@Nullable
	public FiaMatrix getRotationMat(World world,BlockPos pos) {
		if(world.getBlockState(pos).getBlock() != this)
			return null;
		int meta = world.getBlockState(pos).getValue(META);
		if (meta < 12) return null;
		return switch(meta) {
			case 12 -> new FiaMatrix().rotateY(180);
			case 14 -> new FiaMatrix().rotateY(270);
			case 13 -> new FiaMatrix().rotateY(0);
			case 15 -> new FiaMatrix().rotateY(90);
			default -> null;
		};
	}
	public void makeExtra(World world,BlockPos pos) {
		makeExtra(world,pos.getX(),pos.getY(),pos.getZ());
	}

	/// crappers spawning on large machines pmo
	@Override
	public boolean canCreatureSpawn(IBlockState state,IBlockAccess world,BlockPos pos,SpawnPlacementType type) {
		return false;
	}
	/// crappers spawning on large machines pmo
	@Override
	public boolean canSpawnInBlock() {
		return false;
	}
	/// crappers spawning on large machines pmo
	@Override
	public boolean canEntitySpawn(IBlockState state,Entity entityIn) {
		return false;
	}

	/// Dimensions: Up, Down, Forward, Backward, Left, Right
	@Override
	public abstract int[] getDimensions();
}
