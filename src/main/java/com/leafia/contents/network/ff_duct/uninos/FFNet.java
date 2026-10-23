// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.network.ff_duct.uninos;

import com.hbm.uninos.INetworkProvider;
import com.hbm.uninos.NodeNet;
import com.leafia.contents.network.FFNBT;
import com.leafia.dev.LeafiaDebug;
import com.leafia.dev.LeafiaUtil;
import com.leafia.passive.LeafiaPassiveServer;
import com.llib.exceptions.LeafiaDevFlaw;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidTank;

import java.util.*;

public class FFNet extends NodeNet<IFFReceiver,IFFProvider,FFNode,FFNet> {
	public static final INetworkProvider<FFNet> PROVIDER = FFNet::new;
	Map<IFFProvider,Set<FluidTank>> tankMap = new HashMap<>();
	public void addTank(IFFProvider prov,FluidTank tank) {
		tankMap.putIfAbsent(prov,new HashSet<>());
		tankMap.get(prov).add(tank);
	}
	protected static int timeout = 3_000;
	public boolean scheduled = false;
	@Override
	public void update() {
		if (scheduled) return;
		//scheduled = true; this made it buggier
		LeafiaPassiveServer.queueFunction(()->{ // bullshit threading, so here we are
			scheduled = false;
			if (providerEntries.isEmpty()) { tankMap.clear(); return; }
			if (receiverEntries.isEmpty()) { tankMap.clear(); return; }
			final long timestamp = System.currentTimeMillis();
			// i got tired and rushed, have this shitty algorithm
			ObjectIterator<Entry<IFFProvider>> provIt = providerEntries.object2LongEntrySet().fastIterator();
			while (provIt.hasNext()) {
				Object2LongMap.Entry<IFFProvider> entry = provIt.next();
				IFFProvider prov = entry.getKey();
				if (timestamp - entry.getLongValue() > timeout || isBadLink(prov)) {
					provIt.remove();
					continue;
				}
				Set<FluidTank> tanks = tankMap.get(prov);
				if (tanks != null) {
					for (FluidTank tank : tanks) {
						/*{
							ObjectIterator<Object2LongMap.Entry<IFFReceiver>> recIt = receiverEntries.object2LongEntrySet().fastIterator();
							int totalDemand = 0;
							while (recIt.hasNext()) {
								Object2LongMap.Entry<IFFReceiver> entry1 = recIt.next();
								IFFReceiver rec = entry1.getKey();
								if (timestamp-entry1.getLongValue() > timeout || isBadLink(rec)) {
									recIt.remove();
									continue;
								}
								if (prov instanceof TileEntity te) {
									if (te.getWorld() != null) {
										World world = te.getWorld();
										LeafiaDebug.debugPos(world,te.getPos().up(2),0.05f,0x00FFFF,"Tank ID: "+tank.toString(),"","","",tank.getFluidAmount()+"mB");
									}
								}
								if (rec instanceof TileEntity te) {
									if (te.getWorld() != null) {
										World world = te.getWorld();
										LeafiaDebug.debugPos(world,te.getPos(),0.05f,0xFF00FF,"Tank ID: "+tank.toString(),"","","",tank.getFluidAmount()+"mB");
									}
								}
							}
						}*/


						if (tank == null) continue;
						if (tank.getFluid() == null || tank.getFluidAmount() <= 0) continue;
						final List<IFFReceiver> receivers = new ArrayList<>();
						ObjectIterator<Object2LongMap.Entry<IFFReceiver>> recIt = receiverEntries.object2LongEntrySet().fastIterator();
						int totalDemand = 0;
						while (recIt.hasNext()) {
							Object2LongMap.Entry<IFFReceiver> entry1 = recIt.next();
							IFFReceiver rec = entry1.getKey();
							if (timestamp - entry1.getLongValue() > timeout || isBadLink(rec)) {
								recIt.remove();
								continue;
							}
							if (rec.equals(prov)) continue;
							/*if (prov instanceof TileEntity te) {
								if (te.getWorld() != null) {
									World world = te.getWorld();
									LeafiaDebug.debugPos(world,te.getPos(),0.05f,0x00FFFF,"PROVIDER","","REGISTERED");
								}
							}
							if (rec instanceof TileEntity te) {
								if (te.getWorld() != null) {
									World world = te.getWorld();
									LeafiaDebug.debugPos(world,te.getPos(),0.05f,0xFF00FF,"RECEIVER","","REGISTERED");
								}
							}*/
							FluidTank receiving = rec.getCorrespondingTank(tank.getFluid());
							if (receiving == null) continue;
							if (receiving.equals(tank)) continue;
							if (receiving.getFluid() != null && !receiving.getFluid().getFluid().equals(tank.getFluid().getFluid())) continue;
							if (FFNBT.areTagsCompatible(tank.getFluid(),receiving)) {
								int demand = receiving.getCapacity()-receiving.getFluidAmount();
								totalDemand += demand;
								if (demand > 0)
									receivers.add(rec);
							}
						}
						int totalAmt = tank.getFluidAmount();
						for (IFFReceiver rec : receivers) {
							FluidTank receiving = rec.getCorrespondingTank(tank.getFluid());
							int demand = receiving.getCapacity()-receiving.getFluidAmount();
							float ratio = demand/(float)totalDemand;
							int toTransfer = Math.min(demand,(int)Math.ceil(totalAmt*ratio)); // just to be safe
							if (toTransfer > 0) {
								/*if (prov instanceof TileEntity te) {
									if (te.getWorld() != null) {
										World world = te.getWorld();
										LeafiaDebug.debugPos(world,te.getPos(),0.05f,0x00FFFF,"PROVIDER",toTransfer+"mB","");
									}
								}
								if (rec instanceof TileEntity te) {
									if (te.getWorld() != null) {
										World world = te.getWorld();
										LeafiaDebug.debugPos(world,te.getPos(),0.05f,0xFF00FF,"RECEIVER",toTransfer+"mB","");
									}
								}*/
								// at this point, transferring is confirmed, no turning back
								int sent = LeafiaUtil.fillFF(tank,receiving,toTransfer);
								// VV removed this because it now ceil()s the value instead of flooring it
								//if (sent != toTransfer)
									//throw new LeafiaDevFlaw("net: confirmed transfer amount ("+toTransfer+"mB) and actual amount transferred ("+sent+"mB) doesn't match, wtf");
							}
						}
					}
				}
			}
			tankMap.clear();
		});
	}
}
