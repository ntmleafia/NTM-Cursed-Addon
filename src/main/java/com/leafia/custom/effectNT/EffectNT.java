package com.leafia.custom.effectNT;

import com.hbm.animloader.AnimationWrapper.EndResult;
import com.hbm.animloader.AnimationWrapper.EndType;
import com.hbm.config.GeneralConfig;
import com.hbm.main.ResourceManager;
import com.hbm.particle.*;
import com.hbm.particle.bullet_hit.ParticleBloodParticle;
import com.hbm.particle.bullet_hit.ParticleBulletImpact;
import com.hbm.particle.bullet_hit.ParticleHitDebris;
import com.hbm.particle.bullet_hit.ParticleSmokeAnim;
import com.hbm.particle_instanced.InstancedParticleRenderer;
import com.hbm.particle_instanced.ParticleExSmokeInstanced;
import com.hbm.particle_instanced.ParticleRocketFlameInstanced;
import com.hbm.render.amlfrom1710.Vec3;
import com.hbm.render.anim.BusAnimation;
import com.hbm.render.anim.BusAnimationKeyframe;
import com.hbm.render.anim.BusAnimationSequence;
import com.hbm.render.anim.HbmAnimations;
import com.hbm.render.anim.HbmAnimations.Animation;
import com.hbm.render.anim.HbmAnimations.BlenderAnimation;
import com.hbm.sound.SoundLoopCrucible;
import com.hbm.util.BobMathUtil;
import com.leafia.unsorted.ParticleBalefire;
import com.leafia.unsorted.ParticleRedstoneLight;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStainedHardenedClay;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.*;
import net.minecraft.client.particle.ParticleFirework.Spark;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;

public class EffectNT {
	public static void effectNT(NBTTagCompound data) {
		World world = Minecraft.getMinecraft().world;
		if(world == null)
			return;
		EntityPlayer player = Minecraft.getMinecraft().player;
		Random rand = world.rand;
		String type = data.getString("type");
		double x = data.getDouble("posX");
		double y = data.getDouble("posY");
		double z = data.getDouble("posZ");

		if("rslight".equals(type)) {
			float size = data.getFloat("scale");
			ParticleRedstoneLight fx = new ParticleRedstoneLight(
					world,x,y,z,(size == 0) ? 1 : size,
					data.getDouble("mX"),
					data.getDouble("mY"),
					data.getDouble("mZ"),
					data.getFloat("red"),
					data.getFloat("green"),
					data.getFloat("blue")
			);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			return;
		}

	}
}
