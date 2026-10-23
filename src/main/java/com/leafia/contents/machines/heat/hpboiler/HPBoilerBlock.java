// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.heat.hpboiler;

import com.hbm.blocks.ITooltipProvider;
import com.hbm.lib.ForgeDirection;
import com.hbm.tileentity.TileEntityProxyCombo;
import com.leafia.dev.blocks.blockbase.AddonBlockDummyable;
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

public class HPBoilerBlock extends AddonBlockDummyable implements ITooltipProvider {
	public HPBoilerBlock(Material materialIn,String s) {
		super(materialIn,s);
	}
	@Override
	public void addInformation(ItemStack stack,@Nullable World worldIn,List<String> tooltip,ITooltipFlag flagIn) {
		addStandardInfo(tooltip);
		super.addInformation(stack,worldIn,tooltip,flagIn);
	}
	@Override
	public int[] getDimensions() {
		return new int[]{3,0,1,1,1,1};
	}
	@Override
	public int getOffset() {
		return 1;
	}
	@Override
	public @Nullable TileEntity createNewTileEntity(World worldIn,int meta) {
		if (meta >= 12)
			return new HPBoilerTE();
		else if (meta >= extra)
			return new TileEntityProxyCombo(false,false,true);
		return null;
	}
	@Override
	protected void fillSpace(World world,int x,int y,int z,ForgeDirection dir,int o) {
		super.fillSpace(world, x, y, z, dir, o);
		x = x + dir.offsetX * o;
		z = z + dir.offsetZ * o;
		ForgeDirection rot = dir.getRotation(ForgeDirection.UP);
		this.makeExtra(world, x + rot.offsetX, y+1, z + rot.offsetZ); //these add the side ports
		this.makeExtra(world, x - rot.offsetX, y+1, z - rot.offsetZ);
	}
	@Override
	public boolean onBlockActivated(World worldIn,BlockPos pos,IBlockState state,EntityPlayer playerIn,EnumHand hand,EnumFacing facing,float hitX,float hitY,float hitZ) {
		if (!worldIn.isRemote)
			standardOpenBehavior(worldIn,pos,playerIn,0);
		return true;
	}
}