package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.items.weapon.sedna.GunConfig;
import com.hbm.items.weapon.sedna.Receiver;
import com.hbm.items.weapon.sedna.mods.WeaponModBase;
import com.hbm.items.weapon.sedna.mods.WeaponModLasShotgun;
import com.leafia.contents.AddonItems.Guns;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(WeaponModLasShotgun.class)
public abstract class MixinWeaponModLasShotgun extends WeaponModBase {
	public MixinWeaponModLasShotgun(int id,String... slots) {
		super(id,slots);
	}
	@Inject(method = "eval",at = @At(value = "HEAD"),require = 1,remap = false,cancellable = true)
	public <T> void leafia$onEval(T base,ItemStack gun,String key,Object parent,CallbackInfoReturnable<T> cir) {
		if (gun.getItem() == Guns.am_rifle && (Objects.equals(key, Receiver.F_SPLITPROJECTILES) || Objects.equals(key,GunConfig.O_CROSSHAIR))) {
			cir.setReturnValue(base);
			cir.cancel();
		}
		if (gun.getItem() == Guns.am_rifle && Objects.equals(key, Receiver.F_BASEDAMAGE)) {
			cir.setReturnValue(cast((Float)base/2,base));
			cir.cancel();
		}
	}
}
