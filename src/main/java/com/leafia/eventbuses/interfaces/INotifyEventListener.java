package com.leafia.eventbuses.interfaces;

import com.leafia.eventbuses.LeafiaServerListener.Unsorted;
import net.minecraftforge.event.world.BlockEvent.NeighborNotifyEvent;

public interface INotifyEventListener {
	void onBlockNotify(NeighborNotifyEvent evt);
	default void subscribeToBlockNotification() {
		Unsorted.notifyEventListeners.put(this,System.currentTimeMillis());
	}
}
