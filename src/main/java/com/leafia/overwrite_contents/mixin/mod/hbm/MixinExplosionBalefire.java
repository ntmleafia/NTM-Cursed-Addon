// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BlockMeta;
import com.hbm.explosion.ExplosionBalefire;
import com.leafia.contents.AddonBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = ExplosionBalefire.class, remap = false)
public abstract class MixinExplosionBalefire {
    @Shadow public int radius;
    @Shadow public int posX;
    @Shadow public int posY;
    @Shadow public int posZ;
    @Shadow public World worldObj;

    /**
     * @author movblock
     * @reason Addon crater shape, protected materials and baleonitite deposits.
     */
    @Overwrite
    private void breakColumn(int x, int z) {
        double distance = Math.sqrt(x * x + z * z);
        int amplitude = (int) (radius - distance);
        if (amplitude <= 0) return;

        int columnX = posX + x;
        int columnZ = posZ + z;
        int top = posY + 5;
        int surface = top;
        var cursor = new MutableBlockPos(columnX, top, columnZ);
        for (int scan = top; scan >= 0; scan--) {
            cursor.setPos(columnX, scan, columnZ);
            var state = worldObj.getBlockState(cursor);
            if (state.getBlock().isReplaceable(worldObj, cursor)) worldObj.setBlockToAir(cursor);
            if (!worldObj.isAirBlock(cursor)) break;
            surface = scan;
        }

        int depthScale = (int) (10 + radius * 0.25);
        int cavity = (int) (depthScale * amplitude / radius + Math.sin(amplitude * 0.15 + 2) * 2);
        int ceiling = (int) (top + depthScale * 2.5 * Math.pow(Math.cos(distance / radius * Math.PI / 2), 0.375))
                - worldObj.rand.nextInt(5);
        for (int height = top; height < ceiling - 10; height++) {
            cursor.setPos(columnX, height, columnZ);
            worldObj.setBlockState(cursor, Blocks.AIR.getDefaultState(), 2);
        }

        int floor = Math.max(surface - cavity, 0);
        while (surface > floor) {
            cursor.setPos(columnX, surface, columnZ);
            var block = worldObj.getBlockState(cursor).getBlock();
            boolean cluster = block == ModBlocks.block_schrabidium_cluster;
            if (cluster || block == ModBlocks.cmb_brick_reinforced) {
                if (worldObj.rand.nextInt(10) == 0) {
                    leafia$igniteAbove(cursor, surface + 1);
                    if (cluster) leafia$enrichCluster(cursor, block);
                }
                return;
            }
            worldObj.setBlockToAir(cursor);
            surface--;
        }

        if (worldObj.rand.nextInt(10) == 0) {
            cursor.setPos(columnX, surface, columnZ);
            leafia$igniteAbove(cursor, floor + 1);
            var block = worldObj.getBlockState(cursor).getBlock();
            if (block == ModBlocks.block_schrabidium_cluster) leafia$enrichCluster(cursor, block);
        }

        int depositDepth = 6 * amplitude / radius;
        for (int below = 0; below <= depositDepth; below++) {
            cursor.setPos(columnX, floor - below, columnZ);
            if (worldObj.getBlockState(cursor).getMaterial() != Material.ROCK) continue;
            worldObj.setBlockState(cursor,
                    AddonBlocks.baleonitite.getDefaultState().withProperty(BlockMeta.META, Math.min(5, depositDepth - below)));
            break;
        }
    }

    @Unique
    private void leafia$igniteAbove(MutableBlockPos position, int height) {
        int originalY = position.getY();
        position.setY(height);
        worldObj.setBlockState(position, ModBlocks.balefire.getDefaultState());
        position.setY(originalY);
    }

    @Unique
    private void leafia$enrichCluster(MutableBlockPos position, Block originalBlock) {
        int metadata = originalBlock.getMetaFromState(worldObj.getBlockState(position));
        worldObj.setBlockState(position, ModBlocks.block_euphemium_cluster.getStateFromMeta(metadata), 3);
    }
}
