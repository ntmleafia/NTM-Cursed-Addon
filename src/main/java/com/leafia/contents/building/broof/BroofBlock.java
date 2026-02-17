package com.leafia.contents.building.broof;

import com.hbm.lib.InventoryHelper;
import com.leafia.dev.blocks.blockbase.AddonBlockDummyable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BroofBlock extends AddonBlockDummyable {
	public BroofBlock(Material materialIn,String s) {
		super(materialIn,s);
	}
	@Override
	public void addInformation(ItemStack stack,@Nullable World worldIn,List<String> tooltip,ITooltipFlag flagIn) {
		super.addInformation(stack,worldIn,tooltip,flagIn);
		tooltip.add("To celebrate the fact this mod was used in ossmp server");
		tooltip.add("\"BROOF!\"");
	}
	@Override
	public boolean onBlockActivated(World worldIn,BlockPos pos,IBlockState state,EntityPlayer playerIn,EnumHand hand,EnumFacing facing,float hitX,float hitY,float hitZ) {
		return standardOpenBehavior(worldIn,pos,playerIn,0);
	}
	@Override
	public int[] getDimensions() {
		return new int[]{1,0,1,1,1,1};
	}
	@Override
	public int getOffset() {
		return 1;
	}
	@Override
	public void addCollisionBoxToList(@NotNull IBlockState state,@NotNull World worldIn,@NotNull BlockPos pos,@NotNull AxisAlignedBB entityBox,@NotNull List<AxisAlignedBB> collidingBoxes,@Nullable Entity entityIn,boolean isActualState) {
	}
	@Override
	public @Nullable TileEntity createNewTileEntity(World worldIn,int meta) {
		return meta >= 12 ? new BroofTE() : null;
	}
}
