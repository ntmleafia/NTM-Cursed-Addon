package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.main.ModEventHandler;
import com.leafia.init.recipes.AddonCrafting;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent.Register;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ModEventHandler.class)
public class MixinModEventHandler {
	@Inject(method = "craftingRegister",at = @At(value = "INVOKE", target = "Lcom/hbm/main/CraftingManager;init()V", shift = Shift.AFTER, remap = false),remap = false)
	public void onCraftingRegister(Register<IRecipe> e,CallbackInfo ci) {
		AddonCrafting.craftingRegister();
	}
}
