package com.leafia.init;

import com.hbm.lib.HBMSoundHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class LeafiaSoundEvents {
	// sniffs weed OOOOOOOOOOOOOOOOO
	public static List<SoundEvent> ALL_SOUNDS = new ArrayList<SoundEvent>();

	public static SoundEvent literally_nothing;

	public static SoundEvent[] stressSounds;

	public static SoundEvent nuke;
	public static SoundEvent nuke_near;
	public static SoundEvent nuke_far;
	public static SoundEvent nuke_smol;
	public static SoundEvent nuke_folkvangr;
	public static SoundEvent nuke_ambient;
	public static SoundEvent mukeExplosion;

	public static SoundEvent pwrRodStart;
	public static SoundEvent pwrRodLoop;
	public static SoundEvent pwrRodStop;
	public static SoundEvent pwrElement;

	public static SoundEvent machineDestroyed;
	public static SoundEvent machineExplode;

	public static SoundEvent pointed;

	public static SoundEvent dfc_vs;
	public static SoundEvent dfc_tw;
	public static SoundEvent dfc_eoh;
	public static SoundEvent dfc_thingy;

	public static SoundEvent dfc_meltdown;
	public static SoundEvent dfc_explode;

	public static SoundEvent sbPickaxeOre;

	public static SoundEvent s6beep;
	public static SoundEvent s6bell;
	public static SoundEvent electronicpingshort;
	public static SoundEvent skyliftarrive;

	public static SoundEvent UI_BUTTON_INVALID;
	public static SoundEvent mus_sfx_a_lithit;
	public static SoundEvent crucifix;
	public static SoundEvent crucifix_fail;
	public static SoundEvent crafting_tech1_part;
	public static SoundEvent arc_welder;
	public static SoundEvent arc_welder_start;
	public static SoundEvent arc_welder_stop;

	public static SoundEvent assemblerStart;
	public static SoundEvent assemblerStop;
	public static SoundEvent assemblerStrike;
	public static SoundEvent motor;
	public static SoundEvent mechcrafting_lower;
	public static SoundEvent mechcrafting_weld;
	public static SoundEvent mechcrafting_raise;
	public static SoundEvent mechcrafting_loop;

	public static SoundEvent overload;
	public static SoundEvent longexplosion;
	public static SoundEvent actualexplosion;
	public static SoundEvent glitch_alpha10302;

	public static SoundEvent advisor_activate;
	public static SoundEvent advisor_warning;

	public static SoundEvent fuckingfortnite;

	public static SoundEvent geiger7;
	public static SoundEvent geiger8;
	public static SoundEvent geiger9;

	public static SoundEvent reactor_door_handle;
	public static SoundEvent reactor_door_open;
	public static SoundEvent reactor_door_close;

	public static SoundEvent sbWallSwitch;
	public static SoundEvent sbWallButton;

	public static SoundEvent az5;

	public static SoundEvent modular_turbine;
	public static SoundEvent pipestressed;

	public static SoundEvent sbesrottenrain;
	public static SoundEvent sbesrottenrain_above;

	public static SoundEvent sbmoon_surface;
	public static SoundEvent eversionsong7_cut;
	public static SoundEvent digamma_record;

	public static SoundEvent elevator_jam_loop;
	public static SoundEvent elevator_jam_end;
	public static SoundEvent local_forecast;

	public static SoundEvent laser1start;
	public static SoundEvent laser1loop;
	public static SoundEvent laser1stop;
	public static SoundEvent laser2start;
	public static SoundEvent laser2loop;
	public static SoundEvent laser2stop;

	public static SoundEvent crimDoorOpenStart;
	public static SoundEvent crimDoorOpenEnd;
	public static SoundEvent crimDoorCloseStart;
	public static SoundEvent crimDoorCloseEnd;

	public static SoundEvent UI_BUTTON_KEYPAD;

	public static SoundEvent hspActive;
	public static SoundEvent hspIgnite;
	public static SoundEvent dfc_detonate;
	public static SoundEvent amsp_explode;

	public static void init() {
		// this is so retarded
		literally_nothing = register("sdkgjalkdsjgldhsaiuhgui8asd8gy87dast67gt7wy9gty47yaw79g8734");

		mukeExplosion = register("weapon.mukeExplosion");

		nuke = register("weapon.nuke");
		nuke_near = register("weapon.nuke_n");
		nuke_far = register("weapon.nuke_d");
		nuke_smol = register("weapon.nuke_s");
		nuke_folkvangr = register("weapon.nuke_folkvangr");
		nuke_ambient = register("weapon.nuke_a");

		pwrRodStart = register("external.pwrcontrolstart");
		pwrRodLoop = register("external.pwrcontrol");
		pwrRodStop = register("external.pwrcontrolstop");
		pwrElement = register("external.pwrelement");

		machineDestroyed = register("external.machineDestroyed");
		machineExplode = register("external.machineExplode");

		pointed = register("item.pointed");

		dfc_vs = register("block.kfc.vs");
		dfc_tw = register("block.kfc.tw");
		dfc_eoh = register("block.kfc.eoh");
		dfc_thingy = register("block.kfc.thingy");
		dfc_meltdown = register("block.kfc.meltdown");
		dfc_explode = register("block.kfc.explode");

		sbPickaxeOre = register("external.sbpickore");

		s6beep = register("elevators.s6beep");
		s6bell = register("elevators.s6bell");
		electronicpingshort = register("elevators.electronicpingshort");
		skyliftarrive = register("elevators.skyliftarrive");
		UI_BUTTON_INVALID = register("ui.button.invalid");
		mus_sfx_a_lithit = register("external.mus_sfx_a_lithit");
		crucifix = register("external.lsplash.crucifix");
		crucifix_fail = register("external.lsplash.crucifix_fail");
		crafting_tech1_part = register("external.crafting_tech1_part");
		arc_welder = register("external.arc_welder");
		arc_welder_start = register("external.arc_welder_start");
		arc_welder_stop = register("external.arc_welder_stop");

		assemblerStart = register("block.assembler_start");
		assemblerStop = register("block.assembler_stop");
		assemblerStrike = register("block.assembler_strike");
		motor = register("block.motor");
		mechcrafting_lower = register("external.mechcrafting_lower");
		mechcrafting_raise = register("external.mechcrafting_raise");
		mechcrafting_weld = register("external.mechcrafting_weld");
		mechcrafting_loop = register("external.mechcrafting_loop");

		overload = register("external.overload");
		longexplosion = register("external.longexplosion");
		actualexplosion = register("external.actualexplosion");
		glitch_alpha10302 = register("external.glitch_alpha10302");

		stressSounds = new SoundEvent[]{
				register("external.furnacestressed00"),
				register("external.furnacestressed01"),
				register("external.furnacestressed02"),
				register("external.furnacestressed03"),
				register("external.furnacestressed04"),
				register("external.furnacestressed05"),
				register("external.furnacestressed06")
		};

		advisor_activate = register("item.advisor_activate");
		advisor_warning = register("item.advisor_warning");

		fuckingfortnite = register("external.fuckingfortnite");

		// replace 1.7.10 geiger sounds with alcater one
		HBMSoundHandler.geiger1 = register("item.geiger1");
		HBMSoundHandler.geiger2 = register("item.geiger2");
		HBMSoundHandler.geiger3 = register("item.geiger3");
		HBMSoundHandler.geiger4 = register("item.geiger4");
		HBMSoundHandler.geiger5 = register("item.geiger5");
		HBMSoundHandler.geiger6 = register("item.geiger6");
		geiger7 = register("item.geiger7");
		geiger8 = register("item.geiger8");
		geiger9 = register("item.geiger9");
		HBMSoundHandler.geigerSounds = new SoundEvent[]{
				HBMSoundHandler.geiger1,
				HBMSoundHandler.geiger2,
				HBMSoundHandler.geiger3,
				HBMSoundHandler.geiger4,
				HBMSoundHandler.geiger5,
				HBMSoundHandler.geiger6,
				geiger7,
				geiger8,
				geiger9
		};

		reactor_door_handle = register("external.reactor_door_handle");
		reactor_door_open = register("external.reactor_door_open");
		reactor_door_close = register("external.reactor_door_close");

		sbWallSwitch = register("external.sbwallswitch");
		sbWallButton = register("external.sbwallbutton");

		az5 = register("block.az5");

		modular_turbine = register("external.modular_turbine");
		pipestressed = register("external.pipestressed");

		sbesrottenrain = register("external.sbesrottenrain");
		sbesrottenrain_above = register("external.sbesrottenrain.above");

		sbmoon_surface = register("external.sbmoon_surface");
		eversionsong7_cut = register("external.eversionsong7cut");
		digamma_record = register("music.wtf");

		elevator_jam_loop = register("music.lsplash.elevator_jam.loop");
		elevator_jam_end = register("music.lsplash.elevator_jam.end");
		local_forecast = register("music.incompetech.local_forecast");

		laser1start = register("block.laser1.start");
		laser1loop = register("block.laser1.loop");
		laser1stop = register("block.laser1.stop");
		laser2start = register("block.laser2.start");
		laser2loop = register("block.laser2.loop");
		laser2stop = register("block.laser2.stop");

		crimDoorOpenStart = register("external.scp_sl_gate_open.start");
		crimDoorOpenEnd = register("external.scp_sl_gate_open.end");
		crimDoorCloseStart = register("external.scp_sl_gate_close.start");
		crimDoorCloseEnd = register("external.scp_sl_gate_close.end");

		UI_BUTTON_KEYPAD = register("ui.button.keypad");

		hspActive = register("misc.hsp_active");
		hspIgnite = register("misc.hsp_ignite");
		dfc_detonate = register("block.kfc.detonate");

		amsp_explode = register("block.amsp.explode");
	}

	public static SoundEvent register(String name) {
		SoundEvent e = new SoundEvent(new ResourceLocation("leafia", name));
		e.setRegistryName(name);
		ALL_SOUNDS.add(e);
		return e;
	}

	public static SoundEvent registerBypass(String name){
		SoundEvent e = new SoundEvent(new ResourceLocation("leafia", name));
		e.setRegistryName(name);
		ForgeRegistries.SOUND_EVENTS.register(e);
		return e;
	}
}
