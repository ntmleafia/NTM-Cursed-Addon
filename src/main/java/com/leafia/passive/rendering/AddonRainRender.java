// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.passive.rendering;

import com.hbm.render.NTMRenderHelper;
import com.hbm.util.RenderUtil;
import com.leafia.contents.worldgen.biomes.artificial.DigammaCrater;
import com.leafia.init.LeafiaSoundEvents;
import com.leafia.savedata.FalloutSavedData;
import com.leafia.savedata.FalloutSavedData.FalloutData;
import com.leafia.unsorted.ParticleRainBlood;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.Random;

@SideOnly(Side.CLIENT)
public class AddonRainRender {
    public static AddonRainRender INSTANCE = new AddonRainRender();
    private static final ResourceLocation BLOOD = new ResourceLocation("leafia", "textures/bloodrain.png");
    private static final ResourceLocation FALLOUT = new ResourceLocation("leafia", "textures/fallout_dense.png");
    private static final int MAX_RADIUS = 10;
    private static final int DIAMETER = MAX_RADIUS * 2 + 1;
    private static final float[] HALF_X = new float[DIAMETER * DIAMETER];
    private static final float[] HALF_Z = new float[DIAMETER * DIAMETER];

    static {
        for (var index = 0; index < HALF_X.length; index++) {
            var x = index % DIAMETER - MAX_RADIUS;
            var z = index / DIAMETER - MAX_RADIUS;
            if (x == 0 && z == 0) continue;
            var length = MathHelper.sqrt(x * x + z * z);
            HALF_X[index] = -z / length * 0.5F;
            HALF_Z[index] = x / length * 0.5F;
        }
    }

    private final Minecraft mc = Minecraft.getMinecraft();
    private final Random random = new Random();
    private int rainSoundCounter;
    public int rendererUpdateCount;

    public void update() {
        rendererUpdateCount++;
        addRainParticles();
    }

    public void render(float partialTicks) {
        var lighting = RenderUtil.isLightingEnabled();
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        GlStateManager.disableLighting();
        GlStateManager.color(1, 1, 1, 1);
        NTMRenderHelper.resetColor();
        renderFalloutRain(partialTicks);
        renderBloodRain(partialTicks);
        if (lighting) GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }

    private void addRainParticles() {
        random.setSeed((long) rendererUpdateCount * 312987231L);
        var camera = new BlockPos(mc.getRenderViewEntity());
        var world = mc.world;
        var attempts = mc.gameSettings.fancyGraphics ? 100 : 25;
        if (mc.gameSettings.particleSetting == 1) attempts >>= 1;
        if (mc.gameSettings.particleSetting == 2) attempts = 0;
        var hits = 0;
        double soundX = 0, soundY = 0, soundZ = 0;

        for (var attempt = 0; attempt < attempts; attempt++) {
            var dx = random.nextInt(10) - random.nextInt(10);
            var dz = random.nextInt(10) - random.nextInt(10);
            var surface = world.getPrecipitationHeight(camera.add(dx, 0, dz));
            if (surface.getY() < camera.getY() - 10 || surface.getY() > camera.getY() + 10
                    || !DigammaCrater.isDigammaBiome(world.getBiome(surface))) continue;

            var ground = surface.down();
            var state = world.getBlockState(ground);
            var x = ground.getX() + random.nextDouble();
            var z = ground.getZ() + random.nextDouble();
            var box = state.getBoundingBox(world, ground);
            if (state.getMaterial() == Material.LAVA || state.getBlock() == Blocks.MAGMA) {
                world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x,
                        (double) ((float) surface.getY() + 0.1F) - box.minY, z, 0, 0, 0);
            } else if (state.getMaterial() != Material.AIR) {
                var y = (double) ((float) ground.getY() + 0.1F) + box.maxY;
                if (random.nextInt(++hits) == 0) {
                    soundX = x;
                    soundY = y - 1;
                    soundZ = z;
                }
                mc.effectRenderer.addEffect(new ParticleRainBlood(world, x, y, z));
            }
        }

