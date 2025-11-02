package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.hazard.HazardEntry;
import com.hbm.hazard.HazardSystem;
import com.hbm.hazard.modifier.IHazardModifier;
import com.hbm.hazard.type.HazardTypeRadiation;
import com.hbm.lib.Library;
import com.hbm.util.I18nUtil;
import com.leafia.dev.hazards.MultiRad;
import com.leafia.dev.hazards.types.ILeafiaRadType;
import com.leafia.dev.hazards.types.Radon;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.hbm.hazard.type.HazardTypeRadiation.getNewValue;
import static com.hbm.hazard.type.HazardTypeRadiation.getSuffix;

@Mixin(value = HazardSystem.class, remap = false)
public class MixinHazardSystem {
    private static Predicate<HazardEntry> radFilter = hazardEntry -> hazardEntry.type instanceof ILeafiaRadType || hazardEntry.type instanceof HazardTypeRadiation;

    /**
     * @author mlbv
     * @reason Overwrite to make rads ordered
     */
    @Overwrite
    public static void addHazardInfo(ItemStack stack, EntityPlayer player, List<String> list, ITooltipFlag flagIn) {
        List<HazardEntry> hazards = getHazardsFromStack(stack);
        List<HazardEntry> rads = hazards.stream().filter(radFilter).sorted(Comparator.comparingInt(hazard -> ILeafiaRadType.getOrdinal(hazard.type))).collect(Collectors.toList());
        double totalRad = 0;
        boolean radon = false;
        for (HazardEntry rad : rads) {
            if (rad.type instanceof Radon) {
                radon = true;
                continue;
            }
            totalRad += IHazardModifier.evalAllModifiers(stack, player, rad.baseLevel, rad.mods);
        }
        if (totalRad > 0) {
            for (HazardEntry rad : rads) {
                double level = IHazardModifier.evalAllModifiers(stack, player, rad.baseLevel, rad.mods);
                list.add(TextFormatting.GREEN+" -::" + ILeafiaRadType.getColor(rad.type) + I18nUtil.resolveKey(ILeafiaRadType.getTranslationKey(rad.type)) + " " + (Library.roundFloat(getNewValue(level), 3)+ getSuffix(level) + " " + I18nUtil.resolveKey("desc.rads")));
            }
        }
        if (radon) {
            //list.add(TextFormatting.GREEN+" -::" + I18nUtil.resolveKey(MultiRad.RadiationType.RADON.translationKey) + " " + (Library.roundFloat(getNewValue(rad.radon), 3)+ getSuffix(rad.radon) + " " + I18nUtil.resolveKey("desc.rads")));
        }

        for (HazardEntry hazard : hazards) {
            if (hazard.type instanceof HazardTypeRadiation) continue;//our ones are empty anyway
            hazard.type.addHazardInformation(player, list, hazard.baseLevel, stack, hazard.mods);
        }
    }

    @Shadow
    public static List<HazardEntry> getHazardsFromStack(final ItemStack stack) {
        return null;
    }
}
