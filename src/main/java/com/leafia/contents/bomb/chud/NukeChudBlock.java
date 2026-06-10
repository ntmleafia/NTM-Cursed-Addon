package com.leafia.contents.bomb.chud;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ModBlocks;
import com.hbm.config.BombConfig;
import com.hbm.entity.effect.EntityNukeTorex;
import com.hbm.entity.logic.EntityNukeExplosionMK5;
import com.hbm.handler.pollution.PollutionHandler;
import com.hbm.handler.pollution.PollutionHandler.PollutionType;
import com.hbm.interfaces.IBomb;
import com.hbm.interfaces.IBomb.BombReturnCode;
import com.hbm.lib.ForgeDirection;
import com.hbm.tileentity.bomb.TileEntityNukeMan;
import com.hbm.util.I18nUtil;
import com.leafia.contents.worldgen.NTMStructBuffer;
import com.leafia.dev.blocks.blockbase.AddonBlockBase;
import com.llib.exceptions.LeafiaDevFlaw;
import com.llib.group.LeafiaSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NukeChudBlock extends AddonBlockBase implements ITileEntityProvider, IBomb {
	public static final PropertyInteger FACING = PropertyInteger.create("facing", 2, 5);
	public NukeChudBlock(Material m,String s) {
		super(m,s);
	}
	@Override
	public @Nullable TileEntity createNewTileEntity(World worldIn,int meta) {
		return new NukeChudTE();
	}

	@Override
	public void neighborChanged(IBlockState state,World world,BlockPos pos,Block blockIn,BlockPos fromPos) {
		if (world.isBlockPowered(pos) && !world.isRemote)
		{
				this.onPlayerDestroy(world, pos, state);
				world.setBlockToAir(pos);
				igniteTestBomb(world, null, pos.getX(), pos.getY(), pos.getZ());
		}
	}
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.INVISIBLE;
	}
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isBlockNormalCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isNormalCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isNormalCube(IBlockState state,IBlockAccess world,BlockPos pos) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}


	public boolean igniteTestBomb(World world,Entity detonator,int x,int y,int z)
	{
		if (!world.isRemote) {
			world.playSound(null,new BlockPos(x,y,z),SoundEvents.ENTITY_GENERIC_EXPLODE,SoundCategory.BLOCKS,20,1);
			int range = 50;
			MutableBlockPos mbp = new MutableBlockPos();
			LeafiaSet<BlockPos> set = new LeafiaSet<>();
			for (int xo = -range; xo <= range; xo++) {
				for (int yo = -range; yo <= range; yo++) {
					for (int zo = -range; zo <= range; zo++) {
						mbp.setPos(x+xo,y+yo,z+zo);
						Block block = world.getBlockState(mbp).getBlock();
						if (block instanceof BlockDummyable dummyable) {
							BlockPos core = dummyable.findCore(world,mbp);
							set.add(core);
						}
						if (block == Blocks.GRASS) {
							int rand = world.rand.nextInt(3);
							if (rand == 2)
								world.setBlockState(mbp,ModBlocks.dirt_oily.getDefaultState());
							else
								world.setBlockState(mbp,ModBlocks.dirt_dead.getDefaultState());
							//else
							//	world.setBlockState(mbp,Blocks.DIRT.getStateFromMeta(1));
						} else if (block instanceof BlockLeaves)
							world.setBlockToAir(mbp);
						else if (block instanceof BlockTallGrass)
							world.setBlockState(mbp,ModBlocks.plant_dead.getStateFromMeta(world.rand.nextInt(5)));
						else {
							String reg = block.getRegistryName().toString();
							if (reg.contains("brick") || reg.contains("concrete"))
								world.setBlockToAir(mbp);
						}
					}
				}
			}
			for (BlockPos pos : set) {
				IBlockState state = world.getBlockState(pos);
				if (state.getBlock() instanceof BlockDummyable dummyable) {
					int meta = state.getValue(BlockDummyable.META);
					world.setBlockToAir(pos);
					pos = pos.up(world.rand.nextInt(15)+10);
					if (meta >= 12) {
						EnumFacing dir = ForgeDirection.getOrientation(meta-BlockDummyable.offset).toEnumFacing();
						world.setBlockState(pos,dummyable.getStateFromMeta(ForgeDirection.getOrientation(dir).ordinal()+BlockDummyable.offset),0b00010);
						try {
							// thanks movblock
							BlockPos pos1 = pos.offset(dir,-dummyable.getOffset());
							NTMStructBuffer.fillSpaceHandle.invokeExact((BlockDummyable) dummyable,(World) world,pos1.getX(),pos1.getY(),pos1.getZ(),ForgeDirection.getOrientation(dir),dummyable.getOffset());
						} catch (Throwable e) {
							throw new LeafiaDevFlaw(e);
						}
					}
				}
			}
			for (int i = 0; i < 15; i++)
				generateYellowBarrelTower(world,x+world.rand.nextInt(range*2+1)-range,z+world.rand.nextInt(range*2+1)-range);
			return true;
		}
		return false;
	}

	public static void generateYellowBarrelTower(World world,int x,int z) {
		for (int xo = -2; xo <= 2; xo++) {
			for (int zo = -2; zo <= 2; zo++) {
				for (int yo = 0; yo <= 1; yo++) {
					if ((zo == -2 || zo == 2) && (xo == -2 || xo == 2))
						if (world.rand.nextInt(3) != 0) continue;
					int y = world.getHeight(x+xo,z+zo);
					placeYellowBarrel(world,new BlockPos(x+xo,y,z+zo),100);
				}
			}
		}
		int y = world.getHeight(x,z);
		int height = 15+world.rand.nextInt(10);
		for (int yo = 0; yo < height; yo++) {
			placeYellowBarrel(world,new BlockPos(x,y+yo,z),100);
			if (yo < height-2) {
				placeYellowBarrel(world,new BlockPos(x+1,y+yo,z),65);
				placeYellowBarrel(world,new BlockPos(x-1,y+yo,z),65);
				placeYellowBarrel(world,new BlockPos(x,y+yo,z+1),65);
				placeYellowBarrel(world,new BlockPos(x,y+yo,z-1),65);
				placeYellowBarrel(world,new BlockPos(x+1,y+yo,z+1),20);
				placeYellowBarrel(world,new BlockPos(x+1,y+yo,z-1),20);
				placeYellowBarrel(world,new BlockPos(x-1,y+yo,z+1),20);
				placeYellowBarrel(world,new BlockPos(x-1,y+yo,z-1),20);
			}
		}
	}

	public static void placeYellowBarrel(World world,BlockPos pos,int chance) {
		if (chance < 100)
			if (world.rand.nextInt(100) > chance) return;
		IBlockState state;
		if (world.rand.nextBoolean())
			state = ModBlocks.yellow_barrel.getDefaultState();
		else
			state = ModBlocks.sellafield.getStateFromMeta(world.rand.nextInt(3)+3);
		world.setBlockState(pos,state);
	}

	@Override
	public IBlockState getStateForPlacement(World world,BlockPos pos,EnumFacing facing,float hitX,float hitY,float hitZ,int meta,EntityLivingBase player) {
		int i = MathHelper.floor(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

		if(i == 0)
		{
			return this.getDefaultState().withProperty(FACING, 5);
		}
		if(i == 1)
		{
			return this.getDefaultState().withProperty(FACING, 3);
		}
		if(i == 2)
		{
			return this.getDefaultState().withProperty(FACING, 4);
		}
		return this.getDefaultState().withProperty(FACING, 2);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack stack) {
		if (world.getTileEntity(pos) instanceof TileEntityNukeMan man)
			man.placerID = player.getUniqueID();
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		if(meta >= 2 && meta <=5)
			return this.getDefaultState().withProperty(FACING, meta);
		return this.getDefaultState().withProperty(FACING, 2);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[]{FACING});
	}

	@Override
	public BombReturnCode explode(World world,BlockPos pos,Entity detonator) {

		if (!world.isRemote) {
				this.onPlayerDestroy(world, pos, world.getBlockState(pos));
				world.setBlockToAir(pos);
				igniteTestBomb(world, detonator, pos.getX(), pos.getY(), pos.getZ());
				return BombReturnCode.DETONATED;
		}

		return BombReturnCode.UNDEFINED;
	}

	@Override
	public void addInformation(ItemStack stack,World player,List<String> tooltip,ITooltipFlag advanced) {
		tooltip.add("§2[Chud Bomb]"+"§r");
		tooltip.add(" §e"+I18nUtil.resolveKey("desc.radius",50+"§r"));
	}
}
