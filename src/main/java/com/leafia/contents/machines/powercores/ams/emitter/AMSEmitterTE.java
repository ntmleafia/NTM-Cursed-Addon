// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.powercores.ams.emitter;

import com.hbm.explosion.ExplosionLarge;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.ModItems;
import com.hbm.lib.DirPos;
import com.hbm.lib.ForgeDirection;
import com.hbm.lib.HBMSoundHandler;
import com.leafia.contents.machines.powercores.ams.AMSDriveTE;
import com.leafia.contents.machines.powercores.ams.emitter.container.AMSEmitterContainer;
import com.leafia.contents.machines.powercores.ams.emitter.container.AMSEmitterUI;
import com.leafia.dev.LeafiaUtil;
import com.leafia.init.LeafiaSoundEvents;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AMSEmitterTE extends AMSDriveTE {
    public static final long maxPower = 100_000_000;

    public AMSEmitterTE() {
        super(16000);
    }

    @Override
    protected String defaultName() {
        return "tile.ams_emitter.name";
    }

    @Override
    public long getMaxPower() {
        return maxPower;
    }

    @Override
    public DirPos[] getConPos() {
        var top = pos.up(6);
        var result = new DirPos[5];
        result[0] = new DirPos(top, ForgeDirection.UP);
        for (int port = 1; port < result.length; port++) {
            var facing = switch (port) {
                case 1 -> EnumFacing.NORTH;
                case 2 -> EnumFacing.SOUTH;
                case 3 -> EnumFacing.EAST;
                default -> EnumFacing.WEST;
            };
            result[port] = new DirPos(top.offset(facing), ForgeDirection.UP);
        }
        return result;
    }

    @Override
    public void update() {
        if (world.isRemote) return;
        needsUpdate = false;
        if (locked) {
            ExplosionLarge.spawnBurst(world, pos.getX() + 0.5, pos.getY() - 0.5, pos.getZ() + 0.5, rand.nextInt(10), 1);
            efficiency = 0;
            power = 0;
            warning = 3;
        } else {
            subscribeConnections();
            LeafiaUtil.setTypeOnly(tank, 0, 1, inventory, Fluids.CRYOGEL, Fluids.COOLANT, Fluids.WATER);
            tickDrive(3);
            var focus = inventory.getStackInSlot(2);
            if (focus.isEmpty() || focus.getItem() != ModItems.ams_muzzle) {
                efficiency = 0;
                warning = 2;
            }
            finishDriveTick();
        }
        needsUpdate = true;
        sendDriveState();
    }

    @Override
    protected void destroyDrive() {
        for (int burst = 0; burst < 5; burst++) {
            ExplosionLarge.spawnBurst(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 36, 3 - burst * 0.5);
        }
        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), HBMSoundHandler.oldExplosion, SoundCategory.BLOCKS, 10, 1);
        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), LeafiaSoundEvents.machineDestroyed, SoundCategory.BLOCKS, 10, 1);
    }

    @Override
    public Container provideContainer(int id, EntityPlayer player, World world, int x, int y, int z) {
        return new AMSEmitterContainer(player.inventory, this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiScreen provideGUI(int id, EntityPlayer player, World world, int x, int y, int z) {
        return new AMSEmitterUI(player.inventory, this);
    }

    @Override
    public String getPacketIdentifier() {
        return "AMS_EMTR";
    }
}
