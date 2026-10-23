// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.custom_hbm.effectNT;

import com.leafia.unsorted.ParticleRedstoneLight;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

/** Packet effect selection; each factory retains ownership of its particle behavior. */
public class EffectNT {
    public static void effectNT(NBTTagCompound data) {
        var client = Minecraft.getMinecraft();
        var world = client.world;
        if (world == null) return;
        var x = data.getDouble("posX");
        var y = data.getDouble("posY");
        var z = data.getDouble("posZ");
        var motionX = data.getDouble("mX");
        var motionY = data.getDouble("mY");
        var motionZ = data.getDouble("mZ");
        var particle = switch (data.getString("type")) {
            case "rslight" -> new ParticleRedstoneLight(world, x, y, z,
                    data.getFloat("scale") == 0 ? 1 : data.getFloat("scale"),
                    motionX, motionY, motionZ,
                    data.getFloat("red"), data.getFloat("green"), data.getFloat("blue"));
            case "vanillaExt" -> vanilla(data, world, x, y, z, motionX, motionY, motionZ);
            default -> null;
        };
        if (particle != null) client.effectRenderer.addEffect(particle);
    }

    private static Particle vanilla(NBTTagCompound data, World world, double x, double y, double z,
                                    double motionX, double motionY, double motionZ) {
        return switch (data.getString("mode")) {
            case "flame" -> new ParticleFlame.Factory().createParticle(-1, world, x, y, z, motionX, motionY, motionZ);
            case "lava" -> {
                world.spawnParticle(EnumParticleTypes.LAVA, x, y, z, motionX, motionY, motionZ);
                yield null;
            }
            case "smoke" -> smoke(world, x, y, z, motionX, motionY, motionZ);
            case "volcano" -> {
                var particle = smoke(world, x, y, z, motionX, motionY, motionZ);
                LCEParticleUtility.setSmokeScale((ParticleSmokeNormal) particle, 100);
                LCEParticleUtility.setMaxAge(particle, 200 + world.rand.nextInt(50));
                LCEParticleUtility.setNoClip(particle);
                LCEParticleUtility.setMotion(particle, world.rand.nextGaussian() * 0.2,
                        2.5 + world.rand.nextDouble(), world.rand.nextGaussian() * 0.2);
                yield particle;
            }
            case "cloud" -> new ParticleCloud.Factory().createParticle(-1, world, x, y, z, motionX, motionY, motionZ);
            case "reddust" -> new ParticleRedstone.Factory().createParticle(-1, world, x, y, z,
                    (float) motionX, (float) motionY, (float) motionZ);
            case "bluedust" -> new ParticleRedstone.Factory().createParticle(-1, world, x, y, z, 0.01F, 0.01F, 1F);
            case "greendust" -> new ParticleRedstone.Factory().createParticle(-1, world, x, y, z, 0.01F, 0.5F, 0.1F);
            case "largeexplode" -> explosion(data, world, x, y, z);
            case "townaura" -> {
                var particle = new ParticleSuspendedTown.Factory().createParticle(-1, world, x, y, z, 0, 0, 0);
                var brightness = 0.5F + world.rand.nextFloat() * 0.5F;
                particle.setRBGColorF(0.8F * brightness, 0.9F * brightness, brightness);
                LCEParticleUtility.setMotion(particle, motionX, motionY, motionZ);
                yield particle;
            }
            case "blockdust" -> {
                var state = Block.getBlockById(data.getInteger("block")).getDefaultState();
                var particle = new ParticleBlockDust.Factory().createParticle(-1, world, x, y, z,
                        motionX, motionY + 0.2, motionZ, Block.getStateId(state));
                LCEParticleUtility.setMaxAge(particle, 10 + world.rand.nextInt(20));
                yield particle;
            }
            default -> null;
        };
    }

    private static Particle smoke(World world, double x, double y, double z,
                                  double motionX, double motionY, double motionZ) {
        return new ParticleSmokeNormal.Factory().createParticle(-1, world, x, y, z, motionX, motionY, motionZ);
    }

    private static Particle explosion(NBTTagCompound data, World world, double x, double y, double z) {
        var primary = new ParticleExplosionLarge.Factory().createParticle(-1, world, x, y, z,
                data.getFloat("size"), 0, 0);
        var tint = 1F - world.rand.nextFloat() * 0.2F;
        primary.setRBGColorF(tint, tint * 0.9F, tint * 0.5F);
        var effects = Minecraft.getMinecraft().effectRenderer;
        for (var index = 0; index < data.getByte("count"); index++) {
            var secondary = new ParticleExplosion.Factory().createParticle(-1, world, x, y, z, 0, 0, 0);
            var shade = 0.5F * (1F - world.rand.nextFloat() * 0.5F);
            secondary.setRBGColorF(shade, shade, shade);
            secondary.multipleParticleScaleBy(index + 1);
            effects.addEffect(secondary);
        }
        return primary;
    }
}
