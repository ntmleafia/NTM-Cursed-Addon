package com.leafia.unsorted.explosion_vnt;

import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.EntityProcessorCrossSmooth;
import com.hbm.items.weapon.sedna.factory.ConfettiUtil;
import com.hbm.util.EntityDamageUtil;
import com.leafia.init.LeafiaDamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;

public class EntityProcessorPlayerLandmine extends EntityProcessorCrossSmooth {
	public EntityProcessorPlayerLandmine(double nodeDist,float fixedDamage) {
		super(nodeDist,600000);
	}
	@Override
	public void attackEntity(Entity entity,ExplosionVNT source,float amount) {
		if (entity.isEntityAlive()) {
			DamageSource dmg = LeafiaDamageSource.mine_player;
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
