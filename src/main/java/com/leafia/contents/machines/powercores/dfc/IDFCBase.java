// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2025 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.powercores.dfc;

import com.hbm.tileentity.machine.TileEntityCore;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.container_utility.LeafiaPacketReceiver;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityCore;
import com.llib.LeafiaLib;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public interface IDFCBase extends LeafiaPacketReceiver {
    default void leafia$writeTargetPos(NBTTagCompound compound) {
        BlockPos targetPos = leafia$getTargetPosition();
        compound.setInteger("x1",targetPos.getX());
        compound.setInteger("y1",targetPos.getY());
        compound.setInteger("z1",targetPos.getZ());
    }

    default void leafia$readTargetPos(NBTTagCompound compound) {
        leafia$targetPosition(new BlockPos(compound.getInteger("x1"), compound.getInteger("y1"), compound.getInteger("z1")));
        TileEntity thisTE = (TileEntity) this;
        World world = thisTE.getWorld();
        if (world != null && world.isRemote)
            LeafiaPacket._start(thisTE).__write(31,true).__sendToServer();

    }

    @Override
    default void onReceivePacketLocal(byte key,Object value) {
        if (key == 31) leafia$targetPosition((BlockPos)value);
    }

    @Override
    default void onReceivePacketServer(byte key, Object value, EntityPlayer plr) {
        if (key == 31) {
            LeafiaPacket packet = LeafiaPacket._start((TileEntity) this);
            syncClients(packet);
            packet.__sendToClient(plr);
        }
    }

    default LeafiaPacket syncClients(LeafiaPacket packet) {
//        LeafiaDebug.debugLog(world,"syncClients");
        packet.__write(31, leafia$getTargetPosition());
        return packet;
    }

    @Override
    default void onPlayerValidate(EntityPlayer plr) {
        LeafiaPacket._start((TileEntity) this).__write(31,leafia$getTargetPosition()).__sendToClient(plr);
    }

    default void leafia$setTargetPosition(BlockPos pos) {
        TileEntity thisTE = (TileEntity) this;
        leafia$targetPosition(pos);
        if (!thisTE.getWorld().isRemote)
            LeafiaPacket._start(thisTE).__write(31,pos).__sendToAffectedClients();
    }


    default Vec3d leafia$getDirection() {
        TileEntity thisTE = (TileEntity) this;
        return new Vec3d(leafia$getTargetPosition().subtract(thisTE.getPos())).normalize();
    }

    default EnumFacing leafia$getFront() {
        TileEntity thisTE = (TileEntity) this;
        Vec3i relative = leafia$getTargetPosition().subtract(thisTE.getPos());
        int max = Math.max(Math.abs(relative.getX()),Math.max(Math.abs(relative.getY()),Math.abs(relative.getZ())));
        if (max == relative.getX()) return EnumFacing.EAST;
        else if (max == -relative.getX()) return EnumFacing.WEST;
        else if (max == relative.getZ()) return EnumFacing.SOUTH;
        else if (max == -relative.getZ()) return EnumFacing.NORTH;
        else if (max == relative.getY()) return EnumFacing.UP;
        else return EnumFacing.DOWN;
    }

    @Nullable
    default TileEntityCore leafia$getCore(int range) {
        TileEntity thisTE = (TileEntity) this;
        World world = thisTE.getWorld();
        BlockPos pos = thisTE.getPos();
        leafia$lastGetCore(LeafiaLib.leafiaRayTraceBlocksCustom(world,new Vec3d(pos).add(0.5,0.5,0.5),new Vec3d(pos).add(0.5,0.5,0.5).add(leafia$getDirection().scale(range)),(process,config,current) -> {
            if (current.posSnapped.equals(pos)) return process.CONTINUE();
            if (!current.block.canCollideCheck(current.state,true))
                return process.CONTINUE();
            RayTraceResult result = current.state.collisionRayTrace(world,current.posSnapped,current.posIntended.subtract(config.unitDir.scale(2)),current.posIntended.add(config.unitDir.scale(2)));
            if (result == null)
                return process.CONTINUE();
            TileEntity te = world.getTileEntity(current.posSnapped);
            if(te instanceof TileEntityCore) {
                ((IMixinTileEntityCore) te).getDFCComponentPositions().add(pos);
                return process.RETURN((TileEntityCore) te);
            }
            return process.BREAK();
        }));
        return leafia$lastGetCore();
    }

    /// ---------------------------- BOILERPLATE ZONE ---------------------------- ///

    TileEntityCore leafia$lastGetCore();

    void leafia$lastGetCore(TileEntityCore core);

    BlockPos leafia$getTargetPosition();

    /**
     * @deprecated internal setter
     */
    @Deprecated
    void leafia$targetPosition(BlockPos pos);
}
