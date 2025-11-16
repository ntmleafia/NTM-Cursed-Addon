package com.leafia.dev.items;

import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemLens;
import com.hbm.util.I18nUtil;
import com.leafia.contents.AddonItems;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.List;

public class LeafiaItemLens extends ItemLens {
    public float fieldMod;
    public float drainMod;
    public float energyMod;

    // ctor for the existing ams_lens
    public LeafiaItemLens(long maxDamage, String s) {
        super(maxDamage, s);
        fieldMod = 1.0F;
        drainMod = 1.0F;
        energyMod = 1.0F;
    }

    public LeafiaItemLens(long maxDamage, float fieldMod, float drainMod, float energyMod, String s) {
        super(maxDamage, s);
        this.fieldMod = fieldMod;
        this.drainMod = drainMod;
        this.energyMod = energyMod;
        ModItems.ALL_ITEMS.remove(this);
        AddonItems.ALL_ITEMS.add(this);
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(TextFormatting.YELLOW+ I18nUtil.resolveKey("desc.fieldmodifier")+" " + (fieldMod >= 1 ? "§a+" : "§c") + (Math.round(fieldMod * 1000) * .10 - 100) + "%");
        tooltip.add(TextFormatting.YELLOW+I18nUtil.resolveKey("desc.powdrainmodifier")+" " + (drainMod >= 1 ? "§c+" : "§a") + (Math.round(drainMod * 1000) * .10 - 100) + "%");
        tooltip.add(TextFormatting.YELLOW+I18nUtil.resolveKey("desc.energymodifier")+" " + (energyMod > 1 ? "§6+" : ("§8"+(energyMod<=1 ? "+" : ""))) + (Math.round(energyMod * 1000) * .10 - 100) + "%");

    }
}
