package com.leafia.contents.machines.reactors.apr.blocks.port;

import com.hbm.blocks.ITooltipProvider;
import com.leafia.dev.blocks.blockbase.AddonBlockBaked;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class APRFluidIOBlock extends AddonBlockBaked implements ITileEntityProvider, ITooltipProvider {
	public APRFluidIOBlock(Material m,String s) {
		super(m,s,"apr/apr_port");
		setSoundType(SoundType.METAL);
	}
	@Override
	public void addInformation(ItemStack stack,World player,List<String> list,ITooltipFlag advanced) {
		super.addInformation(stack,player,list,advanced);
		addStandardInfo(list);
	}
	@Override
	public @Nullable TileEntity createNewTileEntity(World worldIn,int meta) {
		return new APRFluidIOTE();
	}
}
