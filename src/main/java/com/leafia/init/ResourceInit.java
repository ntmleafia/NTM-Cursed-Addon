package com.leafia.init;

import com.hbm.lib.MethodHandleHelper;
import com.hbm.main.ResourceManager;
import com.hbm.render.loader.HFRWavefrontObject;
import com.hbm.render.loader.WaveFrontObjectVAO;
import com.leafia.contents.effects.folkvangr.visual.LCERenderCloudFleija;
import com.leafia.contents.machines.powercores.dfc.render.DFCComponentRender;
import com.leafia.contents.machines.powercores.dfc.render.DFCCoreRender;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.List;

import static com.leafia.AddonBase._initClass;

@SideOnly(Side.CLIENT)
public class ResourceInit {
	public static final List<WaveFrontObjectVAO> allVAOs = new ArrayList<>();
	private static final MethodHandle pauseSplash = MethodHandleHelper.findStatic(ResourceManager.class, "pauseSplash", MethodType.methodType(void.class));
	private static final MethodHandle resumeSplash = MethodHandleHelper.findStatic(ResourceManager.class, "resumeSplash", MethodType.methodType(void.class));

	static {
		_initClass(LCERenderCloudFleija.class);
		_initClass(DFCCoreRender.class);
		_initClass(DFCComponentRender.class);
	}

	public static void init() {
		try {
			pauseSplash.invokeExact();
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
		for (WaveFrontObjectVAO obj : allVAOs) {
			obj.generate_vaos();
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
}
