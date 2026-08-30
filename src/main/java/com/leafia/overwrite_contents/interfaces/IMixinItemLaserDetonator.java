package com.leafia.overwrite_contents.interfaces;

import com.hbm.items.ModItems;
import com.leafia.dev.custompacket.LeafiaCustomPacketEncoder;
import com.leafia.dev.optimization.bitbyte.LeafiaBuf;
import com.leafia.overwrite_contents.mixin.mod.hbm.MixinItemLaserDetonator;
import com.leafia.passive.LeafiaPassiveServer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface IMixinItemLaserDetonator {
	class RequestLaserPointPacket implements LeafiaCustomPacketEncoder {
		@Override
		public void encode(LeafiaBuf buf) { }
		@Override
		public @Nullable Consumer<MessageContext> decode(LeafiaBuf buf) {
			return (ctx)->{
				LeafiaPassiveServer.queueFunction(()->{
					EntityPlayer player = ctx.getServerHandler().player;
					((IMixinItemLaserDetonator)ModItems.detonator_laser).leafia$doDetonate(player.world,player,EnumHand.MAIN_HAND,false);
				});
			};
		}
	}
	void leafia$doDetonate(World world,EntityPlayer player,EnumHand hand,boolean detonate);
}
