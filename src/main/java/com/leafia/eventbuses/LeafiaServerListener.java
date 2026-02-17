package com.leafia.eventbuses;

import com.hbm.blocks.ModBlocks;
import com.hbm.entity.logic.EntityNukeExplosionMK3;
import com.hbm.entity.logic.EntityNukeExplosionMK3.ATEntry;
import com.hbm.hazard.HazardEntry;
import com.hbm.hazard.HazardSystem;
import com.hbm.lib.HBMSoundHandler;
import com.leafia.contents.machines.reactors.pwr.PWRDiagnosis;
import com.leafia.contents.potion.LeafiaPotion;
import com.leafia.init.hazards.types.HazardTypeSharpEdges;
import com.leafia.dev.optimization.LeafiaParticlePacket;
import com.leafia.dev.optimization.LeafiaParticlePacket.Sweat;
import com.leafia.init.LeafiaSoundEvents;
import com.leafia.passive.LeafiaPassiveServer;
import com.leafia.unsorted.IEntityCustomCollision;
import com.leafia.init.LeafiaDamageSource;
import com.leafia.unsorted.LeafiaBlockReplacer;
import com.llib.exceptions.LeafiaDevFlaw;
import com.llib.group.LeafiaMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IntIdentityHashBiMap;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.BlockStatePaletteHashMap;
import net.minecraft.world.chunk.BlockStatePaletteLinear;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IBlockStatePalette;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.GetCollisionBoxesEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class LeafiaServerListener {
	public static class HandlerServer {
		static Field wasPickedUp;
		static {
			try {
				wasPickedUp = EntityItem.class.getDeclaredField("addon_wasPickedUp");
			} catch (NoSuchFieldException e) {
				throw new LeafiaDevFlaw(e);
			}
		}
		@SubscribeEvent
		public void replaceBlocks(ChunkEvent.Load evt) {
			Chunk chunk = evt.getChunk();
			for (ExtendedBlockStorage storage : chunk.getBlockStorageArray())
				replacePalette(storage);
		}
		public static void replacePalette(ExtendedBlockStorage storage) {
			if (storage == null || storage.data == null || storage.data.palette == null) return;
			IBlockStatePalette palette = storage.data.palette;
			if (palette instanceof BlockStatePaletteHashMap map) {
				for (IBlockState state : map.statePaletteMap) {
					try {
						//System.out.println("STATE/HASH: "+state);
						if (state.getBlock().getClass().getSimpleName().equals("BlockDummyAir")) {
							int id = map.statePaletteMap.getId(state);
							map.statePaletteMap.put(LeafiaBlockReplacer.replaceBlock(state),id);
						}
					} catch (NullPointerException ignored) {}
				}
			} else if (palette instanceof BlockStatePaletteLinear linear) {
				for (int i = 0; i < linear.states.length; i++) {
					try {
						//System.out.println("STATE/LINEAR: "+linear.states[i]);
						if (linear.states[i].getBlock().getClass().getSimpleName().equals("BlockDummyAir"))
							linear.states[i] = LeafiaBlockReplacer.replaceBlock(linear.states[i]);
					} catch (NullPointerException ignored) {}
				}
			} else {
				throw new LeafiaDevFlaw("Impossible case ("+palette.getClass().getName()+")");
			}
		}
		@SubscribeEvent
		public void onEntityItemPickup(EntityItemPickupEvent evt) {
			try {
				wasPickedUp.set(evt.getItem(),true);
			} catch (IllegalAccessException e) {
				throw new LeafiaDevFlaw(e);
			}
		}
		@SubscribeEvent
		public void worldTick(WorldTickEvent evt) {
			if (evt.world != null && !evt.world.isRemote) {
				if (evt.phase == Phase.START)
					LeafiaPassiveServer.priorTick(evt.world);
				if(evt.world.getTotalWorldTime() % 100 == 97) {
					PWRDiagnosis.cleanup();
				}
				if (evt.phase == Phase.END)
					LeafiaPassiveServer.onTick(evt.world);
			}
		}
		@SubscribeEvent
		public void soundRegistering(RegistryEvent.Register<SoundEvent> evt) {
			for (SoundEvent e : LeafiaSoundEvents.ALL_SOUNDS)
				evt.getRegistry().register(e);
		}
	}
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
		@SubscribeEvent
		public void onEntityHurt(LivingDamageEvent evt) {
			DamageSource src = evt.getSource();
			EntityLivingBase entity = evt.getEntityLiving();
			Random rng = entity.getRNG();
			if (src.isFireDamage()) {
				if (rng.nextInt(5+5*LeafiaPotion.getSkinDamage(entity)/*HbmPotion.getSkinDamage(entity)*/) == 0)
					LeafiaPotion.hurtSkin(entity,3);
			}
		}
	}
	public static class SharpEdges {
		public static LeafiaMap<Entity,Float> damageCache = new LeafiaMap<>();
		@SubscribeEvent
		public void onEntityHurt(LivingDamageEvent evt) {
			DamageSource src = evt.getSource();
			if (!src.equals(LeafiaDamageSource.pointed))
				damageCache.put(evt.getEntity(),evt.getAmount());
			if (src.equals(DamageSource.FALL))
				sharpDamageEntity(evt.getEntity(),evt.getAmount(),getItems(evt.getEntity()));
		}
		@SubscribeEvent
		public void onEntityHit(LivingAttackEvent evt) {
			Entity attacker = evt.getSource().getImmediateSource();
			if (attacker != null) {
				List<ItemStack> stacks = new ArrayList<>();
				for (ItemStack stack : attacker.getHeldEquipment())
					stacks.add(stack);
				sharpDamageEntity(evt.getEntity(),evt.getAmount(),stacks);
			}
		}
		@SubscribeEvent
		public void onEntityKnockback(LivingKnockBackEvent evt) {
			if (evt.getStrength() > 0) {
				if (damageCache.containsKey(evt.getEntity())) {
					float damage = damageCache.get(evt.getEntity());
					sharpDamageEntity(evt.getEntity(),damage,getItems(evt.getEntity()));
				}
			}
		}
		@SubscribeEvent
		public void onItemPickup(EntityItemPickupEvent evt) {
			sharpDamageEntity(evt.getEntity(),1,Collections.singletonList(evt.getItem().getItem()));
		}
		List<ItemStack> getItems(Entity entity) {
			List<ItemStack> stacks = new ArrayList<>();
			if (entity instanceof EntityPlayer) {
				InventoryPlayer inventory = ((EntityPlayer)entity).inventory;
				for (int i = 0; i < inventory.getSizeInventory(); i++)
					stacks.add(inventory.getStackInSlot(i));
			} else {
				for (ItemStack stack : entity.getEquipmentAndArmor())
					stacks.add(stack);
			}
			return stacks;
		}
		public void sharpDamageEntity(Entity entity,float baseDamage,List<ItemStack> stacks) {
			float modifier = 0;
			float max = 0;
			for (ItemStack stack : stacks) {
				if (!stack.isEmpty()) {
					List<HazardEntry> hazards = HazardSystem.getHazardsFromStack(stack);
					for (HazardEntry hazard : hazards) {
						if (hazard.type instanceof HazardTypeSharpEdges) {
							modifier += (float) (hazard.baseLevel/100*stack.getCount());
							max = Math.max(max,(float)hazard.baseLevel/100);
						}
					}
				}
			}
			float additionalDamage = baseDamage*(modifier*(1-HazardTypeSharpEdges.sharpStackNerf)+max*HazardTypeSharpEdges.sharpStackNerf);
			if (additionalDamage > 0) {
				LeafiaPassiveServer.queueFunction(()->{
					if (entity.world == null) return;
					entity.world.playSound(null,entity.getPosition(),HBMSoundHandler.blood_splat,SoundCategory.MASTER,0.25f,entity.world.rand.nextFloat()*0.2f+0.9f);
					entity.world.playSound(null,entity.getPosition(),LeafiaSoundEvents.pointed,SoundCategory.MASTER,0.25f,entity.world.rand.nextFloat()*0.2f+0.9f);
					LeafiaParticlePacket.Sweat particle = new Sweat(entity,Blocks.REDSTONE_BLOCK.getDefaultState(),entity.world.rand.nextInt(4)+2);
					particle.emit(new Vec3d(entity.posX,entity.posY,entity.posZ),Vec3d.ZERO,entity.dimension);
					entity.hurtResistantTime = 0;
					entity.attackEntityFrom(LeafiaDamageSource.pointed,additionalDamage);
				});
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
