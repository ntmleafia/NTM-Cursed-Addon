package com.leafia.init.proxy;

import net.minecraftforge.fml.common.FMLCommonHandler;

import java.io.File;

public class ServerProxy {
	public void registerRenderInfo() {};
	public File getDataDir(){
		return FMLCommonHandler.instance().getMinecraftServerInstance().getDataDirectory();
	}
}
