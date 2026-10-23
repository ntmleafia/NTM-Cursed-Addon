// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.handler.pollution.PollutionHandler;
import com.hbm.handler.pollution.PollutionHandler.PollutionType;
import com.hbm.inventory.fluid.trait.FT_Polluting;
import com.hbm.util.I18nUtil;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(value = FT_Polluting.class,remap = false)
public class MixinFT_Polluting {
	@Shadow public HashMap<PollutionType,Float> releaseMap;

	@Shadow public HashMap<PollutionType,Float> burnMap;

	/**
	 * @author ntmleafia
	 * @reason it was ugly
	 */
	@Overwrite
	public void addInfoHidden(List<String> info) {
		if(!this.releaseMap.isEmpty()) {
			info.add(TextFormatting.GOLD+"-::"+I18nUtil.resolveKey("trait.polluspill"));
			for(Map.Entry<PollutionHandler.PollutionType, Float> entry : releaseMap.entrySet())
				info.add(TextFormatting.GOLD+"-::"+TextFormatting.GREEN + " " + entry.getValue()*1000 + " " + I18nUtil.resolveKey(entry.getKey().name) + " / B");
		}

		if(!this.burnMap.isEmpty()) {
			info.add(TextFormatting.GOLD+"-::"+I18nUtil.resolveKey("trait.polluburn"));
			for(Map.Entry<PollutionHandler.PollutionType, Float> entry : burnMap.entrySet())
				info.add(TextFormatting.GOLD+"-::"+TextFormatting.RED + " " + entry.getValue()*1000 + " " + I18nUtil.resolveKey(entry.getKey().name) + " / B");
		}
	}
}
