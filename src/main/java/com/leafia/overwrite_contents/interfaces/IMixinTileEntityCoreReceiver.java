package com.leafia.overwrite_contents.interfaces;

import com.hbm.tileentity.machine.TileEntityCore;
import com.leafia.contents.machines.powercores.dfc.IDFCBase;

public interface IMixinTileEntityCoreReceiver extends IDFCBase {
	double getLevel();
	void setLevel(double value);
	TileEntityCore getCore();
	void explode();
	int fanAngle();
	double joulesPerSec();
}
