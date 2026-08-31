package com.leafia.dev.custompacket;

import com.custom_hbm.explosion.LCEExplosionNT.ExplosionNTSyncPacket;
import com.hbm.handler.threading.PacketThreading;
import com.hbm.packet.threading.ThreadedPacket;
import com.leafia.contents.gear.ILockonWeapon.GetLockonPacket;
import com.leafia.contents.gear.advisor.AdvisorItem;
import com.leafia.contents.gear.advisor.AdvisorItem.AdvisorPacket;
import com.leafia.contents.gear.utility.fuzzy.FuzzyIdentifierItem.FuzzyIdentifierPacket;
import com.leafia.contents.gear.utility.fuzzy.FuzzyIdentifierItem.FuzzyIdentifierResponsePacket;
import com.leafia.contents.gear.wands.ItemWandLoading.WandStructurePacket;
import com.leafia.contents.gear.wands.ItemWandSaving.HighlightSavingWandProduct;
import com.leafia.contents.gear.wands.ItemWandSaving.HighlightSavingWandRemove;
import com.leafia.contents.gear.wands.ItemWandSaving.HighlightSavingWandSave;
import com.leafia.contents.machines.controlpanel.instruments.types.graph.Graph.CCPGraphSyncPacket;
import com.leafia.contents.machines.controlpanel.instruments.types.lever.LeverBase.CCPLeverSyncPacket;
import com.leafia.contents.machines.controlpanel.instruments.types.meters.vertical.MeterVertical.CCPMeterVerticalSyncPacket;
import com.leafia.contents.machines.controlpanel.instruments.types.starbound.LargeSwitch.CCPLargeSwitchSyncPacket;
import com.leafia.contents.machines.controlpanel.instruments.types.textindicator.TextIndicator.CCPTextIndicatorSyncPacket;
import com.leafia.contents.machines.elevators.car.ElevatorEntity.*;
import com.leafia.contents.machines.elevators.items.radio.EvRadioItem.EvRadioSyncRequestPacket;
import com.leafia.contents.machines.elevators.items.radio.EvRadioItem.EvRadioUIPacket;
import com.leafia.contents.machines.misc.modular_turbine.core.MTCoreBlock.TurbineErrorHighlight;
import com.leafia.contents.worldgen.biomes.artificial.DigammaCrater.DigammaBackstabPacket;
import com.leafia.contents.worldgen.biomes.artificial.DigammaCrater.ForceSchizoPacket;
import com.leafia.database.ImpactSeismic.TomImpactCollapsePacket;
import com.leafia.dev.LeafiaDebug.Tracker.VisualizerPacket;
import com.leafia.dev.optimization.bitbyte.LeafiaBuf;
import com.leafia.dev.optimization.diagnosis.RecordablePacket;
import com.leafia.overwrite_contents.interfaces.IMixinEntityMeteor.MeteorSyncPacket;
import com.leafia.overwrite_contents.interfaces.IMixinItemLaserDetonator.RequestLaserPointPacket;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityCore.DFCShockPacket;
import com.leafia.passive.EntityAttachedSounds.AttachedSoundPacket;
import com.leafia.passive.LeafiaPassiveServer.WorldGlobalSyncPacket;
import com.leafia.passive.Wind.WindSyncPacket;
import com.leafia.savedata.FalloutSavedData.FalloutSyncPacket;
import com.llib.exceptions.LeafiaDevFlaw;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.function.Consumer;

