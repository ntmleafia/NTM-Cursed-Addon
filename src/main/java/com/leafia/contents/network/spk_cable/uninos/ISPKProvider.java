package com.leafia.contents.network.spk_cable.uninos;

import com.hbm.lib.ForgeDirection;
import com.hbm.uninos.UniNodespace;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ISPKProvider extends ISPKHandler {

    /**
     * Uses up available power, default implementation has no sanity checking, make sure that the requested power is lequal to the current power
     *
     * @param power The amount of power to use. Ensure this value is less than or equal to the current power.
     */
    @Deprecated
    default void setTransferredSpk(long power) {
        // Subtract the specified power from the current power and update the power level
        //this.setSPK(this.getSPK() - power);
    } // mov, joules are supposed to be reset every tick, we do not need a buffer of them.

    /**
     * Retrieves the maximum speed at which the energy provider can send energy.
     * By default, this method returns the maximum power capacity of the provider.
     *
     * @return The maximum energy transfer speed, represented by the provider's maximum power capacity.
     */
    default long getSPKProviderSpeed() {
        // Return the maximum power capacity as the default provider speed
        return this.getMaxSPK();
    }

    long getNetRemaining();

    /**
     * Attempts to provide SPK to a target tile entity at specific coordinates.
     *
     * @param world The game world.
     * @param x     The x-coordinate of the <b>target tile entity</b> (the potential receiver).
     * @param y     The y-coordinate of the <b>target tile entity</b>.
     * @param z     The z-coordinate of the <b>target tile entity</b>.
     * @param dir   The {@link ForgeDirection} from this provider to the target tile entity.
     * @return The amount of power successfully provided to the receiver.
     */
    default void tryProvideSPK(World world, int x, int y, int z, ForgeDirection dir) {
        BlockPos targetPos = new BlockPos(x, y, z);
        TileEntity targetTE = world.getTileEntity(targetPos);

        if (targetTE == null) return;

        ForgeDirection dirOpposite = dir.getOpposite();
        if (targetTE instanceof ISPKConductor con) {
            if (con.canConnectSPK(dirOpposite)) {
                SPKNode node =  UniNodespace.getNode(world, targetPos, SPKNet.THE_NETWORK_PROVIDER);
                if (node != null && node.net != null) {
                    node.net.addProvider(this);
                }
            }
        }

        if (targetTE instanceof ISPKReceiver rec && targetTE != this) {
            if (rec.canConnectSPK(dirOpposite) && rec.isInputPreferrable(dirOpposite)) {
                long canProvide = Math.min(this.getNetRemaining(), this.getSPKProviderSpeed());
                long canReceive = Math.min(rec.getMaxSPK() - rec.getSPK(), rec.getSPKReceiverSpeed());
                long toTransfer = Math.min(canProvide, canReceive);

                if (toTransfer > 0L) {
                    long rejected = rec.transferSPK(toTransfer, false);
                    long accepted = toTransfer - rejected;
                    if (accepted > 0L) {
                        this.setTransferredSpk(accepted);
                    }
                }
            }
        }
    }

    //output to a specific te, does not add to net
    default long tryProvideSPK(TileEntity targetTE, ForgeDirection dir, long canProvide, boolean simulate) {
        if (targetTE == null) return 0L;
        ForgeDirection dirOpposite = dir.getOpposite();
        if (targetTE instanceof ISPKReceiver rec && targetTE != this) {
            if (rec.canConnectSPK(dirOpposite) && rec.isInputPreferrable(dirOpposite)) {
                long canReceive = Math.min(rec.getMaxSPK() - rec.getSPK(), rec.getSPKReceiverSpeed());
                long toTransfer = Math.min(canProvide, canReceive);
                if (toTransfer > 0L) {
                    long rejected = rec.transferSPK(toTransfer, simulate);
                    long accepted = toTransfer - rejected;
                    if (accepted > 0L) {
                        if (!simulate) this.setTransferredSpk(accepted);
                        return accepted;
                    }
                }
            }
        }
        return 0L;
    }

    default void tryLinkSPK(World world, BlockPos pos, TileEntity te, ForgeDirection dir) {
        ForgeDirection dirOpposite = dir.getOpposite();
        if (te instanceof ISPKConductor con) {
            if (con.canConnectSPK(dirOpposite)) {
                SPKNode node =  UniNodespace.getNode(world,pos, SPKNet.THE_NETWORK_PROVIDER);
                if (node != null && node.net != null) {
                    node.net.addProvider(this);
                }
            }
        }
    }

    default void tryProvideSPK(World world, BlockPos pos, ForgeDirection dir, boolean simulate) {
        tryProvideSPK(world, pos.getX(), pos.getY(), pos.getZ(), dir);
    }
}
