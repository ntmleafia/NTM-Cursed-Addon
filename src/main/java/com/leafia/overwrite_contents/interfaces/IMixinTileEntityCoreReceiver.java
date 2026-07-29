package com.leafia.overwrite_contents.interfaces;

import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.hbm.tileentity.machine.TileEntityCore;
import com.leafia.contents.machines.powercores.dfc.IDFCBase;
import com.leafia.contents.network.spk_cable.uninos.ISPKProvider;
import com.leafia.contents.network.spk_cable.uninos.ISPKReceiver;
import net.minecraft.entity.player.EntityPlayer;

public interface IMixinTileEntityCoreReceiver extends IDFCBase, ISPKProvider, ISPKReceiver {
	double leafia$getLevel();
	void leafia$setLevel(double value);
	TileEntityCore leafia$getCore();
	void leafia$explode();
	int leafia$fanAngle();
	double leafia$joulesPerSec();
	void leafia$sendToPlayer(EntityPlayer player);
	long leafia$syncJoules();
	int leafia$destructionLevel();
	long leafia$syncSpk();
	FluidTankNTM leafia$getOutputTank();
}
