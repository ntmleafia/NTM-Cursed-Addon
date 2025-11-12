package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.custom_hbm.explosion.LCEExplosionNT;
import com.custom_hbm.sound.LCEAudioWrapper;
import com.hbm.blocks.machine.MachineFieldDisturber;
import com.hbm.entity.effect.EntityCloudFleijaRainbow;
import com.hbm.entity.logic.EntityNukeExplosionMK3;
import com.hbm.handler.ArmorUtil;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemCatalyst;
import com.hbm.items.special.ItemAMSCore;
import com.hbm.lib.Library;
import com.hbm.main.AdvancementManager;
import com.hbm.main.MainRegistry;
import com.hbm.packet.PacketDispatcher;
import com.hbm.tileentity.IGUIProvider;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.tileentity.machine.TileEntityCore;
import com.hbm.tileentity.machine.TileEntityCoreReceiver;
import com.leafia.AddonBase;
import com.leafia.CommandLeaf;
import com.leafia.LeafiaHelper;
import com.leafia.contents.AddonItems;
import com.leafia.contents.effects.folkvangr.EntityNukeFolkvangr;
import com.leafia.contents.effects.folkvangr.particles.ParticleFleijaVacuum;
import com.leafia.contents.machines.powercores.dfc.particles.ParticleEyeOfHarmony;
import com.leafia.database.FolkvangrJammers;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.container_utility.LeafiaPacketReceiver;
import com.leafia.dev.custompacket.LeafiaCustomPacket;
import com.leafia.dev.math.FiaMatrix;
import com.leafia.dev.optimization.LeafiaParticlePacket;
import com.leafia.init.LeafiaSoundEvents;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityCore;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityCoreReceiver;
import com.leafia.overwrite_contents.other.LCEItemCatalyst;
import com.leafia.passive.LeafiaPassiveLocal;
import com.leafia.unsorted.LeafiaDamageSource;
import com.llib.LeafiaLib;
import com.llib.math.LeafiaColor;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Contract;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = TileEntityCore.class, remap = false)
public abstract class MixinTileEntityCore extends TileEntityMachineBase implements ITickable, IGUIProvider, LeafiaPacketReceiver, IMixinTileEntityCore {
    @Unique
    private boolean hasCore = false;
    @Shadow
    public int field;
    @Shadow
    public int heat;
    @Shadow
    public int color;
    @Shadow
    public FluidTankNTM[] tanks;
    @Unique
    private double temperature = 0;
    @Unique
    private double stabilization = 0;
    @Unique
    private double containedEnergy = 0; // 1 = 1MSPK
    @Unique
    private double expellingEnergy = 0;
    @Unique
    private double potentialGain = 0;
    @Unique
    private double gainedEnergy = 0;
    @Unique
    private double collapsing = 0;
    @Unique
    private int stabilizers = 0;
    @Unique
    private int lastStabilizers = 0;
    @Unique
    private boolean wasBoosted = false;
    @Unique
    private double[] expelTicks = new double[20];
    @Unique
    private double energyMod = 1;
    @Unique
    private double bonus = 0;
    @Unique
    private List<TileEntityCoreReceiver> absorbers = new ArrayList<>();
    @Unique
    private boolean destroyed = false;
    @Unique
    private double explosionIn = -1;
    @Unique
    private long explosionClock = 0;
    @Unique
    private BlockPos jammerPos = null;
    @Unique
    private List<BlockPos> componentPositions = new ArrayList<>();
    @Unique
    private List<BlockPos> prevComponentPositions = new ArrayList<>();
    @Unique
    private double incomingSpk = 0;
    @Unique
    private double expellingSpk = 0;
    @Unique
    private int meltingPoint = 2250;
    @Unique
    private int ticks = 0;
    @Unique
    private int overloadTimer = 0;
    @Unique
    private int colorCatalyst = 0xFFFFFF;
    @Unique
    private int shockCooldown = 0;
    // == Client ==
    @Unique
    private double client_maxDial = 0.95;
    @Unique
    private Cores client_type = null;
    @Unique
    private LCEAudioWrapper client_sfx = null;
    @Unique
    private boolean sfxPlaying = false;
    @Unique
    private LCEAudioWrapper meltdownSFX = null;
    @Unique
    private LCEAudioWrapper overloadSFX = null;
    @Unique
    private LCEAudioWrapper extinguishSFX = null;
    @Unique
    private LCEAudioWrapper explosionsSFX = null;
    @Unique
    private float angle = 0;
    @Unique
    private float lightRotateSpeed = 15 / 20.0f;
    @Unique
    private boolean finalPhase = false;
    @Unique
    private float ringSpinSpeed = 360 / 20.0f;
    @Unique
    private float ringAngle = 0;
    @Unique
    private float ringAlpha = 0;
    @Unique
    private final List<DFCShock> dfcShocks = new ArrayList<>();

    public MixinTileEntityCore(int scount) {
        super(scount);
    }

    /**
     * @author mlbv
     * @reason can be injected and cancelled but why not just use @Overwrite?
     */
    @Override
    @Overwrite(remap = true)
    public void update() {
        prevComponentPositions.clear();
        prevComponentPositions.addAll(componentPositions);
        componentPositions.clear();
        if (destroyed) return;
        if (!world.isRemote) tickServer(); else tickClient();
    }


