// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.misc.heatex;

import com.hbm.items.IDynamicModels;
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

public class CoolantHeatexBlock extends AddonBlockDummyable {
	public CoolantHeatexBlock(Material materialIn,String s) {
		super(materialIn,s);
		IDynamicModels.INSTANCES.add(this);
	}

	@Override
	public void addInformation(ItemStack stack,@Nullable World worldIn,List<String> tooltip,ITooltipFlag flagIn) {
		MachineTooltip.addBoiler(tooltip);
		MachineTooltip.addCondenser(tooltip);
		tooltip.add("Yes, yes, I know this is just a sloppy recolor.");
		tooltip.add("I can't fucking make the texture for a new model ok?");
		super.addInformation(stack,worldIn,tooltip,flagIn);
	}

	@Override
	public boolean onBlockActivated(World world,BlockPos pos,IBlockState state,EntityPlayer player,EnumHand hand,EnumFacing facing,float hitX,float hitY,float hitZ) {
		return this.standardOpenBehavior(world, pos, player, 0);
	}

	@Override
	public @Nullable TileEntity createNewTileEntity(World worldIn,int meta) {
		if (meta >= 12)
			return new CoolantHeatexTE();
		else if (meta >= 6)
			return new TileEntityProxyCombo().fluid().power();
		return null;
	}

	@Override
	public int[] getDimensions() {
		return new int[] {2, 0, 1, 1, 3, 3};
	}

	@Override
	public int getOffset() {
		return 1;
	}

	@Override
	public void fillSpace(World world,int x,int y,int z,ForgeDirection dir,int o) {
		super.fillSpace(world, x, y, z, dir, o);

		x = x + dir.offsetX * o;
		z = z + dir.offsetZ * o;

		ForgeDirection rot = dir.getRotation(ForgeDirection.UP);

		this.makeExtra(world, x + rot.offsetX * 3, y + 1, z + rot.offsetZ * 3);
		this.makeExtra(world, x - rot.offsetX * 3, y + 1, z - rot.offsetZ * 3);
		this.makeExtra(world, x + dir.offsetX + rot.offsetX, y + 1, z + dir.offsetZ + rot.offsetZ);
		this.makeExtra(world, x + dir.offsetX - rot.offsetX, y + 1, z + dir.offsetZ - rot.offsetZ);
		this.makeExtra(world, x - dir.offsetX + rot.offsetX, y + 1, z - dir.offsetZ + rot.offsetZ);
		this.makeExtra(world, x - dir.offsetX - rot.offsetX, y + 1, z - dir.offsetZ - rot.offsetZ);
	}
}