        if (hits == 0 || random.nextInt(3) >= rainSoundCounter++) return;
        rainSoundCounter = 0;
        var above = soundY > camera.getY() + 1
                && world.getPrecipitationHeight(camera).getY() > MathHelper.floor((float) camera.getY());
        world.playSound(soundX, soundY, soundZ,
                above ? LeafiaSoundEvents.sbesrottenrain_above : LeafiaSoundEvents.sbesrottenrain,
                SoundCategory.WEATHER, 0.3F, 1, false);
    }

    protected void renderBloodRain(float partialTicks) {
        renderColumns(partialTicks, null, 1);
    }

    protected void renderFalloutRain(float partialTicks) {
        var camera = mc.getRenderViewEntity();
        var fallout = FalloutSavedData.forWorld(mc.world).getDensest(new Vec3d(camera.posX, 0, camera.posZ));
        if (fallout == null) return;
        var density = (float) fallout.getDensity();
        var strength = (float) Math.min(Math.pow(density * 2, 0.5), 1);
        if (strength > 0) renderColumns(partialTicks, fallout, strength);
    }

    private void renderColumns(float partialTicks, FalloutData fallout, float strength) {
        var camera = mc.getRenderViewEntity();
        var world = mc.world;
        var blood = fallout == null;
        var radius = mc.gameSettings.fancyGraphics ? MAX_RADIUS : MAX_RADIUS / 2;
        var centerX = MathHelper.floor(camera.posX);
        var centerY = MathHelper.floor(camera.posY);
        var centerZ = MathHelper.floor(camera.posZ);
        var cameraX = camera.lastTickPosX + (camera.posX - camera.lastTickPosX) * partialTicks;
        var cameraY = camera.lastTickPosY + (camera.posY - camera.lastTickPosY) * partialTicks;
        var cameraZ = camera.lastTickPosZ + (camera.posZ - camera.lastTickPosZ) * partialTicks;
        var age = rendererUpdateCount + partialTicks;
        var verticalScroll = ((rendererUpdateCount & 511) + partialTicks) / 512.0F;
        var position = new BlockPos.MutableBlockPos();
        var tessellator = Tessellator.getInstance();
        var buffer = tessellator.getBuffer();
        var drawing = false;

        GlStateManager.disableCull();
        GlStateManager.glNormal3f(0, 1, 0);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.color(1, 1, 1, 1);
        NTMRenderHelper.resetColor();

        for (var z = centerZ - radius; z <= centerZ + radius; z++) {
            for (var x = centerX - radius; x <= centerX + radius; x++) {
                // A perpendicular curtain has no direction at the camera's own column.
                if (x == centerX && z == centerZ) continue;
                position.setPos(x, 50, z);
                if (blood && !DigammaCrater.isDigammaBiome(world.getBiomeForCoordsBody(position))) continue;
                var roof = world.getPrecipitationHeight(position).getY();
                var lower = Math.max(centerY - radius, roof);
                var upper = Math.max(centerY + radius, roof);
                if (lower == upper) continue;

                if (!drawing) {
                    mc.getTextureManager().bindTexture(blood ? BLOOD : FALLOUT);
                    buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
                    buffer.setTranslation(-cameraX, -cameraY, -cameraZ);
                    drawing = true;
                }

                random.setSeed(RainColumnGeometry.seed(x, z));
                float u = 0, drift = 0;
                double bloodScroll = 0;
                if (blood) {
                    var phase = rendererUpdateCount + lower * lower * 3121 + lower * 45238971
                            + roof * roof * 418711 + roof * 13761 & 31;
                    bloodScroll = -(phase + (double) partialTicks) / 32.0D * (3.0D + random.nextDouble());
                } else {
                    u = random.nextFloat() + age * 0.01F * (float) random.nextGaussian();
                    drift = random.nextFloat() + age * (float) random.nextGaussian() * 0.001F;
                }

                var opacity = blood ? 1 : (float) (strength * RainColumnGeometry.falloutMultiplier(
                        fallout.pos.x, fallout.pos.y, fallout.pos.z, fallout.radius, x, z));
                var alpha = RainColumnGeometry.radialFade(camera.posX, camera.posZ, x, z, radius) * opacity;
                var direction = (z - centerZ + MAX_RADIUS) * DIAMETER + x - centerX + MAX_RADIUS;
                RainColumnGeometry.emit(buffer, x, z, lower, upper, HALF_X[direction], HALF_Z[direction],
                        alpha, u, verticalScroll, drift, bloodScroll, blood);
            }
        }

        if (drawing) {
            buffer.setTranslation(0, 0, 0);
            tessellator.draw();
        }
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
    }
}
