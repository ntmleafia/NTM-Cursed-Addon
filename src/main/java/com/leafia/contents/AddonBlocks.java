package com.leafia.contents;

import com.custom_hbm.contents.oilycoal.BlockCoalBurning;
import com.custom_hbm.contents.oilycoal.BlockCoalOil;
import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.ModSoundType;
import com.hbm.lib.HBMSoundHandler;
import com.hbm.main.MainRegistry;
import com.hbm.render.block.BlockBakeFrame;
import com.leafia.AddonBase;
import com.leafia.contents.AddonFluids.AddonFF;
import com.leafia.contents.bomb.balefire.AshBalefire;
import com.leafia.contents.bomb.balefire.BaleoniteBlock;
import com.leafia.contents.bomb.digamma.DigammititeBlock;
import com.leafia.contents.building.broof.BroofBlock;
import com.leafia.contents.building.light.LightBlock;
import com.leafia.contents.building.light.LightEmitter;
import com.leafia.contents.building.mixed.BlockMixedConcrete;
import com.leafia.contents.building.pinkdoor.BlockPinkDoor;
import com.leafia.contents.building.sign.SignBlock;
import com.leafia.contents.debug.ff_test.source.DebugSourceBlock;
import com.leafia.contents.debug.ff_test.tank.DebugTankBlock;
import com.leafia.contents.fluids.FluorideFluid.FluorideFluidBlock;
import com.leafia.contents.machines.elevators.EvBuffer;
import com.leafia.contents.machines.elevators.EvPulley;
import com.leafia.contents.machines.elevators.EvShaft;
import com.leafia.contents.machines.elevators.EvShaftNeo;
import com.leafia.contents.machines.elevators.car.ElevatorLight;
import com.leafia.contents.machines.elevators.floors.EvFloor;
import com.leafia.contents.machines.misc.heatex.CoolantHeatexBlock;
import com.leafia.contents.machines.panel.controltorch.ControlTorchBlock;
import com.leafia.contents.machines.powercores.ams.base.AMSBaseBlock;
import com.leafia.contents.machines.powercores.ams.emitter.AMSEmitterBlock;
import com.leafia.contents.machines.powercores.ams.stabilizer.AMSStabilizerBlock;
import com.leafia.contents.machines.powercores.dfc.AddonCoreComponent;
import com.leafia.contents.machines.powercores.dfc.OsmiridiumBlock;
import com.leafia.contents.machines.processing.mixingvat.MixingVatBlock;
import com.leafia.contents.machines.reactors.lftr.components.arbitrary.MSRArbitraryBlock;
import com.leafia.contents.machines.reactors.lftr.components.control.MSRControlBlock;
import com.leafia.contents.machines.reactors.lftr.components.control.MSRControlExtension;
import com.leafia.contents.machines.reactors.lftr.components.ejector.MSREjectorBlock;
import com.leafia.contents.machines.reactors.lftr.components.element.MSRElementBlock;
import com.leafia.contents.machines.reactors.lftr.components.plug.MSRPlugBlock;
import com.leafia.contents.machines.reactors.lftr.processing.separator.SaltSeparatorBlock;
import com.leafia.contents.machines.reactors.pwr.blocks.PWRHullBlock;
import com.leafia.contents.machines.reactors.pwr.blocks.PWRReflectorBlock;
import com.leafia.contents.machines.reactors.pwr.blocks.components.channel.PWRChannelBlock;
import com.leafia.contents.machines.reactors.pwr.blocks.components.channel.PWRConductorBlock;
import com.leafia.contents.machines.reactors.pwr.blocks.components.channel.PWRExchangerBlock;
import com.leafia.contents.machines.reactors.pwr.blocks.components.computer.PWRComputerBlock;
import com.leafia.contents.machines.reactors.pwr.blocks.components.control.PWRControlBlock;
import com.leafia.contents.machines.reactors.pwr.blocks.components.element.PWRElementBlock;
import com.leafia.contents.machines.reactors.pwr.blocks.components.port.PWRPortBlock;
import com.leafia.contents.machines.reactors.pwr.blocks.components.terminal.PWRTerminalBlock;
import com.leafia.contents.machines.reactors.pwr.blocks.wreckage.PWRMeshedWreck;
import com.leafia.contents.machines.reactors.pwr.blocks.wreckage.PWRWreckMetal;
import com.leafia.contents.machines.reactors.pwr.blocks.wreckage.PWRWreckStone;
import com.leafia.contents.network.computers.audiocable.AudioCableBlock;
import com.leafia.contents.network.computers.cable.ComputerCableBlock;
import com.leafia.contents.network.ff_duct.FFDuctRadShielded;
import com.leafia.contents.network.ff_duct.FFDuctStandard;
import com.leafia.contents.network.ff_duct.utility.converter.FFConverterBlock;
import com.leafia.contents.network.ff_duct.utility.pump.FFPumpBlock;
import com.leafia.contents.network.fluid.gauges.FluidDuctGauge;
import com.leafia.contents.network.fluid.valves.FluidDuctValve;
import com.leafia.contents.network.fluid.valves.FluidDuctValveRS;
import com.leafia.contents.network.pipe_amat.AmatDuctStandard;
import com.leafia.contents.network.pipe_amat.charger.AmatDuctChargerBlock;
import com.leafia.contents.network.spk_cable.SPKCableBlock;
import com.leafia.contents.nonmachines.fftank.FFTankBlock;
import com.leafia.dev.blocks.blockbase.AddonBlockPowder;
import com.leafia.dev.blocks.legacy.LegacyBlockHazardMeta;
import com.leafia.dev.blocks.legacy.LegacyWasteEarth;
import com.leafia.dev.blocks.legacy.LegacyWasteIce;
import com.leafia.dev.blocks.legacy.LegacyWasteSand;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.EnumDyeColor;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.*;
import java.util.Map.Entry;
import static com.leafia.contents.AddonBlocks.GenericBlockResistance.*;

