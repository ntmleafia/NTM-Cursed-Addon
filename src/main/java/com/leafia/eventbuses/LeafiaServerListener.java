package com.leafia.eventbuses;

import com.hbm.entity.logic.EntityNukeExplosionMK3;
import com.hbm.entity.logic.EntityNukeExplosionMK3.ATEntry;
import com.leafia.unsorted.IEntityCustomCollision;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.event.world.GetCollisionBoxesEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class LeafiaServerListener {
	public static class Unsorted {
		@SubscribeEvent
		public void onGetEntityCollision(GetCollisionBoxesEvent evt) {
			if (evt.getEntity() == null) return;
			List<AxisAlignedBB> list = evt.getCollisionBoxesList();
			List<Entity> list1 = evt.getWorld().getEntitiesWithinAABBExcludingEntity(evt.getEntity(), evt.getAabb().grow((double)0.25F));
			for(int i = 0; i < list1.size(); ++i) {
				Entity entity = (Entity)list1.get(i);
				if (!evt.getEntity().isRidingSameEntity(entity)) {
					if (entity instanceof IEntityCustomCollision) {
						List<AxisAlignedBB> aabbs = ((IEntityCustomCollision)entity).getCollisionBoxes(evt.getEntity());
						if (aabbs == null) continue;
						for (AxisAlignedBB aabb : aabbs) {
							if (aabb != null && aabb.intersects(aabb))
								list.add(aabb);
						}
					}
				}
			}
		}
		/*
		@SubscribeEvent
		public void onBlockNotify(NeighborNotifyEvent evt) {
			if (!evt.getWorld().isRemote) {
				LeafiaDebug.debugPos(evt.getWorld(),evt.getPos(),3,0xFF0000,"NeighborNotifyEvent");
				for (Entry<PWRElementTE,LeafiaSet<BlockPos>> entry : PWRElementTE.listeners.entrySet()) {
					if (entry.getKey().isInvalid()) {
						PWRElementTE.listeners.remove(entry.getKey());
						continue;
					}
					if (entry.getValue().contains(evt.getPos()))
						entry.getKey().updateObstacleMappings();
				}
				for (Entry<PWRVentInletTE,LeafiaSet<BlockPos>> entry : PWRVentInletTE.listeners.entrySet()) {
					if (entry.getKey().isInvalid()) {
						PWRVentInletTE.listeners.remove(entry.getKey());
						continue;
					}
					if (entry.getValue().contains(evt.getPos()))
						entry.getKey().rebuildMap();
				}
			}
		}*/
		@SubscribeEvent
		public void worldInit(Load evt) {
			List<ATEntry> entries = new ArrayList<>(EntityNukeExplosionMK3.at.keySet());
			for (ATEntry entry : entries) {
				if (entry.dim == evt.getWorld().provider.getDimension())
					EntityNukeExplosionMK3.at.remove(entry);
			}
		}
	}
	/*
	public static class Fluids {
		@SubscribeEvent
		public void filled(FluidFillingEvent evt) {
			LeafiaDebug.debugLog(evt.getWorld(),"SCREW YOU! "+evt.getClass().getSimpleName());
			//LeafiaDebug.debugPos(evt.getWorld(),evt.getPos(),3,0x00CCFF,evt.getClass().getSimpleName(),evt.getFluid().getFluid().getName());
		}
		@SubscribeEvent
		public void spilled(FluidSpilledEvent evt) {
			LeafiaDebug.debugLog(evt.getWorld(),"SCREW YOU! "+evt.getClass().getSimpleName());
			//LeafiaDebug.debugPos(evt.getWorld(),evt.getPos(),3,0x00CCFF,evt.getClass().getSimpleName(),evt.getFluid().getFluid().getName());
		}
		@SubscribeEvent
		public void moved(FluidMotionEvent evt) {
			LeafiaDebug.debugLog(evt.getWorld(),"SCREW YOU! "+evt.getClass().getSimpleName());
			//LeafiaDebug.debugPos(evt.getWorld(),evt.getPos(),3,0x00CCFF,evt.getClass().getSimpleName(),evt.getFluid().getFluid().getName());
		}
	}*/
}
