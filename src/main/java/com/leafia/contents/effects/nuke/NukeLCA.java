package com.leafia.contents.effects.nuke;

import com.hbm.config.BombConfig;
import com.hbm.config.CompatibilityConfig;
import com.hbm.config.GeneralConfig;
import com.hbm.entity.effect.EntityFalloutRain;
import com.hbm.entity.grenade.EntityGrenadeUniversal;
import com.hbm.entity.logic.EntityExplosionChunkloading;
import com.hbm.entity.mob.EntityGlowingOne;
import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.entity.projectile.EntityBulletBaseNT;
import com.hbm.entity.projectile.EntityExplosiveBeam;
import com.hbm.explosion.ExplosionNukeGeneric;
import com.hbm.explosion.ExplosionNukeRayBatched;
import com.hbm.explosion.ExplosionNukeRayParallelized;
import com.hbm.interfaces.IExplosionRay;
import com.hbm.items.weapon.sedna.factory.ConfettiUtil;
import com.hbm.lib.Library;
import com.hbm.lib.ModDamageSource;
import com.hbm.main.AdvancementManager;
import com.hbm.main.MainRegistry;
import com.hbm.saveddata.satellites.SatelliteDetector;
import com.hbm.saveddata.satellites.SatelliteDetector.BurstIntensity;
import com.hbm.util.ContaminationUtil;
import com.hbm.util.ContaminationUtil.ContaminationType;
import com.hbm.util.ContaminationUtil.HazardType;
import com.hbm.util.EntityDamageUtil;
import com.hbm.util.MutableVec3d;
import com.hbm.world.WorldUtil;
import com.leafia.init.LeafiaDamageSource;
import com.leafia.overwrite_contents.interfaces.IMixinEntityFalloutRain;
import com.leafia.overwrite_contents.interfaces.IMixinExplosionRay;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NukeLCA extends EntityExplosionChunkloading {
	//Strength of the blast
	private int strength;
	//How many rays are calculated per tick
	private int radius;

	private boolean fallout = true;
	private IExplosionRay explosion;
	private boolean initialized = false;
	private int falloutAdd = 0;
	private int algorithm;
	private long explosionStart = 0;
	public ExplosionType type;

	public enum ExplosionType {
		ENDOTHERMIC,
	}

	public NukeLCA setExplosionType(ExplosionType type) {
		this.type = type;
		return this;
	}

	public UUID detonator;
	public int lowestHeight = 0;

	public NukeLCA(World world) {
		super(world);
	}

	public NukeLCA(World world,int strength,int speed,int radius) {
		super(world);
		this.strength = strength;
		this.radius = radius;
		this.algorithm = BombConfig.explosionAlgorithm;
	}

	public static NukeLCA statFac(World world,int r,double x,double y,double z) {
		if (GeneralConfig.enableExtendedLogging && !world.isRemote)
			MainRegistry.logger.log(Level.INFO, "[NUKE] Initialized explosion at " + x + " / " + y + " / " + z + " with radius " + r + "!");

		if (r == 0) r = 25;

		NukeLCA mk5 = new NukeLCA(world);

		mk5.strength = 2 * r;
		mk5.radius = r;
		mk5.algorithm = BombConfig.explosionAlgorithm;

		mk5.setPosition(x, y, z);
		if (BombConfig.disableNuclear) mk5.fallout = false;
		return mk5;
	}

	public static NukeLCA statFacNoRad(World world, int r, double x, double y, double z) {

		NukeLCA mk5 = statFac(world, r, x, y, z);
		mk5.fallout = false;
		return mk5;
	}

	@Override
	public void onUpdate() {
		if (world.isRemote) return;
		requestChunkLoaderTicketIfNeeded();

		if (strength == 0 || !CompatibilityConfig.isWarDim(world)) {
			this.setDead();
			return;
		}
		if (!world.isRemote) loadChunk(chunkCoordX, chunkCoordZ);

       /* for (EntityPlayer player : this.world.playerEntities) {
            AdvancementManager.grantAchievement(player, AdvancementManager.achManhattan);
        }*/
		List<Entity> list = WorldUtil.getEntitiesInRadius(world, this.posX, this.posY, this.posZ, this.radius * 2.0D);
		if (false && fallout && explosion != null && this.ticksExisted < 10 && strength >= 75) {
			List<EntityLivingBase> livingList = new ArrayList<>(list.size());
			for (Entity e : list) if (e instanceof EntityLivingBase livingBase) livingList.add(livingBase);
			radiate(livingList, 2_500_000F / (this.ticksExisted * 5 + 1));
		}
		if (false)
			ExplosionNukeGeneric.dealDamage(world, list, this.posX, this.posY, this.posZ, this.radius * 2.0D);
		if (type == ExplosionType.ENDOTHERMIC)
			dealDamageEndothermic(world,list,posX,posY,posZ,radius,250);

		//radiate until there is fallout rain
		if (false && fallout && ticksExisted == 42)
			EntityGlowingOne.convertInRadiusToGlow((WorldServer) world, this.posX, this.posY, this.posZ, radius * 1.5);

		//Create Explosion Rays
		if (!initialized) {
			explosionStart = System.currentTimeMillis();
			if (type == ExplosionType.ENDOTHERMIC)
				explosion = new ExplosionNukeRayBatched(world, (int) posX, (int) posY, (int) posZ, strength, radius);
			else {
				if (BombConfig.explosionAlgorithm == 1 || BombConfig.explosionAlgorithm == 2)
					explosion = new ExplosionNukeRayParallelized(world, posX, posY, posZ, strength, radius, algorithm);
				else
					explosion = new ExplosionNukeRayBatched(world, (int) posX, (int) posY, (int) posZ, strength, radius);
			}
			explosion.setDetonator(detonator);
			SatelliteDetector.reportEvent(world, SatelliteDetector.DURATION_HIGH, BurstIntensity.HIGH, posX, posZ);
			initialized = true;
		}

		//Calculating crater
		if (!explosion.isComplete()) {
			explosion.update(BombConfig.mk5);
			if (explosion instanceof IMixinExplosionRay ray)
				lowestHeight = ray.leafia$getLowestHeight();
		} else {
			if (GeneralConfig.enableExtendedLogging && explosionStart != 0)
				MainRegistry.logger.log(Level.INFO, "[NUKE] Explosion complete. Time elapsed: {}ms", (System.currentTimeMillis() - explosionStart));
			if (fallout) {
				EntityFalloutRain fallout = new EntityFalloutRain(this.world);
				fallout.posX = this.posX;
				fallout.posY = this.posY;
				fallout.posZ = this.posZ;
				fallout.setScale((int)(this.radius * 2.5 + falloutAdd) * BombConfig.falloutRange / 100);
				IMixinEntityFalloutRain mixin = (IMixinEntityFalloutRain)fallout;
				if (type == ExplosionType.ENDOTHERMIC) {
					mixin.leafia$setEndothermic();
					mixin.leafia$setFloodHeight(lowestHeight+1+(int)Math.round(Math.pow(radius,0.65)/2.5));
				}
				this.world.spawnEntity(fallout);
			}
			this.setDead();
		}
	}

	private void radiate(List<EntityLivingBase> entities, float rads) {
		MutableVec3d vec = new MutableVec3d();
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		for (EntityLivingBase e : entities) {
			vec.set(e.posX - posX, (e.posY + e.getEyeHeight()) - posY, e.posZ - posZ);
			double len = vec.length();
			if (len <= 0.0001D) continue;
			vec.normalizeSelf();

			double res = 0F;
			int steps = MathHelper.floor(len);
			for (int i = 1; i < steps; i++) {
				int ix = MathHelper.floor(posX + vec.x * i);
				int iy = MathHelper.floor(posY + vec.y * i);
				int iz = MathHelper.floor(posZ + vec.z * i);
				float blockRes = world.getBlockState(pos.setPos(ix, iy, iz)).getBlock().getExplosionResistance(null);
				res += blockRes;
			}

			if (res < 1.0) res = 1.0;
			double eRads = rads;
			eRads /= res;
			eRads /= len * len;
			ContaminationUtil.contaminate(e, HazardType.RADIATION, ContaminationType.RAD_BYPASS, eRads);
		}
	}

	public NukeLCA setDetonator(Entity detonator){
		if (detonator instanceof EntityPlayerMP){
			this.detonator = detonator.getUniqueID();
		}
		return this;
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		markChunkLoaderRestoredFromNBT();
		radius = nbt.getInteger("radius");
		strength = nbt.getInteger("strength");
		falloutAdd = nbt.getInteger("falloutAdd");
		fallout = nbt.getBoolean("fallout");
		algorithm = nbt.getInteger("algorithm");
		if (nbt.hasKey("detonator"))
			detonator = nbt.getUniqueId("detonator");
		if (!initialized) {
			if (algorithm == 1 || algorithm == 2)
				explosion = new ExplosionNukeRayParallelized(world, this.posX, this.posY, this.posZ, this.strength, this.radius);
			else
				explosion = new ExplosionNukeRayBatched(world, (int) this.posX, (int) this.posY, (int) this.posZ, this.strength, this.radius);
			explosion.setDetonator(this.detonator);
		}
		explosion.readEntityFromNBT(nbt);
		initialized = true;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setInteger("radius", radius);
		nbt.setInteger("strength", strength);
		nbt.setInteger("falloutAdd", falloutAdd);
		nbt.setBoolean("fallout", fallout);
		nbt.setInteger("algorithm", algorithm);
		if (detonator != null)
			nbt.setUniqueId("detonator", detonator);
		if (explosion != null) {
			explosion.writeEntityToNBT(nbt);
		}
	}

	@Override
	public void setDead() {
		if (explosion != null) explosion.cancel();
		clearChunkLoader();
		super.setDead();
	}

	@Override
	public void onRemovedFromWorld() {
		if (explosion != null) explosion.cancel();
		super.onRemovedFromWorld();
	}

	public NukeLCA moreFallout(int fallout) {
		falloutAdd = fallout;
		return this;
	}

	public NukeLCA forceSpawn() {
		this.forceSpawn = true;
		return this;
	}

	private static boolean isExplosionExempt(Entity e) {
		if (e instanceof EntityOcelot ||
				e instanceof EntityExplosiveBeam ||
				e instanceof EntityBulletBaseNT ||
				e instanceof EntityBulletBaseMK4 ||
				e instanceof EntityGrenadeUniversal) {
			return true;
		}

		return false;
	}
	public static void dealDamageEndothermic(World world, List<Entity> list, double x, double y, double z, double radius, float maxDamage) {
		MutableVec3d knock = new MutableVec3d();
		for (Entity e : list) {
			double dist = e.getDistance(x, y, z);

			if (dist <= radius) {

				double entX = e.posX;
				double entY = e.posY + e.getEyeHeight();
				double entZ = e.posZ;

				if (!isExplosionExempt(e) && !Library.isObstructed(world, x, y, z, entX, entY, entZ)) {

					boolean doKnockback = true;
					double damage = maxDamage * (radius - dist) / radius;
					if (e instanceof EntityLivingBase living && e.isEntityAlive()) {
						doKnockback = EntityDamageUtil.attackEntityFromNT(living, LeafiaDamageSource.endothermic,
								(float) damage, true, true, 0, 100F, 0);
						if (!e.isEntityAlive()) ConfettiUtil.pulverize(living);
					} else {
						e.attackEntityFrom(LeafiaDamageSource.endothermic, (float) damage);
					}

					e.setFire(5);

					if (doKnockback) {
						knock.set(e.posX - x, e.posY + e.getEyeHeight() - y, e.posZ - z).normalizeSelf();
						e.motionX += knock.x * 0.2D;
						e.motionY += knock.y * 0.2D;
						e.motionZ += knock.z * 0.2D;
					}
				}
			}
		}
	}
}