public class AddonBlocks {
	public static final List<Block> ALL_BLOCKS = new ArrayList();

	public enum GenericBlockResistance {
		// Concrete mayhem
		CONCRETE(84),			// used to be 2400 lmao
		CONCRETE_BRICKS(96),	// used to be 3600 lmao
		RE_BRICKS(180),			// used to be 4800 lmao
		REBARPILLAR(108),		// used to be 2400 lmao

		// Early game
		COMPOUND_MESH(240),		// used to be 6000 lmao

		// Reinforced vanilla
		RE_GLASS(15),			// used to be 120 lmao
		DN_STONE(60),			// used to be 1800 lmao
		RE_LIGHTGEM(48),		// easter egg: the texture glowstone is actually called lightgem :o
		RE_SANDSTONE(24),		// used to be 240 lmfao its just a sand
		RE_RSLAMP(48),			// used to be 180 lmao

		//////////////////////////////////////////////////////////////////////////////////////

		// Asbestos
		ASB_CONCRETE(90),

		// Ducrete
		DUCRETE(300),
		DUCRETE_BRICKS(450),
		RE_DUCRETE(600),

		// Specialized bricks
		BRICKS_LIGHT(12),
		BRICKS_FIREBRICKS(21),
		BRICKS_ASBESTOS(600),
		BRICKS_OBSIDIAN(72),	// man 4800

		// Absurd crappies
		CMB_TILE(3E+3),
		RE_CMB_BRICKS(3E+4),

		// Ores
		ORE_VANILLA(5),
		ORE_CLUSTER(6),
		ORE_HEAVY(8),
		ORE_HEAVY_CLUSTER(9),

		PWR_CASING(165),
		PWR_INSIDE(27),
		;
		public final float v;
		GenericBlockResistance(float resistance) {
			this.v = resistance * 5/3; // counter-spaghetti minecraft coding
		}
		GenericBlockResistance(double resistance) {
			this.v = (float)resistance * 5/3; // counter-spaghetti minecraft coding
		}
		public float half() {
			return this.v /2;
		}
		public float third() {
			return this.v /3;
		}
		public float quart() {
			return this.v /4;
		}
		public float quint() {
			return this.v /5;
		}
		public float triquart() {
			return this.v * 3/4;
		}
	}

