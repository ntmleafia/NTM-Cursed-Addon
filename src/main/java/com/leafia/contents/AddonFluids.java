package com.leafia.contents;

import com.hbm.handler.pollution.PollutionHandler;
import com.hbm.handler.pollution.PollutionHandler.PollutionType;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.trait.*;
import com.hbm.render.misc.EnumSymbol;
import com.leafia.contents.fluids.AddonFluidType;
import com.leafia.contents.fluids.FluorideFluid;
import com.leafia.contents.fluids.traits.FT_LFTRCoolant;
import com.llib.exceptions.LeafiaDevFlaw;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import static com.hbm.inventory.fluid.Fluids.*;

public class AddonFluids {
	private static final HashMap<Fluid,FluidType> fluidMapping = new HashMap();
	public static final List<FluidType> metaOrderPointer;
	static {
		Field metaField = null;
		try {
			metaField = Fluids.class.getDeclaredField("metaOrder");
			metaField.setAccessible(true);
			metaOrderPointer = (List<FluidType>)metaField.get(null);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new LeafiaDevFlaw(e);
		}
	}
	public static class AddonFF {
		public static Fluid fluoride = new FluorideFluid("fluoride").setDensity(1000).setTemperature(500+273);
		public static void init() {
			registerFluid(fluoride);
		}
		private static void registerFluid(Fluid fluid) {
			FluidRegistry.registerFluid(fluid);
			FluidRegistry.addBucketForFluid(fluid);
		}
		public static void setFromRegistry() {
			fluoride = FluidRegistry.getFluid("fluoride");
		}
	}
	public static void addCompatFluid(FluidType fluid) {
		if (fluid.getFF() == null) return;
		fluidMapping.put(fluid.getFF(),fluid);
	}
	public static FluidType fromFF(Fluid fluid) {
		return fluidMapping.getOrDefault(fluid,Fluids.NONE);
	}
	/// this particular salt does have a caveat, the lithium isotope it needs to use is Lithium-7 cause Lithium-6 absorbs a neutron to turn into tritium and helium-4 - whatsapp_2
	public static FluidType FLUORIDE;
	public static FluidType UF6_233;
	public static FluidType UF6_235;
	public static FluidType HOT_WATER;
	public static FluidType HOT_AIR;
	public static FluidType RADSPICE_SLOP;
	public static FluidType COOLANT_MAL;
	public static FluidType DEATHSTEAM;
	public static FluidType HF;
	public static FluidType N2O; // will you stop begging me
	public static FluidType FLUORINE; // oh boy fluorine don't exists
	public static void init() {
		Function<FluidTrait,Boolean> rejectBoiling = (trait)->{
			if (trait instanceof FT_Heatable) return false;
			if (trait instanceof FT_Coolable) return false;
			return true;
		};
		FLUORIDE = new AddonFluidType("FLIBE",0xd3d8b9,5,0,0,EnumSymbol.NONE).setTemp(500).addTraits(LIQUID,new FT_Polluting().release(PollutionHandler.PollutionType.POISON, POISON_EXTREME/2).release(PollutionType.HEAVYMETAL,LEAD_FUEL),new FT_LFTRCoolant(1)).setFFNameOverride("fluoride");
		UF6_233 = new AddonFluidType("UF6_233",UF6);
		UF6_235 = new AddonFluidType("UF6_235",UF6);
		HOT_WATER = new AddonFluidType("HOT_WATER",WATER,rejectBoiling).setTemp(70);
		HOT_AIR = new AddonFluidType("HOT_AIR",AIR,rejectBoiling).setTemp(50);
		RADSPICE_SLOP = new AddonFluidType("RADSPICE_SLOP",0x8baf2d,9999999,99999999,9999999,EnumSymbol.RADIATION).addTraits(LIQUID,new FT_VentRadiation(20_000/1000f),VISCOUS);
		COOLANT_HOT.temperature = 400;
		COOLANT_MAL = new AddonFluidType("COOLANT_MAL",0x880f12,1,0,0,EnumSymbol.NONE).setTemp(1000).addTraits(GASEOUS);
		DEATHSTEAM = new AddonFluidType("DEATHSTEAM",0x7c0000,4,0,0,EnumSymbol.NONE).setTemp(900).addTraits(GASEOUS,UNSIPHONABLE);
		HF = new AddonFluidType("HF",0x3ea7ff,4,0,1,EnumSymbol.ACID).addTraits(GASEOUS,new FT_Corrosive(40),new FT_Poison(true, 1));
		N2O = new AddonFluidType("N2O",0x6faf30,2,0,0,EnumSymbol.OXIDIZER).addTraits(GASEOUS);
		if (Fluids.fromName("FLUORINE") != NONE) {
			FLUORINE = Fluids.fromName("FLUORINE");
			AddonFluidType.id++;
		} else
			FLUORINE = new AddonFluidType("FLUORINE",0xc5b055,4,0,4,EnumSymbol.NOWATER).addTraits(GASEOUS);
	}
}
