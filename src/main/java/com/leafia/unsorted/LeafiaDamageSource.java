package com.leafia.unsorted;

import net.minecraft.util.DamageSource;

public class LeafiaDamageSource extends DamageSource {

	public static DamageSource fleija = (new DamageSource("fleija")).setDamageBypassesArmor();
	public static DamageSource back = (new DamageSource("back")).setDamageBypassesArmor().setDamageIsAbsolute().setDamageAllowedInCreativeMode(); // fuck you :D
	public static DamageSource mine = (new DamageSource("mine")).setExplosion().setDamageBypassesArmor();
	public static DamageSource dfc = (new DamageSource("dfc")).setDamageIsAbsolute().setDamageBypassesArmor();
	public static DamageSource dfcMeltdown = (new DamageSource("dfcMeltdown")).setDamageIsAbsolute().setDamageBypassesArmor().setDamageAllowedInCreativeMode();

	public LeafiaDamageSource(String damageTypeIn) {
		super(damageTypeIn);
	}
}