	public static final Block door_fuckoff = new BlockPinkDoor(Material.WOOD, "door_fuckoff").setHardness(3);
	public static final Block spk_cable = new SPKCableBlock(Material.IRON, "spk_cable").setHardness(5.0F).setResistance(10.0F).setCreativeTab(MainRegistry.machineTab);
	public static final Block dfc_reinforced = new AddonCoreComponent(Material.IRON, "dfc_reinforced").setHardness(5.0F).setResistance(50.0F).setCreativeTab(MainRegistry.machineTab);
	public static final Block dfc_exchanger = new AddonCoreComponent(Material.IRON, "dfc_exchanger").setHardness(5.0F).setResistance(50.0F).setCreativeTab(MainRegistry.machineTab);
	public static final Block dfc_cemitter = new AddonCoreComponent(Material.IRON, "dfc_cemitter").setHardness(5.0F).setResistance(50.0F).setCreativeTab(MainRegistry.machineTab);

	static boolean letter_dummy = LetterSigns.dummy;
	public static class LetterSigns {
		static boolean dummy = false;
		private static final String letters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		public static final Map<String,Block> signs = new HashMap<>();
		static {
			for (int i = 0; i < letters.length(); i++) {
				String s = letters.substring(i,i+1);
				SignBlock sign = new SignBlock(s);
				sign.setHardness(5);
				signs.put(s,sign);
			}
		}
	}

	static boolean mixed_dummy = MixedConcretes.dummy;
	public static class MixedConcretes {
		static boolean dummy = false;
		public static final Map<String,String> idMap = new HashMap<>();
		public static final Map<String,BlockMixedConcrete> blocks = new HashMap<>();
		static {
			idMap.put("brick_concrete","brick");
			idMap.put("brick_concrete_mossy","brickm");
			idMap.put("brick_concrete_cracked","brickc");
			idMap.put("brick_concrete_broken","brickb");
			idMap.put("ducrete_brick","ducrete");
			idMap.put("concrete","raw");
			for (EnumDyeColor dye : EnumDyeColor.values())
				idMap.put("concrete_"+dye.getName(),dye.getName());
			Set<Entry<String,String>> set = idMap.entrySet();
			Entry<String,String>[] entries = new Entry[set.size()];
			int index = 0;
			for (Entry<String,String> entry : set)
				entries[index++] = entry;
			for (int i = 0; i < entries.length-1; i++) {
				for (int j = i+1; j < entries.length; j++) {
					String id = "concrete__mixed_"+entries[i].getValue()+"_"+entries[j].getValue();
					blocks.put(id,new BlockMixedConcrete(id,entries[i].getKey(),entries[j].getKey()));
				}
			}
		}
	}

	public static final Block ff_duct = new FFDuctStandard(Material.IRON, "ff_duct").setHardness(5.0F).setResistance(10.0F).setCreativeTab(MainRegistry.templateTab);
	public static final Block ff_pump = new FFPumpBlock(Material.IRON,"ff_pump").setHardness(5.0F).setResistance(10.0F).setCreativeTab(MainRegistry.templateTab);
	public static final Block ff_converter = new FFConverterBlock(Material.IRON,"ff_converter").setHardness(5.0F).setResistance(10.0F).setCreativeTab(MainRegistry.templateTab);

	public static final Block ff_duct_solid_shielded = new FFDuctRadShielded(Material.IRON,"ff_duct_solid_shielded").setHardness(15.0F).setResistance(COMPOUND_MESH.v).setCreativeTab(MainRegistry.templateTab);

	public static final Block amat_duct = new AmatDuctStandard(Material.IRON, "amat_duct").setHardness(5.0F).setResistance(10.0F).setCreativeTab(MainRegistry.templateTab);
	public static final Block amat_charger = new AmatDuctChargerBlock(Material.IRON, "amat_duct_charger").setHardness(5.0F).setResistance(10.0F).setCreativeTab(MainRegistry.templateTab);

	static boolean test_dummy = TestBlocks.dummy;
	public static class TestBlocks {
		static boolean dummy = false;
		public static final Block ffsource = new DebugSourceBlock(Material.ANVIL,"test_ff_source");
		public static final Block fftank = new DebugTankBlock(Material.ANVIL,"test_ff_tank");
	}

