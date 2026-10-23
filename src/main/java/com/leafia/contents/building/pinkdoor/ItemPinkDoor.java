// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.building.pinkdoor;

import com.hbm.config.BombConfig;
import com.hbm.items.ModItems;
import com.hbm.items.tool.ItemModDoor;
import com.hbm.util.I18nUtil;
import com.leafia.contents.AddonBlocks;
import com.leafia.contents.AddonItems;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class ItemPinkDoor extends ItemModDoor {
    public ItemPinkDoor(String name) {
        super(name);
        ModItems.ALL_ITEMS.remove(this);
        AddonItems.ALL_ITEMS.add(this);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos clicked,
                                    EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (side != EnumFacing.UP) return EnumActionResult.FAIL;
        var target = world.getBlockState(clicked).getBlock().isReplaceable(world, clicked)
                ? clicked : clicked.up();
        var stack = player.getHeldItem(hand);
        var door = AddonBlocks.door_fuckoff;
        if (!player.canPlayerEdit(target, side, stack) || !door.canPlaceBlockAt(world, target)) {
            return EnumActionResult.FAIL;
        }

        var facing = EnumFacing.fromAngle(player.rotationYaw);
        var hingeRight = switch (facing) {
            case WEST -> hitZ < 0.5F;
            case EAST -> hitZ > 0.5F;
            case NORTH -> hitX > 0.5F;
            case SOUTH -> hitX < 0.5F;
            default -> throw new IllegalStateException("Non-horizontal door placement: " + facing);
        };
        ItemModDoor.placeDoor(world, target, facing, door, hingeRight);
        var placed = world.getBlockState(target);
        var sound = placed.getBlock().getSoundType(placed, world, target, player);
        world.playSound(player, target, sound.getPlaceSound(), SoundCategory.BLOCKS,
                (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
        stack.shrink(1);
        return EnumActionResult.SUCCESS;
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag advanced) {
        tooltip.add("§2[" + I18nUtil.resolveKey("trait.nuclearbomb") + "]§r");
        tooltip.add(" §e" + I18nUtil.resolveKey("desc.radius", "220"));
        if (!BombConfig.disableNuclear) {
            tooltip.add("§2[" + I18nUtil.resolveKey("trait.fallout") + "]§r");
            tooltip.add(" §e" + I18nUtil.resolveKey("desc.radius", "???"));
        }
    }
}
