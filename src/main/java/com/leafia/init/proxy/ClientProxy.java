package com.leafia.init.proxy;

import com.custom_hbm.contents.torex.LCETorex;
import com.custom_hbm.contents.torex.LCETorexRender;
import com.hbm.entity.effect.EntityCloudFleija;
import com.leafia.contents.AddonBlocks;
import com.leafia.contents.effects.folkvangr.visual.RenderCloudFleija;
import com.leafia.eventbuses.LeafiaClientListener;
import com.llib.exceptions.LeafiaDevFlaw;
import net.minecraft.block.BlockDoor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import java.io.File;

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

		RenderingRegistry.registerEntityRenderingHandler(EntityCloudFleija.class, RenderCloudFleija.FACTORY);
		RenderingRegistry.registerEntityRenderingHandler(LCETorex.class, LCETorexRender.FACTORY);
	}
	@Override
	public File getDataDir() {
		return Minecraft.getMinecraft().gameDir;
	}
}
