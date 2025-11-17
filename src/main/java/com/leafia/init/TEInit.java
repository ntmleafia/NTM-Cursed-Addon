package com.leafia.init;

import com.leafia.AddonBase;
import com.leafia.contents.machines.powercores.dfc.components.creativeemitter.CEmitterTE;
import com.leafia.contents.network.spk_cable.SPKCableTE;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class TEInit {
	public static void preInit() {
        GameRegistry.registerTileEntity(SPKCableTE.class, new ResourceLocation(AddonBase.MODID, "spk_cable_te"));
        GameRegistry.registerTileEntity(CEmitterTE.class, new ResourceLocation(AddonBase.MODID, "core_creative_emitter_te"));
	}
}
