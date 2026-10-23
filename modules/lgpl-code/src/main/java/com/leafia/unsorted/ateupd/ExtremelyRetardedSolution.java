// SPDX-FileCopyrightText: 2022 70000hp
// SPDX-FileCopyrightText: 2020-2021 Drillgon200 <drillgon200@gmail.com>
// SPDX-FileCopyrightText: 2025 George Paton <georgewpaton@me.com>
// SPDX-FileCopyrightText: 2020-2022, 2026 HbmMods (Bob/Boblet) <hbmmods@gmail.com>
// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2025-2026 movblock <admin@movblock.mov>
// SPDX-FileCopyrightText: 2024-2025 TheSlize <r.region-4@yandex.ru>
// SPDX-License-Identifier: LGPL-3.0-only

package com.leafia.unsorted.ateupd;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ModBlocks;
import com.hbm.handler.MultiblockHandlerXR;
import com.hbm.interfaces.ICopiable;
import com.hbm.items.IDynamicModels;
import com.hbm.lib.ForgeDirection;
import com.hbm.lib.InventoryHelper;
import com.hbm.lib.Library;
import com.hbm.main.MainRegistry;
import com.hbm.main.client.StaticTesrBakedModels;
import com.hbm.tileentity.IPersistentNBT;
import com.hbm.world.gen.nbt.INBTBlockTransformable;
import com.leafia.contents.AddonBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/// extremely retarded BlockDummyable clone that just doesn't set registry name
public abstract class ExtremelyRetardedSolution extends BlockContainer implements INBTBlockTransformable {
	public static final PropertyInteger META = BlockDummyable.META;
	public static final int offset = 10;
	public static final int extra = 6;
	private static final long NO_CORE = Long.MIN_VALUE;
	private static final AxisAlignedBB DETAIL_AABB = new AxisAlignedBB((double)0.0F, (double)0.0F, (double)0.0F, (double)1.0F, (double)0.999F, (double)1.0F);
	public static boolean safeRem = false;
	public List<AxisAlignedBB> bounding = new ArrayList();

	public ExtremelyRetardedSolution(Material materialIn,String s) {
		super(materialIn);
		this.setTranslationKey(s);
		this.setTickRandomly(true);
		AddonBlocks.ALL_BLOCKS.add(this);
	}

	public ExtremelyRetardedSolution(Material materialIn, String s, boolean ignoredDontUseIDynamicModel) {
		super(materialIn);
		this.setTranslationKey(s);
		this.setTickRandomly(true);
		AddonBlocks.ALL_BLOCKS.add(this);
	}


	protected int getMaxCoreSearchSteps() {
		return 512;
	}

	private long findCoreSerialized(IBlockAccess world, BlockPos pos, BlockPos.MutableBlockPos scratch) {
		return findCoreSerialized(world, pos.getX(), pos.getY(), pos.getZ(), scratch);
	}

	private long findCoreSerialized(IBlockAccess world, int x, int y, int z, BlockPos.MutableBlockPos scratch) {
		for (int steps = 0, max = getMaxCoreSearchSteps(); steps < max; steps++) {
			scratch.setPos(x, y, z);
			IBlockState state = world.getBlockState(scratch);
			if (state.getBlock() != this) return NO_CORE;
			int meta = state.getValue(META);
			if (meta >= 12) return Library.blockPosToLong(x, y, z);
			if (meta >= extra) meta -= extra;
			ForgeDirection dir = ForgeDirection.getOrientation(meta).getOpposite();
			x += dir.offsetX;
			y += dir.offsetY;
			z += dir.offsetZ;
		}
		return NO_CORE;
	}

	@Nullable
	public BlockPos findCore(IBlockAccess world, BlockPos pos) {
		BlockPos.MutableBlockPos scratch = new BlockPos.MutableBlockPos();
		long core = findCoreSerialized(world, pos, scratch);
		if (core == NO_CORE) return null;
		return BlockPos.fromLong(core);
	}

	@Nullable
	public TileEntity findCoreTE(IBlockAccess world, BlockPos pos) {
		BlockPos.MutableBlockPos scratch = new BlockPos.MutableBlockPos();
		long core = findCoreSerialized(world, pos, scratch);
		if (core == NO_CORE) return null;
		Library.fromLong(scratch, core);
		return world.getTileEntity(scratch);
	}

