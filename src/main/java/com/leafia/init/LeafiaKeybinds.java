package com.leafia.init;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

public class LeafiaKeybinds {
	@SideOnly(Side.CLIENT)
	public static class Local {
		public static final String category = "key.categories.leafia";
		public static KeyBinding laserKey = new KeyBinding(category+".laser",Keyboard.KEY_R,category);
		public static void register() {
			ClientRegistry.registerKeyBinding(laserKey);
		}
	}
}
