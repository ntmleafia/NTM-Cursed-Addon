package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.main.ModEventHandler;
import com.leafia.contents.AddonItems;
import com.leafia.init.recipes.AddonCraftingRecipes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ModEventHandler.class)
public class MixinModEventHandler {
	@Inject(method = "craftingRegister",at = @At(value = "INVOKE", target = "Lcom/hbm/main/CraftingManager;init()V", shift = Shift.AFTER, remap = false),remap = false,require = 1)
	public void onCraftingRegister(Register<IRecipe> e,CallbackInfo ci) {
		AddonCraftingRecipes.craftingRegister();
	}
	@Inject(method = "onPlayerLogin",at = @At(value = "INVOKE", target = "Lcom/hbm/capability/HbmCapability$IHBMData;setReceivedBook(Z)V",remap = false),require = 1,remap = false)
	public void onOnPlayerLogin(PlayerLoggedInEvent event,CallbackInfo ci) {
		event.player.inventory.addItemStackToInventory(new ItemStack(AddonItems.advisor));
		event.player.inventoryContainer.detectAndSendChanges();
	}
}
