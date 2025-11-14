package com.leafia.contents.network.spk_cable.uninos;

import com.hbm.lib.DirPos;
import com.hbm.lib.ForgeDirection;
import com.hbm.uninos.UniNodespace;
import com.hbm.util.Compat;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ISPKReceiver extends ISPKHandler {
    /**
     * Transfers a specified amount of energy to this receiver.
     * If the receiver has enough capacity, all the energy is absorbed.
     * Otherwise, it absorbs as much as it can and returns the excess energy.
     *
     * @param power    The amount of energy to transfer.
     * @param simulate If true, the transfer is simulated and no energy is actually transferred.
     * @return The amount of energy that could not be absorbed (excess energy), or 0 if all energy was absorbed.
     */
    default long transferSPK(long power, boolean simulate) {
        if (power + this.getSPK() <= this.getMaxSPK()) {
            if (!simulate) this.setSPK(power + this.getSPK());
            return 0;
        }
        long capacity = this.getMaxSPK() - this.getSPK();
        long overshoot = power - capacity;
        if (!simulate) this.setSPK(this.getMaxSPK());
        return overshoot;
    }

    default boolean isInputPreferrable(ForgeDirection dir) { return true; }

    /**
     * Retrieves the maximum speed at which this energy receiver can accept energy.
     * By default, it returns the maximum power capacity of the receiver.
     *
     * @return The maximum energy reception speed, which is equal to the receiver's maximum power capacity.
     */
    default long getSPKReceiverSpeed() {
        // Return the maximum power capacity as the default reception speed
        return this.getMaxSPK();
    }

    default void trySubscribeSPK(World world, DirPos pos) { trySubscribeSPK(world, pos.getPos(), pos.getDir()); }

    default void trySubscribeSPK(World world, BlockPos pos, ForgeDirection dir) {
        trySubscribeSPK(world, pos.getX(), pos.getY(), pos.getZ(), dir);
    }
    default void trySubscribeSPK(World world, int x, int y, int z, ForgeDirection dir) {

        TileEntity te = Compat.getTileStandard(world, x, y, z);

        if (te instanceof ISPKConductor con) {
            if (!con.canConnectSPK(dir.getOpposite())) return;

            SPKNode node = UniNodespace.getNode(world, new BlockPos(x, y, z), SPKNet.THE_NETWORK_PROVIDER);

            if (node != null && node.net != null) {
                node.net.addReceiver(this);
            }
        }
    }
}
