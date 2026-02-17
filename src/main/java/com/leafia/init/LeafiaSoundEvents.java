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

	public static void init() {
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
		HBMSoundHandler.geigerSounds = new SoundEvent[]{
				HBMSoundHandler.geiger1,
				HBMSoundHandler.geiger2,
				HBMSoundHandler.geiger3,
				HBMSoundHandler.geiger4,
				HBMSoundHandler.geiger5,
				HBMSoundHandler.geiger6
		};
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
