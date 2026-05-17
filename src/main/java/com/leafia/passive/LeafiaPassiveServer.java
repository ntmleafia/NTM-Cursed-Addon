package com.leafia.passive;

import com.hbm.inventory.control_panel.nodes.Node;
import com.leafia.contents.AddonItems;
import com.leafia.contents.machines.reactors.pwr.PWRDiagnosis;
import com.leafia.contents.machines.reactors.pwr.blocks.wreckage.PWRMeshedWreck;
import com.leafia.dev.LeafiaDebug.Tracker;
import com.leafia.eventbuses.LeafiaServerListener.Unsorted;
import com.leafia.savedata.FalloutSavedData;
import com.leafia.unsorted.StructuralIntegrityHandler;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LeafiaPassiveServer {
	/// NOTE TO MY DUMBASS: basically ArrayList but thread safe
	static final ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();
	public static void onTick() {
		if (AddonItems.wand_leaf.darnit != null)
			AddonItems.wand_leaf.darnit.run();
		PWRDiagnosis.preventScan.clear();
		PWRMeshedWreck.rmCache.clear();
		Unsorted.digammaRainCounter = (Unsorted.digammaRainCounter+1)%70;
	}
	public static void onTickWorld(World world) {
		Tracker.postTick(world);
	}
	public static final Set<Node> tickedNodes = new HashSet<>();
	public static void priorTick() {
		StructuralIntegrityHandler.blockedPoses.clear();
		StructuralIntegrityHandler.calculations = 0;
		tickedNodes.clear();
		//if (ModItems.wand_leaf.darnit != null)
		//	ModItems.wand_leaf.darnit.run();
		//LeafiaServerListener.SharpEdges.damageCache.clear();
		Wind.update();
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
	public static void priorTickWorld(World world) {
		FalloutSavedData.forWorld(world).tick();
		Tracker.preTick(world);
		Wind.updateWorld(world);
	}
	public static void queueFunction(Runnable callback) {
		queue.add(callback);
	}
}