    @Unique
    private void tickServer() {
        lastStabilizers = stabilizers;
        stabilizers = 0;

        ItemStack catalystA = inventory.getStackInSlot(0);
        ItemStack catalystB = inventory.getStackInSlot(2);
        NBTTagCompound tagA = null;
        NBTTagCompound tagB = null;
        double damageA = 100;
        double damageB = 100;

        if (catalystA.getItem() instanceof ItemCatalyst && catalystB.getItem() instanceof ItemCatalyst) {
            LeafiaColor col = new LeafiaColor(calcAvgHex(((ItemCatalyst) catalystA.getItem()).getColor(), ((ItemCatalyst) catalystB.getItem()).getColor()));
            colorCatalyst = col.toInARGB();
            tagA = catalystA.getTagCompound();
            tagB = catalystB.getTagCompound();
            if (tagA == null) {
                tagA = new NBTTagCompound();
                catalystA.setTagCompound(tagA);
            }
            if (tagB == null) {
                tagB = new NBTTagCompound();
                catalystB.setTagCompound(tagB);
            }
            damageA = tagA.getDouble("damage");
            damageB = tagB.getDouble("damage");
            color = col.lerp(new LeafiaColor(world.rand.nextFloat(), world.rand.nextFloat(), world.rand.nextFloat()), Math.pow(Math.max(damageA / 100, damageB / 100), 2))
                       .toInARGB();
            hasCore = true;
        } else {
            color = 0;
            hasCore = false;
        }

        expellingSpk = 0;
        if (inventory.getStackInSlot(1).getItem() instanceof ItemAMSCore) {
            if (tagA != null && tagB != null) {
                meltingPoint = Math.min(1500000, Math.min(LCEItemCatalyst.getMelting(catalystA), LCEItemCatalyst.getMelting(catalystB)));

                double corePower = getCorePower();
                double coreHeatMod = getCoreHeat();
                long catalystPower = ItemCatalyst.getPowerAbs(catalystA) + ItemCatalyst.getPowerAbs(catalystB);
                float catalystPowerMod = ItemCatalyst.getPowerMod(catalystA) * ItemCatalyst.getPowerMod(catalystB);
                float catalystHeatMod = ItemCatalyst.getHeatMod(catalystA) * ItemCatalyst.getHeatMod(catalystB);
                float catalystFuelMod = ItemCatalyst.getFuelMod(catalystA) * ItemCatalyst.getFuelMod(catalystB);
                FluidType type0 = tanks[0].getTankType();
                FluidType type1 = tanks[1].getTankType();
                double fillPct0 = tanks[0].getFill() / (double) tanks[0].getMaxFill();
                double fillPct1 = tanks[1].getFill() / (double) tanks[1].getMaxFill();
                double fuelPower = getFuelEfficiency(type0) * getFuelEfficiency(type1);

                double tempRatio = temperature / meltingPoint;
                double energyRatio = containedEnergy / maxEnergy;

                ticks++;
//                Tracker._startProfile(this, "NeoTick");
                potentialGain = energyMod;
                if (temperature >= 100) {
                    double randRange = Math.pow(tempRatio, 0.65) * 10;
                    potentialGain += world.rand.nextDouble() * randRange / getStabilizationDivAlt() / getStabilizationDiv() + Math.pow(collapsing, 0.666) * 66;
                }

                int consumption = (int) Math.ceil(Math.pow(incomingSpk * catalystFuelMod * getCoreFuel(), 0.5));
//                Tracker._tracePosition(this, pos.up(3), "incomingSpk: ", incomingSpk);
                tanks[0].drain(consumption, true);
                tanks[1].drain(consumption, true);

                double boost = catalystPowerMod * energyMod;
                double deltaEnergy = (Math.pow(Math.pow(incomingSpk, 0.666 / 2) + 1, 0.666 / 2) - 1) * 6.666 / 3 * Math.pow(1.2, potentialGain);
                double addition0 = (deltaEnergy * corePower + Math.pow(Math.max(0, incomingSpk - deltaEnergy), 0.9)) * boost * fillPct0 * fillPct1 /666;
                double addition1 = Math.pow(Math.min(temperature, 10000) / 100, 0.75) * corePower * potentialGain * boost * fillPct0 * fillPct1 * fuelPower / 20 * Math.pow(0.9, potentialGain);
                addition0 = Math.max(addition0, 0);
                addition1 = Math.max(addition1, 0);
                containedEnergy = Math.min(Math.min(containedEnergy + addition0, failsafeLevel) + addition1, failsafeLevel);

                double tgtTemp = temperature;
                tgtTemp += Math.pow(deltaEnergy * 666 * catalystHeatMod, 2 / (1 + stabilization)) * (1 - tempRatio / 2) * coreHeatMod * Math.pow(potentialGain, 0.25);


//                Tracker._tracePosition(this, pos.down(3), "containedEnergy: ", containedEnergy);
//                Tracker._tracePosition(this, pos.down(4), "deltaEnergy: ", deltaEnergy);

                double absorbDiv = 0.001;
                for (TileEntityCoreReceiver absorber : absorbers) absorbDiv += ((IMixinTileEntityCoreReceiver)absorber).getLevel();

                double collapseAddition = Math.pow(collapsing,2)*500_000;
                containedEnergy += Math.max(collapseAddition-addition0-addition1,0);

                containedEnergy = Math.min(containedEnergy,failsafeLevel);

                gainedEnergy = containedEnergy;
                double absorbed = Math.pow(containedEnergy,0.75+0.25*(1-1/(1+absorbDiv)))*absorbDiv;
                double transferred = 0;
                for (TileEntityCoreReceiver absorber : absorbers) {
                    if (finalPhase) {
                        ((IMixinTileEntityCoreReceiver)absorber).explode();
                        continue;
                    }
                    long absorb = (long) (absorbed / absorbDiv * ((IMixinTileEntityCoreReceiver)absorber).getLevel() * 1000_000);
                    containedEnergy -= absorb / 1000_000.0d;
                    transferred += absorb / 1000_000.0d;
                    double val = (catalystPower * Math.pow(tempRatio, 0.1) + incomingSpk * 2000_000) / absorbDiv * ((IMixinTileEntityCoreReceiver)absorber).getLevel();
                    val = Math.min(val, Long.MAX_VALUE);
                    absorber.joules += absorb + (long) val;
                }
                expellingSpk = transferred;
                expelTicks[Math.floorMod(ticks, 20)] = expellingSpk;
                containedEnergy = Math.max(containedEnergy, 0);
                double rdc = 1-energyRatio;
                tgtTemp -= Math.pow(Math.abs(rdc),0.5)*Math.signum(rdc)*tempRatio;//*10;

                tgtTemp -= Math.max(0, Math.pow(temperature / meltingPoint, 4) * temperature * getStabilizationDivAlt()) * (0.5 + (Math.pow(Math.abs(rdc), 0.01) * Math.signum(rdc)) / 2);
                tgtTemp = Math.min(Math.max(tgtTemp, 0), 5000000);
                double deltaTemp = tgtTemp - temperature;
                if (!finalPhase) {
                    double limit = 1000 + world.rand.nextInt(1000) + world.rand.nextDouble();
                    temperature += Math.min(Math.pow(Math.abs(deltaTemp), 0.5) * Math.signum(deltaTemp), limit);
                } else {
                    temperature = temperature + world.rand.nextInt(5000) + 10000 + world.rand.nextDouble();
                }
                temperature = Math.max(temperature, 0);

                if (shockCooldown > 0) shockCooldown--;
                double energyPerShock = 300_000 * 0.75;
                if (containedEnergy >= 100_000 * (world.rand.nextInt(150) + 6.66) + 0.5 && shockCooldown <= 0) {
                    double count = Math.ceil(containedEnergy / energyPerShock);
                    for (int i = 0; i < Math.pow(count, 0.25); i++) shock();
                    world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, LeafiaSoundEvents.mus_sfx_a_lithit, SoundCategory.BLOCKS, 6.66f, 1 + (float) world.rand.nextGaussian() * 0.1f);
                    PacketDispatcher.wrapper.sendToAllAround(new CommandLeaf.ShakecamPacket(new String[]{"type=smooth", "preset=RUPTURE", "duration/4", "blurDulling*2", "intensity/2", "range=50"}).setPos(pos), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 100));
                    PacketDispatcher.wrapper.sendToAllAround(new CommandLeaf.ShakecamPacket(new String[]{"type=smooth", "preset=QUAKE", "duration/2", "intensity/4", "range=100"}).setPos(pos), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 150));
                    containedEnergy = Math.max(containedEnergy - count * energyPerShock, 0);
                    shockCooldown = 100 - (int) (90 * Math.pow(collapsing, 1.75));
                }
//                Tracker._endProfile(this);