	@Nullable
	public TileEntity findCoreTE(IBlockAccess world, int x, int y, int z) {
		BlockPos.MutableBlockPos scratch = new BlockPos.MutableBlockPos();
		long core = findCoreSerialized(world, x, y, z, scratch);
		if (core == NO_CORE) return null;
		Library.fromLong(scratch, core);
		return world.getTileEntity(scratch);
	}

	public int @Nullable [] findCore(IBlockAccess world, int x, int y, int z) {
		BlockPos.MutableBlockPos scratch = new BlockPos.MutableBlockPos();
		long core = findCoreSerialized(world, x, y, z, scratch);
		if (core == NO_CORE) return null;
		return new int[]{Library.getBlockPosX(core), Library.getBlockPosY(core), Library.getBlockPosZ(core)};
	}

	@Override
	public void neighborChanged(@NotNull IBlockState state, World world, @NotNull BlockPos pos, @NotNull Block blockIn, @NotNull BlockPos fromPos) {
		if (world.isRemote || safeRem) return;

		int metadata = state.getValue(META);

		//if it's an extra, remove the extra-ness
		if (metadata >= extra) metadata -= extra;

		ForgeDirection dir = ForgeDirection.getOrientation(metadata).getOpposite();
		BlockPos other = pos.add(dir.offsetX, dir.offsetY, dir.offsetZ);
		if (world.getBlockState(other).getBlock() != this) {
			world.setBlockToAir(pos);
		}
	}

	@Override
	public void updateTick(@NotNull World world, @NotNull BlockPos pos, @NotNull IBlockState state, @NotNull Random rand) {
		super.updateTick(world, pos, state, rand);
		if (world.isRemote) return;

		int metadata = state.getValue(META);

		//if it's an extra, remove the extra-ness
		if (metadata >= extra) metadata -= extra;

		ForgeDirection dir = ForgeDirection.getOrientation(metadata).getOpposite();
		BlockPos other = pos.add(dir.offsetX, dir.offsetY, dir.offsetZ);
		if (world.getBlockState(other).getBlock() != this) {
			world.setBlockToAir(pos);
		}
	}

