package com.leafia.init.hazards.types.radiation;

import com.hbm.hazard.modifier.IHazardModifier;
import com.hbm.hazard.type.IHazardType;
import com.hbm.util.ContaminationUtil;
import com.leafia.contents.potion.LeafiaPotion;
import com.leafia.init.hazards.types.HazardTypeHelper;
import com.leafia.init.hazards.types.LCERad;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.List;

public class Alpha implements IHazardType, LCERad {
    private Alpha() {
    }

    public static final Alpha INSTANCE = new Alpha();

    @Override
    public void onUpdate(EntityLivingBase target, double level, ItemStack stack) {
        if (level <= 0.0) return;
        int amp = LeafiaPotion.getSkinDamage(target);
        float health = target.getHealth() / target.getMaxHealth();
        double div = health / 4.0 * 3.0 + 0.25d;
        if (amp >= 2) {
            if (target.getRNG().nextInt(2000 - Math.min((int) Math.floor(Math.pow(level / 10.0, 0.8)) * 100, 950)) == 0)
                LeafiaPotion.hurtSkin(target, 3);
        }
        ContaminationUtil.contaminate(target, ContaminationUtil.HazardType.RADIATION, ContaminationUtil.ContaminationType.CREATIVE, level * HazardTypeHelper.accountConfig(amp >= 3 ? 1.5 / div : 0.0));
    }

    @Override
    public void updateEntity(EntityItem item, double level) {
    }

    @Override
    public void addHazardInformation(EntityPlayer player, List<String> list, double level, ItemStack stack, List<IHazardModifier> modifiers) {
        list.add("I am alpha");
    }
}