	public static final Block salt_separator = new SaltSeparatorBlock(Material.IRON,"salt_separator").setHardness(5.0F).setResistance(20.0F).setCreativeTab(MainRegistry.machineTab);

	static boolean msr_dummy = LFTR.dummy;
	public static class LFTR {
		static boolean dummy = false;
		public static final float generalHardness = 12;
		public static final Block element = new MSRElementBlock(Material.IRON,"msr_element").setCreativeTab(MainRegistry.machineTab).setHardness(generalHardness);
		public static final Block plug = new MSRPlugBlock(Material.IRON,"msr_plug").setCreativeTab(MainRegistry.machineTab).setHardness(generalHardness);
		public static final Block control = new MSRControlBlock(Material.IRON,"msr_control").setCreativeTab(MainRegistry.machineTab).setHardness(generalHardness);
		public static final Block extension = new MSRControlExtension(Material.IRON,"msr_control_extension").setCreativeTab(MainRegistry.machineTab).setHardness(generalHardness);
		public static final Block arbitrary = new MSRArbitraryBlock(Material.IRON,"msr_arbitrary").setCreativeTab(MainRegistry.machineTab).setHardness(generalHardness);
		public static final Block ejector = new MSREjectorBlock(Material.IRON,"msr_ejector").setCreativeTab(MainRegistry.machineTab).setHardness(generalHardness);
	}
	public static final Block mixingvat = new MixingVatBlock(Material.IRON,"mixingvat").setCreativeTab(MainRegistry.machineTab);
	public static final Block coolant_heatex = new CoolantHeatexBlock(Material.IRON, "coolant_heatex").setHardness(5.0F).setResistance(10.0F).setCreativeTab(MainRegistry.machineTab);

	public static class PWR {
		public static final int guiID = 273;
		public static final float generalHardness = 24;
		public static final float innerHardness = 8;
		public static SoundType soundTypePWRTube = ModSoundType.placeBreakStep(HBMSoundHandler.pipePlaced, HBMSoundHandler.metalBlock, HBMSoundHandler.metalBlock, 0.5F, 1.0F);
		public static final Block hull = new PWRHullBlock().setCreativeTab(MainRegistry.machineTab).setHardness(generalHardness).setResistance(PWR_CASING.v);
		public static final Block reflector = new PWRReflectorBlock().setCreativeTab(MainRegistry.machineTab).setHardness(generalHardness).setResistance(PWR_CASING.v);

		public static final Block element = new PWRElementBlock("lwr_element").setCreativeTab(MainRegistry.machineTab).setHardness(innerHardness).setResistance(PWR_INSIDE.v);
		public static final Block element_old = new PWRElementBlock("reactor_element").setCreativeTab(MainRegistry.machineTab).setHardness(5.0F).setResistance(10.0F);
		public static final Block element_old_blank = new PWRElementBlock("reactor_element_blank").setCreativeTab(MainRegistry.machineTab).setHardness(5.0F).setResistance(10.0F);
		public static final Block control = new PWRControlBlock("lwr_control").setCreativeTab(MainRegistry.machineTab).setHardness(innerHardness).setResistance(PWR_INSIDE.v);
		public static final Block reactor_control = new PWRControlBlock("reactor_control").setCreativeTab(MainRegistry.machineTab).setHardness(5.0F).setResistance(10.0F);

		public static final Block channel = new PWRChannelBlock().setCreativeTab(MainRegistry.machineTab).setHardness(innerHardness).setResistance(PWR_INSIDE.v);
		public static final Block conductor = new PWRConductorBlock().setCreativeTab(MainRegistry.machineTab).setHardness(innerHardness).setResistance(PWR_INSIDE.v);
		public static final Block exchanger = new PWRExchangerBlock().setCreativeTab(MainRegistry.machineTab).setHardness(innerHardness).setResistance(PWR_INSIDE.v);

