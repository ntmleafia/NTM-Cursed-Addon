package com.leafia.overwrite_contents.interfaces;

import com.hbm.tileentity.machine.TileEntityCore;

public interface IMixinTileEntityCoreReceiver {
	double getLevel();
	void setLevel(double value);
	TileEntityCore getCore();
	void explode();
	int fanAngle();
	double joulesPerSec();
}
