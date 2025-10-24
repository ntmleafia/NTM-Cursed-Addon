package com.leafia.init;

import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.custompacket.LeafiaCustomPacket;
import com.leafia.dev.optimization.LeafiaParticlePacket;
import com.leafia.mixin.mod.hbm.other.LaserDetonatorPacket;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketInit {
	public static final SimpleNetworkWrapper wrapper = NetworkRegistry.INSTANCE.newSimpleChannel("leafia");

	public static final void registerPackets() {
		int i = 0;
		wrapper.registerMessage(LeafiaPacket.Handler.class,LeafiaPacket.class,i++,Side.SERVER);
		wrapper.registerMessage(LeafiaPacket.Handler.class,LeafiaPacket.class,i++,Side.CLIENT);
		wrapper.registerMessage(LeafiaCustomPacket.Handler.class,LeafiaCustomPacket.class,i++,Side.SERVER);
		wrapper.registerMessage(LeafiaCustomPacket.Handler.class,LeafiaCustomPacket.class,i++,Side.CLIENT);
		wrapper.registerMessage(LeafiaParticlePacket.Handler.class,LeafiaParticlePacket.class,i++,Side.CLIENT);
		wrapper.registerMessage(LaserDetonatorPacket.Handler.class,LaserDetonatorPacket.class,i++,Side.CLIENT);
	}
}