		public static final Block terminal = new PWRTerminalBlock("lwr_terminal").setCreativeTab(MainRegistry.machineTab).setHardness(PWR_CASING.v).setResistance(PWR_CASING.v);
		public static final Block hatch = new PWRTerminalBlock("reactor_hatch").setCreativeTab(MainRegistry.machineTab).setHardness(5.0F).setResistance(CONCRETE_BRICKS.v);
		public static final Block hatch_alt = new PWRTerminalBlock("reactor_hatch_alt").setCreativeTab(MainRegistry.machineTab).setHardness(5.0F).setResistance(CONCRETE_BRICKS.v);
		public static final Block port = new PWRPortBlock().setCreativeTab(MainRegistry.machineTab).setHardness(PWR_CASING.v).setResistance(PWR_CASING.v);
		public static final Block computer = new PWRComputerBlock().setCreativeTab(MainRegistry.machineTab).setHardness(PWR_CASING.v).setResistance(PWR_CASING.v);

		/*public static final Block ventElement = new PWRVentElementBlock().setCreativeTab(MainRegistry.machineTab).setHardness(generalHardness).setResistance(PWR_CASING.v);
		public static final Block ventOutlet = new PWRVentOutletBlock().setCreativeTab(MainRegistry.machineTab).setHardness(generalHardness).setResistance(PWR_CASING.v);
		public static final Block ventInlet = new PWRVentInletBlock().setCreativeTab(MainRegistry.machineTab).setHardness(generalHardness).setResistance(PWR_CASING.v);
		public static final Block ventDuct = new PWRVentDuctBlock().setCreativeTab(null).setHardness(generalHardness).setResistance(PWR_CASING.v);*/

		public static final PWRMeshedWreck wreck_stone = new PWRWreckStone();
		public static final PWRMeshedWreck wreck_metal = new PWRWreckMetal();
	}

	public static final Block lightUnlit = new LightBlock(Material.IRON,"light_unlit",false).setHardness(5.0F).setResistance(10.0F).setCreativeTab(MainRegistry.blockTab);
	public static final Block lightLit = new LightBlock(Material.IRON,"light_lit",true).setHardness(5.0F).setResistance(10.0F).setCreativeTab(MainRegistry.blockTab);
	public static final Block lightEmitter = new LightEmitter(Material.AIR,"light_emitter");

	public static final Block control_torch = new ControlTorchBlock("control_torch",true).setCreativeTab(null).setLightLevel(0.5F);
	public static final Block control_torch_unlit = new ControlTorchBlock("control_torch_unlit",false);

	public static final Block fluid_duct_gauge_mdl = new FluidDuctGauge(Material.IRON, "fluid_duct_gauge_mdl").setHardness(5.0F).setResistance(10.0F).setCreativeTab(MainRegistry.templateTab);
	public static final Block fluid_duct_valve_mdl = new FluidDuctValve(Material.IRON, "fluid_duct_valve_mdl").setHardness(5.0F).setResistance(10.0F).setCreativeTab(MainRegistry.templateTab);
	public static final Block fluid_duct_valve_mdl_rs = new FluidDuctValveRS(Material.IRON, "fluid_duct_valve_mdl_rs").setHardness(5.0F).setResistance(10.0F).setCreativeTab(MainRegistry.templateTab);

	public static final Block baleonitite = new BaleoniteBlock(Material.ROCK, SoundType.STONE, "baleonite").setHardness(5.0F).setResistance(6F).setCreativeTab(MainRegistry.resourceTab);
	public static final Block ash_balefire = new AshBalefire(Material.SAND, "ash_balefire", SoundType.SAND).setLightLevel(9F/12F).setCreativeTab(MainRegistry.resourceTab).setHardness(0.5F);

	public static final Block digammitite = new DigammititeBlock(Material.ROCK, SoundType.STONE, "digammitite").setHardness(5.0F).setResistance(6F).setCreativeTab(MainRegistry.resourceTab);

	public static Block oc_cable;
	public static Block audio_cable;
	public static Block oc_cable_rad;
	public static Block audio_cable_rad;

	static boolean legacy_dummy = LegacyBlocks.dummy;
	public static class LegacyBlocks {
		static boolean dummy = false;
		public static final Block ore_coal_oil = new BlockCoalOil("ore_coal_oil").setCreativeTab(MainRegistry.resourceTab).setHardness(5.0F).setResistance(15.0F);
		public static final Block ore_coal_oil_burning = new BlockCoalBurning("ore_coal_oil_burning").setCreativeTab(MainRegistry.resourceTab).setLightLevel(10F/15F).setHardness(5.0F).setResistance(15.0F);

