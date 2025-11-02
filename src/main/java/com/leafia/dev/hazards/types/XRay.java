package com.leafia.dev.hazards.types;

import com.hbm.hazard.modifier.IHazardModifier;
import com.hbm.util.ContaminationUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

public class XRay implements ILeafiaRadType {
    private XRay() {
    }

    public static final XRay INSTANCE = new XRay();

    @Override
    public void onUpdate(EntityLivingBase target, double level, ItemStack stack) {
        ContaminationUtil.contaminate(target, ContaminationUtil.HazardType.RADIATION, ContaminationUtil.ContaminationType.CREATIVE, level * 0.2);
    }

    @Override
    public void updateEntity(EntityItem item, double level) {

    }

    @Override
    public void addHazardInformation(EntityPlayer player, List<String> list, double level, ItemStack stack, List<IHazardModifier> modifiers) {

    }
    @Override
    public int ordinal() {
        return 3;
    }

    @Override
    public TextFormatting color() {
        return TextFormatting.DARK_AQUA;
    }

    @Override
    public String translationKey() {
        return "trait._hazarditem.radioactive.x";
    }
}
