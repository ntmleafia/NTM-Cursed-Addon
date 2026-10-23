// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.powercores.ams.stabilizer;

import com.hbm.explosion.ExplosionLarge;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.lib.DirPos;
import com.hbm.lib.ForgeDirection;
import com.hbm.lib.HBMSoundHandler;
import com.hbm.util.ParticleUtil;
import com.leafia.contents.AddonItems;
import com.leafia.contents.machines.powercores.ams.AMSDriveTE;
import com.leafia.contents.machines.powercores.ams.stabilizer.container.AMSStabilizerContainer;
import com.leafia.contents.machines.powercores.ams.stabilizer.container.AMSStabilizerUI;
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

public class AMSStabilizerTE extends AMSDriveTE {
    public static final long maxPower = 10_000_000;
    public int mode;

    public AMSStabilizerTE() {
        super(8000);
    }

    public static int rotateShitfuck(int metadata) {
        return EnumFacing.byIndex(metadata - 10).rotateY().getIndex() + 10;
    }

    @Override
    protected String defaultName() {
        return "tile.ams_limiter.name";
    }

    @Override
    public long getMaxPower() {
        return maxPower;
    }

    @Override
    public DirPos[] getConPos() {
        var front = EnumFacing.byIndex(getBlockMetadata() - 10);
        var side = front.rotateY();
        var result = new DirPos[8];
        for (int port = 0; port < result.length; port++) {
            int sideOffset = port < 2 ? 0 : port < 5 ? 2 : -2;
            boolean edge = port == 4 || port == 7;
            boolean reverse = port == 1 || port == 3 || port == 6;
            var direction = edge ? (sideOffset > 0 ? side : side.getOpposite())
                    : (reverse ? front.getOpposite() : front);
            var point = edge ? pos.offset(side, sideOffset > 0 ? 3 : -3)
                    : pos.offset(side, sideOffset).offset(direction);
            result[port] = new DirPos(point, ForgeDirection.getOrientation(direction));
        }
        return result;
    }

    @Override
    public void update() {
        if (world.isRemote) return;
        if (locked) {
            int facing = rotateShitfuck(getBlockMetadata()) - 10;
            double slide = rand.nextDouble() * 2.5;
            double x = pos.getX() + 0.5;
            double z = pos.getZ() + 0.5;
            switch (facing) {
                case 2 -> { x += 0.25; z -= slide; }
                case 3 -> { x -= 0.25; z += slide; }
                case 4 -> { x -= slide; z -= 0.25; }
                case 5 -> { x += slide; z += 0.25; }
                default -> { }
            }
            if (facing >= 2 && facing <= 5) ParticleUtil.spawnGasFlame(world, x, pos.getY() + 5.5, z, 0, 0, 0);
            efficiency = 0;
            power = 0;
            warning = 3;
        } else {
            LeafiaUtil.setTypeOnly(tank, 0, 1, inventory, Fluids.WATER, Fluids.COOLANT, Fluids.CRYOGEL);
            subscribeConnections();
            needsUpdate = false;
            tickDrive(1);
            mode = 0;
            var focus = inventory.getStackInSlot(2);
            if (!focus.isEmpty() && focus.getItem() == AddonItems.ams_focus_limiter) mode = 1;
            else if (!focus.isEmpty() && focus.getItem() == AddonItems.ams_focus_booster) mode = 2;
            else {
                efficiency = 0;
                warning = 2;
            }
            finishDriveTick();
        }
        sendDriveState();
    }

    @Override
    protected void destroyDrive() {
        ExplosionLarge.spawnShock(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 24, 3);
        ExplosionLarge.spawnBurst(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 24, 3);
        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), HBMSoundHandler.oldExplosion, SoundCategory.BLOCKS, 10, 1);
        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), LeafiaSoundEvents.machineDestroyed, SoundCategory.BLOCKS, 10, 1);
    }

    public boolean isValidFluid(FluidType fluid) {
        return fluid == Fluids.WATER || fluid == Fluids.COOLANT || fluid == Fluids.CRYOGEL;
    }

    @Override
    public Container provideContainer(int id, EntityPlayer player, World world, int x, int y, int z) {
        return new AMSStabilizerContainer(player.inventory, this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiScreen provideGUI(int id, EntityPlayer player, World world, int x, int y, int z) {
        return new AMSStabilizerUI(player.inventory, this);
    }

    @Override
    public String getPacketIdentifier() {
        return "AMS_LMTR";
    }
}
