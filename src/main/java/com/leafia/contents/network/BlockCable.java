// SPDX-FileCopyrightText: 2025-2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.network;

import com.hbm.lib.ForgeDirection;
import com.hbm.lib.Library;
import com.hbm.main.MainRegistry;
import com.hbm.tileentity.network.energy.TileEntityCableBaseNT;
import com.leafia.dev.blocks.blockbase.AddonBlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCable extends AddonBlockContainer {
    private static final ForgeDirection[] SIDES = {
            ForgeDirection.EAST, ForgeDirection.WEST, ForgeDirection.UP,
            ForgeDirection.DOWN, ForgeDirection.SOUTH, ForgeDirection.NORTH
    };
    private static final AxisAlignedBB[] BOUNDS = new AxisAlignedBB[64];

    static {
        var core = new AxisAlignedBB(11 / 32D, 11 / 32D, 11 / 32D, 21 / 32D, 21 / 32D, 21 / 32D);
        for (int mask = 0; mask < BOUNDS.length; mask++) {
            var shape = core;
            for (int side = 0; side < SIDES.length; side++) {
                if ((mask & (1 << side)) != 0) {
                    var facing = SIDES[side];
                    shape = shape.expand(facing.offsetX * 11 / 32D, facing.offsetY * 11 / 32D, facing.offsetZ * 11 / 32D);
                }
            }
            BOUNDS[mask] = shape;
        }
    }

    public BlockCable(Material material, String name) {
        super(material, name);
        setCreativeTab(MainRegistry.blockTab);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityCableBaseNT();
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (!(world.getTileEntity(pos) instanceof TileEntityCableBaseNT cable)) return FULL_BLOCK_AABB;
        int mask = 0;
        var origin = cable.getPos();
        for (int side = 0; side < SIDES.length; side++) {
            var facing = SIDES[side];
            if (Library.canConnect(world, origin.add(facing.offsetX, facing.offsetY, facing.offsetZ), facing)) {
                mask |= 1 << side;
            }
        }
        return BOUNDS[mask];
    }

    protected AxisAlignedBB getBB(boolean posX, boolean posY, boolean posZ, boolean negX, boolean negY, boolean negZ) {
        int mask = (posX ? 1 : 0) | (negX ? 2 : 0) | (posY ? 4 : 0)
                | (negY ? 8 : 0) | (posZ ? 16 : 0) | (negZ ? 32 : 0);
        return BOUNDS[mask];
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) { return EnumBlockRenderType.ENTITYBLOCK_ANIMATED; }
    @Override
    public boolean isFullCube(IBlockState state) { return false; }
    @Override
    public boolean isBlockNormalCube(IBlockState state) { return false; }
    @Override
    public boolean isNormalCube(IBlockState state) { return false; }
    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) { return false; }
    @Override
    public boolean isOpaqueCube(IBlockState state) { return false; }
    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.CENTER;
    }
}
