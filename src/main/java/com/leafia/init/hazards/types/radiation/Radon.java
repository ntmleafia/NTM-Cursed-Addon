package com.leafia.init.hazards.types.radiation;

import com.hbm.config.GeneralConfig;
import com.hbm.handler.ArmorUtil;
import com.hbm.hazard.modifier.IHazardModifier;
import com.hbm.hazard.type.IHazardType;
import com.hbm.util.ArmorRegistry;
import com.hbm.util.ContaminationUtil;
import com.leafia.init.hazards.types.LCERad;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

import java.util.List;

public class Radon implements IHazardType, LCERad {
    private Radon() {
    }

    public static final Radon INSTANCE = new Radon();

    @Override
    public void onUpdate(EntityLivingBase target, double level, ItemStack stack) {
        if (!GeneralConfig.enableRadon) return;
        if(ArmorRegistry.hasProtection(target, EntityEquipmentSlot.HEAD, ArmorRegistry.HazardClass.PARTICLE_FINE)) {
            ArmorUtil.damageGasMaskFilter(target, hazardRate);
        } else {
            ContaminationUtil.contaminate(target, ContaminationUtil.HazardType.RADIATION, ContaminationUtil.ContaminationType.RAD_BYPASS, level * hazardRate);
        }
    }

    @Override
    public void updateEntity(EntityItem item, double level) {
    }

    @Override
    public void addHazardInformation(EntityPlayer player, List<String> list, double level, ItemStack stack, List<IHazardModifier> modifiers) {
        list.add("I am radon");
    }
}