                double timeToMeltdown = 10;
                double timeToRegen = 30;
                tagA.setDouble("damage", MathHelper.clamp(damageA + (temperature >= LCEItemCatalyst.getMelting(catalystA) ? 5 / timeToMeltdown : -5 / timeToRegen), 0, 100));
                tagB.setDouble("damage", MathHelper.clamp(damageB + (temperature >= LCEItemCatalyst.getMelting(catalystB) ? 5 / timeToMeltdown : -5 / timeToRegen), 0, 100));
            }
        }

        handleOverloadAndExplosion(damageA, damageB);
        accumulateExpellingEnergy();
        wasBoosted = incomingSpk > 0;
        incomingSpk = 0;
        energyMod = 1;
        absorbers.clear();

        if (temperature > 100) vaporization();
        broadcastState();
        heat = 0;
        stabilization = 0;
        if (this.collapsing > 0) {
            List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(null, LeafiaHelper.getAABBRadius(LeafiaHelper.getBlockPosCenter(this.pos), getPullRange()));
            for (Entity e : list) if (!(e instanceof EntityFallingBlock)) pull(e);
        }
        this.markDirty();
    }

    private void accumulateExpellingEnergy() {
        expellingEnergy = 0;
        for (double energy : expelTicks) expellingEnergy += energy;
    }

    @Unique
    private void handleOverloadAndExplosion(double damageA, double damageB) {
        if ((damageA >= 100 || damageB >= 100) && temperature > 100 || finalPhase) {
            EntityNukeExplosionMK3 exp = null;
            if (jammerPos != null) {
                if (!(world.getBlockState(jammerPos).getBlock() instanceof MachineFieldDisturber)) jammerPos = null;
            }
            if (explosionIn < 10 || jammerPos == null) {
                exp = new EntityNukeExplosionMK3(world);
                exp.posX = pos.getX();
                exp.posY = pos.getY();
                exp.posZ = pos.getZ();
                exp.destructionRange = 20 + (int) Math.pow(temperature, 0.4);
                exp.speed = 25;
                exp.coefficient = 1.0F;
                exp.waste = false;
            }
            if (jammerPos == null) {
                if (overloadTimer <= 20 * 6) {
                    if (overloadTimer == 0) {
                        LeafiaPacket._start(this).__write(packetKeys.PLAY_SOUND.key, 2).__sendToAll();
                    }
                    overloadTimer++;
                } else {
                    world.playSound(null, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 100000.0F, 1.0F);
                    world.playSound(null, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, LeafiaSoundEvents.actualexplosion, SoundCategory.BLOCKS, 50.0F, 1.0F);
                    PacketDispatcher.wrapper.sendToAllAround(new CommandLeaf.ShakecamPacket(new String[]{"type=smooth", "preset=RUPTURE", "blurDulling*2", "speed*1.5", "duration/2", "range=300"}).setPos(pos), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 400));
                    LeafiaColor col = new LeafiaColor(colorCatalyst);
                    LeafiaParticlePacket.DFCBlastParticle blast = new LeafiaParticlePacket.DFCBlastParticle((float) col.red, (float) col.green, (float) col.blue, 250);
                    blast.emit(new Vec3d(pos).add(0.5, 0.5, 0.5), new Vec3d(0, 1, 0), world.provider.getDimension(), 200);

                    LCEExplosionNT nt = new LCEExplosionNT(world, null, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, 50);
                    nt.maxExplosionResistance = 28;
                    nt.iterationLimit = 150;
                    nt.ignoreBlockPoses.add(pos);
                    nt.explode();

                    if (!EntityNukeExplosionMK3.isJammed(this.world, exp)) {
                        destroyed = true;
                        world.spawnEntity(exp);
                        EntityCloudFleijaRainbow cloud = new EntityCloudFleijaRainbow(world, exp.destructionRange);
                        cloud.posX = pos.getX();
                        cloud.posY = pos.getY();
                        cloud.posZ = pos.getZ();
                        world.spawnEntity(cloud);
                    } else {
                        jammerPos = FolkvangrJammers.lastDetectedJammer;
                        if (explosionIn < 0) {
                            explosionIn = 120;
                            explosionClock = System.currentTimeMillis();
                            LeafiaPacket._start(this).__write(packetKeys.PLAY_SOUND.key, 0).__sendToAll();
                        }
                    }
                }
            }
            if (jammerPos != null) {
                boolean tick = true;
                MinecraftServer server = world.getMinecraftServer();
                if (server != null) {
                    if (!server.isDedicatedServer()) tick = !Minecraft.getMinecraft().isGamePaused();
                }
                if (tick) {
                    long time = System.currentTimeMillis();
                    explosionIn = Math.max(explosionIn - (time - explosionClock) / 1000.0d, 0);
                    collapsing = MathHelper.clamp(1 - explosionIn / 120, 0, 1);
                    explosionClock = time;
                    if (explosionIn <= 15 && !finalPhase) {
                        finalPhase = true;
                        PacketDispatcher.wrapper.sendToAllAround(new CommandLeaf.ShakecamPacket(new String[]{"type=smooth", "preset=RUPTURE", "blurDulling*2", "speed*1.5", "duration/2", "range=300"}).setPos(pos), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 400));
                        PacketDispatcher.wrapper.sendToAllAround(new CommandLeaf.ShakecamPacket(new String[]{"type=smooth", "preset=QUAKE", "blurDulling*4", "speed*3", "duration=40", "range=300"}).setPos(pos), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 400));
                        LeafiaPacket._start(this).__write(packetKeys.PLAY_SOUND.key, 3).__sendToAll();

                        LCEExplosionNT nt = new LCEExplosionNT(world, null, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, 150);
                        nt.iterationLimit = 150;
                        nt.overrideResolution(24);
                        nt.ignoreBlockPoses.add(pos);
                        nt.addAttrib(LCEExplosionNT.LCEExAttrib.FIRE);
                        nt.addAttrib(LCEExplosionNT.LCEExAttrib.DFC_FALL);
                        nt.explode();
                        LeafiaColor col = new LeafiaColor(colorCatalyst);
                        LeafiaParticlePacket.DFCBlastParticle blast = new LeafiaParticlePacket.DFCBlastParticle((float) col.red, (float) col.green, (float) col.blue, 250);
                        blast.emit(new Vec3d(pos).add(0.5, 0.5, 0.5), new Vec3d(0, 1, 0), world.provider.getDimension(), 200);
                    }
                    if (finalPhase) {
                        LeafiaColor col = new LeafiaColor(colorCatalyst);
                        LeafiaParticlePacket.DFCBlastParticle blast = new LeafiaParticlePacket.DFCBlastParticle((float) col.red, (float) col.green, (float) col.blue, 20);
                        blast.emit(new Vec3d(pos).add(0.5, 0.5, 0.5), new Vec3d(0, 1, 0), world.provider.getDimension(), 200);
                    }
                    if (explosionIn <= 0 && exp != null) {
                        PacketDispatcher.wrapper.sendToAllAround(new CommandLeaf.ShakecamPacket(new String[]{"type=smooth", "preset=PWR_NEAR", "duration*2", "intensity*1.5", "range=200"}).setPos(pos), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 300));
                        world.playSound(null, pos, LeafiaSoundEvents.dfc_explode, SoundCategory.BLOCKS, 100, 1);
                        destroyed = true;
                        world.spawnEntity(exp);
                        EntityCloudFleijaRainbow cloud = new EntityCloudFleijaRainbow(world, exp.destructionRange);
                        cloud.posX = pos.getX();
                        cloud.posY = pos.getY();
                        cloud.posZ = pos.getZ();
                        world.spawnEntity(cloud);
                    }
                }
            }
        } else {
            if (explosionIn >= 0) {
                jammerPos = null;
                explosionIn = -1;
                collapsing = 0;
                LeafiaPacket._start(this).__write(packetKeys.PLAY_SOUND.key, 1).__sendToAll();
            } else if (overloadTimer > 0) {
                LeafiaPacket._start(this).__write(packetKeys.PLAY_SOUND.key, 1).__sendToAll();
                overloadTimer = 0;
            }
        }
    }

    // =====================
    // === Client logic ====
    // =====================
    @SideOnly(Side.CLIENT)
    private void tickClient() {
        ticks++;
        client_maxDial = world.rand.nextDouble() * 0.08 + 0.9;

        if (client_sfx != null) {
            if (temperature >= 100 && !sfxPlaying) {
                sfxPlaying = true;
                client_sfx.startSound();
            } else if (temperature < 100 && sfxPlaying) {
                sfxPlaying = false;
                client_sfx.stopSound();
            }
        }

        if (collapsing > 0.666) pullLocal();
        for (DFCShock shock : dfcShocks) shock.ticks++;
        while (!dfcShocks.isEmpty()) {
            if (dfcShocks.get(0).ticks > 4) dfcShocks.remove(0);
            else break;
        }

        if (temperature > 100) {
            if (client_type == Cores.ams_core_eyeofharmony) {
                float r = (color >> 16 & 255) / 255.0F;
                float g = (color >> 8 & 255) / 255.0F;
                float b = (color & 255) / 255.0F;
                float scale = (float) Math.log(temperature / 50 + 1);
                ParticleEyeOfHarmony fx = new ParticleEyeOfHarmony(world, pos, r, g, b, scale);
                Minecraft.getMinecraft().effectRenderer.addEffect(fx);
                angle = angle + lightRotateSpeed;
                if (angle > 360) angle -= 360;
            }
        }

        ringSpinSpeed = 360 / 20.0f;
        if (120 - collapsing * 120 <= 15) finalPhase = true;
        if (collapsing > 0.95) {
            double percent = (collapsing - 0.95) / 0.05;
            ringSpinSpeed += 10800 / 20.0f * (float) percent;
        }
        if (finalPhase) ringAlpha = MathHelper.clamp(ringAlpha + 0.025f, 0, 1);
        ringAngle = MathHelper.positiveModulo(ringAngle + ringSpinSpeed, 360);
    }

    @SideOnly(Side.CLIENT)
    void pullLocal() {
        pull(Minecraft.getMinecraft().player);
        Vec3d p = LeafiaHelper.getBlockPosCenter(pos);
        EntityNukeFolkvangr.VacuumInstance vacuum = new EntityNukeFolkvangr.VacuumInstance(p, 0, 10, getPullRange(), 0.1);
        p = new FiaMatrix(p).rotateY(world.rand.nextDouble() * 360).rotateX(world.rand.nextDouble() * 360)
                            .translate(0, 0, world.rand.nextDouble() * getPullRange()).position;
        ParticleFleijaVacuum fx = new ParticleFleijaVacuum(Minecraft.getMinecraft().world, p.x, p.y, p.z, world.rand.nextFloat() * 2 + 2, world.rand.nextFloat() * 0.1f + 0.1f, vacuum);
        Minecraft.getMinecraft().effectRenderer.addEffect(fx);
    }

    // =====================
    // ===== Effects =======
    // =====================
    public void shock() {
        double rlen = 1;
        double length = 0.5;
        Vec3d core = LeafiaHelper.getBlockPosCenter(pos);
        Vec3d p0 = LeafiaHelper.getBlockPosCenter(pos);
        Vec3d p1 = new FiaMatrix(p0).rotateY(world.rand.nextDouble() * 360).rotateX(world.rand.nextDouble() * 360)
                                    .translate(0, 0, length + world.rand.nextDouble() * rlen).position;
        DFCShockPacket packet = new DFCShockPacket();
        packet.pos = pos;
        packet.poses0.add(p0);
        packet.poses0.add(p1);
//        Tracker._startProfile(this, "shock");
        for (int i = 0; i < 25; i++) {
            p0 = p1;
            p1 = new FiaMatrix(p1, core).translate(world.rand.nextGaussian() * 2, world.rand.nextGaussian() * 2, length + world.rand.nextDouble() * rlen).position;
            RayTraceResult res = LeafiaLib.leafiaRayTraceBlocks(world, p0, p1, false, true, false);
            if (res != null && res.hitVec != null) {
                p1 = res.hitVec;
                packet.poses0.add(p1);
                world.newExplosion(null, p1.x, p1.y, p1.z, world.rand.nextFloat() * 5 + 2, true, true);
                break;
            }
            packet.poses0.add(p1);
        }
//        for (int i = 0; i < packet.poses0.size() - 1; i++) Tracker._traceLine(this, packet.poses0.get(i), packet.poses0.get(i + 1), i);
//        Tracker._endProfile(this);
        LeafiaCustomPacket.__start(packet).__sendToAll();
    }


    @Override
    public void validate() {
        super.validate();
        MinecraftServer server = world.getMinecraftServer();
        if (server != null && !world.isRemote) {
            if (!server.isDedicatedServer()) LeafiaPassiveLocal.trackingCores.add(this);
        }
        if (world.isRemote) {
            meltdownSFX = AddonBase.proxy.getLoopedSound(LeafiaSoundEvents.dfc_meltdown, SoundCategory.BLOCKS, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, 1.0f, 1)
                                         .setCustomAttentuation((intended, distance) -> Math.pow(MathHelper.clamp(1 - (distance - 50) / 500, 0, 1), 6.66))
                                         .setLooped(false);
            extinguishSFX = AddonBase.proxy.getLoopedSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, 1.0f, 0.8f)
                                           .setCustomAttentuation((intended, distance) -> Math.pow(MathHelper.clamp(1 - (distance - 50) / 500, 0, 1), 6.66))
                                           .setLooped(false);
            overloadSFX = AddonBase.proxy.getLoopedSound(LeafiaSoundEvents.overload, SoundCategory.BLOCKS, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, 1.0f, 1)
                                         .setCustomAttentuation((intended, distance) -> Math.pow(MathHelper.clamp(1 - (distance - 20) / 300, 0, 1), 6.66))
                                         .setLooped(false);
            explosionsSFX = AddonBase.proxy.getLoopedSound(LeafiaSoundEvents.longexplosion, SoundCategory.BLOCKS, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, 1.0f, 1)
                                           .setCustomAttentuation((intended, distance) -> Math.pow(MathHelper.clamp(1 - (distance - 50) / 500, 0, 1), 6.66))
                                           .setLooped(false);
        }
    }

    @Override
    public void invalidate() {
        if (client_sfx != null) {
            client_sfx.stopSound();
            client_sfx = null;
            sfxPlaying = false;
        }
        if (meltdownSFX != null) {
            meltdownSFX.stopSound();
            meltdownSFX = null;
        }
        if (extinguishSFX != null) {
            extinguishSFX.stopSound();
            extinguishSFX = null;
        }
        if (overloadSFX != null) {
            overloadSFX.stopSound();
            overloadSFX = null;
        }
        if (explosionsSFX != null) {
            explosionsSFX.stopSound();
            explosionsSFX = null;
        }
        MinecraftServer server = world.getMinecraftServer();
        if (server != null && !world.isRemote) {
            if (!server.isDedicatedServer()) LeafiaPassiveLocal.trackingCores.remove(this);
        }
        super.invalidate();
    }


    /// ---------------------------- HELPERS FROM LCE ---------------------------- ///
    private void vaporization() {
        double scale = (int) Math.log(temperature / 50 + 1) * 1.25 / 4 + 0.5;
        int range = (int) (scale * 4);
        List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos.getX() - range + 0.5, pos.getY() - range + 0.5, pos.getZ() - range + 0.5, pos.getX() + range + 0.5, pos.getY() + range + 0.5, pos.getZ() + range + 0.5));
        for (Entity e : list) {
            if (isFixTool(e)) continue;
            if (e instanceof EntityFallingBlock) continue;
            boolean isPlayer = e instanceof EntityPlayer;
            if (!(isPlayer && ArmorUtil.checkForHazmat((EntityPlayer) e))) {
                if (!(Library.isObstructed(world, pos.getX() + 0.5, pos.getY() + 0.5 + 6, pos.getZ() + 0.5, e.posX, e.posY + e.getEyeHeight(), e.posZ))) {
                    if (!isPlayer || (isPlayer && !((EntityPlayer) e).capabilities.isCreativeMode))
                        e.attackEntityFrom(LeafiaDamageSource.dfc, (int) (this.temperature / 100));
                    e.setFire(3);
                }
            }
            if (isPlayer) AdvancementManager.grantAchievement(((EntityPlayer) e), AdvancementManager.progress_dfc);
        }

        List<Entity> list3 = world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos.getX() + 0.4, pos.getY() + 0.4, pos.getZ() + 0.4, pos.getX() + 0.6, pos.getY() + 0.6, pos.getZ() + 0.6));
        if (collapsing > 0) {
            for (Entity e : list3) {
                if (isFixTool(e) || isSurvivalFixTool(e)) {
                    e.setEntityInvulnerable(false);
                    e.setDead();
                    world.createExplosion(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 20, false);
                    if (finalPhase || wasBoosted || isSurvivalFixTool(e) && world.rand.nextInt(100) < 80 - lastStabilizers * 10) {
                        world.playSound(null, pos, LeafiaSoundEvents.crucifix_fail, SoundCategory.BLOCKS, 20, 1);
                        continue;
                    }
                    temperature = 0;
                    containedEnergy = 0;
                    tanks[0].drain(1000000000, true);
                    tanks[1].drain(1000000000, true);
                    world.playSound(null, pos, LeafiaSoundEvents.crucifix, SoundCategory.BLOCKS, 20, 1);
                    continue;
                }
                e.attackEntityFrom(LeafiaDamageSource.dfcMeltdown, (int) (this.temperature / 10));
                if (!(e instanceof EntityLivingBase)) e.setDead();
            }
        }
    }

    private void broadcastState() {
        NBTTagCompound fluidA = new NBTTagCompound();
        NBTTagCompound fluidB = new NBTTagCompound();
        tanks[0].writeToNBT(fluidA, "t0");
        tanks[1].writeToNBT(fluidB, "t1");
        Integer coreId = null;
        try {
            coreId = Cores.valueOf(inventory.getStackInSlot(1).getItem().getRegistryName().getPath()).ordinal();
        } catch (IllegalArgumentException | NullPointerException ignored) {}
        LeafiaPacket._start(this)
                    .__write(packetKeys.TANK_A.key, fluidA)
                    .__write(packetKeys.TANK_B.key, fluidB)
                    .__write(packetKeys.TEMP.key, temperature)
                    .__write(packetKeys.STABILIZATION.key, stabilization)
                    .__write(packetKeys.CONTAINED.key, containedEnergy)
                    .__write(packetKeys.EXPELLING.key, expellingEnergy)
                    .__write(packetKeys.POTENTIAL.key, potentialGain)
                    .__write(packetKeys.EXPEL_TICK.key, expellingSpk)
                    .__write(packetKeys.MAXIMUM.key, meltingPoint)
                    .__write(packetKeys.COLOR.key, color)
                    .__write(packetKeys.COLOR_CATALYST.key, colorCatalyst)
                    .__write(packetKeys.CORE_TYPE.key, coreId)
                    .__write(packetKeys.JAMMER.key, jammerPos)
                    .__write(packetKeys.COLLAPSE.key, collapsing)
                    .__write(packetKeys.HASCORE.key, hasCore)
                    .__sendToAffectedClients();
    }

    private double getPullRange() {
        return 150;
    }

    private double getPull(Entity e) {
        Vec3d p = new Vec3d(pos).add(0.5, 0.5, 0.5);
        double distance = p.distanceTo(e.getPositionVector());
        if (distance > getPullRange()) return 0;
        double pull = MathHelper.clamp(1 - distance / getPullRange(), 0, 1);
        pull = Math.pow(pull, 1.5);
        return pull * 0.1 * Math.pow(Math.max(0, collapsing / 0.666 - 1) * 2, 0.5);
    }

    public void pull(Entity e) {
        double pull = getPull(e);
        if (pull <= 0) return;
        Vec3d p = new Vec3d(pos).add(0.5, 0.5, 0.5);
        Vec3d lookAt = new FiaMatrix(e.getPositionVector(), p).frontVector;
        e.addVelocity(lookAt.x * pull, lookAt.y * pull, lookAt.z * pull);
    }

    @Unique
    private double getStabilizationDiv() {
        return 1.0 + Math.sqrt(stabilization * 10.0);
    }

    @Unique
    private double getStabilizationDivAlt() {
        return Math.pow(stabilization / 3.0, 2.0) * 3.0 + 1.0;
    }

    @Unique
    private int getCorePower() {
        return ItemAMSCore.getPowerBase(inventory.getStackInSlot(1));
    }

    @Unique
    private float getCoreHeat() {
        return ItemAMSCore.getHeatBase(inventory.getStackInSlot(1));
    }

    @Unique
    private float getCoreFuel() {
        return ItemAMSCore.getFuelBase(inventory.getStackInSlot(1));
    }

    boolean isFixTool(Entity e) {
        return e instanceof EntityItem && ((EntityItem) e).getItem().getItem() == AddonItems.fix_tool;
    }

    boolean isSurvivalFixTool(Entity e) {
        return e instanceof EntityItem && ((EntityItem) e).getItem().getItem() == AddonItems.fix_survival;
    }

    /// -------------------------- LeafiaPacketReceiver -------------------------- ///

    @Override
    public String getPacketIdentifier() { return "dfcore"; }

    @Override
    @SideOnly(Side.CLIENT)
    public void onReceivePacketLocal(byte key, Object value) {
        for (packetKeys pkt : packetKeys.values()) {
            if (key == pkt.key) {
                switch (pkt) {
                    case TEMP: temperature = (double) value; break;
                    case STABILIZATION: stabilization = (double) value; break;
                    case MAXIMUM: meltingPoint = (int) value; break;
                    case CONTAINED: containedEnergy = (double) value; break;
                    case EXPELLING: expellingEnergy = (double) value; break;
                    case POTENTIAL: potentialGain = (double) value; break;
                    case TANK_A: tanks[0].readFromNBT((NBTTagCompound) value, "t0"); break;
                    case TANK_B: tanks[1].readFromNBT((NBTTagCompound) value, "t1"); break;
                    case EXPEL_TICK: expellingSpk = (double) value; break;
                    case COLOR: color = (int) value; break;
                    case COLOR_CATALYST: colorCatalyst = (int) value; break;
                    case JAMMER: jammerPos = (BlockPos) value; break;
                    case CORE_TYPE:
                        Cores lastCore = client_type;
                        Integer id = (Integer) value;
                        client_type = id == null ? null : Cores.values()[id];
                        if (client_type != lastCore) {
                            if (client_sfx != null) { client_sfx.stopSound(); client_sfx = null; sfxPlaying = false; }
                            if (client_type != null) {
                                client_sfx = AddonBase.proxy.getLoopedSound(
                                        client_type.sfx, SoundCategory.BLOCKS,
                                        pos.getX(), pos.getY(), pos.getZ(),
                                        1, 1
                                ).setCustomAttentuation(client_type.attentuationFunction);
                            }
                        }
                        break;
                    case PLAY_SOUND:
                        if (value == null) break;
                        int type = (int) value;
                        if (type == 0 || type == 1) {
                            if (meltdownSFX != null) meltdownSFX.stopSound();
                            if (overloadSFX != null && type == 1) overloadSFX.stopSound();
                            if (type == 0 && meltdownSFX != null) meltdownSFX.startSound();
                            if (type == 1 && extinguishSFX != null) extinguishSFX.startSound();
//                            if (type == 1) LeafiaDebug.debugLog(world, "STOP: 1");
                        } else if (type == 2 && overloadSFX != null) {
                            overloadSFX.startSound();
                        } else if (type == 3 && explosionsSFX != null) {
                            explosionsSFX.startSound();
                            finalPhase = true;
                        }
                        break;
                    case COLLAPSE: collapsing = (double) value; break;
                    case HASCORE: hasCore = (boolean) value; break;
                }
            }
        }
    }

    @Override
    public void onReceivePacketServer(byte key, Object value, EntityPlayer plr) { }
    @Override
    public void onPlayerValidate(EntityPlayer plr) { }
    @Override
    public double affectionRange() { return 300; }

    /// ------------------------- STATIC HELPERS FROM LCE ------------------------- ///

    @Unique
    @Contract(pure = true)
    private static int calcAvgHex(int h1, int h2) {
        int r1 = ((h1 & 0xFF0000) >> 16);
        int g1 = ((h1 & 0x00FF00) >> 8);
        int b1 = ((h1 & 0x0000FF));
        int r2 = ((h2 & 0xFF0000) >> 16);
        int g2 = ((h2 & 0x00FF00) >> 8);
        int b2 = ((h2 & 0x0000FF));
        int r = (((r1 + r2) / 2) << 16);
        int g = (((g1 + g2) / 2) << 8);
        int b = (((b1 + b2) / 2));
        return r | g | b;
    }

    /// ------------------------------- SHADOW ZONE ------------------------------- ///

    @Shadow
    public abstract float getFuelEfficiency(FluidType type);

    /// ---------------------------- BOILERPLATE ZONE ---------------------------- ///
    @Unique
    @Override
    public double getDFCFailsafeLevel() {
        return IMixinTileEntityCore.failsafeLevel;
    }

    @Unique
    @Override
    public double getDFCMaxEnergy() {
        return IMixinTileEntityCore.maxEnergy;
    }

    // core/state
    @Unique
    @Override
    public boolean isDFCHasCore() {
        return hasCore;
    }

    @Unique
    @Override
    public void setDFCHasCore(boolean v) {
        hasCore = v;
    }

    @Unique
    @Override
    public double getDFCTemperature() {
        return temperature;
    }

    @Unique
    @Override
    public void setDFCTemperature(double v) {
        temperature = v;
    }

    @Unique
    @Override
    public double getDFCStabilization() {
        return stabilization;
    }

    @Unique
    @Override
    public void setDFCStabilization(double v) {
        stabilization = v;
    }

    @Unique
    @Override
    public double getDFCContainedEnergy() {
        return containedEnergy;
    }

    @Unique
    @Override
    public void setDFCContainedEnergy(double v) {
        containedEnergy = v;
    }

    @Unique
    @Override
    public double getDFCExpellingEnergy() {
        return expellingEnergy;
    }

    @Unique
    @Override
    public void setDFCExpellingEnergy(double v) {
        expellingEnergy = v;
    }

    @Unique
    @Override
    public double getDFCPotentialGain() {
        return potentialGain;
    }

    @Unique
    @Override
    public void setDFCPotentialGain(double v) {
        potentialGain = v;
    }

    @Unique
    @Override
    public double getDFCGainedEnergy() {
        return gainedEnergy;
    }

    @Unique
    @Override
    public void setDFCGainedEnergy(double v) {
        gainedEnergy = v;
    }

    @Unique
    @Override
    public double getDFCCollapsing() {
        return collapsing;
    }

    @Unique
    @Override
    public void setDFCCollapsing(double v) {
        collapsing = v;
    }

    @Unique
    @Override
    public int getDFCStabilizers() {
        return stabilizers;
    }

    @Unique
    @Override
    public void setDFCStabilizers(int v) {
        stabilizers = v;
    }

    @Unique
    @Override
    public int getDFCLastStabilizers() {
        return lastStabilizers;
    }

    @Unique
    @Override
    public void setDFCLastStabilizers(int v) {
        lastStabilizers = v;
    }

    @Unique
    @Override
    public boolean isDFCWasBoosted() {
        return wasBoosted;
    }

    @Unique
    @Override
    public void setDFCWasBoosted(boolean v) {
        wasBoosted = v;
    }

    @Unique
    @Override
    public double[] getDFCExpelTicks() {
        return expelTicks;
    }

    @Unique
    @Override
    public void setDFCExpelTicks(double[] v) {
        expelTicks = v;
    }

    @Unique
    @Override
    public double getDFCEnergyMod() {
        return energyMod;
    }

    @Unique
    @Override
    public void setDFCEnergyMod(double v) {
        energyMod = v;
    }

    @Unique
    @Override
    public double getDFCBonus() {
        return bonus;
    }

    @Unique
    @Override
    public void setDFCBonus(double v) {
        bonus = v;
    }

    @Unique
    @Override
    public List<TileEntityCoreReceiver> getDFCAbsorbers() {
        return absorbers;
    }

    @Unique
    @Override
    public void setDFCAbsorbers(List<TileEntityCoreReceiver> v) {
        absorbers = v;
    }

    @Unique
    @Override
    public boolean isDFCDestroyed() {
        return destroyed;
    }

    @Unique
    @Override
    public void setDFCDestroyed(boolean v) {
        destroyed = v;
    }

    @Unique
    @Override
    public double getDFCExplosionIn() {
        return explosionIn;
    }

    @Unique
    @Override
    public void setDFCExplosionIn(double v) {
        explosionIn = v;
    }

    @Unique
    @Override
    public long getDFCExplosionClock() {
        return explosionClock;
    }

    @Unique
    @Override
    public void setDFCExplosionClock(long v) {
        explosionClock = v;
    }

    @Unique
    @Override
    public BlockPos getDFCJammerPos() {
        return jammerPos;
    }

    @Unique
    @Override
    public void setDFCJammerPos(BlockPos v) {
        jammerPos = v;
    }

    @Unique
    @Override
    public List<BlockPos> getDFCComponentPositions() {
        return componentPositions;
    }

    @Unique
    @Override
    public void setDFCComponentPositions(List<BlockPos> v) {
        componentPositions = v;
    }

    @Unique
    @Override
    public List<BlockPos> getDFCPrevComponentPositions() {
        return prevComponentPositions;
    }

    @Unique
    @Override
    public void setDFCPrevComponentPositions(List<BlockPos> v) {
        prevComponentPositions = v;
    }

    @Unique
    @Override
    public double getDFCIncomingSpk() {
        return incomingSpk;
    }

    @Unique
    @Override
    public void setDFCIncomingSpk(double v) {
        incomingSpk = v;
    }

    @Unique
    @Override
    public double getDFCExpellingSpk() {
        return expellingSpk;
    }

    @Unique
    @Override
    public void setDFCExpellingSpk(double v) {
        expellingSpk = v;
    }

    @Unique
    @Override
    public int getDFCMeltingPoint() {
        return meltingPoint;
    }

    @Unique
    @Override
    public void setDFCMeltingPoint(int v) {
        meltingPoint = v;
    }

    @Unique
    @Override
    public int getDFCTicks() {
        return ticks;
    }

    @Unique
    @Override
    public void setDFCTicks(int v) {
        ticks = v;
    }

    @Unique
    @Override
    public int getDFCOverloadTimer() {
        return overloadTimer;
    }

    @Unique
    @Override
    public void setDFCOverloadTimer(int v) {
        overloadTimer = v;
    }

    @Unique
    @Override
    public int getDFCColorCatalyst() {
        return colorCatalyst;
    }

    @Unique
    @Override
    public void setDFCColorCatalyst(int v) {
        colorCatalyst = v;
    }

    @Unique
    @Override
    public int getDFCShockCooldown() {
        return shockCooldown;
    }

    @Unique
    @Override
    public void setDFCShockCooldown(int v) {
        shockCooldown = v;
    }

    // client/visual
    @Unique
    @Override
    public double getDFCClientMaxDial() {
        return client_maxDial;
    }

    @Unique
    @Override
    public void setDFCClientMaxDial(double v) {
        client_maxDial = v;
    }

    @Unique
    @Override
    public Cores getDFCClientType() {
        return client_type;
    }

    @Unique
    @Override
    public void setDFCClientType(Cores v) {
        client_type = v;
    }

    @Unique
    @Override
    public LCEAudioWrapper getDFCClientSfx() {
        return client_sfx;
    }

    @Unique
    @Override
    public void setDFCClientSfx(LCEAudioWrapper v) {
        client_sfx = v;
    }

    @Unique
    @Override
    public boolean isDFCSfxPlaying() {
        return sfxPlaying;
    }

    @Unique
    @Override
    public void setDFCSfxPlaying(boolean v) {
        sfxPlaying = v;
    }

    @Unique
    @Override
    public LCEAudioWrapper getDFCMeltdownSFX() {
        return meltdownSFX;
    }

    @Unique
    @Override
    public void setDFCMeltdownSFX(LCEAudioWrapper v) {
        meltdownSFX = v;
    }

    @Unique
    @Override
    public LCEAudioWrapper getDFCOverloadSFX() {
        return overloadSFX;
    }

    @Unique
    @Override
    public void setDFCOverloadSFX(LCEAudioWrapper v) {
        overloadSFX = v;
    }

    @Unique
    @Override
    public LCEAudioWrapper getDFCExtinguishSFX() {
        return extinguishSFX;
    }

    @Unique
    @Override
    public void setDFCExtinguishSFX(LCEAudioWrapper v) {
        extinguishSFX = v;
    }

    @Unique
    @Override
    public LCEAudioWrapper getDFCExplosionsSFX() {
        return explosionsSFX;
    }

    @Unique
    @Override
    public void setDFCExplosionsSFX(LCEAudioWrapper v) {
        explosionsSFX = v;
    }

    @Unique
    @Override
    public float getDFCAngle() {
        return angle;
    }

    @Unique
    @Override
    public void setDFCAngle(float v) {
        angle = v;
    }

    @Unique
    @Override
    public float getDFCLightRotateSpeed() {
        return lightRotateSpeed;
    }

    @Unique
    @Override
    public void setDFCLightRotateSpeed(float v) {
        lightRotateSpeed = v;
    }

    @Unique
    @Override
    public boolean isDFCFinalPhase() {
        return finalPhase;
    }

    @Unique
    @Override
    public void setDFCFinalPhase(boolean v) {
        finalPhase = v;
    }

    @Unique
    @Override
    public float getDFCRingSpinSpeed() {
        return ringSpinSpeed;
    }

    @Unique
    @Override
    public void setDFCRingSpinSpeed(float v) {
        ringSpinSpeed = v;
    }

    @Unique
    @Override
    public float getDFCRingAngle() {
        return ringAngle;
    }

    @Unique
    @Override
    public void setDFCRingAngle(float v) {
        ringAngle = v;
    }

    @Unique
    @Override
    public float getDFCRingAlpha() {
        return ringAlpha;
    }

    @Unique
    @Override
    public void setDFCRingAlpha(float v) {
        ringAlpha = v;
    }

    public List<DFCShock> getDfcShocks() {
        return dfcShocks;
    }
}
