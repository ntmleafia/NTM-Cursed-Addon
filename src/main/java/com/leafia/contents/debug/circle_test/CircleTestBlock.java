package com.leafia.contents.debug.circle_test;

import com.leafia.contents.machines.reactors.apr.blocks.core.APRCoreBlock;
import com.leafia.dev.blocks.blockbase.AddonBlockBase;
import com.leafia.dev.math.FiaMatrix;
import com.leafia.dev.math.FiaMatrix.RotationOrder;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class CircleTestBlock extends AddonBlockBase {
	public CircleTestBlock(Material m,String s) {
		super(m,s);
	}

	@Override
	public boolean onBlockActivated(World worldIn,BlockPos pos,IBlockState state,EntityPlayer playerIn,EnumHand hand,EnumFacing facing,float hitX,float hitY,float hitZ) {
		if (!worldIn.isRemote) {
			Map<BlockPos,IBlockState> states = new HashMap<>();
			APRCoreBlock.generateTorus(pos,6,states);
			for (Entry<BlockPos,IBlockState> entry : states.entrySet()) {
				worldIn.setBlockState(entry.getKey(),entry.getValue());
			}
		}
		return true;
	}

	public static void generateCircle(World world,BlockPos pos,double radius,IBlockState state) {
		double subdivs = Math.floor(radius*2*Math.PI/4);
		FiaMatrix mat = new FiaMatrix(new Vec3d(0.5,0.5,0.5));
		for (int i = 0; i <= subdivs; i++) {
			double ratio = i/subdivs;
			BlockPos offset = new BlockPos(mat.rotate(RotationOrder.XYZ,0,ratio*90,0).translate(0,0,-radius+0.25).position);
			world.setBlockState(pos.add(offset.getX(),0,offset.getZ()),state);
			world.setBlockState(pos.add(-offset.getX(),0,offset.getZ()),state);
			world.setBlockState(pos.add(offset.getX(),0,-offset.getZ()),state);
			world.setBlockState(pos.add(-offset.getX(),0,-offset.getZ()),state);
		}
	}
}
