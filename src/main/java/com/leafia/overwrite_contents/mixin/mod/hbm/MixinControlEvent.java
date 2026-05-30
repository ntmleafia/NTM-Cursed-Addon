package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.inventory.control_panel.ControlEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.hbm.inventory.control_panel.ControlEvent.register;

@Mixin(value = ControlEvent.class)
public class MixinControlEvent {
	@Inject(method = "init",at = @At("TAIL"),remap = false,require = 1)
	private static void onInit(CallbackInfo ci) {
		register(new ControlEvent("set_booster_level").setVar("level",0));
		register(new ControlEvent("set_booster_active").setVar("active",0));
		register(new ControlEvent("set_absorber_level").setVar("level",0));
		register(new ControlEvent("set_stabilizer_level").setVar("level",0));
		register(new ControlEvent("torch_set_state").setVar("isOn", 0));
		register(new ControlEvent("pwr_ctrl_set_level").setVar("level", 0));
		register(new ControlEvent("lftr_ctrl_set_level").setVar("level", 0));
		register(new ControlEvent("pwr_computer_set_master").setVar("level", 0));
		register(new ControlEvent("pwr_computer_set_level").setVar("name","").setVar("level", 0));
		register(new ControlEvent("set_pump_buffer").setVar("size",0));
		register(new ControlEvent("set_pump_pressure").setVar("pu",0));
		register(new ControlEvent("set_pump_priority").setVar("priority",2));
		register(new ControlEvent("zirnox_ctrl_set_level").setVar("level", 0));
		register(new ControlEvent("setGlobalVar").setVar("name","").setVar("value",""));
		register(new ControlEvent("setIOMode").setVar("mode",0));
		register(new ControlEvent("heater_set").setVar("heatPerTick",0));
		register(new ControlEvent("burner_set").setVar("isOn",0));
		register(new ControlEvent("set_pulser_active").setVar("active",0).setVar("code",""));
		register(new ControlEvent("set_pulser_detonation").setVar("charge",0).setVar("code",""));
	}
}
