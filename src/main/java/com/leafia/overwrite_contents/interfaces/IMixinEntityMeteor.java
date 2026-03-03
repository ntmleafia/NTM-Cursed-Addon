package com.leafia.overwrite_contents.interfaces;

import com.leafia.dev.custompacket.LeafiaCustomPacketEncoder;
import com.leafia.dev.optimization.bitbyte.LeafiaBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;
/// omg fuck this rendering bullshit
public interface IMixinEntityMeteor {
	class MeteorSyncPacket implements LeafiaCustomPacketEncoder {
		public Entity entity;
		public double x;
		public double z;
		@Override
		public void encode(LeafiaBuf buf) {
			UUID uuid = entity.getPersistentID();
			buf.writeLong(uuid.getMostSignificantBits());
			buf.writeLong(uuid.getLeastSignificantBits());
			buf.writeDouble(x);
			buf.writeDouble(z);
		}
		@Override
		@SideOnly(Side.CLIENT)
		public @Nullable Consumer<MessageContext> decode(LeafiaBuf buf) {
			UUID uuid = new UUID(buf.readLong(),buf.readLong());
			double x = buf.readDouble();
			double z = buf.readDouble();
			return (ctx)->{
				for (Entity entity : Minecraft.getMinecraft().world.loadedEntityList) {
					if (entity.getPersistentID().equals(uuid)) {
						if (Math.abs(entity.posX-x) > 10 || Math.abs(entity.posZ-z) > 10)
							entity.setLocationAndAngles(x,entity.posY,z,entity.rotationYaw,entity.rotationPitch);
					}
				}
			};
		}
	}
}
