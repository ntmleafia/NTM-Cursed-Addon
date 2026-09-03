package com.leafia.contents.gear.combat.guns.am_rifle;

import com.hbm.entity.projectile.EntityBulletBeamBase;
import com.hbm.items.weapon.sedna.BulletConfig;
import com.hbm.util.Vec3NT;
import com.leafia.dev.math.FiaMatrix;
import com.leafia.dev.math.FiaMatrix.RotationOrder;
import com.leafia.dev.math.FiaMatrix.TupleRotation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class AMRifleBeam extends EntityBulletBeamBase {
	public AMRifleBeam(EntityLivingBase entity,Entity lockTarget,BulletConfig config,float baseDamage,double sideOffset,double heightOffset,double frontOffset) {
		this(entity.world);

		this.thrower = entity;
		this.setBulletConfig(config);

		this.damage = baseDamage * this.config.damageMult;

		if (lockTarget == null)
			this.setLocationAndAngles(thrower.posX, thrower.posY + thrower.getEyeHeight(), thrower.posZ, thrower.rotationYaw, thrower.rotationPitch);
		else {
			FiaMatrix mat = new FiaMatrix(new Vec3d(thrower.posX,thrower.posY+thrower.getEyeHeight(),thrower.posZ),new Vec3d(lockTarget.posX,lockTarget.posY+lockTarget.getEyeHeight(),lockTarget.posZ));
			TupleRotation rot = mat.getRotation(RotationOrder.YXZ);
			this.setLocationAndAngles(thrower.posX, thrower.posY + thrower.getEyeHeight(), thrower.posZ,
					(float)-rot.angleY+180,(float)-rot.angleX
			);
		}

		Vec3NT offset = new Vec3NT(sideOffset, heightOffset, frontOffset);
		offset.rotateAroundXRad(-this.rotationPitch / 180F * (float) Math.PI);
		offset.rotateAroundYRad(-this.rotationYaw / 180F * (float) Math.PI);

		this.posX += offset.x;
		this.posY += offset.y;
		this.posZ += offset.z;

		this.setPosition(this.posX, this.posY, this.posZ);

		this.headingX = -MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI);
		this.headingZ = MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI);
		this.headingY = -MathHelper.sin((this.rotationPitch) / 180.0F * (float) Math.PI);

		double range = 250D;
		this.headingX *= range;
		this.headingY *= range;
		this.headingZ *= range;

		performHitscan();
	}
	public AMRifleBeam(World world) {
		super(world);
	}
}
