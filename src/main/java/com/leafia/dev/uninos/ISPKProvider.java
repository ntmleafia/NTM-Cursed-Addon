package com.leafia.dev.uninos;

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
    default void useSPK(long power) {
        // Subtract the specified power from the current power and update the power level
        this.setSPK(this.getSPK() - power);
    }

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

    /**
     * Attempts to provide energy to a target tile entity at specific coordinates.
     * It checks for HBM's native energy interfaces first, and then checks for Forge Energy capability
     *
     * @param world The game world.
     * @param x     The x-coordinate of the <b>target tile entity</b> (the potential receiver).
     * @param y     The y-coordinate of the <b>target tile entity</b>.
     * @param z     The z-coordinate of the <b>target tile entity</b>.
     * @param dir   The {@link ForgeDirection} from this provider to the target tile entity.
     */
    default void tryProvideSPK(World world, int x, int y, int z, ForgeDirection dir) {
        BlockPos targetPos = new BlockPos(x, y, z);
        TileEntity targetTE = world.getTileEntity(targetPos);

        if (targetTE == null) return;

        if (targetTE instanceof ISPKConductor con) {
            if (con.canConnectSPK(dir.getOpposite())) {
                SPKNode node =  UniNodespace.getNode(world, targetPos, SPKNet.THE_NETWORK_PROVIDER);
                if (node != null && node.net != null) {
                    node.net.addProvider(this);
                }
            }
        }

        if (targetTE instanceof ISPKReceiver rec && targetTE != this) {
            if (rec.canConnectSPK(dir.getOpposite())) {
                long canProvide = Math.min(this.getSPK(), this.getSPKProviderSpeed());
                long canReceive = Math.min(rec.getMaxSPK() - rec.getSPK(), rec.getSPKReceiverSpeed());
                long toTransfer = Math.min(canProvide, canReceive);

                if (toTransfer > 0) {
                    long rejected = rec.transferSPK(toTransfer, false);
                    long accepted = toTransfer - rejected;
                    if (accepted > 0) {
                        this.useSPK(accepted);
                    }
                }
            }
        }
    }

    default void tryProvideSPK(World world, BlockPos pos, ForgeDirection dir) {
        tryProvideSPK(world, pos.getX(), pos.getY(), pos.getZ(), dir);
    }

}
