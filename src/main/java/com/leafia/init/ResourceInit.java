package com.leafia.init;

import com.hbm.lib.internal.MethodHandleHelper;
import com.hbm.main.ResourceManager;
import com.hbm.render.loader.HFRWavefrontObject;
import com.hbm.render.loader.WaveFrontObjectVAO;
import com.leafia.contents.bomb.missile.AddonMissileItemRender;
import com.leafia.contents.building.broof.BroofRender;
import com.leafia.contents.building.light.LightRender;
import com.leafia.contents.effects.folkvangr.visual.LCERenderCloudFleija;
import com.leafia.contents.gear.advisor.AdvisorRender;
import com.leafia.contents.machines.elevators.EvBufferRender;
import com.leafia.contents.machines.elevators.EvPulleyRender;
import com.leafia.contents.machines.elevators.EvShaftRender;
import com.leafia.contents.machines.elevators.car.ElevatorRender;
import com.leafia.contents.machines.elevators.floors.EvFloorRender;
import com.leafia.contents.machines.elevators.weight.EvWeightRender;
import com.leafia.contents.machines.powercores.ams.stabilizer.AMSStabilizerRender;
import com.leafia.contents.machines.powercores.dfc.render.DFCComponentRender;
import com.leafia.contents.machines.powercores.dfc.render.DFCCoreRender;
import com.leafia.contents.machines.processing.mixingvat.MixingVatRender;
import com.leafia.contents.machines.reactors.lftr.processing.separator.SaltSeparatorRender;
import com.leafia.contents.machines.reactors.pwr.blocks.components.control.PWRControlRender;
import com.leafia.contents.machines.reactors.pwr.debris.RenderPWRDebris;
import com.leafia.contents.network.ff_duct.utility.FFDuctUtilityRender;
import com.leafia.contents.network.fluid.FluidDuctEquipmentRender;
import com.leafia.contents.network.spk_cable.SPKCableRender;
import com.leafia.contents.nonmachines.fftank.FFTankRender;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.leafia.AddonBase._initClass;
import static com.leafia.AddonBase._initMemberClasses;

@SideOnly(Side.CLIENT)
public class ResourceInit {
	public static final List<WaveFrontObjectVAO> allVAOs = new ArrayList<>();
	private static final MethodHandle pauseSplash = MethodHandleHelper.findStatic(ResourceManager.class, "pauseSplash", MethodType.methodType(void.class));
	private static final MethodHandle resumeSplash = MethodHandleHelper.findStatic(ResourceManager.class, "resumeSplash", MethodType.methodType(void.class));

	/*public static Map<String,WaveFrontObjectVAO> LWRWreckModels = new HashMap<>();
	private static WaveFrontObjectVAO getWreckModel(String name) {
		return getVAO(new ResourceLocation("leafia", "models/leafia/pwrwreck/"+name+".obj"));
	}
	private static void setWreckModel(String s) {
		LWRWreckModels.put(s,getWreckModel(s));
	}*/

	public static class FirestormAssets {
		static ResourceLocation get(String s) {
			return new ResourceLocation("leafia","models/leafia/firestorm/"+s+".obj");
		}
		public static final WaveFrontObjectVAO chem_destroyed = getVAO(get("chem_destroyed"));
	}

	static {
		_initClass(FirestormAssets.class);
		_initClass(LCERenderCloudFleija.class);
		_initClass(DFCCoreRender.class);
		_initClass(DFCComponentRender.class);
		_initClass(SPKCableRender.class);
		_initClass(FFDuctUtilityRender.class);
		_initClass(SaltSeparatorRender.class);
		_initClass(MixingVatRender.class);
		_initClass(RenderPWRDebris.Meshes.class);
		_initClass(PWRControlRender.class);
		_initClass(LightRender.class);
		_initClass(FluidDuctEquipmentRender.class);
		_initClass(AdvisorRender.class);
		_initClass(AddonMissileItemRender.class);
		_initClass(AMSStabilizerRender.class);
		_initClass(BroofRender.class);
		_initClass(FFTankRender.class);
		_initClass(ElevatorRender.class);
		_initMemberClasses(ElevatorRender.class);
		_initClass(EvWeightRender.class);
		_initClass(EvBufferRender.class);
		_initClass(EvShaftRender.class);
		_initClass(EvPulleyRender.class);
		_initClass(EvFloorRender.class);
		/*{
			setWreckModel("intact");
			setWreckModel("metal_rubble_0");
			for (int i = 0; i <= 1; i++)
				setWreckModel("metal_slight_"+i);
			setWreckModel("metal_wreck_0");
			setWreckModel("stone_rubble_0_flip");
			for (int i = 0; i <= 3; i++)
				setWreckModel("stone_rubble_"+i);
			for (int i = 0; i <= 2; i++)
				setWreckModel("stone_slight"+i);
			setWreckModel("stone_slight_2_old");
			setWreckModel("wreck_stone");
			setWreckModel("wreck_stone_2");
			setWreckModel("wreck_stone_3");
		}*/
	}

	public static void init() {
		try {
			pauseSplash.invokeExact();
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
		for (WaveFrontObjectVAO obj : allVAOs) {
			obj.uploadModels();
		}
		try {
			resumeSplash.invokeExact();
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	public static WaveFrontObjectVAO getVAO(ResourceLocation model) {
		WaveFrontObjectVAO vao = new HFRWavefrontObject(model).asVBO();
		WaveFrontObjectVAO.allVBOs.remove(vao);
		allVAOs.add(vao);
		return vao;
	}

	public static ResourceLocation getIntegrated(String s) {
		return new ResourceLocation("leafia","textures/_integrated/"+s);
	}
}