	@Override
	public void onBlockPlacedBy(@NotNull World world, @NotNull BlockPos pos, @NotNull IBlockState state, @NotNull EntityLivingBase player, @NotNull ItemStack itemStack) {
		if(!(player instanceof EntityPlayer pl))
			return;
		safeRem = true;
		world.setBlockToAir(pos);
		safeRem = false;

		EnumHand hand = pl.getHeldItemMainhand() == itemStack ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;

		int i = MathHelper.floor(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
		int o = -getOffset();
		pos = new BlockPos(pos.getX(), pos.getY() + getHeightOffset(), pos.getZ());

		ForgeDirection dir = switch (i) {
			case 0 -> ForgeDirection.getOrientation(2);
			case 1 -> ForgeDirection.getOrientation(5);
			case 2 -> ForgeDirection.getOrientation(3);
			case 3 -> ForgeDirection.getOrientation(4);
			default -> ForgeDirection.NORTH;
		};

		dir = getDirModified(dir);

		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		if(!checkRequirement(world, x, y, z, dir, o)) {
			if(!pl.capabilities.isCreativeMode) {
				ItemStack stack = pl.inventory.mainInventory.get(pl.inventory.currentItem);
				Item item = Item.getItemFromBlock(this);

				if(stack.isEmpty()) {
					pl.inventory.mainInventory.set(pl.inventory.currentItem, new ItemStack(this));
				} else {
					if(stack.getItem() != item || stack.getCount() == stack.getMaxStackSize()) {
						pl.inventory.addItemStackToInventory(new ItemStack(this));
					} else {
						pl.getHeldItem(hand).grow(1);
					}
				}
			}

			return;
		}

		if(!world.isRemote){
			BlockPos cur = new BlockPos(x + dir.offsetX * o , y + dir.offsetY * o, z + dir.offsetZ * o);
			int meta = getMetaForCore(world, cur, pl, dir.ordinal() + offset);
			world.setBlockState(cur, this.getDefaultState().withProperty(META, meta), 3);
			IPersistentNBT.onBlockPlacedBy(world, cur, itemStack);
			fillSpace(world, x, y, z, dir, o);
		}
		pos = new BlockPos(pos.getX(), pos.getY() - getHeightOffset(), pos.getZ());
		world.scheduleUpdate(pos, this, 1);
		world.scheduleUpdate(pos, this, 2);

		super.onBlockPlacedBy(world, pos, state, player, itemStack);
	}

	protected boolean standardOpenBehavior(World world, BlockPos pos, EntityPlayer player, int id){
		return this.standardOpenBehavior(world, pos.getX(), pos.getY(), pos.getZ(), player, id);
	}

	protected boolean standardOpenBehavior(World world, int x, int y, int z, EntityPlayer player, int id) {

		if(world.isRemote) {
			return true;
		} else if(!player.isSneaking()) {
			int[] pos = this.findCore(world, x, y, z);

			if(pos == null)
				return false;

			player.openGui(MainRegistry.instance, id, world, pos[0], pos[1], pos[2]);
			return true;
		} else {
			return true;
		}
	}

	/**
	 * A bit more advanced than the dir modifier, but it is important that the resulting direction meta is in the core range.
	 * Using the "extra" metas is technically possible but requires a bit of tinkering, e.g. preventing a recursive loop
	 * in the core finder and making sure the TE uses the right metas.
	 */
	protected int getMetaForCore(World world, BlockPos pos, EntityPlayer player, int original) {
		return original;
	}

	public ForgeDirection getDirModified(ForgeDirection dir) {
		return dir;
	}

	protected final EnumFacing getDirModified(EnumFacing dir) {
		if (dir == null) return null;
		ForgeDirection modified = getDirModified(ForgeDirection.getOrientation(dir));
		EnumFacing facing = modified.toEnumFacing();
		return facing != null ? facing : dir;
	}

	public boolean checkRequirement(World world, int x, int y, int z, ForgeDirection dir, int o) {
		return MultiblockHandlerXR.checkSpace(world, x + dir.offsetX * o , y + dir.offsetY * o, z + dir.offsetZ * o, getDimensions(), x, y, z, dir);
	}

	protected void fillSpace(World world, int x, int y, int z, ForgeDirection dir, int o) {
		MultiblockHandlerXR.fillSpace(world, x + dir.offsetX * o , y + dir.offsetY * o, z + dir.offsetZ * o, getDimensions(), this, dir);
	}

	//"upgrades" regular dummy blocks to ones with the extra flag
	public void makeExtra(World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		if(world.getBlockState(pos).getBlock() != this)
			return;

		int meta = world.getBlockState(pos).getValue(META);

		if(meta > 5)
			return;

		//world.setBlockMetadataWithNotify(x, y, z, meta + extra, 3);
		safeRem = true;
		world.setBlockState(pos, this.getDefaultState().withProperty(META, meta + extra), 3);
		safeRem = false;
	}

	//Drillgon200: Removes the extra. I could have sworn there was already a method for this, but I can't find it.
	public void removeExtra(World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		if(world.getBlockState(pos).getBlock() != this)
			return;

		int meta = world.getBlockState(pos).getValue(META);

		if(meta <= 5 || meta >= 12)
			return;

		//world.setBlockMetadataWithNotify(x, y, z, meta + extra, 3);
		safeRem = true;
		world.setBlockState(pos, this.getDefaultState().withProperty(META, meta - extra), 3);
		safeRem = false;
	}

	//checks if the dummy metadata is within the extra range
	public boolean hasExtra(int meta) {
		return meta > 5 && meta < 12;
	}

	@Override
	public void breakBlock(@NotNull World world, @NotNull BlockPos pos, IBlockState state) {
		int i = state.getValue(META);
		if(i < 12 && !safeRem) {

			if(i >= extra)
				i -= extra;

			ForgeDirection dir = ForgeDirection.getOrientation(i).getOpposite();

			BlockPos.MutableBlockPos scratch = new BlockPos.MutableBlockPos();
			long core = findCoreSerialized(world, pos.getX() + dir.offsetX, pos.getY() + dir.offsetY, pos.getZ() + dir.offsetZ, scratch);

			if (core != NO_CORE) {
				Library.fromLong(scratch, core);
				world.setBlockToAir(scratch);
			}
		}
		InventoryHelper.dropInventoryItems(world, pos, world.getTileEntity(pos));
		super.breakBlock(world, pos, state);
	}

	public boolean useDetailedHitbox() {
		return !bounding.isEmpty();
	}

	@Override
	public void addCollisionBoxToList(@NotNull IBlockState state, @NotNull World worldIn, @NotNull BlockPos pos, @NotNull AxisAlignedBB entityBox,
	                                  @NotNull List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
		if (!this.useDetailedHitbox()) {
			super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState);
			return;
		}

		BlockPos.MutableBlockPos scratch = new BlockPos.MutableBlockPos();
		long core = findCoreSerialized(worldIn, pos, scratch);
		if (core == NO_CORE) return;

		int coreX = Library.getBlockPosX(core);
		int coreY = Library.getBlockPosY(core);
		int coreZ = Library.getBlockPosZ(core);

		scratch.setPos(coreX, coreY, coreZ);
		IBlockState coreState = worldIn.getBlockState(scratch);

		for (AxisAlignedBB aabb : this.bounding) {
			AxisAlignedBB rotatedBox = getAABBRotationOffset(aabb, coreX + 0.5, coreY, coreZ + 0.5, getRotationFromState(coreState));

			if (entityBox.intersects(rotatedBox)) {
				collidingBoxes.add(rotatedBox);
			}
		}
	}

