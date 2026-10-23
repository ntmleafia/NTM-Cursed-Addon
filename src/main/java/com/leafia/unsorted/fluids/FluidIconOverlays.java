// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.unsorted.fluids;

import com.hbm.inventory.fluid.FluidType;
import com.leafia.contents.AddonFluids;

import java.util.*;

public class FluidIconOverlays {
	public static String prefix = "items/fluid_icon_overlays/";
	public static List<String> overlays = new ArrayList<>();
	static {
		overlays.add("isotope_lime");
		overlays.add("isotope_orange");
	}
	public static String getOverlay(FluidType fluid) {
		if (fluid.equals(AddonFluids.UF6_233))
			return "isotope_orange";
		if (fluid.equals(AddonFluids.UF6_235))
			return "isotope_lime";
		return null;
	}
}
