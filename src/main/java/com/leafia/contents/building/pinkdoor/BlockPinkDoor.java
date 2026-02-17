package com.leafia.contents.building.pinkdoor;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BlockModDoor;
import com.hbm.entity.grenade.EntityGrenadeZOMG;
import com.hbm.entity.projectile.EntityBoxcar;
import com.hbm.entity.projectile.EntityBulletBase;
import com.hbm.entity.projectile.EntityFallingNuke;
import com.hbm.entity.projectile.EntityRBMKDebris;
import com.hbm.entity.projectile.EntityRBMKDebris.DebrisType;
import com.hbm.explosion.ExplosionChaos;
import com.hbm.explosion.ExplosionLarge;
import com.hbm.handler.BulletConfigSyncingUtil;
import com.hbm.interfaces.IBomb;
import com.hbm.lib.HBMSoundHandler;
import com.leafia.contents.AddonBlocks;
import com.leafia.dev.optimization.LeafiaParticlePacket.PinkRBMK;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

// Why. Just why. This is brutal.
public class BlockPinkDoor extends BlockModDoor implements IBomb {
	public BlockPinkDoor(Material materialIn,String s) {
		super(materialIn,s);
		this.setSoundType(SoundType.WOOD);
        ModBlocks.ALL_BLOCKS.remove(this);
		AddonBlocks.ALL_BLOCKS.add(this);
	}

	private int getCloseSound()
	{
		return 1012;
	}

	private int getOpenSound()
	{
		return 1006;
	}

