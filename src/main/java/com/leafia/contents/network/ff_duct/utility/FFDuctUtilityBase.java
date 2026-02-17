package com.leafia.contents.network.ff_duct.utility;

import com.hbm.blocks.ILookOverlay;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.items.machine.IItemFluidIdentifier;
import com.hbm.util.I18nUtil;
import com.leafia.contents.network.ff_duct.FFDuctTE;
import com.leafia.dev.blocks.blockbase.AddonBlockContainer;
import com.leafia.dev.machine.MachineTooltip;
import com.llib.exceptions.LeafiaDevFlaw;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class FFDuctUtilityBase extends AddonBlockContainer implements ILookOverlay {
	public static final PropertyDirection FACING = BlockDirectional.FACING;
	public FFDuctUtilityBase(Material materialIn,String s) {
		super(materialIn,s);
	}

	@Override
	public boolean onBlockActivated(World worldIn,BlockPos pos,IBlockState state,EntityPlayer playerIn,EnumHand hand,EnumFacing facing,float hitX,float hitY,float hitZ) {
		ItemStack stack = playerIn.getHeldItem(hand);
		if (stack.getItem() instanceof IItemFluidIdentifier identifier) {
			if (!worldIn.isRemote) {
				TileEntity te = worldIn.getTileEntity(pos);
				if (te instanceof FFDuctUtilityTEBase base)
					base.setType(identifier.getType(worldIn,pos.getX(),pos.getY(),pos.getZ(),stack));
			}
			return true;
		}
		return false;
	}

	@Override
	public void printHook(Pre event,World world,int x,int y,int z) {
		TileEntity te = world.getTileEntity(new BlockPos(x,y,z));
		if (!(te instanceof FFDuctUtilityTEBase duct))
			return;
		List<String> text = new ArrayList<>();
		text.add("&[" + duct.getType().getColor() + "&]" + duct.getType().getLocalizedName());
		duct.addInfo(text);
		ILookOverlay.printGeneric(event, I18nUtil.resolveKey(getTranslationKey() + ".name"), 0x00ff00, 0x004000, text);
	}

	@Override
	public IBlockState getStateForPlacement(World world,BlockPos pos,EnumFacing facing,float hitX,float hitY,float hitZ,int meta,EntityLivingBase placer,EnumHand hand) {
		boolean snek = placer.isSneaking();
		if (hand.equals(EnumHand.OFF_HAND))
			snek = !snek;
		return getDefaultState().withProperty(FACING,snek ? facing : facing.getOpposite());
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this,FACING);
	}
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(FACING,EnumFacing.byIndex(meta%6));
	}
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex();
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state,IBlockAccess source,BlockPos pos) {
		return getBB(new BlockPos(0,0,0),state.getValue(FACING).getAxis());
	}

	@Override
	public void addCollisionBoxToList(IBlockState state,World worldIn,BlockPos pos,AxisAlignedBB entityBox,List<AxisAlignedBB> collidingBoxes,@Nullable Entity entityIn,boolean isActualState) {
		AxisAlignedBB bb = getBB(pos,state.getValue(FACING).getAxis());
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

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullBlock(IBlockState state) {
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
}
