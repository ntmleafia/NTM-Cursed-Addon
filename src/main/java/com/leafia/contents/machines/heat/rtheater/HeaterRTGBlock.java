// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.heat.rtheater;

import com.hbm.blocks.ILookOverlay;
import com.hbm.blocks.ITooltipProvider;
import com.hbm.lib.ForgeDirection;
import com.hbm.tileentity.TileEntityProxyCombo;
import com.hbm.util.I18nUtil;
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
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class HeaterRTGBlock extends AddonBlockDummyable implements ITooltipProvider, ILookOverlay {
    private static final EnumFacing[] PORTS = {EnumFacing.EAST, EnumFacing.WEST, EnumFacing.SOUTH, EnumFacing.NORTH, EnumFacing.UP};

    public HeaterRTGBlock(Material material, String name) { super(material, name); }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        if (metadata >= 12) return new HeaterRTGTE();
        if (!hasExtra(metadata)) return null;
        return metadata > extra + 1 ? new TileEntityProxyCombo(true, false, false)
                : new TileEntityProxyCombo(false, false, false, true);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing face, float hitX, float hitY, float hitZ) {
        return standardOpenBehavior(world, pos.getX(), pos.getY(), pos.getZ(), player, 0);
    }

    @Override
    public int[] getDimensions() { return new int[]{1, 0, 1, 1, 1, 1}; }

    @Override
    public int getOffset() { return 1; }

    @Override
    protected void fillSpace(World world, int x, int y, int z, ForgeDirection direction, int distance) {
        super.fillSpace(world, x, y, z, direction, distance);
        int coreX = x + direction.offsetX * distance;
        int coreZ = z + direction.offsetZ * distance;
        for (var face : PORTS) makeExtra(world, coreX + face.getXOffset(), y + face.getYOffset(), coreZ + face.getZOffset());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag advanced) {
        addStandardInfo(tooltip);
    }

    @Override
    public void printHook(Pre event, World world, BlockPos pos) {
        var core = findCore(world, pos);
        if (core == null || !(world.getTileEntity(core) instanceof HeaterRTGTE heater)) return;
        var lines = new ArrayList<String>(2);
        lines.add(String.format("%,d", heater.heatEnergy) + " TU");
        lines.add("§c<- §r" + String.format("%,d", heater.heatGen) + " TU/t");
        ILookOverlay.printGeneric(event, I18nUtil.resolveKey(getTranslationKey() + ".name"), 0xffff00, 0x404000, lines);
    }
}
