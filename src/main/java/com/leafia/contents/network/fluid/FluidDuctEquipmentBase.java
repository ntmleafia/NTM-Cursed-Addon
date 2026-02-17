package com.leafia.contents.network.fluid;

import com.hbm.blocks.ILookOverlay;
import com.hbm.blocks.ModBlocks;
import com.hbm.tileentity.network.TileEntityPipeBaseNT;
import com.hbm.util.I18nUtil;
import com.leafia.contents.AddonBlocks;
import com.llib.exceptions.LeafiaDevFlaw;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class FluidDuctEquipmentBase extends BlockContainer implements ILookOverlay {
	public FluidDuctEquipmentBase(Material materialIn,String s) {
		super(materialIn);
		this.setTranslationKey(s);
		this.setRegistryName(s);

		AddonBlocks.ALL_BLOCKS.add(this);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public void onBlockPlacedBy(World worldIn,BlockPos pos,IBlockState state,EntityLivingBase placer,ItemStack stack) {
		TileEntity te = worldIn.getTileEntity(pos);
		if (te instanceof FluidDuctEquipmentTE) {
			FluidDuctEquipmentTE duct = (FluidDuctEquipmentTE)te;
			AxisAlignedBB top = new AxisAlignedBB(pos.up());
			AxisAlignedBB btm = new AxisAlignedBB(pos.down());
			Vec3d vecA = placer.getPositionEyes(0);
			Vec3d vecB = vecA.add(placer.getLookVec().scale(10));
			duct.vertical = false;
			if (vecA.y < pos.getY()+1)
				duct.vertical = duct.vertical || top.calculateIntercept(vecA,vecB) != null;
			if (vecA.y > pos.getY())
				duct.vertical = duct.vertical || btm.calculateIntercept(vecA,vecB) != null;
			duct.direction = placer.getHorizontalFacing();
			if (placer.getLookVec().y > 0.707)
				duct.face = -1;
			else if (placer.getLookVec().y < -0.707)
				duct.face = 1;
			else
				duct.face = 0;
		}
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
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
		return false;
	}
	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public void printHook(Pre event,World world,int x,int y,int z) {
		TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
		if (!(te instanceof TileEntityPipeBaseNT duct))
			return;

		List<String> text = new ArrayList<>();
		text.add("&[" + duct.getType().getColor() + "&]" + duct.getType().getLocalizedName());
		ILookOverlay.printGeneric(event, I18nUtil.resolveKey(getTranslationKey() + ".name"), 0xffff00, 0x404000, text);
	}

	public Axis getAxis(IBlockAccess world,BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof FluidDuctEquipmentTE duct) {
			if (duct.vertical) return Axis.Y;
			if (duct.face == 0 || duct.face == 2) return Axis.X;
		}
		return Axis.Z;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state,IBlockAccess source,BlockPos pos) {
		return getBB(new BlockPos(0,0,0),getAxis(source,pos));
	}

	@Override
	public void addCollisionBoxToList(IBlockState state,World worldIn,BlockPos pos,AxisAlignedBB entityBox,List<AxisAlignedBB> collidingBoxes,@Nullable Entity entityIn,boolean isActualState) {
		AxisAlignedBB bb = getBB(pos,getAxis(worldIn,pos));
		if (bb.intersects(entityBox))
			collidingBoxes.add(bb);
	}

	AxisAlignedBB getBB(BlockPos pos,Axis axis) {
		if (axis.equals(Axis.X))
			return new AxisAlignedBB(pos.getX() + 0.0D, pos.getY() + 0.3125D, pos.getZ() + 0.3125D, pos.getX() + 1.0D, pos.getY() + 0.6875D, pos.getZ() + 0.6875D);
		if (axis.equals(Axis.Y))
			return new AxisAlignedBB(pos.getX() + 0.3125D, pos.getY() + 0.0D, pos.getZ() + 0.3125D, pos.getX() + 0.6875D, pos.getY() + 1.0D, pos.getZ() + 0.6875D);
		if (axis.equals(Axis.Z))
			return new AxisAlignedBB(pos.getX() + 0.3125D, pos.getY() + 0.3125D, pos.getZ() + 0.0D, pos.getX() + 0.6875D, pos.getY() + 0.6875D, pos.getZ() + 1.0D);
		throw new LeafiaDevFlaw("impossible");
	}
}
