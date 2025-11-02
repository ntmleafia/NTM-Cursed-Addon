package com.leafia.dev.hazards;

import com.hbm.hazard.HazardData;
import com.hbm.hazard.HazardEntry;
import com.hbm.hazard.HazardSystem;
import com.hbm.items.ModItems;
import com.leafia.dev.hazards.modifiers.NBTModifier;
import com.leafia.dev.hazards.types.*;
import net.minecraft.item.Item;

public class ItemRads {

	public static MultiRadContainer cobalt60 = new MultiRadContainer(0,30,30,60,0).multiply(1/3f);
	public static MultiRadContainer gold198 = new MultiRadContainer(0,500,500,500,0).multiply(1/2f);
	public static MultiRadContainer plutonium241 = new MultiRadContainer(0,25f,25f,0,0).multiply(1/2f);
	public static MultiRadContainer tritium = new MultiRadContainer(0,0,0.5f,0,0);
	public static MultiRadContainer waste = new MultiRadContainer(0,125,125,50,25);
	public static MultiRadContainer waste_v = waste.copy().multiply(1/2f);

    //call after com.hbm.hazard.HazardRegistry.registerItems
    public void register() {
        cobalt60.register(ModItems.ingot_co60);
    }

    public static class MultiRadContainer {
        public double alpha;
        public double beta;
        public double gamma;
        public double x;
        public double neutrons;

        public MultiRadContainer(double alpha, double beta, double x, double gamma, double neutrons) {
            this.alpha = alpha;
            this.beta = beta;
            this.x = x;
            this.gamma = gamma;
            this.neutrons = neutrons;
        }

        public MultiRadContainer multiply(double v) {
            alpha *= v;
            beta *= v;
            x *= v;
            gamma *= v;
            neutrons *= v;
            return this;
        }

        public MultiRadContainer copy() {
            return new MultiRadContainer(alpha, beta, x, gamma, neutrons);
        }

        public void register(Item item) {
            HazardData data = HazardSystem.itemMap.computeIfAbsent(item, k -> new HazardData());
            // if you need to add a hazard modifier to HazardTypeRadiation, use
            // data.entries.stream().filter(e -> e.type instanceof HazardTypeRadiation).findFirst().ifPresent(e -> e.mods.add(new NBTModifier(NBTModifier.NBTKey.ACTIVATION)));
            if (alpha > 0) data.addEntry(new HazardEntry(Alpha.INSTANCE, alpha).addMod(new NBTModifier(NBTModifier.NBTKey.ALPHA)));
            if (beta > 0) data.addEntry(new HazardEntry(Beta.INSTANCE, beta).addMod(new NBTModifier(NBTModifier.NBTKey.BETA)));
            if (gamma > 0) data.addEntry(new HazardEntry(Gamma.INSTANCE, gamma).addMod(new NBTModifier(NBTModifier.NBTKey.GAMMA)));
            if (neutrons > 0) data.addEntry(new HazardEntry(Neutrons.INSTANCE, neutrons).addMod(new NBTModifier(NBTModifier.NBTKey.NEUTRONS)));
            if (x > 0) data.addEntry(new HazardEntry(XRay.INSTANCE, x).addMod(new NBTModifier(NBTModifier.NBTKey.XRAY)));
        }
    }
}
