package com.leafia.init.hazards.modifiers;

import com.hbm.hazard.modifier.IHazardModifier;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class NBTModifier implements IHazardModifier {
    private final NBTKey nbtKey;

    public NBTModifier(NBTKey nbtKey) {
        this.nbtKey = nbtKey;
    }

    @Override
    public double modify(ItemStack stack, EntityLivingBase holder, double level) {
        if (!stack.hasTagCompound()) return level;
        NBTTagCompound compound = stack.getTagCompound();
        if (compound.hasKey(nbtKey.getAbsKey())) {
            return compound.getDouble(nbtKey.getAbsKey());
        }
        if (compound.hasKey(nbtKey.getModKey())) {
            double value = compound.getDouble(nbtKey.getModKey());
            return value * level;
        }
        return level;
    }

    public void setAbsolute(ItemStack stack, double value) {
        setAbsolute(stack, nbtKey, value);
    }

    public void setMod(ItemStack stack, double value) {
        setMod(stack, nbtKey, value);
    }

    public static void setAbsolute(ItemStack stack, NBTKey nbtKey, double value) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound().setDouble(nbtKey.getAbsKey(), value);
    }

    public static void setMod(ItemStack stack, NBTKey nbtKey, double value) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound().setDouble(nbtKey.getModKey(), value);
    }

    public enum NBTKey {
        ALPHA("alpha"),
        BETA("beta"),
        GAMMA("gamma"),
        NEUTRONS("neutrons"),
        ACTIVATION("activation"),//original radiaion
        RADON("radon"),
        XRAY("xray");

        private final String absKey;
        private final String modKey;

        NBTKey(String key) {
            this.absKey = key + "_abs";
            this.modKey = key + "_mod";
        }

        public String getModKey() {
            return modKey;
        }

        public String getAbsKey() {
            return absKey;
        }
    }
}
