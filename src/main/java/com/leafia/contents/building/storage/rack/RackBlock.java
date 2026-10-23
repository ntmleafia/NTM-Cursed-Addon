// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.building.storage.rack;

import com.hbm.main.MainRegistry;
import com.leafia.dev.LeafiaDebug;
import com.leafia.dev.blocks.blockbase.AddonBlockDummyable;
import com.leafia.dev.math.FiaMatrix;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RackBlock extends AddonBlockDummyable {
	public RackBlock(Material materialIn,String s) {
		super(materialIn,s);
	}
	@Override
	public int[] getDimensions() {
		return new int[]{ 2,0,1,0,1,1 };
	}
	@Override
	public int getOffset() {
		return 0;
	}

	public FiaMatrix getMatrix(int meta) {
		FiaMatrix mat = new FiaMatrix();
		switch(meta - 10) {
			case 2:
				mat = mat.rotateY(180); break;
			case 3:
				mat = mat.rotateY(0); break;
			case 4:
				mat = mat.rotateY(270); break;
			case 5:
				mat = mat.rotateY(90); break;
		}
		return mat;
	}
	public FiaMatrix getMatrix(IBlockAccess source,BlockPos pos) {
		int[] shit = findCore(source,pos.getX(),pos.getY(),pos.getZ());
		if (shit == null) return new FiaMatrix();
		IBlockState state = source.getBlockState(new BlockPos(shit[0],shit[1],shit[2]));
		return getMatrix(getMetaFromState(state));
	}
	@Override
	public boolean onBlockActivated(World world,BlockPos pos,IBlockState state,EntityPlayer player,EnumHand hand,EnumFacing facing,float hitX,float hitY,float hitZ) {
		if (!world.isRemote) {
			int[] core = this.findCore(world,pos.getX(),pos.getY(),pos.getZ());
			if (core == null) return false;
			ItemStack stack = player.getHeldItem(hand);
			TileEntity te = world.getTileEntity(new BlockPos(core[0],core[1],core[2]));
			if (te instanceof RackTE rack) {
				IBlockState coreState = world.getBlockState(new BlockPos(core[0],core[1],core[2]));
				FiaMatrix rot = getMatrix(getMetaFromState(coreState));
				FiaMatrix mat = new FiaMatrix(new Vec3d(core[0]+0.5,core[1],core[2]+0.5)).rotateAlong(rot);
				FiaMatrix clicked = new FiaMatrix(new Vec3d(pos.getX()+hitX,pos.getY()+hitY,pos.getZ()+hitZ));
				FiaMatrix rel = mat.toObjectSpace(clicked);
				boolean isLeft = rel.getX() <= 0;
				boolean isUpper = rel.getY() >= 1.5;
				int index = (isLeft ? 0 : 1) + (isUpper ? 2 : 0);
				if (rack.crates[index] != null) {
					if (player.isSneaking() && stack.getCount() == 0) {
						ItemStack box = rack.removeCrate(index,true);
						if (box != ItemStack.EMPTY)
							player.inventory.add(player.inventory.currentItem,box);
					} else
						FMLNetworkHandler.openGui(player,MainRegistry.instance,index,world,core[0],core[1],core[2]);
				} else if (!stack.equals(ItemStack.EMPTY)) {
					if (rack.storeCrate(stack,index,true))
						stack.shrink(1);
				}
			}
		}
		return true;
	}

	@Override
	public void breakBlock(@NotNull World world,@NotNull BlockPos pos,IBlockState state) {
		if (!world.isRemote) {
			TileEntity te = world.getTileEntity(pos);
			if (te instanceof RackTE rack) {
				for (int i = 0; i < 4; i++) {
					ItemStack stack = rack.removeCrate(i,false);
					if (stack != ItemStack.EMPTY)
						InventoryHelper.spawnItemStack(world,pos.getX(),pos.getY(),pos.getZ(),stack);
				}
			}
		}
		super.breakBlock(world,pos,state);
	}

	@Override
	public @Nullable TileEntity createNewTileEntity(World worldIn,int meta) {
		if (meta >= 12)
			return new RackTE();
		return null;
	}
}
