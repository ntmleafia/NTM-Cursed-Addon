package com.leafia.init.hazards;

import com.hbm.hazard.HazardData;
import com.hbm.hazard.HazardEntry;
import com.hbm.hazard.HazardRegistry;
import com.hbm.hazard.HazardSystem;
import com.hbm.hazard.type.HazardTypeRadiation;
import com.hbm.inventory.OreDictManager.DictFrame;
import com.leafia.database.AddonOreDictHazards;
import com.leafia.init.hazards.modifiers.NBTModifier;
import com.leafia.init.hazards.modifiers.NBTModifier.NBTKey;
import com.leafia.init.hazards.types.radiation.*;
import com.leafia.settings.AddonConfig;
import net.minecraft.item.Item;

import java.util.*;
import java.util.Map.Entry;

public class ItemRads {

	public static MultiRadContainer actinium227 = new MultiRadContainer(0,30,0,0,0);

	public static MultiRadContainer americium241 = new MultiRadContainer(8.5,0,0,0,0);
	public static MultiRadContainer americium242 = new MultiRadContainer(0,9.5,0,0,0);
	public static MultiRadContainer americiumRG = new MultiRadContainer(0,9,0,0,0);
	public static MultiRadContainer americiumFuel = new MultiRadContainer(0,4.75,0,0,0);

	public static MultiRadContainer balefire = new MultiRadContainer(0,0,0,300000,0);

	public static MultiRadContainer cobalt60 = new MultiRadContainer(0,30,30,60,0).multiply(1/3f);

	public static MultiRadContainer flashlead = new MultiRadContainer(0,500+10000+2000,500,500,0).multiply(1/2f);

	public static MultiRadContainer gold198 = new MultiRadContainer(0,500,500,500,0).multiply(1/2f);

	public static MultiRadContainer lead209 = new MultiRadContainer(0,10000,0,0,0);

	public static MultiRadContainer moxie = new MultiRadContainer(2.5,0,0,0,0);

	public static MultiRadContainer neptunium237 = new MultiRadContainer(2.5,0,0,0,0);
	public static MultiRadContainer neptuniumFuel = new MultiRadContainer(1.5,0,0,0,0);

	public static MultiRadContainer plutonium = new MultiRadContainer(7.5,0,0,0,0);
	public static MultiRadContainer plutoniumRG = new MultiRadContainer(6.25,0,0,0,0);
	public static MultiRadContainer plutoniumFuel = new MultiRadContainer(4.25,0,0,0,0);
	public static MultiRadContainer plutonium238 = new MultiRadContainer(10,0,0,0,0);
	public static MultiRadContainer plutonium239 = new MultiRadContainer(5,0,0,0,0);
	public static MultiRadContainer plutonium240 = new MultiRadContainer(7,0,0,0,0);
	public static MultiRadContainer plutonium241 = new MultiRadContainer(0,25f,25f,0,0).multiply(1/2f);
	public static MultiRadContainer plutonium238be = plutonium238.multiply(0.5);

	public static MultiRadContainer polonium210 = new MultiRadContainer(75,0,0,0,0);
	public static MultiRadContainer polonium210be = polonium210.multiply(0.5);

	public static MultiRadContainer radium226 = new MultiRadContainer(7.5,0,0,0,0,5,0);
	public static MultiRadContainer radium226be = radium226.multiply(0.5);

	public static MultiRadContainer schrabidium326 = new MultiRadContainer(15,0,0,0,0);
	public static MultiRadContainer solinium327 = new MultiRadContainer(17.5,0,0,0,0);
	public static MultiRadContainer schrabidate = new MultiRadContainer(1.5,0,0,0,0);
	public static MultiRadContainer schraranium = new MultiRadContainer(1.5,0,0,0,0);
	public static MultiRadContainer schrabidiumLow = new MultiRadContainer(2.52,0,0,0,0);
	public static MultiRadContainer schrabidiumMedium = new MultiRadContainer(5.85,0,0,0,0);
	public static MultiRadContainer schrabidiumHigh = new MultiRadContainer(8.8,0,0,0,0);

	public static MultiRadContainer technetium99 = new MultiRadContainer(0,2.75,0,0,0);

	public static MultiRadContainer tritium = new MultiRadContainer(0,0,0.5f,0,0);

	public static MultiRadContainer thorium232 = new MultiRadContainer(0.1,0,0,0,0);
	public static MultiRadContainer thoriumFuel = new MultiRadContainer(1.75,0,0,0,0);

	public static MultiRadContainer uranium = new MultiRadContainer(0.35,0,0,0,0,0.5,0);
	public static MultiRadContainer uranium233 = new MultiRadContainer(5,0,0,0,0,0.85,0);
	public static MultiRadContainer uranium235 = new MultiRadContainer(1,0,0,0,0,0.75,0);
	public static MultiRadContainer uranium238 = new MultiRadContainer(0.25,0,0,0,0,0.4,0);
	public static MultiRadContainer uraniumFuel = new MultiRadContainer(0.5,0,0,0,0,0.65,0);

