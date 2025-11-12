package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.custom_hbm.contents.torex.LCETorex;
import com.hbm.entity.effect.EntityNukeTorex;
import com.hbm.main.NetworkHandler;
import com.hbm.packet.PacketDispatcher;
import com.leafia.CommandLeaf;
import com.leafia.contents.effects.folkvangr.EntityNukeFolkvangr;
import com.leafia.contents.effects.folkvangr.EntityNukeFolkvangr.ClearChunkPacket;
import com.leafia.dev.LeafiaDebug.Tracker.LeafiaTrackerPacket;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.custompacket.LeafiaCustomPacket;
import com.leafia.dev.optimization.LeafiaParticlePacket;
import com.leafia.overwrite_contents.packets.LaserDetonatorPacket;
import com.leafia.overwrite_contents.packets.TorexFinishPacket;
import com.leafia.overwrite_contents.packets.TorexPacket;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraftforge.fml.relauncher.Side;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PacketDispatcher.class, remap = false)
public class MixinPacketDispatcher {
    @Shadow
    @Final
    public static NetworkHandler wrapper;

    @Inject(method = "registerPackets", at = @At("TAIL"))
    private static void onRegisterPackets(CallbackInfo ci, @Local int i) {
        wrapper.registerMessage(LeafiaPacket.Handler.class, LeafiaPacket.class, i++, Side.SERVER);
        wrapper.registerMessage(LeafiaPacket.Handler.class, LeafiaPacket.class, i++, Side.CLIENT);
        wrapper.registerMessage(LeafiaCustomPacket.Handler.class, LeafiaCustomPacket.class, i++, Side.SERVER);
        wrapper.registerMessage(LeafiaCustomPacket.Handler.class, LeafiaCustomPacket.class, i++, Side.CLIENT);
        wrapper.registerMessage(LeafiaParticlePacket.Handler.class, LeafiaParticlePacket.class, i++, Side.CLIENT);
        wrapper.registerMessage(LaserDetonatorPacket.Handler.class, LaserDetonatorPacket.class, i++, Side.CLIENT);
        wrapper.registerMessage(EntityNukeFolkvangr.ClearChunkPacket.Handler.class, EntityNukeFolkvangr.ClearChunkPacket.class, i++, Side.CLIENT);
        wrapper.registerMessage(EntityNukeFolkvangr.FolkvangrVacuumPacket.Handler.class, EntityNukeFolkvangr.FolkvangrVacuumPacket.class, i++, Side.CLIENT);
        wrapper.registerMessage(CommandLeaf.ShakecamPacket.Handler.class, CommandLeaf.ShakecamPacket.class, i++, Side.CLIENT);
        wrapper.registerMessage(TorexPacket.Handler.class, TorexPacket.class, i++, Side.CLIENT);
        wrapper.registerMessage(TorexFinishPacket.Handler.class, TorexFinishPacket.class, i++, Side.CLIENT);
        wrapper.registerMessage(LeafiaTrackerPacket.Handler.class, LeafiaTrackerPacket.class, i++, Side.CLIENT);
        wrapper.registerMessage(LCETorex.TorexPacket.Handler.class, LCETorex.TorexPacket.class, i++, Side.CLIENT);
        wrapper.registerMessage(LCETorex.TorexFinishPacket.Handler.class, LCETorex.TorexFinishPacket.class, i++, Side.CLIENT);
    }
}
