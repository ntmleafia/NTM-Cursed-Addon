// SPDX-FileCopyrightText: 2025-2026 HbmMods (Bob/Boblet) <hbmmods@gmail.com>
// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-FileCopyrightText: 2025-2026 movblock <admin@movblock.mov>
// SPDX-FileCopyrightText: 2025 Vidarin <smeds.vidar@gmail.com>
// SPDX-License-Identifier: LGPL-3.0-only

package com.leafia.contents.network.pipe_amat.uninos;

import com.hbm.api.energymk2.IEnergyReceiverMK2.ConnectionPriority;
import com.hbm.api.fluidmk2.IFluidProviderMK2;
import com.hbm.api.fluidmk2.IFluidReceiverMK2;
import com.hbm.api.fluidmk2.IFluidUserMK2;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.trait.FluidTraitSimple.FT_Amat;
import com.hbm.uninos.INetworkProvider;
import com.hbm.uninos.NodeNet;
import com.hbm.util.Tuple;
import com.hbm.util.Tuple.ObjectLongPair;
import com.leafia.contents.fluids.traits.FT_Magnetic;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AmatNet extends NodeNet<IFluidReceiverMK2,IFluidProviderMK2,AmatNode,AmatNet> {
	public static long consumption = 1000/20;
	public static long maxPower = consumption*20;
	public long power = maxPower;
	public static final Map<Integer,INetworkProvider<AmatNet>> PROVIDERS = new HashMap<>();
	public static INetworkProvider<AmatNet> getProvider(FluidType type) {
		if (PROVIDERS.containsKey(type.getID()))
			return PROVIDERS.get(type.getID());
		INetworkProvider<AmatNet> provider = () -> new AmatNet(type);
		PROVIDERS.put(type.getID(),provider);
		return provider;
	}
	private boolean isConnectedToAnywhere(IFluidProviderMK2 provider) {
		ObjectIterator<Entry<IFluidReceiverMK2>> iterator = receiverEntries.object2LongEntrySet().fastIterator();
		while (iterator.hasNext()) {
			Object2LongMap.Entry<IFluidReceiverMK2> entry = iterator.next();
			if (entry.getKey() != provider)
				return true;
		}
		return false;
	}
	public boolean checkExplode(IFluidProviderMK2 provider) {
		if (!type.hasTrait(FT_Amat.class)) {
			if (power <= 0 || !type.hasTrait(FT_Magnetic.class)) return true;
		}
		if (!isConnectedToAnywhere(provider)) return false;
		if (power > 0 && type.hasTrait(FT_Magnetic.class)) return false;
		if (provider instanceof TileEntity te) {
			// fuck off te.getWorld().newExplosion(null,te.getPos().getX(),te.getPos().getY(),te.getPos().getZ(),5,true,true);
			return true;
		}
		return false;
	}

	// this entire shit below is copy-pasted
	public long fluidTracker = 0L;

	protected static int timeout = 3_000;
	protected long currentTime = 0;
	protected FluidType type;

	public AmatNet(FluidType type) {
		this.type = type;
		for(int i = 0; i < IFluidUserMK2.HIGHEST_VALID_PRESSURE + 1; i++) providers[i] = new ArrayList<>();
		for(int i = 0; i < IFluidUserMK2.HIGHEST_VALID_PRESSURE + 1; i++) for(int j = 0; j < ConnectionPriority.VALUES.length; j++) receivers[i][j] = new ArrayList<>();
	}

	@Override public void resetTrackers() { this.fluidTracker = 0; }

	@Override
	public void update() {
		power = Math.max(power-consumption,0);

		if(providerEntries.isEmpty()) return;
		if(receiverEntries.isEmpty()) return;
		currentTime = System.currentTimeMillis();

		setupFluidProviders();
		setupFluidReceivers();
		transferFluid();

		cleanUp();
	}

	//this sucks ass, but it makes the code just a smidge more structured
	public long[] fluidAvailable = new long[IFluidUserMK2.HIGHEST_VALID_PRESSURE + 1];
	public List<ObjectLongPair<IFluidProviderMK2>>[] providers = new ArrayList[IFluidUserMK2.HIGHEST_VALID_PRESSURE + 1];
	public long[][] fluidDemand = new long[IFluidUserMK2.HIGHEST_VALID_PRESSURE + 1][ConnectionPriority.VALUES.length];
	public List<Tuple.ObjectLongPair<IFluidReceiverMK2>>[][] receivers = new ArrayList[IFluidUserMK2.HIGHEST_VALID_PRESSURE + 1][ConnectionPriority.VALUES.length];
	public long[] transfered = new long[IFluidUserMK2.HIGHEST_VALID_PRESSURE + 1];
	private final long[] received = new long[IFluidUserMK2.HIGHEST_VALID_PRESSURE + 1];
	private final long[] notAccountedFor = new long[IFluidUserMK2.HIGHEST_VALID_PRESSURE + 1];
	private static long boundedTotal(long current, long addition) {
		return addition > Long.MAX_VALUE - current ? Long.MAX_VALUE : current + addition;
	}
	private long debitProvider(IFluidProviderMK2 provider, int pressure, long requested, boolean notifyZero) {
		long available = provider.getFluidAvailable(type, pressure);
		if (requested < 0 || available < 0) {
			throw new IllegalStateException("Negative fluid debit or availability at pressure " + pressure);
		}
		long debit = Math.min(requested, available);
		if (debit > 0 || notifyZero) provider.useUpFluid(type, pressure, debit);
		return debit;
	}

	public void setupFluidProviders() {
		ObjectIterator<Entry<IFluidProviderMK2>> iterator = providerEntries.object2LongEntrySet().fastIterator();
		while(iterator.hasNext()) {
			Object2LongMap.Entry<IFluidProviderMK2> entry = iterator.next();
			if(currentTime - entry.getLongValue() > timeout || isBadLink(entry.getKey()) || checkExplode(entry.getKey())) {
				iterator.remove(); continue;
			}
			IFluidProviderMK2 provider = entry.getKey();
			int[] pressureRange = provider.getProvidingPressureRange(type);
			for(int p = pressureRange[0]; p <= pressureRange[1]; p++) {
				long available = Math.min(provider.getFluidAvailable(type, p), provider.getProviderSpeed(type, p));
				providers[p].add(new Tuple.ObjectLongPair<>(provider, available));
				fluidAvailable[p] = boundedTotal(fluidAvailable[p], available);
			}
		}
	}

	public void setupFluidReceivers() {
		ObjectIterator<Object2LongMap.Entry<IFluidReceiverMK2>> iterator = receiverEntries.object2LongEntrySet().fastIterator();

		while(iterator.hasNext()) {
			Object2LongMap.Entry<IFluidReceiverMK2> entry = iterator.next();
			if(currentTime - entry.getLongValue() > timeout || isBadLink(entry.getKey())) { iterator.remove(); continue; }
			IFluidReceiverMK2 receiver = entry.getKey();
			int[] pressureRange = receiver.getReceivingPressureRange(type);
			for(int p = pressureRange[0]; p <= pressureRange[1]; p++) {
				long required = Math.min(receiver.getDemand(type, p), receiver.getReceiverSpeed(type, p));
				int priority = receiver.getFluidPriority().ordinal();
				receivers[p][priority].add(new Tuple.ObjectLongPair(receiver, required));
				fluidDemand[p][priority] = boundedTotal(fluidDemand[p][priority], required);
			}
		}
	}

	public void transferFluid() {

		Arrays.fill(received, 0L);
		Arrays.fill(notAccountedFor, 0L);

		for(int p = 0; p <= IFluidUserMK2.HIGHEST_VALID_PRESSURE; p++) { // if the pressure range were ever to increase, we might have to rethink this

			long totalAvailable = fluidAvailable[p];

			for(int i = ConnectionPriority.VALUES.length - 1; i >= 0; i--) {

				long toTransfer = Math.min(fluidDemand[p][i], totalAvailable);
				if(toTransfer <= 0) continue;

				long priorityDemand = fluidDemand[p][i];
				long receivedBeforePriority = received[p];

				for(Tuple.ObjectLongPair<IFluidReceiverMK2> entry : receivers[p][i]) {
					double weight = (double) entry.getValue() / (double) (priorityDemand);
					long toSend = (long) Math.max(toTransfer * weight, 0D);
					long remaining = toTransfer - (received[p] - receivedBeforePriority);
					toSend = Math.min(toSend, Math.min(entry.getValue(), remaining));
					toSend -= entry.getKey().transferFluid(type, p, toSend);
					received[p] += toSend;
					fluidTracker = boundedTotal(fluidTracker, toSend);
				}

				totalAvailable -= received[p] - receivedBeforePriority;
			}

			notAccountedFor[p] = received[p];
		}

		for(int p = 0; p <= IFluidUserMK2.HIGHEST_VALID_PRESSURE; p++) {

			for(Tuple.ObjectLongPair<IFluidProviderMK2> entry : providers[p]) {
				double weight = (double) entry.getValue() / (double) fluidAvailable[p];
				long toUse = (long) Math.max(received[p] * weight, 0D);
				toUse = Math.min(toUse, Math.min(entry.getValue(), notAccountedFor[p]));
				notAccountedFor[p] -= debitProvider(entry.getKey(), p, toUse, true);
			}
		}

		for(int p = 0; p <= IFluidUserMK2.HIGHEST_VALID_PRESSURE; p++) {

			int iterationsLeft = 100;
			while(iterationsLeft > 0 && notAccountedFor[p] > 0 && !providers[p].isEmpty()) {
				iterationsLeft--;

				Tuple.ObjectLongPair<IFluidProviderMK2> selected = providers[p].get(rand.nextInt(providers[p].size()));
				IFluidProviderMK2 scapegoat = selected.getKey();

				notAccountedFor[p] -= debitProvider(scapegoat, p, notAccountedFor[p], true);
			}

			// Keep the randomized distribution, then fund a remainder it failed to reach.
			for (var provider : providers[p]) {
				if (notAccountedFor[p] == 0) break;
				notAccountedFor[p] -= debitProvider(provider.getKey(), p, notAccountedFor[p], false);
			}
			if (notAccountedFor[p] != 0) {
				throw new IllegalStateException("Fluid network could not debit " + notAccountedFor[p]
						+ " of " + received[p] + " delivered units at pressure " + p);
			}
		}
	}

	public void cleanUp() {
		for(int i = 0; i < IFluidUserMK2.HIGHEST_VALID_PRESSURE + 1; i++) {
			fluidAvailable[i] = 0;
			providers[i].clear();
			transfered[i] = 0;

			for(int j = 0; j < ConnectionPriority.VALUES.length; j++) {
				fluidDemand[i][j] = 0;
				receivers[i][j].clear();
			}
		}
	}
}
