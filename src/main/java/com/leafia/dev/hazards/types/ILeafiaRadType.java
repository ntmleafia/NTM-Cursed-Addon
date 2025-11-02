package com.leafia.dev.hazards.types;

import com.hbm.hazard.type.HazardTypeRadiation;
import com.hbm.hazard.type.IHazardType;
import net.minecraft.util.text.TextFormatting;

public interface ILeafiaRadType extends IHazardType {
    int ordinal();

    TextFormatting color();

    String translationKey();

    static int getOrdinal(IHazardType type) {
        if (type instanceof ILeafiaRadType leafiaRadType) return leafiaRadType.ordinal();
        if (type instanceof HazardTypeRadiation) return 5;
        return -1;
    }

    static TextFormatting getColor(IHazardType type) {
        if (type instanceof ILeafiaRadType leafiaRadType) return leafiaRadType.color();
        if (type instanceof HazardTypeRadiation) return TextFormatting.DARK_GRAY;
        return TextFormatting.WHITE;
    }

    static String getTranslationKey(IHazardType type) {
        if (type instanceof ILeafiaRadType leafiaRadType) return leafiaRadType.translationKey();
        if (type instanceof HazardTypeRadiation) return "trait._hazarditem.radioactive.activation";
        return "";
    }
}
