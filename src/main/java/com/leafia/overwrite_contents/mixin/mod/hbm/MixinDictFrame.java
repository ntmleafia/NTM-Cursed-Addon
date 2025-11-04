package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.inventory.OreDictManager.DictFrame;
import com.hbm.inventory.material.MaterialShapes;
import com.leafia.database.AddonOreDictHazards;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.*;

@Mixin(value = DictFrame.class)
public class MixinDictFrame {
	@Shadow(remap = false) public String[] mats;
	@Shadow(remap = false) private float hazMult;
	@Inject(method = "makeObject",at = @At(value = "RETURN"),remap = false)
	void onMakeObject(MaterialShapes shape,Object[] objects,CallbackInfoReturnable<DictFrame> cir) {
		AddonOreDictHazards.dictMap.putIfAbsent(this,new HashMap<>());
		Map<String,Float> map = AddonOreDictHazards.dictMap.get(this);
		for (String mat : this.mats) {
			for (String prefix : shape.prefixes) {
				map.put(prefix+mat,hazMult);
			}
		}
	}
}
