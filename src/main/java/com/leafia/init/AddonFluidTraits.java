package com.leafia.init;

import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.trait.FT_Coolable;
import com.hbm.inventory.fluid.trait.FT_Coolable.CoolingType;
import com.hbm.inventory.fluid.trait.FT_Heatable;
import com.hbm.inventory.fluid.trait.FT_Heatable.HeatingType;
import com.hbm.inventory.fluid.trait.FluidTrait;
import com.leafia.contents.AddonFluids;
import com.leafia.contents.fluids.AddonFluidType;
import com.leafia.contents.fluids.traits.*;
import com.leafia.contents.machines.misc.modular_turbine.core.MTCoreTE;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static com.hbm.inventory.fluid.trait.FluidTrait.traitList;
import static com.hbm.inventory.fluid.trait.FluidTrait.traitNameMap;

public class AddonFluidTraits {
	static {
		registerTrait("dfceff",FT_DFCFuel.class);
		registerTrait("desc",FT_Description.class);
		registerTrait("lftrcoolant",FT_LFTRCoolant.class);
		registerTrait("magnetic",FT_Magnetic.class);
		registerTrait("aprcoolant",FT_APRCoolant.class);
		registerTrait("aprcoolable",FT_APRCoolable.class);
	}
	public static final Map<AddonFluidType,FluidType> copyTraits = new HashMap<>();
	public static final FT_Magnetic MAGNETIC = new FT_Magnetic();
	public static void preInit() {
		Fluids.DEUTERIUM.addTraits(new FT_DFCFuel(1.2F));
		Fluids.TRITIUM.addTraits(new FT_DFCFuel(1.3F));
		Fluids.OXYGEN.addTraits(new FT_DFCFuel(1.1F),MAGNETIC,new FT_Heatable().setEff(HeatingType.BOILER,1).addStep(3,1,AddonFluids.OXYGEN_GAS,50));
		Fluids.HYDROGEN.addTraits(new FT_DFCFuel(1F));
		Fluids.NITAN.addTraits(new FT_DFCFuel(1.6F));
		Fluids.UF6.addTraits(new FT_DFCFuel(1.3F));
		Fluids.PUF6.addTraits(new FT_DFCFuel(1.4F));
		Fluids.SAS3.addTraits(new FT_DFCFuel(1.5F));
		Fluids.SCHRABIDIC.addTraits(new FT_DFCFuel(1.7F));
		Fluids.AMAT.addTraits(new FT_DFCFuel(2.2F),MAGNETIC);
		Fluids.ASCHRAB.addTraits(new FT_DFCFuel(2.5F),MAGNETIC);
		Fluids.PEROXIDE.addTraits(new FT_DFCFuel(1.05F));
		Fluids.SULFURIC_ACID.addTraits(new FT_DFCFuel(1.3F));
		Fluids.NITRIC_ACID.addTraits(new FT_DFCFuel(1.4F));
		Fluids.SOLVENT.addTraits(new FT_DFCFuel(1.45F));
		Fluids.RADIOSOLVENT.addTraits(new FT_DFCFuel(1.6F));
		Fluids.NITROGLYCERIN.addTraits(new FT_DFCFuel(1.5F));
		Fluids.DEATH.addTraits(new FT_DFCFuel(1.8F));
		Fluids.WATZ.addTraits(new FT_DFCFuel(1.5F));
		Fluids.XENON.addTraits(new FT_DFCFuel(1.25F));
		Fluids.BALEFIRE.addTraits(new FT_DFCFuel(2.4F));
		Fluids.STELLAR_FLUX.addTraits(new FT_DFCFuel(2.65F),MAGNETIC);
		AddonFluids.N2O.addTraits(new FT_DFCFuel(1.4F));
		AddonFluids.PYROGEL.addTraits(new FT_DFCFuel(2.0F));

		Fluids.COOLANT_HOT.addTraits(new FT_Heatable().setEff(HeatingType.BOILER, 1.0D).setEff(HeatingType.HEATEXCHANGER, 1.0D).addStep(700, 1, AddonFluids.COOLANT_MAL, 1));
		AddonFluids.COOLANT_MAL.addTraits(new FT_Coolable(Fluids.COOLANT_HOT, 1, 1, 700).setEff(CoolingType.HEATEXCHANGER, 1.0D));

		double eff_steam_boil = 1.0D;
		double eff_steam_heatex = 0.25D;
		Fluids.ULTRAHOTSTEAM.addTraits(new FT_Heatable().setEff(HeatingType.BOILER, eff_steam_boil).setEff(HeatingType.HEATEXCHANGER, eff_steam_heatex).addStep(960, 10, AddonFluids.DEATHSTEAM, 1));

		double eff_steam_turbine = 1.0D;
		double eff_steam_cool = 0.5D;
		// sorry to break it to you but DEATHSTEAM isn't meant to be used in turbines,
		// the modular turbine feature is a bonus feature and I don't want to
		// encourage new players into thinking they're usable
		// - ntmleafia
		AddonFluids.DEATHSTEAM.addTraits(new FT_Coolable(Fluids.ULTRAHOTSTEAM, 1, 10, 960)/*.setEff(CoolingType.TURBINE, eff_steam_turbine)*/.setEff(CoolingType.HEATEXCHANGER, eff_steam_cool));

		MTCoreTE.steamTypes.add(AddonFluids.DEATHSTEAM);
		MTCoreTE.steamTypes.add(Fluids.ULTRAHOTSTEAM);
		MTCoreTE.steamTypes.add(Fluids.SUPERHOTSTEAM);
		MTCoreTE.steamTypes.add(Fluids.HOTSTEAM);
		MTCoreTE.steamTypes.add(Fluids.STEAM);
		MTCoreTE.steamTypes.add(Fluids.SPENTSTEAM);

		/*
		ES    UDS    SDS     DS        S
		1B -> 10B -> 100B -> 1,000B -> 10,000B
		 */

		AddonFluids.N2O.addTraits(new FT_Description("Will you stop begging me?",false));
		AddonFluids.CRYOINTER.addTraits(new FT_Heatable().setEff(HeatingType.PA,2.5).setEff(HeatingType.BOILER,1).addStep(750,1,Fluids.COOLANT,1));
		Fluids.CRYOGEL.addTraits(new FT_Description("Lord bob asked me to$change his horrendous snowball recipe",false));

		AddonFluids.OXYGEN_GAS.addTraits(new FT_APRCoolant(Fluids.OXYGEN,50,1));

		for (Entry<AddonFluidType,FluidType> entry : copyTraits.entrySet()) {
			if (entry.getKey().copyFunction != null)
				entry.getKey().copyTraits(entry.getValue(),entry.getKey().copyFunction);
		}
	}
	private static void registerTrait(String name, Class<? extends FluidTrait> clazz) {
		traitNameMap.put(name, clazz);
		traitList.add(clazz);
	}
}
