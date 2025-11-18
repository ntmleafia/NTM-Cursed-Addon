package com.leafia.init;

import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.trait.FluidTrait;
import com.leafia.contents.fluids.traits.FT_DFCFuel;

import static com.hbm.inventory.fluid.trait.FluidTrait.traitList;
import static com.hbm.inventory.fluid.trait.FluidTrait.traitNameMap;

public class AddonFluidTraits {
	static {
		registerTrait("dfceff",FT_DFCFuel.class);
	}
	public static void preInit() {
		Fluids.DEUTERIUM.addTraits(new FT_DFCFuel(1.2F));
		Fluids.TRITIUM.addTraits(new FT_DFCFuel(1.3F));
		Fluids.OXYGEN.addTraits(new FT_DFCFuel(1.1F));
		Fluids.HYDROGEN.addTraits(new FT_DFCFuel(1F));
		Fluids.NITAN.addTraits(new FT_DFCFuel(1.6F));
		Fluids.UF6.addTraits(new FT_DFCFuel(1.3F));
		Fluids.PUF6.addTraits(new FT_DFCFuel(1.4F));
		Fluids.SAS3.addTraits(new FT_DFCFuel(1.5F));
		Fluids.SCHRABIDIC.addTraits(new FT_DFCFuel(1.7F));
		Fluids.AMAT.addTraits(new FT_DFCFuel(2.2F));
		Fluids.ASCHRAB.addTraits(new FT_DFCFuel(2.5F));
		Fluids.PEROXIDE.addTraits(new FT_DFCFuel(1.05F));
		Fluids.SULFURIC_ACID.addTraits(new FT_DFCFuel(1.3F));
		Fluids.NITRIC_ACID.addTraits(new FT_DFCFuel(1.4F));
		Fluids.SOLVENT.addTraits(new FT_DFCFuel(1.45F));
		Fluids.RADIOSOLVENT.addTraits(new FT_DFCFuel(1.6F));
		Fluids.NITROGLYCERIN.addTraits(new FT_DFCFuel(1.5F));
		//Fluids.LIQUID_OSMIRIDIUM.addTraits(new FT_DFCFuel(1.8F));
		Fluids.WATZ.addTraits(new FT_DFCFuel(1.5F));
		Fluids.XENON.addTraits(new FT_DFCFuel(1.25F));
		Fluids.BALEFIRE.addTraits(new FT_DFCFuel(2.4F));
	}
	private static void registerTrait(String name, Class<? extends FluidTrait> clazz) {
		traitNameMap.put(name, clazz);
		traitList.add(clazz);
	}
}