	private ForgeDirection getRotationFromState(IBlockState state) {
		int meta = state.getValue(META);
		return ForgeDirection.getOrientation(meta - offset).getRotation(ForgeDirection.UP);
	}

	public static AxisAlignedBB getAABBRotationOffset(AxisAlignedBB aabb, double x, double y, double z, ForgeDirection dir) {
		AxisAlignedBB newBox = null;

		if (dir == ForgeDirection.NORTH) {
			newBox = new AxisAlignedBB(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
		} else if (dir == ForgeDirection.EAST) {
			newBox = new AxisAlignedBB(-aabb.maxZ, aabb.minY, aabb.minX, -aabb.minZ, aabb.maxY, aabb.maxX);
		} else if (dir == ForgeDirection.SOUTH) {
			newBox = new AxisAlignedBB(-aabb.maxX, aabb.minY, -aabb.maxZ, -aabb.minX, aabb.maxY, -aabb.minZ);
		} else if (dir == ForgeDirection.WEST) {
			newBox = new AxisAlignedBB(aabb.minZ, aabb.minY, -aabb.maxX, aabb.maxZ, aabb.maxY, -aabb.minX);
		}

		if (newBox != null) {
			return newBox.offset(x, y, z);
		}

		return new AxisAlignedBB(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ).offset(x + 0.5, y + 0.5, z + 0.5);
	}

	@Override
	public boolean isOpaqueCube(@NotNull IBlockState state) {
		return false;
	}

	@Override
	public boolean isBlockNormalCube(@NotNull IBlockState state) {
		return false;
	}

	@Override
	public boolean isNormalCube(@NotNull IBlockState state) {
		return false;
	}

	@Override
	public boolean isNormalCube(@NotNull IBlockState state, @NotNull IBlockAccess world, @NotNull BlockPos pos) {
		return false;
	}
	@Override
	public boolean shouldSideBeRendered(@NotNull IBlockState blockState, @NotNull IBlockAccess blockAccess, @NotNull BlockPos pos, @NotNull EnumFacing side) {
		return false;
	}

	@Override
	protected @NotNull BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, META);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(META);
	}

	@Override
	public @NotNull IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(META, meta);
	}

	public abstract int[] getDimensions();

	public abstract int getOffset();

	public int getHeightOffset() {
		return 0;
	}

	@Override
	public @NotNull AxisAlignedBB getBoundingBox(@NotNull IBlockState state, @NotNull IBlockAccess source, @NotNull BlockPos pos) {
		if (!this.useDetailedHitbox()) {
			return FULL_BLOCK_AABB;
		} else {
			return DETAIL_AABB;
		}
	}

	@Override
	public int transformMeta(int meta, int coordBaseMode) {
		boolean isOffset = meta >= 12; // squishing causes issues
		boolean isExtra = !isOffset && meta >= extra;

		if(isOffset) {
			meta -= offset;
		} else if(isExtra) {
			meta -= extra;
		}

		meta = INBTBlockTransformable.transformMetaDeco(meta, coordBaseMode);

		if(isOffset) {
			meta += offset;
		} else if(isExtra) {
			meta += extra;
		}

		return meta;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumBlockRenderType getRenderType(IBlockState state) {
		if (StaticTesrBakedModels.isManagedBlock(this)) {
			return EnumBlockRenderType.MODEL;
		}
		return super.getRenderType(state);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		if (StaticTesrBakedModels.isManagedBlock(this)) {
			return BlockRenderLayer.CUTOUT;
		}
		return super.getRenderLayer();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
		if (StaticTesrBakedModels.isManagedBlock(this)) {
			return layer == BlockRenderLayer.CUTOUT;
		}
		return super.canRenderInLayer(state, layer);
	}
}
