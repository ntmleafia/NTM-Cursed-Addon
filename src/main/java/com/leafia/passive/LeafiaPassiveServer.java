package com.leafia.passive;

import com.hbm.inventory.control_panel.nodes.Node;
import com.hbm.saveddata.TomSaveData;
import com.leafia.contents.AddonItems;
import com.leafia.contents.gear.ILockonWeapon.GetLockonPacket;
import com.leafia.contents.machines.reactors.pwr.PWRDiagnosis;
import com.leafia.contents.machines.reactors.pwr.blocks.wreckage.PWRMeshedWreck;
import com.leafia.contents.potion.LeafiaPotion;
import com.leafia.database.ImpactSeismic;
import com.leafia.dev.LeafiaDebug.Tracker;
import com.leafia.dev.custompacket.LeafiaCustomPacket;
import com.leafia.dev.custompacket.LeafiaCustomPacketEncoder;
import com.leafia.dev.optimization.bitbyte.LeafiaBuf;
import com.leafia.eventbuses.LeafiaServerListener.Unsorted;
import com.leafia.overwrite_contents.interfaces.IMixinTomSaveData;
import com.leafia.savedata.FalloutSavedData;
import com.leafia.unsorted.StructuralIntegrityHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

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
		List<EntityPlayer> players = new ArrayList<>(GetLockonPacket.lockons.keySet());
		for (EntityPlayer player : players) {
			if (System.currentTimeMillis() > GetLockonPacket.lockons.get(player).getB()+60_000)
				GetLockonPacket.lockons.remove(player);
		}
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
		for (EntityPlayer player : world.playerEntities) {
			NBTTagCompound tag = player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
			if (tag.hasKey("pillMeltdownCooldown")) {
				int v = tag.getInteger("pillMeltdownCooldown")-1;
				if (v > 0) {
					tag.setInteger("pillMeltdownCooldown",v);
					PotionEffect overdose = player.getActivePotionEffect(LeafiaPotion.overdose);
					if (overdose != null) {
						if (overdose.getDuration() >= v-5)
							continue;
					}
					player.addPotionEffect(new PotionEffect(LeafiaPotion.overdose,v,0,false,false));
				} else
					tag.removeTag("pillMeltdownCooldown");
			}
		}

		WorldGlobalSyncPacket sync = new WorldGlobalSyncPacket();
		sync.world = world;
		LeafiaCustomPacket.__start(sync).__sendToAllInDimension(world.provider.getDimension());
	}
	public static class WorldGlobalSyncPacket implements LeafiaCustomPacketEncoder {
		public World world;
		@Override
		public void encode(LeafiaBuf buf) {
			TomSaveData data = TomSaveData.forWorld(world);
			buf.writeFloat(((IMixinTomSaveData)data).leafia$getSeismic());
		}
		@SideOnly(Side.CLIENT)
		public void run(LeafiaBuf buf) {
			World world = Minecraft.getMinecraft().world;
			ImpactSeismic.seismic = buf.readFloat();
		}
		@Override
		public @Nullable Consumer<MessageContext> decode(LeafiaBuf buf) {
			return (ctx)->{
				if (ctx.side == Side.CLIENT)
					run(buf);
			};
		}
	}
	public static void queueFunction(Runnable callback) {
		queue.add(callback);
	}
}
