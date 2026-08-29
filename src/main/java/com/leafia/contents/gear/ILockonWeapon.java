package com.leafia.contents.gear;

import com.custom_hbm.util.LCETuple.Pair;
import com.leafia.dev.custompacket.LeafiaCustomPacket;
import com.leafia.dev.custompacket.LeafiaCustomPacketEncoder;
import com.leafia.dev.math.FiaMatrix;
import com.leafia.dev.optimization.bitbyte.LeafiaBuf;
import com.leafia.init.LeafiaSoundEvents;
import com.leafia.passive.LeafiaPassiveServer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public interface ILockonWeapon {
	boolean canLockon(ItemStack stack);
	double maxInclination(ItemStack stack);
	default boolean freezeTick(ItemStack stack) {
		return false;
	}
	static Entity getLoadedLockon(EntityPlayer player) {
		Pair<Entity,Long> pair = GetLockonPacket.lockons.get(player);
		if (pair != null)
			return pair.getA();
		return null;
	}
	class GetLockonPacket implements LeafiaCustomPacketEncoder {
		public static Map<EntityPlayer,Pair<Entity,Long>> lockons = new HashMap<>();
		Entity target;
		public GetLockonPacket() { }
		public GetLockonPacket(Entity target) {
			this.target = target;
		}
		@Override
		public void encode(LeafiaBuf buf) {
			// doesnt matter for server->client packet as it would always have no target set
			if (target != null) {
				buf.writeBoolean(true);
				UUID uuid = target.getUniqueID();
				buf.writeLong(uuid.getMostSignificantBits());
				buf.writeLong(uuid.getLeastSignificantBits());
			} else
				buf.writeBoolean(false);
		}
		@Override
		public @Nullable Consumer<MessageContext> decode(LeafiaBuf buf) {
			return (ctx)->{
				if (ctx.side == Side.CLIENT)
					LeafiaCustomPacket.__start(new GetLockonPacket(LockonHandler.timer >= 32 ? LockonHandler.target : null)).__sendToServer();
				else {
					LeafiaPassiveServer.queueFunction(()->{
						EntityPlayer plr = ctx.getServerHandler().player;
						if (buf.readBoolean()) {
							World world = plr.world;
							UUID uuid = new UUID(buf.readLong(),buf.readLong());
							for (Entity entity : world.loadedEntityList) {
								if (entity.getUniqueID().equals(uuid)) {
									lockons.put(plr,new Pair<>(entity,System.currentTimeMillis()));
									return;
								}
							}
							lockons.remove(plr);
						} else
							lockons.remove(plr);
					});
				}
			};
		}
	}
	class LockonHandler {
		public static Entity target = null;
		public static boolean locked = false;
		public static int timer = 0;
		@SideOnly(Side.CLIENT)
		public static void handleLockon(EntityPlayer player,World world) {
			ItemStack stack = player.getHeldItemMainhand();
			if (stack.getItem() instanceof ILockonWeapon weapon2 && weapon2.freezeTick(stack)) return;
			if (stack.getItem() instanceof ILockonWeapon weapon && weapon.canLockon(stack)) {
				Vec3d plrVec = new Vec3d(player.posX,player.posY+player.getEyeHeight(),player.posZ);
				FiaMatrix face = new FiaMatrix(plrVec,plrVec.add(player.getLookVec()));

				Entity closest = null;
				Entity pivot = player;
				if (target != null) pivot = target;
				Vec3d pivotVec = new Vec3d(pivot.posX,pivot.posY+pivot.getEyeHeight(),pivot.posZ);
				FiaMatrix pivotRelative = face.toObjectSpace(new FiaMatrix(pivotVec));
				double closestDist = Double.MAX_VALUE;

				boolean currentValid = false;

				for (Entity entity : world.loadedEntityList) {
					if (entity == player) continue;
					Vec3d tgtVec = new Vec3d(entity.posX,entity.posY+entity.getEyeHeight(),entity.posZ);
					FiaMatrix relative = face.toObjectSpace(new FiaMatrix(tgtVec));
					if (relative.getZ() < 0) {
						double depth = -relative.getZ();
						double x = relative.getX()/depth;
						double y = relative.getY()/depth;
						double maxInclination = weapon.maxInclination(stack);
						if (Math.abs(x) <= maxInclination && Math.abs(y) <= maxInclination) {
							RayTraceResult res = world.rayTraceBlocks(plrVec,tgtVec);
							if (res != null && res.typeOfHit != Type.MISS) continue;

							if (entity == target) currentValid = true;

							double dist = tgtVec.distanceTo(face.translate(pivotRelative.getX(),pivotRelative.getY(),-depth).position);
							if (dist < closestDist) {
								closestDist = dist;
								closest = entity;
							}
						}
					}
				}
				if (!currentValid) {
					target = closest;
					timer = 0;
				}

				if (target != null) {
					if (timer%2 == 0) {
						int t = timer/2;
						if (t < 8) {
							if (t%2 == 0)
								beep(player,world,LeafiaSoundEvents.lockon0);
						} else if (t < 16)
							beep(player,world,LeafiaSoundEvents.lockon1);
						else if (t == 16)
							beep(player,world,LeafiaSoundEvents.lockon2);
					}
					timer++;
				}
			} else
				target = null;
		}
		public static void beep(EntityPlayer player,World world,SoundEvent evt) {
			world.playSound(player,(float)player.posX,(float)player.posY,(float)player.posZ,evt,SoundCategory.MASTER,0.5f,1);
		}
	}
}