public class LeafiaCustomPacket extends RecordablePacket {
	public enum CustomPacketType { // add your packets here, that's literally all registering you have to do!
		NONE,
		VISUALIZER_TRACE(new VisualizerPacket()),
		HIGHLIGHT_SAVING_SAVE(new HighlightSavingWandSave()),
		HIGHLIGHT_SAVING_REMOVE(new HighlightSavingWandRemove()),
		HIGHLIGHT_SAVING_PRODUCT(new HighlightSavingWandProduct()),
		WAND_STRUCTURE(new WandStructurePacket()),
		FUZZY_IDENTIFIER(new FuzzyIdentifierPacket()),
		DFC_SHOCK(new DFCShockPacket()),
		EXPLOSION_NT(new ExplosionNTSyncPacket()),
		FUZZY_RESPONSE(new FuzzyIdentifierResponsePacket()),
		ADVISOR_WARNING(new AdvisorItem.AdvisorWarningPacket()),
		ELEVATOR_BUTTON_SYNC(new EvButtonSyncPacket()),
		ELEVATOR_BUTTON_ENABLED_SYNC(new EvButtonEnablePacket()),
		ELEVATOR_BUTTON_CLICK_SYNC(new EvButtonClickPacket()),
		ELEVATOR_BUTTON_CLICKED(new EvButtonInteractPacket()),
		ELEVATOR_BUTTON_MODIFY(new EvButtonModifyPacket()),
		ELEVATOR_SPECIAL_SYNC(new EvSpecialFloorsSyncPacket()),
		ELEVATOR_SYNC_REQUEST(new EvSyncRequestPacket()),
		ELEVATOR_INVENTORY_SYNC(new EvInventorySyncPacket()),
		METEOR_SYNC(new MeteorSyncPacket()),
		ADVISOR(new AdvisorPacket()),
		TURBINE_ERROR(new TurbineErrorHighlight()),
		CCP_SWITCH_SYNC(new CCPLargeSwitchSyncPacket()),
		CCP_INDICATOR_SYNC(new CCPTextIndicatorSyncPacket()),
		CCP_GRAPH_SYNC(new CCPGraphSyncPacket()),
		CCP_METER_V_SYNC(new CCPMeterVerticalSyncPacket()),
		CCP_LEVER_SYNC(new CCPLeverSyncPacket()),
		WIND_SYNC(new WindSyncPacket()),
		FALLOUT_SYNC(new FalloutSyncPacket()),
		DRX_BACKSTAB(new DigammaBackstabPacket()),
		EV_RADIO_UI(new EvRadioUIPacket()),
		EV_RADIO_REQUEST(new EvRadioSyncRequestPacket()),
		FORCE_SCHIZO(new ForceSchizoPacket()),
		WORLD_GLOBAL_SYNC(new WorldGlobalSyncPacket()),
		IMPACT_COLLAPSE(new TomImpactCollapsePacket()),
		ATTACHED_SOUND(new AttachedSoundPacket()),
		GET_LOCKON(new GetLockonPacket()),
		REQUEST_POINT(new RequestLaserPointPacket()),
		;
		final LeafiaCustomPacketEncoder encoder;
		CustomPacketType() { encoder = null; }
		CustomPacketType(LeafiaCustomPacketEncoder encoder) { this.encoder = encoder; }
	}
	CustomPacketType packetType = CustomPacketType.NONE;
	LeafiaCustomPacketEncoder encoder = null;
	Consumer<MessageContext> callback = null;
	public static LeafiaCustomPacket __start(LeafiaCustomPacketEncoder encoder) {
		for (CustomPacketType type : CustomPacketType.values()) {
			if (type.encoder != null && type.encoder.getClass().isInstance(encoder)) {
				LeafiaCustomPacket packet = new LeafiaCustomPacket();
				packet.encoder = encoder;
				packet.packetType = type;
				return packet;
			}
		}
		throw new LeafiaDevFlaw("Encoder "+encoder.getClass().getSimpleName()+" isn't registered to enum CustomPacketType");
	}
	public void __sendToAll() {
		PacketThreading.createSendToAllThreadedPacket(this);
	}
	public void __sendToAllAround(int dim,BlockPos pos,double range) {
		PacketThreading.createSendToAllTrackingThreadedPacket(this,new TargetPoint(dim,pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5,range));
	}
	public void __sendToAllAround(int dim,Vec3d pos,double range) {
		PacketThreading.createSendToAllTrackingThreadedPacket(this,new TargetPoint(dim,pos.x,pos.y,pos.z,range));
	}
	//@Deprecated
	// why is this deprecated
	public void __sendToAllInDimension(int dimension) {
		PacketThreading.createSendToDimensionThreadedPacket(this,dimension);
	}
	public void __sendToServer() {
		PacketThreading.createSendToServerThreadedPacket(this);
	}
	public void __sendToClient(EntityPlayer player) {
		_sendToClient(this,player);
	}
	public static void _sendToClient(ThreadedPacket message,EntityPlayer player) {
		if (player instanceof EntityPlayerMP)
			PacketThreading.createSendToThreadedPacket(message,(EntityPlayerMP)player);
		else
			PacketThreading.createSendToAllThreadedPacket(message);
	}
	@Override
	public void fromBits(LeafiaBuf buf) {
		short protocol = buf.readShort();
		if ((protocol&0xFF) != CustomPacketType.values().length) {
			packetType = CustomPacketType.NONE;
			callback = (ctx)->{
				ITextComponent reason = new TextComponentString("########").setStyle(new Style().setColor(TextFormatting.GOLD))
						.appendSibling(new TextComponentString(" NTM:LCE FATAL ERROR ").setStyle(new Style().setColor(TextFormatting.WHITE)))
						.appendSibling(new TextComponentString("########\nInvalid protocol on LeafiaCustomPacket").setStyle(new Style().setColor(TextFormatting.GOLD)))
						.appendSibling(
								new TextComponentString("\nThe server supports "+(protocol&0xFF)+" custom packet variations. Your client supports "+CustomPacketType.values().length).setStyle(new Style().setColor(TextFormatting.WHITE))
						)
						.appendSibling(new TextComponentString("\n\nPossible reasons are:").setStyle(new Style().setColor(TextFormatting.AQUA)))
						.appendSibling(
								new TextComponentString("\n- Your client is outdated. Check for any updates on github (I don't add version numbers!)\n- Or the server is outdated. Contact server owner\n- Else perhaps it's some unpredictable fucks going on idfk").setStyle(new Style().setColor(TextFormatting.GRAY))
						);
				if (ctx.side.isClient())
					Minecraft.getMinecraft().player.connection.getNetworkManager().closeChannel(reason);
				else if (ctx.side.isServer())
					ctx.getServerHandler().disconnect(reason);
			};
			return;
		}
		packetType = CustomPacketType.values()[protocol>>8];
		if (packetType.encoder != null)
			callback = packetType.encoder.decode(buf);
	}
	@Override
	public void toBits(LeafiaBuf buf) {
		buf.writeShort(packetType.ordinal()<<8|CustomPacketType.values().length);
		if (encoder != null)
			encoder.encode(buf);
	}
	public static class Handler implements IMessageHandler<LeafiaCustomPacket,IMessage> {
		@Override
		public IMessage onMessage(LeafiaCustomPacket message,MessageContext ctx) {
			if (message.callback != null)
				message.callback.accept(ctx);
			return null;
		}
	}
}