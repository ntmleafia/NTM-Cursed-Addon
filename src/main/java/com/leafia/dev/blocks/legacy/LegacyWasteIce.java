package com.leafia.dev.blocks.legacy;

import com.hbm.blocks.ModBlocks;
import com.hbm.hazard.HazardSystem;
import com.leafia.contents.AddonBlocks;
import net.minecraft.block.BlockIce;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LegacyWasteIce extends BlockIce {

	public LegacyWasteIce(String s) {
		super();
		this.setTranslationKey(s);
		this.setRegistryName(s);
		this.setSoundType(SoundType.GLASS);
		this.setHarvestLevel("pickaxe", -1);
		AddonBlocks.ALL_BLOCKS.add(this);
	}

	@Override
	public void onEntityWalk(World worldIn,BlockPos pos,Entity entity) {
		if (entity instanceof EntityLivingBase)
			HazardSystem.applyHazards(this, (EntityLivingBase)entity);
	}

	@Override
	public void onEntityCollision(World worldIn,BlockPos pos,IBlockState state,Entity entity) {
		if (entity instanceof EntityLivingBase)
			HazardSystem.applyHazards(this, (EntityLivingBase)entity);
	}
}
