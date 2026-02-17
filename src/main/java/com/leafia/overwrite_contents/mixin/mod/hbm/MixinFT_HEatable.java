package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.inventory.fluid.trait.FT_Heatable;
import com.hbm.inventory.fluid.trait.FT_Heatable.HeatingStep;
import com.hbm.inventory.fluid.trait.FT_Heatable.HeatingType;
import com.hbm.util.I18nUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(value = FT_Heatable.class,remap = false)
public abstract class MixinFT_HEatable {
	@Shadow public abstract HeatingStep getFirstStep();

	@Shadow public abstract double getEfficiency(HeatingType type);

	/**
	 * @author addInfoHidden
	 * @reason remove pwr
	 */
	@Overwrite
	public void addInfoHidden(List<String> info) {
		info.add(I18nUtil.resolveKey("trait.thermalcap", this.getFirstStep().heatReq));
		for(HeatingType type : HeatingType.values()) {
			if (type.equals(HeatingType.PWR)) continue;
			double eff = getEfficiency(type);
			if(eff > 0) {
				info.add(I18nUtil.resolveKey("trait.chefficiency", I18nUtil.resolveKey(type.name), ((int) (eff * 100D))) + "%");
			}
		}
	}
}
