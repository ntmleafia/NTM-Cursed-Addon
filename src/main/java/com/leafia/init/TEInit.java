package com.leafia.init;

import com.leafia.AddonBase;
import com.leafia.contents.building.broof.BroofTE;
import com.leafia.contents.building.light.LightTE;
import com.leafia.contents.building.sign.SignTE;
import com.leafia.contents.debug.ff_test.source.DebugSourceTE;
import com.leafia.contents.debug.ff_test.tank.DebugTankTE;
import com.leafia.contents.machines.elevators.EvBufferTE;
import com.leafia.contents.machines.elevators.EvPulleyTE;
import com.leafia.contents.machines.elevators.EvShaftTE;
import com.leafia.contents.machines.elevators.floors.EvFloorTE;
import com.leafia.contents.machines.misc.heatex.CoolantHeatexTE;
import com.leafia.contents.machines.panel.controltorch.ControlTorchTE;
import com.leafia.contents.machines.powercores.ams.base.AMSBaseTE;
import com.leafia.contents.machines.powercores.ams.emitter.AMSEmitterTE;
import com.leafia.contents.machines.powercores.ams.stabilizer.AMSStabilizerTE;
import com.leafia.contents.machines.powercores.dfc.components.cemitter.CoreCEmitterTE;
import com.leafia.contents.machines.powercores.dfc.components.exchanger.CoreExchangerTE;
import com.leafia.contents.machines.processing.mixingvat.MixingVatTE;
import com.leafia.contents.machines.processing.mixingvat.proxy.MixingVatProxy;
import com.leafia.contents.machines.reactors.lftr.components.arbitrary.MSRArbitraryTE;
import com.leafia.contents.machines.reactors.lftr.components.control.MSRControlTE;
import com.leafia.contents.machines.reactors.lftr.components.ejector.MSREjectorTE;
import com.leafia.contents.machines.reactors.lftr.components.element.MSRElementTE;
import com.leafia.contents.machines.reactors.lftr.components.plug.MSRPlugTE;
import com.leafia.contents.machines.reactors.lftr.processing.separator.SaltSeparatorTE;
import com.leafia.contents.machines.reactors.pwr.blocks.components.computer.PWRComputerTE;
import com.leafia.contents.machines.reactors.pwr.blocks.components.control.PWRControlTE;
import com.leafia.contents.machines.reactors.pwr.blocks.components.element.PWRElementTE;
import com.leafia.contents.machines.reactors.pwr.blocks.components.element.PWRProxyTE;
import com.leafia.contents.machines.reactors.pwr.blocks.components.port.PWRPortTE;
import com.leafia.contents.machines.reactors.pwr.blocks.components.terminal.PWRTerminalTE;
import com.leafia.contents.machines.reactors.pwr.blocks.wreckage.PWRMeshedWreckEntity;
import com.leafia.contents.network.computers.audiocable.AudioCableTE;
import com.leafia.contents.network.computers.cable.ComputerCableTE;
import com.leafia.contents.network.ff_duct.FFDuctTE;
import com.leafia.contents.network.ff_duct.utility.converter.FFConverterTE;
import com.leafia.contents.network.ff_duct.utility.pump.FFPumpTE;
import com.leafia.contents.network.fluid.gauges.FluidDuctGaugeTE;
import com.leafia.contents.network.fluid.valves.FluidDuctValveTE;
import com.leafia.contents.network.pipe_amat.AmatDuctTE;
import com.leafia.contents.network.pipe_amat.charger.AmatDuctChargerTE;
import com.leafia.contents.network.spk_cable.SPKCableTE;
import com.leafia.contents.nonmachines.fftank.FFTankTE;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class TEInit {
	public static void preInit() {
		{
			// Debug TEs
			register(DebugSourceTE.class,"debug_ff_source");
			register(DebugTankTE.class,"debug_ff_tank");
		}
		register(SPKCableTE.class,"spk_cable_te");
		register(CoreCEmitterTE.class,"core_creative_emitter_te");
		register(CoreExchangerTE.class,"core_exchanger_te");
		register(SignTE.class,"letter_sign_te");
		register(FFDuctTE.class,"ff_duct_te");
		register(FFPumpTE.class,"ff_pump_te");
		register(FFConverterTE.class,"ff_converter_te");
		register(SaltSeparatorTE.class,"salt_separator_te");
		register(MSRArbitraryTE.class,"lftr_arbitrary_te");
		register(MSRControlTE.class,"lftr_control_te");
		register(MSREjectorTE.class,"lftr_ejector_te");
		register(MSRElementTE.class,"lftr_element_te");
		register(MSRPlugTE.class,"lftr_plug_te");
		register(MixingVatTE.class,"mixing_vat_te");
		register(MixingVatProxy.class,"mixing_vat_proxy_te");
		register(CoolantHeatexTE.class,"coolant_heatex_te");
		register(PWRControlTE.class,"lwr_control_te");
		register(PWRElementTE.class,"lwr_element_te");
		register(PWRProxyTE.class,"lwr_proxy_te");
		register(PWRPortTE.class,"lwr_port_te");
		register(PWRTerminalTE.class,"lwr_terminal_te");
		register(PWRComputerTE.class,"lwr_computer_te");
		register(PWRMeshedWreckEntity.class,"lwr_wreck_te");
		register(AmatDuctTE.class,"pipe_amat_te");
		register(AmatDuctChargerTE.class,"charger_amat_te");
		register(LightTE.class,"fluorescent_light_te");
		register(ControlTorchTE.class,"control_torch_te");
		register(FluidDuctGaugeTE.class,"duct_gauge_te");
		register(FluidDuctValveTE.class,"duct_valve_te");
		register(AudioCableTE.class,"cable_audio_te");
		register(ComputerCableTE.class,"cable_computer_te");
		register(AMSBaseTE.class,"ams_base_te");
		register(AMSStabilizerTE.class,"ams_stabilizer_te");
		register(AMSEmitterTE.class,"ams_emitter_te");
		register(BroofTE.class,"broof_te");
		register(FFTankTE.class,"ff_tank_te");
		register(EvFloorTE.class,"ev_floor_te");
		register(EvPulleyTE.class,"ev_pulley_te");
		register(EvShaftTE.class,"ev_shaft_te");
		register(EvBufferTE.class,"ev_buffer_te");
	}
	private static void register(Class<? extends TileEntity> clazz,String res) {
		GameRegistry.registerTileEntity(clazz,new ResourceLocation(AddonBase.MODID,res));
	}
}