		public static final Block waste_terracotta = new LegacyBlockHazardMeta(Material.ROCK, SoundType.STONE, "waste_terracotta", "contamination/terracotta/", BlockBakeFrame.BlockForm.ALL, (short) 7).setCreativeTab(MainRegistry.resourceTab).setHardness(4.0F).setResistance(8.0F);
		public static final Block waste_sand_red = new LegacyWasteSand(Material.SAND, SoundType.SAND, "waste_sand_red").setHardness(0.5F).setResistance(1.0F).setCreativeTab(MainRegistry.resourceTab);
		public static final Block waste_red_sandstone = new LegacyBlockHazardMeta(Material.ROCK, SoundType.STONE, "waste_red_sandstone", "contamination/red_sandstone/", BlockBakeFrame.BlockForm.PILLAR_BOTTOM, (short) 7).setCreativeTab(MainRegistry.resourceTab).setHardness(3.0F).setResistance(6.0F);
		public static final Block waste_sand = new LegacyWasteSand(Material.SAND, SoundType.SAND, "waste_sand").setHardness(0.5F).setResistance(1.0F).setCreativeTab(MainRegistry.resourceTab);
		public static final Block waste_sandstone = new LegacyBlockHazardMeta(Material.ROCK, SoundType.STONE, "waste_sandstone", "contamination/sandstone/", BlockBakeFrame.BlockForm.PILLAR_BOTTOM, (short) 7).setCreativeTab(MainRegistry.resourceTab).setHardness(3.0F).setResistance(6.0F);
		public static final Block waste_gravel = new LegacyWasteSand(Material.GROUND, SoundType.GROUND, "waste_gravel").setHardness(0.5F).setResistance(1.0F).setCreativeTab(MainRegistry.resourceTab);
		public static final Block waste_dirt = new LegacyWasteEarth(Material.GROUND, SoundType.GROUND, true, "waste_dirt").setHardness(0.5F).setResistance(1.0F).setCreativeTab(MainRegistry.resourceTab);
		public static final Block waste_snow = new AddonBlockPowder(Material.SNOW, SoundType.SNOW, "waste_snow").setCreativeTab(MainRegistry.resourceTab).setHardness(0.1F).setLightOpacity(0);
		public static final Block waste_snow_block = new LegacyBlockHazardMeta(Material.SNOW, SoundType.SNOW, "waste_snow_block", "contamination/snow/", BlockBakeFrame.BlockForm.ALL, (short) 7).setCreativeTab(MainRegistry.resourceTab).setHardness(0.2F);
		public static final Block waste_ice = new LegacyWasteIce("waste_ice").setCreativeTab(MainRegistry.resourceTab).setHardness(0.2F);
	}

	public static final Block ams_base = new AMSBaseBlock(Material.IRON, "ams_base").setHardness(5.0F).setResistance(100.0F).setCreativeTab(MainRegistry.machineTab);
	public static final Block ams_emitter = new AMSEmitterBlock(Material.IRON, "ams_emitter").setHardness(5.0F).setResistance(100.0F).setCreativeTab(MainRegistry.machineTab);
	public static final Block ams_limiter = new AMSStabilizerBlock(Material.IRON, "ams_limiter").setHardness(5.0F).setResistance(100.0F).setCreativeTab(MainRegistry.machineTab);

	public static final Block block_welded_osmiridium = new OsmiridiumBlock(Material.IRON,"block_welded_osmiridium").setHardness(15).setResistance(6500000);

	public static final Block broof = new BroofBlock(Material.CARPET,"broof").setHardness(0.1F).setSoundType(SoundType.CLOTH).setCreativeTab(MainRegistry.blockTab);

	public static final Block ff_tank = new FFTankBlock(Material.IRON,"ff_tank").setHardness(5).setResistance(100).setCreativeTab(MainRegistry.machineTab);

	static boolean ev_dummy = Elevators.dummy;
	public static class Elevators {
		static boolean dummy = false;
		public static final int guiIdFloor = 365;
		public static final int guiIdCabin = 366;
		public static final Block pulley = new EvPulley(Material.IRON,"elevator_pulley").setHardness(15);
		public static final Block shaft = new EvShaftNeo(Material.IRON,"elevator_shaft").setHardness(15);
		public static final Block buffer = new EvBuffer(Material.IRON,"elevator_buffer").setHardness(15);
		public static final Block s6_floor = new EvFloor(Material.IRON,"elevator_s6floor").setHardness(15);

