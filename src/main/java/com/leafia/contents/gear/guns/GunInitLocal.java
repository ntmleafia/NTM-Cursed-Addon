package com.leafia.contents.gear.guns;

import com.hbm.entity.projectile.EntityBulletBeamBase;
import com.hbm.items.weapon.sedna.factory.LegoClient;
import com.leafia.contents.AddonItems.Guns;
import com.llib.math.LeafiaColor;

import java.util.function.BiConsumer;

import static com.hbm.items.weapon.sedna.factory.GunFactoryClient.setRendererBulkBeam;
import static com.hbm.items.weapon.sedna.factory.LegoClient.renderStandardLaser;

public class GunInitLocal {
	public static void init() {
		Guns.am_rifle.getConfig(null,0).hud(LegoClient.HUD_COMPONENT_DURABILITY);
		setRendererBulkBeam(RENDER_LASER_RAINBOW,GunInit.am_beam);
	}
	public static BiConsumer<EntityBulletBeamBase, Float> RENDER_LASER_RAINBOW = (bullet,interp) ->{
		LeafiaColor color = LeafiaColor.fromHSV(bullet.world.rand.nextDouble()*360,1,0.5);
		renderStandardLaser(bullet,interp,(int)(color.red*255),(int)(color.green*255),(int)(color.blue*255));
	};
}
