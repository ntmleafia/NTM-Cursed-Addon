package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.hazard.HazardEntry;
import com.hbm.hazard.HazardSystem;
import com.hbm.hazard.modifier.IHazardModifier;
import com.hbm.hazard.type.HazardTypeRadiation;
import com.hbm.hazard.type.IHazardType;
import com.hbm.lib.Library;
import com.hbm.util.I18nUtil;
import com.leafia.init.hazards.types.LCERad;
import com.leafia.init.hazards.types.radiation.*;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static com.hbm.hazard.HazardSystem.getHazardsFromStack;
import static com.hbm.hazard.type.HazardTypeRadiation.getNewValue;
import static com.hbm.hazard.type.HazardTypeRadiation.getSuffix;

@Mixin(value = HazardSystem.class)
public class MixinHazardSystem {
	@Redirect(method = "addHazardInfo",at = @At(value = "INVOKE", target = "Lcom/hbm/hazard/type/IHazardType;addHazardInformation(Lnet/minecraft/entity/player/EntityPlayer;Ljava/util/List;DLnet/minecraft/item/ItemStack;Ljava/util/List;)V"),remap = false,require = 1)
	private static void onAddHazardInformation(IHazardType instance,EntityPlayer player,List<String> list,double v,ItemStack stack,List<IHazardModifier> iHazardModifiers) {
		if (!(instance instanceof HazardTypeRadiation) && !(instance instanceof LCERad))
			instance.addHazardInformation(player,list,v,stack,iHazardModifiers);
	}
	@Inject(method = "addHazardInfo",at = @At(value = "HEAD"),remap = false,require = 1)
	private static void onAddHazardInfo(CallbackInfo ci,@Local ItemStack stack,@Local EntityPlayer player,@Local List<String> list) {
		List<HazardEntry> hazards = getHazardsFromStack(stack);
		double total = 0;
		double alpha = 0;
		double beta = 0;
		double x = 0;
		double gamma = 0;
		double neutrons = 0;
		double activation = 0;
		double radon = 0;
		for (HazardEntry hazard : hazards) {
			if (hazard.type instanceof HazardTypeRadiation || hazard.type instanceof LCERad) {
				double level = IHazardModifier.evalAllModifiers(stack,player,hazard.baseLevel,hazard.mods);
				total += level;
				if (hazard.type instanceof Alpha)
					alpha += level;
				else if (hazard.type instanceof Beta)
					beta += level;
				else if (hazard.type instanceof XRay)
					x += level;
				else if (hazard.type instanceof Gamma)
					gamma += level;
				else if (hazard.type instanceof Neutrons)
					neutrons += level;
				else if (hazard.type instanceof HazardTypeRadiation)
					activation += level;
				else if (hazard.type instanceof Radon)
					radon += level;
			}
		}
		if (total > 0) {
			list.add(TextFormatting.GREEN + "[" + I18nUtil.resolveKey("trait.radioactive") + "]");
			if (alpha > 0)
				list.add(TextFormatting.GREEN+" -::" +TextFormatting.RED+ I18nUtil.resolveKey("trait._hazarditem.radioactive.alpha") + " " + (Library.roundFloat(getNewValue(alpha), 3)+ getSuffix(alpha) + " " + I18nUtil.resolveKey("desc.rads")));
			if (beta > 0)
				list.add(TextFormatting.GREEN+" -::" +TextFormatting.AQUA+ I18nUtil.resolveKey("trait._hazarditem.radioactive.beta") + " " + (Library.roundFloat(getNewValue(beta), 3)+ getSuffix(beta) + " " + I18nUtil.resolveKey("desc.rads")));
			if (x > 0)
				list.add(TextFormatting.GREEN+" -::" +TextFormatting.DARK_AQUA+ I18nUtil.resolveKey("trait._hazarditem.radioactive.x") + " " + (Library.roundFloat(getNewValue(x), 3)+ getSuffix(x) + " " + I18nUtil.resolveKey("desc.rads")));
			if (gamma > 0)
				list.add(TextFormatting.GREEN+" -::" +TextFormatting.DARK_GREEN+ I18nUtil.resolveKey("trait._hazarditem.radioactive.gamma") + " " + (Library.roundFloat(getNewValue(gamma), 3)+ getSuffix(gamma) + " " + I18nUtil.resolveKey("desc.rads")));
			if (neutrons > 0)
				list.add(TextFormatting.GREEN+" -::" +TextFormatting.YELLOW+ I18nUtil.resolveKey("trait._hazarditem.radioactive.neutrons") + " " + (Library.roundFloat(getNewValue(neutrons), 3)+ getSuffix(neutrons) + " " + I18nUtil.resolveKey("desc.rads")));
			if (activation > 0)
				list.add(TextFormatting.GREEN+" -::" +TextFormatting.DARK_GRAY+ I18nUtil.resolveKey("trait._hazarditem.radioactive.activation") + " " + (Library.roundFloat(getNewValue(activation), 3)+ getSuffix(activation) + " " + I18nUtil.resolveKey("desc.rads")));
			if(stack.getCount() > 1) {
				list.add(TextFormatting.GREEN+" -::" + TextFormatting.GOLD + I18nUtil.resolveKey("desc.stack")+" " + Library.roundFloat(getNewValue(total), 3) + getSuffix(total) + " " + I18nUtil.resolveKey("desc.rads"));
			}
			if (radon > 0)
				list.add(TextFormatting.GREEN+" -::" + I18nUtil.resolveKey("trait._hazarditem.radioactive.radon") + " " + (Library.roundFloat(getNewValue(radon), 3)+ getSuffix(radon) + " " + I18nUtil.resolveKey("desc.rads")));
		}
	}
}
