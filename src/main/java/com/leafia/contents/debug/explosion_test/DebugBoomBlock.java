package com.leafia.contents.debug.explosion_test;

import com.custom_hbm.util.LCETuple.Pair;
import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BlockSellafieldSlaked;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.*;
import com.hbm.handler.threading.PacketThreading;
import com.hbm.interfaces.IBomb;
import com.hbm.particle.helper.ExplosionCreator;
import com.leafia.CommandLeaf;
import com.leafia.dev.LeafiaUtil;
import com.leafia.dev.blocks.blockbase.AddonBlockBase;
import com.leafia.dev.optimization.LeafiaParticlePacket.SmokeShockwaveParticle;
import com.leafia.unsorted.ParticleBasicSmoke;
import com.leafia.unsorted.ParticleSmokeShockwave;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.util.ArrayList;
import java.util.List;

public class DebugBoomBlock extends AddonBlockBase implements IBomb {
	public DebugBoomBlock(Material m,String s) {
		super(m,s);
	}

	@Override
	public boolean onBlockActivated(World world,BlockPos pos,IBlockState state,EntityPlayer playerIn,EnumHand hand,EnumFacing facing,float hitX,float hitY,float hitZ) {
		int x = pos.getX(), y = pos.getY(), z = pos.getZ();
		if (world.isRemote) {
			//ParticleSmokeShockwave uwu = new ParticleSmokeShockwave(world,x+0.5,y+2,z+0.5);
			//Minecraft.getMinecraft().effectRenderer.addEffect(uwu);
		}
		return true;
	}

	@Override
	public BombReturnCode explode(World world,BlockPos pos,Entity entity) {
		int x = pos.getX(), y = pos.getY(), z = pos.getZ();
		if (!world.isRemote) {
			/*ExplosionVNT vnt = new ExplosionVNT(world, x + .5, y + .5, z + .5, 35F, entity);
			vnt.setBlockAllocator(new BlockAllocatorStandard());
			vnt.setBlockProcessor(new BlockProcessorStandard());
			vnt.setEntityProcessor(new EntityProcessorCrossSmooth(0.5, 10F).setupPiercing(5F, 0.2F));
			vnt.setPlayerProcessor(new PlayerProcessorStandard());
			vnt.setSFX(new ExplosionEffectWeapon(5, 1F, .5F));
			vnt.explode();*/
			createMeteorCrater(world,pos);
			createMeteorExplosionEffect(world,x+0.5,y+0.5,z+0.5);
		}
		return BombReturnCode.DETONATED;
	}
	public static final int radius = 11;
	public static final int maxDepth = 8;
	public static void createMeteorCrater(World world,BlockPos pos) {
		int cx = pos.getX();
		int cz = pos.getZ();
		List<Pair<BlockPos,IBlockState>> debris = new ArrayList<>();
		MutableBlockPos p = new MutableBlockPos();
		for (int ox = -radius; ox <= radius; ox++) {
			for (int oz = -radius; oz <= radius; oz++) {
				double horizontalDistance = Math.sqrt(ox*ox+oz*oz);
				double ratio = horizontalDistance/radius;
				double depthRatio = Math.max(1-ratio,0);
				int depth = (int)Math.ceil(maxDepth*depthRatio)-world.rand.nextInt(2);
				if (depth > 0) {
					int my = world.getHeight(cx+ox,cz+oz)-1;
					for (int y = my; y > my-depth; y--) {
						p.setPos(cx+ox,y,cz+oz);
						if (y <= 0) break;
						if (world.rand.nextInt(5) == 0) {
							IBlockState state = world.getBlockState(p);
							if (state.getRenderType() != EnumBlockRenderType.INVISIBLE && !state.getMaterial().isLiquid()) {
								double moveRatio = 1-Math.sqrt(p.distanceSq(pos))/radius*0.5;
								if (moveRatio > 0) {
									double moveAmt = Math.pow(moveRatio,2)*radius*(0.95*world.rand.nextDouble()*0.5)+radius/2d;
									BlockPos newPos = p.add(ox/horizontalDistance*moveAmt,0,oz/horizontalDistance*moveAmt);
									debris.add(new Pair<>(newPos,state));
								}
							}
						}
						if (y < pos.getY()-maxDepth+maxDepth*0.3)
							world.setBlockState(p,Blocks.LAVA.getDefaultState());
						else {
							world.setBlockToAir(p);
							if (y == my-depth+1) {
								if (world.rand.nextInt(8) == 0)
									world.setBlockState(p,Blocks.FIRE.getDefaultState());
							}
						}
					}
					for (int y = my-depth; y > my-depth-3; y--) {
						p.setPos(cx+ox,y,cz+oz);
						if (y <= 0) break;
						if (LeafiaUtil.isSolidVisibleCube(world.getBlockState(p)))
							world.setBlockState(
									p,
									ModBlocks.sellafield_slaked.getDefaultState().withProperty(BlockSellafieldSlaked.SHADE,(int)(Math.pow(1-ratio,0.65)*6))
							);
					}
				}
			}
		}
		for (Pair<BlockPos,IBlockState> pair : debris)
			world.setBlockState(world.getHeight(pair.getA()),pair.getB());
	}
	public static void createMeteorExplosionEffect(World world,double x,double y,double z) {
		SmokeShockwaveParticle particle = new SmokeShockwaveParticle();
		particle.emit(new Vec3d(x,y,z),new Vec3d(0,1,0),world.provider.getDimension(),500);
		ExplosionCreator.composeEffect(
				world,x,y,z,
				10,2F,0.5F,
				0F,
				5,16,50,0.5F,3F,-2F,
				0
		);
		ExplosionCreator.composeEffect(
				world,x,y,z,
				10,2F,0.5F,
				0,
				12,8,50,1F,3F,-2F,
				0
		);
		ExplosionCreator.composeEffect(
				world,x,y,z,
				10,2F,0.5F,
				0,
				16,4,50,1.2F,3F,-2F,
				0
		);
		PacketThreading.createSendToAllTrackingThreadedPacket(
				new CommandLeaf.ShakecamPacket(new String[]{
						"type=smooth",
						"preset=RUPTURE",
						"blurDulling*2",
						"duration/2",
						"speed/2",
						"intensity*1.5",
						"range=250"
				}).setPos(new BlockPos(x,y,z)),
				new NetworkRegistry.TargetPoint(world.provider.getDimension(),x,y,z,300)
		);
	}
}
