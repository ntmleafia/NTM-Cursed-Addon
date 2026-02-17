package com.leafia.contents.network.fluid.valves;

import com.hbm.items.machine.IItemFluidIdentifier;
import com.hbm.lib.HBMSoundHandler;
import com.leafia.contents.network.fluid.FluidDuctEquipmentBase;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FluidDuctValveRS extends FluidDuctEquipmentBase {
	public FluidDuctValveRS(Material materialIn,String s) {
		super(materialIn,s);
	}
	@Override
	public TileEntity createNewTileEntity(World worldIn,int meta) {
		return new FluidDuctValveTE();
	}
	@Override
	public boolean canConnectRedstone(IBlockState state,IBlockAccess world,BlockPos pos,@Nullable EnumFacing side) {
		return true;
	}
	@Override
	public void neighborChanged(IBlockState state,World worldIn,BlockPos pos,Block blockIn,BlockPos fromPos) {
		super.neighborChanged(state,worldIn,pos,blockIn,fromPos);
		if (!worldIn.isRemote) {
			TileEntity te = worldIn.getTileEntity(pos);
			if (te instanceof FluidDuctValveTE valve)
				valve.open = worldIn.isBlockPowered(pos);
		}
	}
}
