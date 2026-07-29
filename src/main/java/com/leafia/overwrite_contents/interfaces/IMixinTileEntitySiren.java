package com.leafia.overwrite_contents.interfaces;

import com.leafia.unsorted.TileEntityMachineSirenSounder;

import java.util.List;

public interface IMixinTileEntitySiren {
	boolean leafia$speakerMode();

	List<TileEntityMachineSirenSounder> leafia$sounders();
}
