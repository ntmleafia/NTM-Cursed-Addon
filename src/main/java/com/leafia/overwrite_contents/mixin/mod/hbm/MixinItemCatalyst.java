package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.items.machine.ItemCatalyst;
import com.leafia.overwrite_contents.other.LCEItemCatalyst;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ItemCatalyst.class)
public class MixinItemCatalyst extends Item {
	@Inject(method = "addInformation",at = @At("HEAD"))
	public void onAddInformation(ItemStack stack,World worldIn,List<String> tooltip,ITooltipFlag flagIn,CallbackInfo ci) {
		tooltip.add("§6Melting Point: §8" +LCEItemCatalyst.meltingPoints.get(this)+ "°C");
	}
}
