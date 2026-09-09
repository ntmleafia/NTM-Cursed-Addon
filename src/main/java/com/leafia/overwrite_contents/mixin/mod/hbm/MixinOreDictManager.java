package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.inventory.OreDictManager;
import com.hbm.inventory.OreDictManager.DictFrame;
import com.hbm.inventory.OreDictManager.DictGroup;
import com.leafia.init.AddonOreDict;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = OreDictManager.class,remap = false)
public class MixinOreDictManager {
	@Inject(method = "registerOres",at = @At(value = "HEAD"),require = 1)
	private static void onRegisterOres(CallbackInfo ci) {
		AddonOreDict.registerOres();
	}
	@Redirect(method = "<clinit>",at = @At(value = "NEW", target = "(Ljava/lang/String;[Lcom/hbm/inventory/OreDictManager$DictFrame;)Lcom/hbm/inventory/OreDictManager$DictGroup;"),require = 1,remap = false)
	private static DictGroup onClinit(String groupName,DictFrame[] frames) {
		DictGroup group = new DictGroup(groupName,frames);
		if (groupName.equals("AnyResistantAlloy"))
			group.addFrames(AddonOreDict.TNALLOY);
		return group;
	}
}
