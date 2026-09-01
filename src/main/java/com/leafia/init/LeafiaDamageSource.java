package com.leafia.init;

import net.minecraft.util.DamageSource;

public class LeafiaDamageSource extends DamageSource {

	public static DamageSource fleija = (new DamageSource("fleija")).setDamageBypassesArmor();
	public static DamageSource back = (new DamageSource("back")).setDamageBypassesArmor().setDamageIsAbsolute().setDamageAllowedInCreativeMode(); // fuck you :D
	public static DamageSource mine = (new DamageSource("mine")).setExplosion().setDamageBypassesArmor();
	public static DamageSource dfc = (new DamageSource("dfc")).setDamageIsAbsolute().setDamageBypassesArmor();
	public static DamageSource dfcMeltdown = (new DamageSource("dfcMeltdown")).setDamageIsAbsolute().setDamageBypassesArmor().setDamageAllowedInCreativeMode();
	public static DamageSource pointed = (new DamageSource("pointed")).setDifficultyScaled();
	public static DamageSource poison = new DamageSource("poison").setDamageAllowedInCreativeMode().setDamageIsAbsolute().setDamageBypassesArmor();
	public static DamageSource drinkcryo = new DamageSource("drinkcryo").setDamageAllowedInCreativeMode().setDamageIsAbsolute().setDamageBypassesArmor();
	public static DamageSource drinkhot = new DamageSource("drinkhot").setDamageAllowedInCreativeMode().setDamageIsAbsolute().setDamageBypassesArmor().setFireDamage();
	public static DamageSource drinkacid = new DamageSource("drinkacid").setDamageAllowedInCreativeMode().setDamageIsAbsolute().setDamageBypassesArmor().setFireDamage();
	public static DamageSource flywheel = new DamageSource("flywheel").setDamageIsAbsolute().setDamageBypassesArmor();
	public static DamageSource mine_player = (new DamageSource("mine_player")).setDamageIsAbsolute();
	public static DamageSource am_rifle = (new DamageSource("am_rifle")).setDamageIsAbsolute().setDamageAllowedInCreativeMode().setDamageBypassesArmor().setMagicDamage();
	public static DamageSource pillMeltdown = new DamageSource("pillMeltdown").setDamageAllowedInCreativeMode().setDamageIsAbsolute().setDamageBypassesArmor();

	public LeafiaDamageSource(String damageTypeIn) {
		super(damageTypeIn);
	}
}
