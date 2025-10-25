package com.leafia.init.proxy;

import com.leafia.contents.AddonBlocks;
import com.leafia.eventbuses.LeafiaClientListener;
import com.llib.exceptions.LeafiaDevFlaw;
import net.minecraft.block.BlockDoor;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends ServerProxy {
	@Override
	public void registerRenderInfo() {
		for (Class<?> cl : LeafiaClientListener.class.getClasses()) {
			try {
				MinecraftForge.EVENT_BUS.register(cl.newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				LeafiaDevFlaw flaw = new LeafiaDevFlaw(e.getMessage());
				flaw.setStackTrace(e.getStackTrace());
				throw flaw;
			}
		}
        ModelLoader.setCustomStateMapper(AddonBlocks.door_fuckoff, new StateMap.Builder().ignore(BlockDoor.POWERED).build());
	}
}
