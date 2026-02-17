package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.hazard.type.HazardTypeHot;
import com.leafia.contents.gear.advisor.AdvisorItem;
import com.leafia.contents.gear.advisor.AdvisorItem.AdvisorWarningPacket;
import com.leafia.dev.custompacket.LeafiaCustomPacket;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = HazardTypeHot.class,remap = false)
public class MixinHazardTypeHot {
	@Inject(method = "onUpdate",at = @At(value = "TAIL"),require = 1)
	public void onOnUpdate(EntityLivingBase target,double level,ItemStack stack,CallbackInfo ci) {
		if (target instanceof EntityPlayer player)
			LeafiaCustomPacket.__start(new AdvisorWarningPacket(0)).__sendToClient(player);
	}
}
