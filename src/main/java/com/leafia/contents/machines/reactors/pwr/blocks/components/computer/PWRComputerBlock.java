package com.leafia.contents.machines.reactors.pwr.blocks.components.computer;

import com.hbm.blocks.ITooltipProvider;
import com.hbm.handler.radiation.RadiationSystemNT;
import com.hbm.interfaces.IRadResistantBlock;
import com.leafia.contents.machines.reactors.pwr.blocks.components.PWRComponentBlock;
import com.leafia.dev.blocks.blockbase.AddonBlockBaked;
import com.leafia.dev.machine.MachineTooltip;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PWRComputerBlock extends AddonBlockBaked implements ITooltipProvider, PWRComponentBlock, IRadResistantBlock, ITileEntityProvider {
	public PWRComputerBlock() {
		super(Material.IRON,"lwr_signal","pwr/pwr_control_port");
	}
	@Override
	public void addInformation(ItemStack stack,@javax.annotation.Nullable World player,List<String> tooltip,ITooltipFlag advanced) {
		MachineTooltip.addMultiblock(tooltip);
		MachineTooltip.addModular(tooltip);
		addStandardInfo(tooltip);
		super.addInformation(stack,player,tooltip,advanced);
	}
	@Override
	public @Nullable TileEntity createNewTileEntity(World worldIn,int meta) {
		return new PWRComputerTE();
	}
	@Override
	public boolean tileEntityShouldCreate(World world,BlockPos pos) {
		return true;
	}
	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		RadiationSystemNT.markSectionForRebuild(worldIn, pos);
		super.onBlockAdded(worldIn, pos, state);
	}
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		RadiationSystemNT.markSectionForRebuild(worldIn, pos);
		super.breakBlock(worldIn, pos, state);
	}
}
