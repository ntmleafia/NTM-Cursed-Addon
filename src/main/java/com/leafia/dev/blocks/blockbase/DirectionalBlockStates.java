// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.dev.blocks.blockbase;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;

/** Vanilla six-facing state bindings shared by addon blocks. */
public final class DirectionalBlockStates {
    public static final PropertyDirection FACING = BlockDirectional.FACING;

    private DirectionalBlockStates() {}

    public static BlockStateContainer createContainer(Block block) {
        return new BlockStateContainer(block, FACING);
    }

    public static IBlockState fromMetadata(IBlockState base, int metadata) {
        return base.withProperty(FACING, EnumFacing.byIndex(metadata));
    }

    public static int metadata(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }

    public static IBlockState rotate(IBlockState state, Rotation rotation) {
        return state.withProperty(FACING, rotation.rotate(state.getValue(FACING)));
    }

    public static IBlockState mirror(IBlockState state, Mirror mirror) {
        return state.withRotation(mirror.toRotation(state.getValue(FACING)));
    }
}
