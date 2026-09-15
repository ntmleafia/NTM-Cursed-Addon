package com.leafia.overwrite_contents.mixin;

import com.hbm.hazard.HazardEntry;
import com.hbm.hazard.HazardSystem;
import com.leafia.init.hazards.types.containment.HazardTypeContainmentBase;
import com.leafia.overwrite_contents.interfaces.IMixinEntityItem;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityItem.class)
public abstract class MixinEntityItem implements IMixinEntityItem {

	@Unique
	private EntityPlayer leafia$addonDroppedBy;

	@Unique
	private boolean leafia$addonWasPickedUp;

	@Override
	public EntityPlayer leafia$getDroppedBy() {
		return leafia$addonDroppedBy;
	}

	@Override
	public void leafia$setDroppedBy(EntityPlayer player) {
		leafia$addonDroppedBy = player;
	}

	@Override
	public boolean leafia$getWasPickedUp() {
		return leafia$addonWasPickedUp;
	}

	@Override
	public void leafia$setWasPickedUp(boolean pickedUp) {
		leafia$addonWasPickedUp = pickedUp;
	}

	@Inject(method = "attackEntityFrom",at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/item/EntityItem;setDead()V", shift = Shift.AFTER),require = 1)
	public void leafia$onAttackEntityFrom(DamageSource source,float amount,CallbackInfoReturnable<Boolean> cir) {
		EntityItem entity = ((EntityItem)(IMixinEntityItem)this);
		ItemStack stack = entity.getItem();
		for (HazardEntry entry : HazardSystem.getHazardsFromStack(stack)) {
			if (entry.type instanceof HazardTypeContainmentBase contain)
				contain.onDestroyed(source,entity.getEntityWorld(),new Vec3d(entity.posX,entity.posY+entity.getEyeHeight(),entity.posZ),HazardSystem.getHazardLevelFromStack(stack,entry.type));
		}
	}
}
