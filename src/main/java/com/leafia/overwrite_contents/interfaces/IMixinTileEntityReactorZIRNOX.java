package com.leafia.overwrite_contents.interfaces;

public interface IMixinTileEntityReactorZIRNOX {
	enum packetKeys {
		HULL_TEMP(0),
		PRESSURE(1),
		CONTROL_RODS(2),
		COMPRESSION(3),
		CONTROL_RODS_TARGET(4),
		OPENVALVE(5),
		TANKS(6);

		public byte key;
		packetKeys(int key) {
			this.key = (byte)key;
		}
	}
	byte getRods();
	byte getRodsTarget();
	double getHullTemp();
	double getMeltingPoint();
	double getPressure();
	double getLastPressure();
	double getMaxPressure();
	byte getCompression();
	boolean getValveOpen();
	double getAvgHeat();
	int dialX();
	int dialY();
	int valveLevel();
}
