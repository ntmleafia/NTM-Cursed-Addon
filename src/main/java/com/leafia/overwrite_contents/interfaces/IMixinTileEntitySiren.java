// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.interfaces;

import com.leafia.unsorted.TileEntityMachineSirenSounder;

import java.util.List;

public interface IMixinTileEntitySiren {
	boolean leafia$speakerMode();

	List<TileEntityMachineSirenSounder> leafia$sounders();
}
