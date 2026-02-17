package com.leafia.init.hazards.types.radiation;

import com.hbm.hazard.modifier.IHazardModifier;
import com.hbm.hazard.type.IHazardType;
import com.hbm.util.ContaminationUtil;
import com.leafia.init.hazards.types.LCERad;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.List;

public class Gamma implements IHazardType, LCERad {
    private Gamma() {
    }

    public static final Gamma INSTANCE = new Gamma();

    @Override
    public void onUpdate(EntityLivingBase target, double level, ItemStack stack) {
        ContaminationUtil.contaminate(target, ContaminationUtil.HazardType.RADIATION, ContaminationUtil.ContaminationType.CREATIVE, level * 0.05);
    }

    @Override
    public void updateEntity(EntityItem item, double level) {

    }

    @Override
    public void addHazardInformation(EntityPlayer player, List<String> list, double level, ItemStack stack, List<IHazardModifier> modifiers) {
        list.add("I am gamma");
    }
}
