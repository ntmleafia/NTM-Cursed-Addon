// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.network.pipe_amat.uninos;

import com.hbm.api.energymk2.IEnergyReceiverMK2.ConnectionPriority;
import com.hbm.api.fluidmk2.IFluidProviderMK2;
import com.hbm.api.fluidmk2.IFluidReceiverMK2;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.hbm.uninos.NodeNet;
import com.hbm.util.Tuple.ObjectLongPair;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AmatNetTransferTest {
    @Test
    void eachPriorityUsesOnlyItsOwnAcceptedAmount() {
        var network = new AmatNet(null);
        var source = new Source(7);
        var highest = addReceiver(network, ConnectionPriority.HIGHEST, 5, 2);
        var high = addReceiver(network, ConnectionPriority.HIGH, 4, 4);
        var normal = addReceiver(network, ConnectionPriority.NORMAL, 5, 5);
        network.providers[0].add(new ObjectLongPair<>(source, 7));
        network.fluidAvailable[0] = 7;

        network.transferFluid();

        assertEquals(2, highest.accepted);
        assertEquals(4, high.accepted);
        assertEquals(1, normal.accepted);
        assertEquals(7, network.fluidTracker);
        assertEquals(0, source.available);
    }

    @Test
    void rejectionAndScratchReusePreserveConservation() {
        var network = new AmatNet(null);
        for (var tick = 0; tick < 2; tick++) {
            var source = new Source(1);
            var rejecting = addReceiver(network, ConnectionPriority.HIGHEST, 1, 0);
            var accepting = addReceiver(network, ConnectionPriority.HIGH, 1, 1);
            network.providers[0].add(new ObjectLongPair<>(source, 1));
            network.fluidAvailable[0] = 1;

            network.transferFluid();

            assertEquals(0, rejecting.accepted);
            assertEquals(1, accepting.accepted);
            assertEquals(0, source.available);
            assertEquals(tick + 1, network.fluidTracker);
            network.cleanUp();
        }
    }

    @Test
    void roundedLongMaxSharesCannotOverdrawAProvider() {
        var network = new AmatNet(null);
        var small = new Source(1);
        var large = new Source(Long.MAX_VALUE - 1);
        var receiver = addReceiver(network, ConnectionPriority.NORMAL, Long.MAX_VALUE, Long.MAX_VALUE);
        network.providers[0].add(new ObjectLongPair<>(small, small.available));
        network.providers[0].add(new ObjectLongPair<>(large, large.available));
        network.fluidAvailable[0] = Long.MAX_VALUE;

        network.transferFluid();

        assertEquals(Long.MAX_VALUE, receiver.accepted);
        assertEquals(Long.MAX_VALUE, network.fluidTracker);
        assertEquals(0, small.available);
        assertEquals(0, large.available);
    }

    @Test
    void overflowingSupplyAndDemandStayBoundedAndConserveFluid() {
        var network = new AmatNet(null) {
            @Override public boolean checkExplode(IFluidProviderMK2 provider) { return false; }
        };
        var firstSource = new Source(Long.MAX_VALUE);
        var secondSource = new Source(Long.MAX_VALUE);
        var firstSink = new Sink(Long.MAX_VALUE);
        var secondSink = new Sink(Long.MAX_VALUE);
        network.addProvider(firstSource);
        network.addProvider(secondSource);
        network.addReceiver(firstSink);
        network.addReceiver(secondSink);
        network.setupFluidProviders();
        network.setupFluidReceivers();
        assertEquals(Long.MAX_VALUE, network.fluidAvailable[0]);
        assertEquals(Long.MAX_VALUE, network.fluidDemand[0][ConnectionPriority.NORMAL.ordinal()]);

        network.transferFluid();

        var delivered = java.math.BigInteger.valueOf(firstSink.accepted)
                .add(java.math.BigInteger.valueOf(secondSink.accepted));
        var debited = java.math.BigInteger.valueOf(Long.MAX_VALUE).subtract(java.math.BigInteger.valueOf(firstSource.available))
                .add(java.math.BigInteger.valueOf(Long.MAX_VALUE).subtract(java.math.BigInteger.valueOf(secondSource.available)));
        assertEquals(java.math.BigInteger.valueOf(Long.MAX_VALUE), delivered);
        assertEquals(delivered, debited);
        assertEquals(Long.MAX_VALUE, network.fluidTracker);
    }

    @Test
    void unpaidRandomRemainderIsDebitedWithoutConsumingMoreRandomNumbers() {
        var network = new AmatNet(null);
        var firstSource = new Source(1);
        var secondSource = new Source(1);
        network.providers[0].add(new ObjectLongPair<>(firstSource, 1));
        network.providers[0].add(new ObjectLongPair<>(secondSource, 1));
        for (var index = 2; index < 1024; index++) {
            network.providers[0].add(new ObjectLongPair<>(new Source(0), 0));
        }
        network.fluidAvailable[0] = 2;
        var receiver = addReceiver(network, ConnectionPriority.NORMAL, 1, 1);
        var previousRandom = NodeNet.rand;
        var expectedRandom = new Random(2);
        for (var attempt = 0; attempt < 100; attempt++) expectedRandom.nextInt(1024);
        try {
            NodeNet.rand = new Random(2);
            network.transferFluid();

            assertEquals(1, receiver.accepted);
            assertEquals(1, firstSource.available + secondSource.available);
            assertEquals(expectedRandom.nextLong(), NodeNet.rand.nextLong());
        } finally {
            NodeNet.rand = previousRandom;
        }
    }

    @Test
    void inconsistentProviderAvailabilityFailsInsteadOfLeavingAnUnpaidDelivery() {
        var network = new AmatNet(null);
        network.providers[0].add(new ObjectLongPair<>(new Source(0), 1));
        network.fluidAvailable[0] = 1;
        addReceiver(network, ConnectionPriority.NORMAL, 1, 1);
        var previousRandom = NodeNet.rand;
        try {
            NodeNet.rand = new Random(0);
            assertThrows(IllegalStateException.class, network::transferFluid);
        } finally {
            NodeNet.rand = previousRandom;
        }
    }

    private static Sink addReceiver(AmatNet network, ConnectionPriority priority, long demand, long capacity) {
        var receiver = new Sink(capacity);
        network.receivers[0][priority.ordinal()].add(new ObjectLongPair<>(receiver, demand));
        network.fluidDemand[0][priority.ordinal()] += demand;
        return receiver;
    }

    private static final class Source implements IFluidProviderMK2 {
        private long available;

        private Source(long available) {
            this.available = available;
        }

        @Override public void useUpFluid(FluidType type, int pressure, long amount) { available -= amount; }
        @Override public long getFluidAvailable(FluidType type, int pressure) { return available; }
        @Override public long getProviderSpeed(FluidType type, int pressure) { return Long.MAX_VALUE; }
        @Override public boolean isLoaded() { return true; }
        @Override public FluidTankNTM[] getAllTanks() { return new FluidTankNTM[0]; }
    }

    private static final class Sink implements IFluidReceiverMK2 {
        private final long capacity;
        private long accepted;

        private Sink(long capacity) {
            this.capacity = capacity;
        }

        @Override
        public long transferFluid(FluidType type, int pressure, long amount) {
            var transferred = Math.min(amount, capacity - accepted);
            accepted += transferred;
            return amount - transferred;
        }

        @Override public long getDemand(FluidType type, int pressure) { return capacity - accepted; }
        @Override public long getReceiverSpeed(FluidType type, int pressure) { return Long.MAX_VALUE; }
        @Override public boolean isLoaded() { return true; }
        @Override public FluidTankNTM[] getAllTanks() { return new FluidTankNTM[0]; }
    }
}