		public static final Block light = new ElevatorLight(Material.AIR,"elevator_light").setCreativeTab(null);
	}

	static {
		if (Loader.isModLoaded("opencomputers")) {
			oc_cable = new ComputerCableBlock(Material.IRON, "integ_cable_oc",false,"leafia/sealed_network/audio/cable_audio").setHardness(5.0F).setResistance(10.0F).setCreativeTab(MainRegistry.machineTab);
			oc_cable_rad = new ComputerCableBlock(Material.IRON, "integ_cable_oc_rad",true,"leafia/sealed_network/audio/cable_audio_rad").setHardness(15.0F).setResistance(COMPOUND_MESH.v).setCreativeTab(MainRegistry.machineTab);
		}
		if (Loader.isModLoaded("computronics")) {
			audio_cable = new AudioCableBlock(Material.IRON, "integ_cable_audio",false,"leafia/sealed_network/oc/cable_oc").setHardness(5.0F).setResistance(10.0F).setCreativeTab(MainRegistry.machineTab);
			audio_cable_rad = new AudioCableBlock(Material.IRON, "integ_cable_audio_rad",true,"leafia/sealed_network/oc/cable_oc_rad").setHardness(15.0F).setResistance(COMPOUND_MESH.v).setCreativeTab(MainRegistry.machineTab);
		}
	}

	private static void modifyBlockParams() {
		ModBlocks.dfc_core.setResistance(65000000);
		ModBlocks.dfc_emitter.setResistance(50);
		ModBlocks.dfc_receiver.setResistance(50);
		ModBlocks.dfc_injector.setResistance(50);
		ModBlocks.dfc_stabilizer.setResistance(50);
		ModBlocks.deco_aluminium.setResistance(30);
		ModBlocks.deco_asbestos.setResistance(30);
		ModBlocks.deco_beryllium.setResistance(30);
		ModBlocks.deco_lead.setResistance(30);
		ModBlocks.deco_steel.setResistance(30);
		ModBlocks.deco_tungsten.setResistance(30);
		ModBlocks.deco_titanium.setResistance(30);
		ModBlocks.deco_red_copper.setResistance(30);
		ModBlocks.control_panel_custom.setResistance(30);
		ModBlocks.steel_corner.setResistance(30);
		ModBlocks.steel_wall.setResistance(30);
		ModBlocks.steel_roof.setResistance(30);
		ModBlocks.steel_scaffold.setResistance(30);
		ModBlocks.steel_grate.setResistance(15);
		ModBlocks.steel_grate_wide.setResistance(15);

		ModBlocks.pwr_block.setCreativeTab(null);
		ModBlocks.pwr_casing.setCreativeTab(null);
		ModBlocks.pwr_channel.setCreativeTab(null);
		ModBlocks.pwr_control.setCreativeTab(null);
		ModBlocks.pwr_fuelrod.setCreativeTab(null);
		ModBlocks.pwr_controller.setCreativeTab(null);
		ModBlocks.pwr_heatex.setCreativeTab(null);
		ModBlocks.pwr_heatsink.setCreativeTab(null);
		ModBlocks.pwr_neutron_source.setCreativeTab(null);
		ModBlocks.pwr_port.setCreativeTab(null);
		ModBlocks.pwr_reflector.setCreativeTab(null);
	}

	public static void preInit(){
		modifyBlockParams();
		AddonBase._initMemberClasses(AddonBlocks.class);
		System.out.println("Blocks: "+ALL_BLOCKS.size());
		for(Block block : ALL_BLOCKS){
			ForgeRegistries.BLOCKS.register(block);
		}
		registerFluidBlocks();
	}
	public static Block fluid_fluoride = new FluorideFluidBlock(AddonFF.fluoride, Material.LAVA, "fluoride_fluid");
	private static void registerFluidBlocks() {
		AddonFF.fluoride.setBlock(fluid_fluoride);
	}
}