	@Override
	public boolean onBlockActivated(World worldIn,BlockPos pos,IBlockState state,EntityPlayer playerIn,EnumHand hand,EnumFacing facing,float hitX,float hitY,float hitZ) {
		BlockPos base = state.getValue(HALF) == EnumDoorHalf.LOWER ? pos : pos.down();
		IBlockState baseState = pos.equals(base) ? state : worldIn.getBlockState(base);
		if (baseState.getBlock() != this) {
			return false;
		} else {
			IBlockState toggled = baseState.cycleProperty(OPEN);
			worldIn.setBlockState(base, toggled, 10);
			worldIn.markBlockRangeForRenderUpdate(base, pos);
			worldIn.playEvent(playerIn, ((Boolean)state.getValue(OPEN)).booleanValue() ? this.getOpenSound() : this.getCloseSound(), pos, 0);
			return true;
		}
	}
	@Override
	public void toggleDoor(World worldIn, BlockPos pos, boolean open) {
		IBlockState state = worldIn.getBlockState(pos);
		if (state.getBlock() == this) {
			BlockPos base = state.getValue(HALF) == EnumDoorHalf.LOWER ? pos : pos.down();
			IBlockState baseState = pos.equals(base) ? state : worldIn.getBlockState(base);
			if (baseState.getBlock() == this && (Boolean)baseState.getValue(OPEN) != open) {
				worldIn.setBlockState(base, baseState.withProperty(OPEN, open), 10);
				worldIn.markBlockRangeForRenderUpdate(base, pos);
				worldIn.playEvent((EntityPlayer)null, open ? this.getOpenSound() : this.getCloseSound(), pos, 0);
			}

		}
	}
	@Override
	public void neighborChanged(IBlockState state,World worldIn,BlockPos pos,Block blockIn,BlockPos fromPos) {
		if (state.getValue(HALF) == EnumDoorHalf.UPPER) {
			BlockPos below = pos.down();
			IBlockState belowState = worldIn.getBlockState(below);
			if (belowState.getBlock() != this) {
				worldIn.setBlockToAir(pos);
			} else if (blockIn != this) {
				belowState.neighborChanged(worldIn, below, blockIn, fromPos);
			}

		} else {
			boolean changed = false;
			BlockPos above = pos.up();
			IBlockState aboveState = worldIn.getBlockState(above);
			if (aboveState.getBlock() != this) {
				worldIn.setBlockToAir(pos);
				changed = true;
			}

			if (!worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos.down(), EnumFacing.UP)) {
				worldIn.setBlockToAir(pos);
				changed = true;
				if (aboveState.getBlock() == this) {
					worldIn.setBlockToAir(above);
				}
			}

			if (changed) {
				if (!worldIn.isRemote) {
					this.dropBlockAsItem(worldIn, pos, state, 0);
				}

			} else {
				boolean powered = worldIn.isBlockPowered(pos) || worldIn.isBlockPowered(above);
				if (blockIn != this && (powered || blockIn.getDefaultState().canProvidePower()) && powered != (Boolean)aboveState.getValue(POWERED)) {
					worldIn.setBlockState(above, aboveState.withProperty(POWERED, powered), 2);
					if (powered != (Boolean)state.getValue(OPEN)) {
						worldIn.setBlockState(pos, state.withProperty(OPEN, powered), 2);
						worldIn.markBlockRangeForRenderUpdate(pos, pos);
						worldIn.playEvent((EntityPlayer)null, powered ? this.getOpenSound() : this.getCloseSound(), pos, 0);
					}
				}

			}
		}
	}

	protected void spawnDebris(World world,BlockPos pos,DebrisType type) { // oh boy
		EntityRBMKDebris debris = new EntityRBMKDebris(world, pos.getX() + 0.5D, pos.getY() + 1, pos.getZ() + 0.5D, type);
		debris.motionX = world.rand.nextGaussian() * 0.25D * 2;
		debris.motionZ = world.rand.nextGaussian() * 0.25D * 2;
		debris.motionY = 0.5D + world.rand.nextDouble() * 1.5D;

		if(type == DebrisType.LID) {
			debris.motionX *= 0.5D;
			debris.motionY += 0.5D;
			debris.motionZ *= 0.5D;
		}

		world.spawnEntity(debris);
	}
	protected void fuckyou(World world,BlockPos pos,int height) {
		EntityBulletBase projectile = new EntityBulletBase(world,BulletConfigSyncingUtil.NUKE_NORMAL);
		projectile.setPosition(pos.getX(),world.getHeight(pos.getX(),pos.getY())+height,pos.getZ());
		projectile.setVelocity(0,-0.15,0);
		world.spawnEntity(projectile);
	}
	@Override
	public BombReturnCode explode(World world,BlockPos pos,Entity detonator) {
		world.setBlockToAir(pos);
		/*PacketThreading.createSendToAllTrackingThreadedPacket(
				new CommandLeaf.ShakecamPacket(new String[]{
						"type=smooth",
						"preset=RUPTURE",
						"duration/2",
						"blurDulling*4","blurExponent*2",
						"speed*1.5","duration/2",
						"range="+300
				}).setPos(pos),
				new NetworkRegistry.TargetPoint(world.provider.getDimension(),pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5,350)
		);*/
		world.createExplosion(null,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,15,true);
		new PinkRBMK().emit(new Vec3d(pos).add(.5,-1,.5),new Vec3d(0,0,0),world.provider.getDimension());
		world.playSound(null,pos,HBMSoundHandler.rbmk_explosion,SoundCategory.BLOCKS,30,1);
		for (int i = 0; i < 15; i++) spawnDebris(world,pos,DebrisType.BLANK);
		for (int i = 0; i < 10; i++) spawnDebris(world,pos,DebrisType.GRAPHITE);
		for (int i = 0; i < 4; i++) spawnDebris(world,pos,DebrisType.LID);
		for (int i = 0; i < 6; i++) spawnDebris(world,pos,DebrisType.FUEL);
		for (int i = 0; i < 9; i++) spawnDebris(world,pos,DebrisType.ROD);
		ExplosionLarge.spawnShrapnelShower(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 1D, 0, 35, 0.2D);
		ExplosionLarge.spawnShrapnels(world, pos.getZ() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 8);
		ExplosionChaos.zomg(
				world,pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5,30,null,
				new EntityGrenadeZOMG(world,pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5)
		);
		for (int i = 0; i < 25; i++) {
			world.spawnEntity(new EntityFallingBlock(world,
					pos.getX() + world.rand.nextInt(201)-100,
					world.rand.nextInt(50)+455,
					pos.getZ() + world.rand.nextInt(201)-100,
					ModBlocks.corium_block.getDefaultState()
			));
		}
		for (int i = 0; i < 19; i++) {
			if (i == 2 || i == 3) {
				EntityBoxcar pippo = new EntityBoxcar(world);
				pippo.posX = pos.getX() + world.rand.nextGaussian() * 25;
				pippo.posY = 350;
				pippo.posZ = pos.getZ() + world.rand.nextGaussian() * 25;
				world.spawnEntity(pippo);
				continue;
			}
			fuckyou(world,pos.add((world.rand.nextDouble()*2-0.5)*20,0,(world.rand.nextDouble()*2-0.5)*20),50+i*15);
		}
		EntityFallingNuke nuke = new EntityFallingNuke(world,detonator,0,0,174,0,50,0,0,0);
		nuke.setPosition(pos.getX(),world.getHeight(pos.getX(),pos.getZ())+80,pos.getZ());
		world.spawnEntity(nuke);
		return BombReturnCode.DETONATED;
	}
}