	public static MultiRadContainer waste = new MultiRadContainer(0,125,125,50,25);
	public static MultiRadContainer waste_v = waste.copy().multiply(1/2f);
	public static MultiRadContainer wasteUranium = waste; // temporary
	public static MultiRadContainer wastePlutonium = waste; // temporary
	public static MultiRadContainer wasteThorium = waste; // temporary
	public static MultiRadContainer wasteMOX = waste; // temporary
	public static MultiRadContainer wasteSchrabidium = waste; // temporary

	public static MultiRadContainer bismuth209zfb = uranium235.multiply(0.35f);
	public static MultiRadContainer plutonium241zfb = plutonium241.multiply(0.5f);
	public static MultiRadContainer americium242zfb = americiumRG.multiply(0.5f);

	public static class MultiRadContainer {
		public double alpha;
		public double beta;
		public double gamma;
		public double x;
		public double neutrons;
		public double radon;
		public double activation;

		public MultiRadContainer(double alpha, double beta, double x, double gamma, double neutrons) {
			this.alpha = alpha;
			this.beta = beta;
			this.x = x;
			this.gamma = gamma;
			this.neutrons = neutrons;
		}

		public MultiRadContainer(double alpha, double beta, double x, double gamma, double neutrons, double radon, double activation) {
			this(alpha,beta,x,gamma,neutrons);
			this.radon = radon;
			this.activation = activation;
		}

		public MultiRadContainer multiply(double v) {
			alpha *= v;
			beta *= v;
			x *= v;
			gamma *= v;
			neutrons *= v;
			radon *= v;
			activation *= v;
			return this;
		}

		public MultiRadContainer copy() {
			return new MultiRadContainer(alpha, beta, x, gamma, neutrons, radon, activation);
		}

		public void register(Item item) {
			HazardData data = HazardSystem.itemMap.computeIfAbsent(item, k -> new HazardData());
			// if you need to add a hazard modifier to HazardTypeRadiation, use
			// data.entries.stream().filter(e -> e.type instanceof HazardTypeRadiation).findFirst().ifPresent(e -> e.mods.add(new NBTModifier(NBTModifier.NBTKey.ACTIVATION)));
			apply(data);
		}
		public void register(DictFrame frame) {
			Map<String,Float> map = AddonOreDictHazards.dictMap.get(frame);
			if (map == null) {
				System.out.println("\uD83C\uDF3FCAUTION: dictMap for "+frame.ingot()+" could not be captured");
				return;
			}
			for (Entry<String,Float> entry : map.entrySet())
				register(entry.getKey(),entry.getValue());
		}
		public void register(String item) { register(item,1); }
		public void register(String item,float mul) {
			HazardData data = HazardSystem.oreMap.computeIfAbsent(item, k -> new HazardData());
			apply(data,mul);
		}
		public void apply(HazardData data) { apply(data,1); }
		public void apply(HazardData data,float multiplier) {
			/// thanks mov but im just deleting it ^-^
			if (!AddonConfig.enableHealthMod) return;
			for (HazardEntry entry : data.entries) {
				if (entry.type instanceof HazardTypeRadiation) {
					data.entries.remove(entry);
					break;
				}
			}
			if (alpha > 0) data.addEntry(new HazardEntry(Alpha.INSTANCE, alpha*multiplier).addMod(new NBTModifier(NBTModifier.NBTKey.ALPHA)));
			if (beta > 0) data.addEntry(new HazardEntry(Beta.INSTANCE, beta*multiplier).addMod(new NBTModifier(NBTModifier.NBTKey.BETA)));
			if (gamma > 0) data.addEntry(new HazardEntry(Gamma.INSTANCE, gamma*multiplier).addMod(new NBTModifier(NBTModifier.NBTKey.GAMMA)));
			if (neutrons > 0) data.addEntry(new HazardEntry(Neutrons.INSTANCE, neutrons*multiplier).addMod(new NBTModifier(NBTModifier.NBTKey.NEUTRONS)));
			if (x > 0) data.addEntry(new HazardEntry(XRay.INSTANCE, x*multiplier).addMod(new NBTModifier(NBTModifier.NBTKey.XRAY)));
			if (radon > 0) data.addEntry(new HazardEntry(Radon.INSTANCE, radon*multiplier).addMod(new NBTModifier(NBTKey.RADON)));
			if (activation > 0) data.addEntry(new HazardEntry(HazardRegistry.RADIATION, activation*multiplier).addMod(new NBTModifier(NBTKey.ACTIVATION)));
		}
	}
}
