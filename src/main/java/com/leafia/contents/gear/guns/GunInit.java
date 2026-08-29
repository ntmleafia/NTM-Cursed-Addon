package com.leafia.contents.gear.guns;

import com.hbm.entity.projectile.EntityBulletBeamBase;
import com.hbm.inventory.RecipesCommon;
import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.items.ModItems;
import com.hbm.items.weapon.sedna.BulletConfig;
import com.hbm.items.weapon.sedna.GunConfig;
import com.hbm.items.weapon.sedna.ItemGunBaseNT.WeaponQuality;
import com.hbm.items.weapon.sedna.Receiver;
import com.hbm.items.weapon.sedna.factory.ConfettiUtil;
import com.hbm.items.weapon.sedna.factory.GunFactory;
import com.hbm.items.weapon.sedna.factory.GunFactory.EnumModSpecial;
import com.hbm.items.weapon.sedna.mags.MagazineInfinite;
import com.hbm.items.weapon.sedna.mods.WeaponModScope;
import com.hbm.items.weapon.sedna.mods.XWeaponModManager;
import com.hbm.items.weapon.sedna.mods.XWeaponModManager.WeaponModDefinition;
import com.hbm.render.misc.RenderScreenOverlay.Crosshair;
import com.hbm.util.DamageResistanceHandler;
import com.hbm.util.EntityDamageUtil;
import com.leafia.AddonBase;
import com.leafia.contents.AddonItems.Guns;
import com.leafia.contents.gear.guns.am_rifle.AMRifle;
import com.leafia.contents.miscellanous.slop.SlopTE;
import com.leafia.init.LeafiaDamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;

import java.util.function.BiConsumer;

import static com.hbm.items.weapon.sedna.factory.XFactoryEnergy.*;
import static com.hbm.items.weapon.sedna.mods.XWeaponModManager.ID_LAS_SHOTGUN;
import static com.hbm.items.weapon.sedna.mods.XWeaponModManager.ID_SCOPE;

public class GunInit {
	public static BulletConfig am_beam;
	public static BiConsumer<EntityBulletBeamBase,RayTraceResult> LAMBDA_AM_BEAM_HIT = (bullet,mop) ->{
		if (mop.typeOfHit == mop.typeOfHit.ENTITY) {
			Entity entity = mop.entityHit;

			if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).getHealth() <= 0) return;

			if (entity instanceof EntityLivingBase living) {
				float health = living.getHealth();
				EntityDamageUtil.attackEntityFromIgnoreIFrame(entity,LeafiaDamageSource.am_rifle,bullet.damage);
				float desiredHealth = Math.max(0,health-bullet.damage);
				if (living.getHealth() > desiredHealth) {
					living.setHealth(desiredHealth);
					if (desiredHealth <= 0) {
						SlopTE.tryKill(living);
						living.onDeath(LeafiaDamageSource.am_rifle);
					}
				}
			} else
				EntityDamageUtil.attackEntityFromIgnoreIFrame(entity,LeafiaDamageSource.am_rifle,bullet.damage);
		}
	};
	public static void init() {
		am_beam = new BulletConfig().setupDamageClass(DamageResistanceHandler.DamageClass.LASER).setBeam().setSpread(0.0F).setLife(5).setRenderRotations(false).setOnBeamImpact(LAMBDA_AM_BEAM_HIT);
		// the real bullshit that needs you to read code to find out-
		Guns.am_rifle = new AMRifle(
				WeaponQuality.SECRET,"gun_leafia_amrifle",new GunConfig()
				.dura(2000).draw(10).crosshair(Crosshair.CIRCLE).scopeTexture(scope_luna)
				.rec(
						new Receiver(0)
								.dmg(200)
								.mag(new MagazineInfinite(am_beam))
								.offset(0.75,-0.0625*1.5,-0.1875)
								.setupStandardFire().fire(AMRifle::fire)
				)
				.setupStandardConfiguration().pp(AMRifle::primary)
				.anim(AMRifle.LAMBDA_AMRIFLE).orchestra(AMRifle::orchestra)
		);
		AddonBase.proxy.registerGunCfg();
	}
	public static void initMods() {
		getDef(EnumModSpecial.SCOPE).addMod(new Item[]{Guns.am_rifle},XWeaponModManager.idToMod.get(ID_SCOPE));
		getDef(EnumModSpecial.LAS_SHOTGUN).addMod(new Item[]{Guns.am_rifle},XWeaponModManager.idToMod.get(ID_LAS_SHOTGUN));
	}
	public static WeaponModDefinition getDef(GunFactory.EnumModSpecial num) {
		return XWeaponModManager.stackToMod.get(new ComparableStack(ModItems.weapon_mod_special,1,num.ordinal()));
	}
}
