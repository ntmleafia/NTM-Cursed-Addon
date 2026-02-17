package com.leafia.passive;

import com.leafia.contents.machines.reactors.pwr.PWRDiagnosis;
import com.leafia.contents.machines.reactors.pwr.blocks.wreckage.PWRMeshedWreck;
import com.leafia.dev.LeafiaDebug.Tracker;
import com.leafia.eventbuses.LeafiaServerListener;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LeafiaPassiveServer {
	/// NOTE TO MY DUMBASS: basically ArrayList but thread safe
	static final ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();
	public static void onTick(World world) {
		PWRDiagnosis.preventScan.clear();
		Tracker.postTick(world);
		PWRMeshedWreck.rmCache.clear();
	}
	public static void priorTick(World world) {
		//if (ModItems.wand_leaf.darnit != null)
		//	ModItems.wand_leaf.darnit.run();
		Tracker.preTick(world);
		//LeafiaServerListener.SharpEdges.damageCache.clear();
		List<Runnable> running = new ArrayList<>(queue);
		queue.clear();
		for (Runnable callback : running) {
			if (callback != null) // idk how tf but apparently this happens
			/*
									java.lang.NullPointerException: Exception in server tick loop
									at com.leafia.passive.LeafiaPassiveServer.priorTick(LeafiaPassiveServer.java:26)
									at com.leafia.eventbuses.LeafiaServerListener$HandlerServer.worldTick(LeafiaServerListener.java:49)
			 */
				callback.run();
		}
		//WorldServerLeafia.violatedPositions.clear();
	}
	public static void queueFunction(Runnable callback) {
		queue.add(callback);
	}
}
