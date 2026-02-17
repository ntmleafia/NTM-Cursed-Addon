package com.leafia.unsorted.explosion_vnt;

import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.EntityProcessorCrossSmooth;
import com.hbm.items.weapon.sedna.factory.ConfettiUtil;
import com.hbm.util.EntityDamageUtil;
import com.leafia.init.LeafiaDamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;

public class EntityProcessorLandmine extends EntityProcessorCrossSmooth {
	public EntityProcessorLandmine(double nodeDist,float fixedDamage) {
		super(nodeDist,fixedDamage*4.5f);
	}
	@Override
	public void attackEntity(Entity entity,ExplosionVNT source,float amount) {
		if (entity.isEntityAlive()) {
			//if (source.exploder == entity) { tf was this for
				amount *= 0.5F;
			//}

			DamageSource dmg = LeafiaDamageSource.mine;
			if (!(entity instanceof EntityLivingBase)) {
				entity.attackEntityFrom(dmg, amount);
			} else {
				EntityDamageUtil.attackEntityFromNT((EntityLivingBase)entity, dmg, amount, true, false, (double)0.0F, this.pierceDT, this.pierceDR);
				if (!entity.isEntityAlive()) {
					ConfettiUtil.decideConfetti((EntityLivingBase)entity, dmg);
				}
			}

		}
	}
}
