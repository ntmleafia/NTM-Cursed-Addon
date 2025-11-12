package com.leafia.overwrite_contents.interfaces;

import com.custom_hbm.sound.LCEAudioWrapper;
import com.hbm.sound.AudioWrapper;
import com.hbm.tileentity.machine.TileEntityCore;
import com.hbm.tileentity.machine.TileEntityCoreReceiver;
import com.leafia.dev.custompacket.LeafiaCustomPacketEncoder;
import com.leafia.dev.optimization.bitbyte.LeafiaBuf;
import com.leafia.init.LeafiaSoundEvents;
import com.llib.exceptions.LeafiaDevFlaw;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public interface IMixinTileEntityCore {
    // ===== constants =====
    double failsafeLevel = 250000000.0;
    double maxEnergy     = 10_000.0;

    enum Cores {
        ams_core_sing(
                LeafiaSoundEvents.dfc_vs, (intended, distance) ->
                Math.pow(MathHelper.clamp(1 - (distance - 3) / 15, 0, 1), 1.5)),
        ams_core_wormhole(
                LeafiaSoundEvents.dfc_tw, (intended, distance) ->
                Math.pow(MathHelper.clamp(1 - (distance - 3) / 40, 0, 1), 2)),
        ams_core_eyeofharmony(
                LeafiaSoundEvents.dfc_eoh, (intended, distance) ->
                Math.pow(MathHelper.clamp(1 - (distance - 3) / 150, 0, 1), 3)),
        glitch(LeafiaSoundEvents.glitch_alpha10302, (intended,distance) ->
                Math.pow(MathHelper.clamp(1 - (distance - 3) / 125, 0, 1), 3));

        public final SoundEvent sfx;
        public final BiFunction<Float, Double, Double> attentuationFunction;

        Cores(SoundEvent sfx, BiFunction<Float, Double, Double> attentuationFunction) {
            this.sfx = sfx;
            this.attentuationFunction = attentuationFunction;
        }
    }

    enum packetKeys {
        TEMP, STABILIZATION, MAXIMUM,
        CONTAINED, EXPELLING, POTENTIAL,
        TANK_A, TANK_B,
        EXPEL_TICK, COLOR, COLOR_CATALYST, CORE_TYPE,
        PLAY_SOUND, JAMMER,
        COLLAPSE,
        HASCORE;

        public int key;

        packetKeys() { this.key = this.ordinal(); }
    }

    class DFCShock {
        public final List<Vec3d> poses;
        public int ticks = 0;
        public DFCShock(List<Vec3d> poses) { this.poses = poses; }
    }

    class DFCShockPacket implements LeafiaCustomPacketEncoder {
        public BlockPos pos;
        public List<Vec3d> poses0 = new ArrayList<>();
        @Override
        public void encode(LeafiaBuf buf) {
            buf.writeVec3i(pos);
            buf.writeByte(poses0.size());
            for (Vec3d pos : poses0) {
                buf.writeFloat((float) pos.x);
                buf.writeFloat((float) pos.y);
                buf.writeFloat((float) pos.z);
            }
        }
        @Nullable
        @Override
        @SideOnly(Side.CLIENT)
        public Consumer<MessageContext> decode(LeafiaBuf buf) {
            List<Vec3d> poses = new ArrayList<>();
            TileEntity te = Minecraft.getMinecraft().world.getTileEntity(new BlockPos(buf.readVec3i()));
            int leng = buf.readByte();
            for (int i = 0; i < leng; i++) poses.add(new Vec3d(buf.readFloat(), buf.readFloat(), buf.readFloat()));
            return (context) -> {
                if (te == null) return;
                if (!(te instanceof TileEntityCore core)) throw new LeafiaDevFlaw("TileEntity is not a TileEntityCore");
//                for (int i = 0; i < poses.size(); i++) LeafiaDebug.debugPos(Minecraft.getMinecraft().world, new BlockPos(poses.get(i)), 1, 0xFFD800, Integer.toString(i));
                ((IMixinTileEntityCore)core).getDfcShocks().add(new DFCShock(poses));
            };
        }
    }

    double getDFCFailsafeLevel();
    double getDFCMaxEnergy();

    // ===== state =====
    boolean isDFCHasCore();
    void setDFCHasCore(boolean value);

    double getDFCTemperature();
    void setDFCTemperature(double value);

    double getDFCStabilization();
    void setDFCStabilization(double value);

    double getDFCContainedEnergy();
    void setDFCContainedEnergy(double value);

    double getDFCExpellingEnergy();
    void setDFCExpellingEnergy(double value);

    double getDFCPotentialGain();
    void setDFCPotentialGain(double value);

    double getDFCGainedEnergy();
    void setDFCGainedEnergy(double value);

    double getDFCCollapsing();
    void setDFCCollapsing(double value);

    int getDFCStabilizers();
    void setDFCStabilizers(int value);

    int getDFCLastStabilizers();
    void setDFCLastStabilizers(int value);

    boolean isDFCWasBoosted();
    void setDFCWasBoosted(boolean value);

    double[] getDFCExpelTicks();
    void setDFCExpelTicks(double[] value);

    double getDFCEnergyMod();
    void setDFCEnergyMod(double value);

    double getDFCBonus();
    void setDFCBonus(double value);

    List<TileEntityCoreReceiver> getDFCAbsorbers();
    void setDFCAbsorbers(List<TileEntityCoreReceiver> value);

    boolean isDFCDestroyed();
    void setDFCDestroyed(boolean value);

    double getDFCExplosionIn();
    void setDFCExplosionIn(double value);

    long getDFCExplosionClock();
    void setDFCExplosionClock(long value);

    BlockPos getDFCJammerPos();
    void setDFCJammerPos(BlockPos value);

    List<BlockPos> getDFCComponentPositions();
    void setDFCComponentPositions(List<BlockPos> value);

    List<BlockPos> getDFCPrevComponentPositions();
    void setDFCPrevComponentPositions(List<BlockPos> value);

    double getDFCIncomingSpk();
    void setDFCIncomingSpk(double value);

    double getDFCExpellingSpk();
    void setDFCExpellingSpk(double value);

    int getDFCMeltingPoint();
    void setDFCMeltingPoint(int value);

    int getDFCTicks();
    void setDFCTicks(int value);

    int getDFCOverloadTimer();
    void setDFCOverloadTimer(int value);

    int getDFCColorCatalyst();
    void setDFCColorCatalyst(int value);

    int getDFCShockCooldown();
    void setDFCShockCooldown(int value);

    // ===== client (visual) =====
    double getDFCClientMaxDial();
    void setDFCClientMaxDial(double value);

    Cores getDFCClientType();
    void setDFCClientType(Cores value);

    LCEAudioWrapper getDFCClientSfx();
    void setDFCClientSfx(LCEAudioWrapper value);

    boolean isDFCSfxPlaying();
    void setDFCSfxPlaying(boolean value);

    LCEAudioWrapper getDFCMeltdownSFX();
    void setDFCMeltdownSFX(LCEAudioWrapper value);

    LCEAudioWrapper getDFCOverloadSFX();
    void setDFCOverloadSFX(LCEAudioWrapper value);

    LCEAudioWrapper getDFCExtinguishSFX();
    void setDFCExtinguishSFX(LCEAudioWrapper value);

    LCEAudioWrapper getDFCExplosionsSFX();
    void setDFCExplosionsSFX(LCEAudioWrapper value);

    float getDFCAngle();
    void setDFCAngle(float value);

    float getDFCLightRotateSpeed();
    void setDFCLightRotateSpeed(float value);

    boolean isDFCFinalPhase();
    void setDFCFinalPhase(boolean value);

    float getDFCRingSpinSpeed();
    void setDFCRingSpinSpeed(float value);

    float getDFCRingAngle();
    void setDFCRingAngle(float value);

    float getDFCRingAlpha();
    void setDFCRingAlpha(float value);

    List<DFCShock> getDfcShocks();
}
