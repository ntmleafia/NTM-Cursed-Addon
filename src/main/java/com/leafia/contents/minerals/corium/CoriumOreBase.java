package com.leafia.contents.minerals.corium;

import com.hbm.handler.radiation.ChunkRadiationManager;
import com.leafia.contents.minerals.AddonOreBaked;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class CoriumOreBase extends AddonOreBaked {
	public static int rads = 450;
	public CoriumOreBase(String s,int harvestLvl,String texture) {
		super(s,harvestLvl,"resources/ores/corium/"+texture);
		this.setTickRandomly(true);
	}
	@Override
	public void onBlockAdded(World worldIn,BlockPos pos,IBlockState state) {
		super.onBlockAdded(worldIn,pos,state);
		worldIn.scheduleUpdate(pos,this,this.tickRate(worldIn));
	}
	@Override
	public void updateTick(World worldIn,BlockPos pos,IBlockState state,Random rand) {
		ChunkRadiationManager.proxy.incrementRad(worldIn,pos,rads/10f,rads);
		worldIn.scheduleUpdate(pos,this,this.tickRate(worldIn));
	}
	@Override
	public int tickRate(World world) {
		return 60 + world.rand.nextInt(500);
	}
}
