package com.leafia.dev.hazards.types;

import com.hbm.hazard.modifier.IHazardModifier;
import com.hbm.hazard.type.IHazardType;
import com.hbm.util.ContaminationUtil;
import com.leafia.contents.potion.LeafiaPotion;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.List;

public class Beta implements IHazardType {
    private Beta() {
    }

    public static final Beta INSTANCE = new Beta();

    @Override
    public void onUpdate(EntityLivingBase target, double level, ItemStack stack) {
        if (level <= 0) return;
        int amp = LeafiaPotion.getSkinDamage(target);
        float health = target.getHealth()/target.getMaxHealth();
        if (target.getRNG().nextInt(2000) == 0)
            LeafiaPotion.hurtSkin(target,2);
        ContaminationUtil.contaminate(target, ContaminationUtil.HazardType.RADIATION, ContaminationUtil.ContaminationType.CREATIVE, level * HazardTypeHelper.accountConfig(amp>=2 ? (health < 0.5 ? 0.9 : 0.6) : (amp+1)*(amp+1)*0.01));
    }

    @Override
    public void updateEntity(EntityItem item, double level) {
    }

    @Override
    public void addHazardInformation(EntityPlayer player, List<String> list, double level, ItemStack stack, List<IHazardModifier> modifiers) {

    }
}
