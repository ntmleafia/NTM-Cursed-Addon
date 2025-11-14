package com.leafia.init;

import com.leafia.AddonBase;
import com.leafia.contents.network.spk_cable.SPKCableTE;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class TEInit {
	public static void preInit() {
        GameRegistry.registerTileEntity(SPKCableTE.class, new ResourceLocation(AddonBase.MODID, "spk_cable_te"));
	}
}
