package com.leafia.contents.machines.reactors.apr.blocks.core;

import com.hbm.tileentity.TileEntityProxyCombo;
import com.leafia.contents.AddonBlocks.APR;
import com.leafia.contents.machines.reactors.apr.blocks.APRComponentBlock;
import com.leafia.contents.machines.reactors.apr.blocks.APRComponentBlock.APRComponentType;
import com.leafia.dev.blocks.blockbase.AddonBlockDummyable;
import com.leafia.dev.math.FiaMatrix;
import com.leafia.dev.math.FiaMatrix.RotationOrder;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public class APRCoreBlock extends AddonBlockDummyable {
	public APRCoreBlock(Material materialIn,String s) {
		super(materialIn,s);
	}
	public static void generateTorus(BlockPos pos,double radius,Map<BlockPos,IBlockState> poses) {
		pos = pos.down();
		APRCoreBlock.generateCircle(pos,radius+.5,poses,3,false);
		APRCoreBlock.generateCircle(pos,radius-.5,poses,3,false);
		APRCoreBlock.generateCircle(pos,radius+1,poses,3,false);
		APRCoreBlock.generateCircle(pos,radius-1,poses,3,false);
		APRCoreBlock.generateCircle(pos.up(),radius,poses,5,true);
		/*radius++; this sucks man
		double subdivs = Math.floor(radius*2*Math.PI/4);
		FiaMatrix mat = new FiaMatrix(new Vec3d(0.5,0.5,0.5));
		IBlockState hull = APR.apr_component.getDefaultState()
				.withProperty(APRComponentBlock.VARIANT,APRComponentType.HULL_ASSEMBLED);
		IBlockState support = APR.apr_component.getDefaultState()
				.withProperty(APRComponentBlock.VARIANT,APRComponentType.SUPPORT);
		IBlockState inner = APR.apr_component.getDefaultState()
				.withProperty(APRComponentBlock.VARIANT,APRComponentType.BLADES);
		for (int i = 0; i <= subdivs; i++) {
			double ratio = i/subdivs;
			FiaMatrix rot = mat.rotate(RotationOrder.XYZ,0,ratio*90,0);
			int j = 1;
			while (true) {
				BlockPos offset = new BlockPos(rot.translate(0,0,-radius+0.25+j*0.75).position);
				IBlockState state = hull;
				if (offset.getX() == 0 || offset.getZ() == 0)
					state = support;
				if (j == 1) {
					for (int y = 0; y >= -depth; y--) {
						poses.put(center.add(offset.getX(),y,offset.getZ()),inner);
						poses.put(center.add(-offset.getX(),y,offset.getZ()),inner);
						poses.put(center.add(offset.getX(),y,-offset.getZ()),inner);
						poses.put(center.add(-offset.getX(),y,-offset.getZ()),inner);
					}

					poses.put(center.add(offset.getX(),1,offset.getZ()),state);
					poses.put(center.add(-offset.getX(),1,offset.getZ()),state);
					poses.put(center.add(offset.getX(),1,-offset.getZ()),state);
					poses.put(center.add(-offset.getX(),1,-offset.getZ()),state);

					poses.put(center.add(offset.getX(),-depth-1,offset.getZ()),state);
					poses.put(center.add(-offset.getX(),-depth-1,offset.getZ()),state);
					poses.put(center.add(offset.getX(),-depth-1,-offset.getZ()),state);
					poses.put(center.add(-offset.getX(),-depth-1,-offset.getZ()),state);
				} else {
					for (int y = 0; y >= -depth; y--) {
						poses.put(center.add(offset.getX(),y,offset.getZ()),state);
						poses.put(center.add(-offset.getX(),y,offset.getZ()),state);
						poses.put(center.add(offset.getX(),y,-offset.getZ()),state);
						poses.put(center.add(-offset.getX(),y,-offset.getZ()),state);
					}
				}
				if (j == 1)
					j = 0;
				else if (j == 0)
					j = 2;
				else
					break;
			}
		}
		*/
	}
	public static void generateCircle(BlockPos center,double radius,Map<BlockPos,IBlockState> poses,int depth,boolean isInner) {
		double subdivs = Math.floor(radius*2*Math.PI/4);
		FiaMatrix mat = new FiaMatrix(new Vec3d(0.5,0.5,0.5));
		IBlockState hull = APR.apr_component.getDefaultState()
				.withProperty(APRComponentBlock.VARIANT,APRComponentType.HULL_ASSEMBLED);
		IBlockState support = APR.apr_component.getDefaultState()
				.withProperty(APRComponentBlock.VARIANT,APRComponentType.SUPPORT);
		IBlockState inner = APR.apr_component.getDefaultState()
				.withProperty(APRComponentBlock.VARIANT,APRComponentType.BLADES);
		for (int i = 0; i <= subdivs; i++) {
			double ratio = i/subdivs;
			FiaMatrix rot = mat.rotate(RotationOrder.XYZ,0,ratio*90,0);
			BlockPos offset = new BlockPos(rot.translate(0,0,-radius+0.25).position);
			IBlockState state = hull;
			if (offset.getX() == 0 || offset.getZ() == 0)
				state = support;
			for (int y = 0; y >= -depth; y--) {
				if (isInner && y < 0 && y > -depth)
					state = inner;
				poses.put(center.add(offset.getX(),y,offset.getZ()),state);
				poses.put(center.add(-offset.getX(),y,offset.getZ()),state);
				poses.put(center.add(offset.getX(),y,-offset.getZ()),state);
				poses.put(center.add(-offset.getX(),y,-offset.getZ()),state);
			}
		}
	}
	@Override
	public int[] getDimensions() {
		return new int[]{0,0,1,1,1,1};
	}
	@Override
	public int getOffset() {
		return 0;
	}
	@Override
	public @Nullable TileEntity createNewTileEntity(World worldIn,int meta) {
		if (meta >= 12)
			return new APRCoreTE();
		else if (meta >= extra)
			return new TileEntityProxyCombo(false,false,true);
		return null;
	}
	@Override
	public boolean onBlockActivated(World worldIn,BlockPos pos,IBlockState state,EntityPlayer playerIn,EnumHand hand,EnumFacing facing,float hitX,float hitY,float hitZ) {
		return standardOpenBehavior(worldIn,pos,playerIn,0);
	}
}
