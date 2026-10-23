// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.unsorted.explosion_vnt;

import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.interfaces.IBlockMutator;
import com.leafia.dev.LeafiaUtil;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockMutatorCollapse implements IBlockMutator {
	Long2ObjectOpenHashMap<IBlockState> map = new Long2ObjectOpenHashMap<>();
	@Override
	public void mutatePre(ExplosionVNT explosion,IBlockState block, BlockPos pos) {
		map.put(pos.toLong(),block);
	}
	@Override
	public void mutatePost(ExplosionVNT explosion,BlockPos pos) {
		IBlockState state = map.get(pos.toLong());
		if (state == null) return;
		if (explosion.world.rand.nextInt(5) != 0) return;
		boolean destroy = false;
		//if (worldObj.getBlockState(pos.down()).getMaterial().isReplaceable()) {
		boolean canFall = true;
		BlockPos checkPos = pos;
		World worldObj = explosion.world;
		while (true) {
			if (!explosion.world.isAirBlock(checkPos)) {
				canFall = false;
				break;
			}
			if (worldObj.getBlockState(checkPos.down()).getMaterial().isReplaceable())
				break;
			else {
				checkPos = checkPos.down();
			}
		}
		if (canFall) {
			Block bluk = state.getBlock();
			if (LeafiaUtil.isSolidVisibleCube(state)) {
				if (bluk instanceof ITileEntityProvider);
				else {
					if (worldObj.rand.nextInt(5) > 0) {
						EntityFallingBlock fallingBlock = new EntityFallingBlock(worldObj,pos.getX()+0.5,pos.getY(),pos.getZ()+0.5,state);
						fallingBlock.fallTime = 1;
						worldObj.spawnEntity(fallingBlock);
					}
				}
			}
		}
	}
}
