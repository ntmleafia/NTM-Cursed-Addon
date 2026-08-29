package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.items.weapon.sedna.GunConfig;
import com.hbm.items.weapon.sedna.Receiver;
import com.hbm.items.weapon.sedna.mods.WeaponModBase;
import com.hbm.items.weapon.sedna.mods.WeaponModLasShotgun;
import com.hbm.items.weapon.sedna.mods.WeaponModScope;
import com.leafia.contents.AddonItems.Guns;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

import static com.hbm.items.weapon.sedna.factory.XFactoryEnergy.scope_luna;

@Mixin(WeaponModScope.class)
public abstract class MixinWeaponModScope extends WeaponModBase {
	public MixinWeaponModScope(int id,String... slots) {
		super(id,slots);
	}
	@Inject(method = "eval",at = @At(value = "HEAD"),require = 1,remap = false,cancellable = true)
	public <T> void leafia$onEval(T base,ItemStack gun,String key,Object parent,CallbackInfoReturnable<T> cir) {
		if (gun.getItem() == Guns.am_rifle && Objects.equals(key,GunConfig.O_SCOPETEXTURE)) {
			cir.setReturnValue(cast(scope_luna,base));
			cir.cancel();
		}
	}
}
