package com.leafia.eventbuses;

import com.hbm.interfaces.IHoldableWeapon;
import com.hbm.items.weapon.ItemGunShotty;
import com.leafia.contents.gear.IADSWeapon;
import com.leafia.unsorted.IEntityCustomCollision;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.world.GetCollisionBoxesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public class LeafiaClientListener {
	public static class HandlerClient {
		public static float getViewADS(EntityPlayer player) {
			if (player.isSneaking()) {
				boolean canADS = true;
				ItemStack main = player.getHeldItemMainhand();
				ItemStack sub = player.getHeldItemOffhand();
				if ((!main.isEmpty() || !sub.isEmpty()) && (main.isEmpty() != sub.isEmpty())) {
					Item holding = main.isEmpty() ? sub.getItem() : main.getItem();
					if (holding instanceof IADSWeapon weapon) {
						if (weapon.getADS() != 1f)
							return weapon.getADS()*(main.isEmpty()  ? -1 : 1)*(player.getPrimaryHand().equals(EnumHandSide.RIGHT) ? 1 : -1);
					}
				}
			}
			return 0;
		}
		@SubscribeEvent
		public void fovUpdate(FOVUpdateEvent e){
			EntityPlayer player = e.getEntity();
			float multiplier = 1.0F;
			//if(player.getHeldItemMainhand().getItem() == Armory.gun_supershotgun && ItemGunShotty.hasHookedEntity(player.world, player.getHeldItemMainhand())) {
			//	multiplier *= 1.1F;
			//}
			float viewADS = getViewADS(player);
			if (viewADS != 0)
				multiplier *= Math.abs(viewADS);
			//multiplier *= IdkWhereThisShitBelongs.fovM;
			e.setNewfov(e.getFov()*multiplier);
		}
	}
	public static class Unsorted {
		/**
		 * Thank you forge for naming it like this
		 * <p>Yes, {@link RenderGameOverlayEvent.Text} is the event solely for debug screen, despite the radically confusing name just "Text".
		 * <p>Good job, forge. I'll kindly prepare 9800 schrabidium missiles to serve you.
		 */
		@SubscribeEvent
		public void dammit(RenderGameOverlayEvent.Text debug) {
			//LeafiaGeneralLocal.injectDebugInfoLeft(debug.getLeft());
		}

		@SubscribeEvent
		public void onGetEntityCollision(GetCollisionBoxesEvent evt) {
			if (evt.getEntity() == null) return;
			List<AxisAlignedBB> list = evt.getCollisionBoxesList();
			List<Entity> list1 = evt.getWorld().getEntitiesWithinAABBExcludingEntity(evt.getEntity(), evt.getAabb().grow((double)02.25F));
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
	}
	public static class Fluids {
		/*@SubscribeEvent
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
		}*/
	}
}
