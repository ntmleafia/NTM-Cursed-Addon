package com.leafia.contents.machines.reactors.apr.blocks.core;

import com.hbm.handler.MultiblockHandlerXR;
import com.hbm.lib.ForgeDirection;
import com.hbm.main.MainRegistry;
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
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import org.jetbrains.annotations.NotNull;
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
				IBlockState layer = (isInner && y < 0 && y > -depth) ? inner : state;
				poses.put(center.add(offset.getX(),y,offset.getZ()),layer);
				poses.put(center.add(-offset.getX(),y,offset.getZ()),layer);
				poses.put(center.add(offset.getX(),y,-offset.getZ()),layer);
				poses.put(center.add(-offset.getX(),y,-offset.getZ()),layer);
			}
		}
	}
	@Override
	public int[] getDimensions() {
		return new int[]{0,0,2,2,1,1};
	}
	@Override
	public int getOffset() {
		return 2;
	}
	@Override
	protected void fillSpace(World world,int x,int y,int z,ForgeDirection dir,int o) {
		super.fillSpace(world,x,y,z,dir,o);
		x += dir.offsetX * o;
		z += dir.offsetZ * o;
		MultiblockHandlerXR.fillSpace(world,x,y,z,new int[]{0,0,1,1,2,-2},this,dir);
		MultiblockHandlerXR.fillSpace(world,x,y,z,new int[]{0,0,1,1,-2,2},this,dir);
		MultiblockHandlerXR.fillSpace(world,x,y,z,new int[]{1,-1,0,0,0,0},this,dir);
		makeExtra(world,x,y+1,z);
	}
	@Override
	public boolean checkRequirement(World world,int x,int y,int z,ForgeDirection dir,int o) {
		x += dir.offsetX * o;
		z += dir.offsetZ * o;
		if (!MultiblockHandlerXR.checkSpace(world,x,y,z,new int[]{0,0,1,1,2,-2},x,y,z,dir))
			return false;
		if (!MultiblockHandlerXR.checkSpace(world,x,y,z,new int[]{0,0,1,1,-2,2},x,y,z,dir))
			return false;
		if (!MultiblockHandlerXR.checkSpace(world,x,y,z,new int[]{1,-1,0,0,0,0},x,y,z,dir))
			return false;
		return MultiblockHandlerXR.checkSpace(world,x,y,z,getDimensions(),x,y,z,dir);
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
		if (playerIn.isSneaking())
			return true;
		BlockPos core = findCore(worldIn,pos);
		if (core != null && worldIn.getTileEntity(core) instanceof APRCoreTE te) {
			boolean assembled = te.checkAssembled();
			if (worldIn.isRemote) {
				if (!te.assembled && !assembled)
					FMLNetworkHandler.openGui(playerIn,MainRegistry.instance,0,worldIn,core.getX(),core.getY(),core.getZ());
				return true;
			} else {
				if (!te.assembled) {
					te.assembled = assembled;
					if (assembled)
						te.onAssemble();
					return true;
				}
			}
		}
		return standardOpenBehavior(worldIn,pos,playerIn,0);
	}
	@Override
	public void breakBlock(@NotNull World world,@NotNull BlockPos pos,IBlockState state) {
		BlockPos core = findCore(world,pos);
		if (core != null && world.getTileEntity(core) instanceof APRCoreTE te)
			te.disassemble(core);
		super.breakBlock(world,pos,state);
	}
	@Override
	public void onBlockExploded(World world,BlockPos pos,Explosion explosion) {
		BlockPos core = findCore(world,pos);
		if (core != null && world.getTileEntity(core) instanceof APRCoreTE te)
			te.disassemble(core);
		super.onBlockExploded(world,pos,explosion);
	}
}
