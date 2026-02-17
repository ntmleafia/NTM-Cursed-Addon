package com.leafia.contents.machines.powercores.ams.base;

import com.hbm.lib.ForgeDirection;
import com.hbm.tileentity.TileEntityProxyCombo;
import com.leafia.dev.blocks.blockbase.AddonBlockDummyable;
import com.leafia.dev.machine.MachineTooltip;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AMSBaseBlock extends AddonBlockDummyable {
	public AMSBaseBlock(Material materialIn,String s) {
		super(materialIn,s);
	}
	@Override
	public void addInformation(ItemStack stack,@javax.annotation.Nullable World player,List<String> tooltip,ITooltipFlag advanced) {
		MachineTooltip.addMultiblock(tooltip);
		MachineTooltip.addCore(tooltip);
		MachineTooltip.addGenerator(tooltip);
		tooltip.add("Yeah I DO know spectrometers don't generate power.");
		tooltip.add("Just shut up.");
		super.addInformation(stack,player,tooltip,advanced);
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
	public @Nullable TileEntity createNewTileEntity(World worldIn,int meta) {
		if (meta >= 12)
			return new AMSBaseTE();
		else if (meta >= 6)
			return new TileEntityProxyCombo(true,true,true);
		return null;
	}
	@Override
	protected void fillSpace(World world,int x,int y,int z,ForgeDirection dir,int o) {
		super.fillSpace(world,x,y,z,dir,o);
		x -= dir.offsetX;
		z -= dir.offsetZ;
		makeExtra(world,x,y,z);
		makeExtra(world,x+1,y,z);
		makeExtra(world,x-1,y,z);
		makeExtra(world,x,y,z+1);
		makeExtra(world,x,y,z-1);
	}
	@Override
	public boolean onBlockActivated(World worldIn,BlockPos pos,IBlockState state,EntityPlayer playerIn,EnumHand hand,EnumFacing facing,float hitX,float hitY,float hitZ) {
		return standardOpenBehavior(worldIn,pos,playerIn,0);
	}
}
