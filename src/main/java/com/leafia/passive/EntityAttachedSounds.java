package com.leafia.passive;

import com.custom_hbm.sound.LCEAudioWrapper;
import com.leafia.AddonBase;
import com.leafia.dev.custompacket.LeafiaCustomPacketEncoder;
import com.leafia.dev.optimization.bitbyte.LeafiaBuf;
import com.llib.technical.FifthString;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class EntityAttachedSounds {
	public static long serverId = 0;
	public static List<EntityAttachedSound> sounds = new ArrayList<>();
	public static void update(int dimension) {
		int i = 0;
		while (i < sounds.size()) {
			EntityAttachedSound snd = sounds.get(i);
			if (snd.isStale(dimension)) {
				if (snd.wrapper != null)
					snd.wrapper.stopSound();
				sounds.remove(snd);
				continue;
			}
			snd.update();
			i++;
		}
	}
	public static class EntityAttachedSound {
		public LCEAudioWrapper wrapper;
		public long id = 0;
		public long lastTimestamp = 0;
		public Entity target;
		public EntityAttachedSound(Entity target,long id) {
			this.target = target;
			this.id = id;
			lastTimestamp = System.currentTimeMillis();
		}
		public boolean isStale(int dimension) {
			return target.dimension != dimension || System.currentTimeMillis() > lastTimestamp+2000;
		}
		public void update() {
			if (wrapper != null)
				wrapper.updatePosition((float)target.posX,(float)target.posY,(float)target.posZ);
		}
	}
	public static class AttachedSoundPacket extends AttachedSoundPacketBase {
		public boolean play = false;
		public SoundEvent evt;
		public float volume;
		public float pitch;
		public AttachedSoundPacket() {
		}
		public AttachedSoundPacket(Entity target,long id) {
			super(target,id);
		}
		public AttachedSoundPacket(Entity target,long id,SoundEvent evt,float volume,float pitch) {
			super(target,id);
			play = true;
			this.evt = evt;
			this.volume = volume;
			this.pitch = pitch;
		}
		@Override
		public void encode(LeafiaBuf buf) {
			super.encode(buf);
			buf.writeBoolean(play);
			if (play) {
				buf.writeFifthString(new FifthString(evt.getRegistryName().toString()));
				buf.writeFloat(volume);
				buf.writeFloat(pitch);
			}
		}
		@SideOnly(Side.CLIENT)
		@Override
		public void run(EntityAttachedSound snd,LeafiaBuf buf) {
			Minecraft.getMinecraft().addScheduledTask(()->{
				boolean play = buf.readBoolean();
				if (play) {
					SoundEvent evt = SoundEvent.REGISTRY.getObject(new ResourceLocation(buf.readFifthString().toString()));
					float volume = buf.readFloat();
					float pitch = buf.readFloat();
					if (snd.wrapper == null) {
						snd.wrapper = AddonBase.proxy.getLoopedSoundStartStop(snd.target.world,evt,null,null,SoundCategory.PLAYERS,(float)snd.target.posX,(float)snd.target.posY,(float)snd.target.posZ,volume,pitch);
						snd.wrapper.startSound();
					}
					if (snd.wrapper.getVolume() != volume)
						snd.wrapper.updateVolume(volume);
					if (snd.wrapper.getPitch() != pitch)
						snd.wrapper.updatePitch(pitch);
				} else {
					if (snd.wrapper != null)
						snd.wrapper.stopSound();
					sounds.remove(snd);
				}
			});
		}
	}
	public static abstract class AttachedSoundPacketBase implements LeafiaCustomPacketEncoder {
		public long id = 0;
		public Entity target;
		public AttachedSoundPacketBase() { }
		public AttachedSoundPacketBase(Entity target,long id) {
			this.target = target;
			this.id = id;
		}
		@Override
		public void encode(LeafiaBuf buf) {
			UUID uuid = target.getUniqueID();
			buf.writeLong(uuid.getMostSignificantBits());
			buf.writeLong(uuid.getLeastSignificantBits());
			buf.writeLong(id);
		}
		@SideOnly(Side.CLIENT)
		public abstract void run(EntityAttachedSound snd,LeafiaBuf buf);
		@SideOnly(Side.CLIENT)
		private void doRun(LeafiaBuf buf) {
			Minecraft mc = Minecraft.getMinecraft();
			UUID uuid = new UUID(buf.readLong(),buf.readLong());
			for (Entity entity : mc.world.loadedEntityList) {
				if (entity.getUniqueID().equals(uuid)) {
					EntityAttachedSound snd = null;
					long id = buf.readLong();
					for (EntityAttachedSound sound : sounds) {
						if (sound.id == id) {
							snd = sound;
							break;
						}
					}
					if (snd == null) {
						snd = new EntityAttachedSound(entity,id);
						sounds.add(snd);
					}
					snd.lastTimestamp = System.currentTimeMillis();
					run(snd,buf);
					break;
				}
			}
		}
		@Override
		public @Nullable Consumer<MessageContext> decode(LeafiaBuf buf) {
			return (ctx)->{
				if (ctx.side == Side.CLIENT)
					doRun(buf);
			};
		}
	}
}
