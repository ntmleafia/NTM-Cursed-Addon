package com.leafia.contents.gear;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IADSWeapon {
	// Returns the FOV of ADS.
	@SideOnly(Side.CLIENT)
	public default float getADS(){ return 1; }
}
