package com.leafia.overwrite_contents.interfaces;

import com.hbm.tileentity.machine.TileEntityCore;
import com.leafia.contents.machines.powercores.dfc.IDFCBase;
import com.leafia.contents.network.spk_cable.uninos.ISPKProvider;
import com.leafia.contents.network.spk_cable.uninos.ISPKReceiver;
import net.minecraft.entity.player.EntityPlayer;

public interface IMixinTileEntityCoreReceiver extends IDFCBase, ISPKProvider, ISPKReceiver {
	double getLevel();
	void setLevel(double value);
	TileEntityCore getCore();
	void explode();
	int fanAngle();
	double joulesPerSec();
	void sendToPlayer(EntityPlayer player);
	long syncJoules();
}
