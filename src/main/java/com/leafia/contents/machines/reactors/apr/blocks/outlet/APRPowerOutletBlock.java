package com.leafia.contents.machines.reactors.apr.blocks.outlet;

import com.leafia.dev.blocks.blockbase.AddonBlockBaked;
import com.leafia.dev.machine.MachineTooltip;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class APRPowerOutletBlock extends AddonBlockBaked implements ITileEntityProvider {
	public APRPowerOutletBlock(Material m,String s) {
		super(m,s,"apr/apr_outlet","apr/apr_plating_dark");
		setSoundType(SoundType.METAL);
	}
	@Override
	public void addInformation(ItemStack stack,World player,List<String> list,ITooltipFlag advanced) {
		MachineTooltip.addMultiblock(list);
		super.addInformation(stack,player,list,advanced);
	}
	@Override
	public @Nullable TileEntity createNewTileEntity(World worldIn,int meta) {
		return new APRPowerOutletTE();
	}
}
