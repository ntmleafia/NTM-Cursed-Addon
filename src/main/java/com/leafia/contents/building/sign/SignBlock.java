// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.building.sign;

import com.hbm.main.MainRegistry;
import com.hbm.util.I18nUtil;
import com.leafia.contents.AddonBlocks;
import com.leafia.dev.container_utility.LeafiaPacket;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class SignBlock extends BlockContainer {
	public static final PropertyDirection FACING = BlockHorizontal.FACING;
	protected static final AxisAlignedBB SIGN_EAST_AABB = new AxisAlignedBB(0.0D, 0, 0.0D, 0.125D, 1, 1.0D);
	protected static final AxisAlignedBB SIGN_WEST_AABB = new AxisAlignedBB(0.875D, 0, 0.0D, 1.0D, 1, 1.0D);
	protected static final AxisAlignedBB SIGN_SOUTH_AABB = new AxisAlignedBB(0.0D, 0, 0.0D, 1.0D, 1, 0.125D);
	protected static final AxisAlignedBB SIGN_NORTH_AABB = new AxisAlignedBB(0.0D, 0, 0.875D, 1.0D, 1, 1.0D);
	public final String letter;
	public SignBlock(String letter) {
		super(Material.IRON);
		this.letter = letter;
		String s = "letter_sign_"+letter.toLowerCase();
		this.setTranslationKey(s);
		this.setRegistryName(s);
		this.setHarvestLevel("pickaxe", 0);
		this.setCreativeTab(MainRegistry.blockTab);
		AddonBlocks.ALL_BLOCKS.add(this);
	}
	@Override
	public boolean onBlockActivated(World worldIn,BlockPos pos,IBlockState state,EntityPlayer playerIn,EnumHand hand,EnumFacing facing,float hitX,float hitY,float hitZ) {
		ItemStack stack = playerIn.getHeldItem(hand);
		if (stack.getItem() instanceof ItemDye) {
			EnumDyeColor color = EnumDyeColor.byDyeDamage(stack.getMetadata());
			TileEntity te = worldIn.getTileEntity(pos);
			if (te instanceof SignTE sign) {
				sign.color = color;
				sign.markDirty();
				LeafiaPacket._start(sign).__write(0,color.getMetadata()).__sendToAffectedClients();
			}
			return true;
		}
		return false;
	}
	@Override
	public String getLocalizedName() {
		return I18nUtil.resolveKey("tile.letter_sign.name",letter);
	}
	@Override
	public @Nullable TileEntity createNewTileEntity(World worldIn,int meta) {
		return new SignTE();
	}
	public AxisAlignedBB getBoundingBox(IBlockState state,IBlockAccess source,BlockPos pos)
	{
		switch ((EnumFacing)state.getValue(FACING))
		{
			case NORTH:
			default:
				return SIGN_NORTH_AABB;
			case SOUTH:
				return SIGN_SOUTH_AABB;
			case WEST:
				return SIGN_WEST_AABB;
			case EAST:
				return SIGN_EAST_AABB;
		}
	}
	public void neighborChanged(IBlockState state,World worldIn,BlockPos pos,Block blockIn,BlockPos fromPos)
	{
		EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);

		if (!worldIn.getBlockState(pos.offset(enumfacing.getOpposite())).getMaterial().isSolid())
		{
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockToAir(pos);
		}

		super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
	}
	public IBlockState getStateFromMeta(int meta)
	{
		EnumFacing enumfacing = EnumFacing.byIndex(meta);

		if (enumfacing.getAxis() == EnumFacing.Axis.Y)
		{
			enumfacing = EnumFacing.NORTH;
		}

		return this.getDefaultState().withProperty(FACING, enumfacing);
	}
	public int getMetaFromState(IBlockState state)
	{
		return ((EnumFacing)state.getValue(FACING)).getIndex();
	}
	public IBlockState withRotation(IBlockState state, Rotation rot)
	{
		return state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
	}
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
	{
		return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue(FACING)));
	}

	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {FACING});
	}
	@Nullable
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
	{
		return NULL_AABB;
	}
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}
	public boolean isPassable(IBlockAccess worldIn, BlockPos pos)
	{
		return true;
	}
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}
	public boolean canSpawnInBlock()
	{
		return true;
	}
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
	{
		return !this.hasInvalidNeighbor(worldIn, pos) && super.canPlaceBlockAt(worldIn, pos);
	}
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn,IBlockState state,BlockPos pos,EnumFacing face)
	{
		return BlockFaceShape.UNDEFINED;
	}
	public void onBlockPlacedBy(World worldIn,BlockPos pos,IBlockState state,EntityLivingBase placer,ItemStack stack) {
		worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()));
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
	}
}
