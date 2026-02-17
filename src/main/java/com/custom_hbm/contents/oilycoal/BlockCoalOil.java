package com.custom_hbm.contents.oilycoal;

import com.hbm.blocks.ModBlocks;
import com.leafia.contents.AddonBlocks;
import com.leafia.contents.AddonBlocks.LegacyBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import java.util.Random;

public class BlockCoalOil extends BlockOre {

	public BlockCoalOil(String s) {
		super();
		this.setTranslationKey(s);
		this.setRegistryName(s);

		AddonBlocks.ALL_BLOCKS.add(this);
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
		for(EnumFacing dir : EnumFacing.VALUES) {

        	IBlockState nS = world.getBlockState(pos.offset(dir));
        	Block n = nS.getBlock();

        	if(n == LegacyBlocks.ore_coal_oil_burning || n == ModBlocks.balefire || n == Blocks.FIRE || nS.getMaterial() == Material.LAVA) {
        		world.scheduleUpdate(pos, this, world.rand.nextInt(20) + 2);
        	}
        }
	}

	@Override
	public void onExplosionDestroy(World worldIn, BlockPos pos, Explosion explosionIn) {
		worldIn.setBlockState(pos, Blocks.FIRE.getDefaultState());
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		world.setBlockState(pos, LegacyBlocks.ore_coal_oil_burning.getDefaultState());
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Items.COAL;
	}

	@Override
	public int quantityDropped(IBlockState state, int fortune, Random random) {
		return 2 + random.nextInt(2);
	}
}
