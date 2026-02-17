package com.leafia.contents.network.spk_cable.uninos;

import com.hbm.uninos.INetworkProvider;
import com.hbm.uninos.NodeNet;
import com.hbm.util.Tuple;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;

import java.util.ArrayList;
import java.util.List;

/**
 * Copy-pasted PowerNetMk2 shit
 *
 * @author mlbv
 */
public class SPKNet extends NodeNet<ISPKReceiver, ISPKProvider, SPKNode, SPKNet> {
    public static final INetworkProvider<SPKNet> THE_NETWORK_PROVIDER = SPKNet::new;

    public long energyTracker = 0L;
    protected static int timeout = 3_000;

    @Override
    public void resetTrackers() {
        this.energyTracker = 0;
    }

    @Override
    public void update() {

        if (providerEntries.isEmpty()) return;
        if (receiverEntries.isEmpty()) return;

        final long timestamp = System.currentTimeMillis();

        final List<Tuple.ObjectLongPair<ISPKProvider>> providers = new ArrayList<>();
        long powerAvailable = 0;

        ObjectIterator<Object2LongMap.Entry<ISPKProvider>> provIt =
                providerEntries.object2LongEntrySet().fastIterator();

        while (provIt.hasNext()) {
            Object2LongMap.Entry<ISPKProvider> entry = provIt.next();
            ISPKProvider prov = entry.getKey();

            if (timestamp - entry.getLongValue() > timeout || isBadLink(prov)) {
                provIt.remove();
                continue;
            }

            long src = Math.min(prov.getNetRemaining(), prov.getSPKProviderSpeed());
            if (src > 0) {
                providers.add(new Tuple.ObjectLongPair<>(prov, src));
                powerAvailable += src;
            }
        }

        if (powerAvailable <= 0) return;

        final List<Tuple.ObjectLongPair<ISPKReceiver>> receivers = new ArrayList<>();
        long totalDemand = 0;

        ObjectIterator<Object2LongMap.Entry<ISPKReceiver>> recIt =
                receiverEntries.object2LongEntrySet().fastIterator();

        while (recIt.hasNext()) {
            Object2LongMap.Entry<ISPKReceiver> entry = recIt.next();
            ISPKReceiver rec = entry.getKey();

            if (timestamp - entry.getLongValue() > timeout || isBadLink(rec)) {
                recIt.remove();
                continue;
            }

            long need = Math.min(rec.getMaxSPK() - rec.getSPK(), rec.getSPKReceiverSpeed());
            if (need > 0) {
                receivers.add(new Tuple.ObjectLongPair<>(rec, need));
                totalDemand += need;
            }
        }

        if (totalDemand <= 0) return;

        final long toTransfer = Math.min(powerAvailable, totalDemand);
        long energyUsed = 0L;

        for (Tuple.ObjectLongPair<ISPKReceiver> entry : receivers) {
            ISPKReceiver rec = entry.getKey();
            long want = entry.getValue();
            long toSend = (long) Math.max(((double) toTransfer) * ((double) want / (double) totalDemand), 0D);

            long leftoverFromRecv = rec.transferSPK(toSend, false);
            energyUsed += (toSend - leftoverFromRecv);
        }

        this.energyTracker += energyUsed;

        if (energyUsed <= 0) return;

        long leftover = energyUsed;

        for (Tuple.ObjectLongPair<ISPKProvider> entry : providers) {
            ISPKProvider prov = entry.getKey();
            long canProvide = entry.getValue();

            double weight = (double) canProvide / (double) powerAvailable;
            long toUse = (long) Math.max(energyUsed * weight, 0.0D);

            prov.setTransferredSpk(toUse);
            leftover -= toUse;
        }

        int iterationsLeft = 100;
        while (iterationsLeft > 0 && leftover > 0 && !providers.isEmpty()) {
            iterationsLeft--;

            Tuple.ObjectLongPair<ISPKProvider> selected = providers.get(rand.nextInt(providers.size()));
            ISPKProvider scapegoat = selected.getKey();

            long toUse = Math.min(leftover, scapegoat.getNetRemaining());
            scapegoat.setTransferredSpk(toUse);
            leftover -= toUse;
        }
    }
}
