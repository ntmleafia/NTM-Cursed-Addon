// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.controlpanel.instruments.types;

import com.hbm.render.loader.WaveFrontObjectVAO;

import static com.leafia.AddonBase.getIntegrated;
import static com.leafia.init.ResourceInit.getVAO;

public class AddonInstrumentModels {
	static final String basePath = "control_panel/instruments/";
	public static final WaveFrontObjectVAO nixie = getVAO(getIntegrated(basePath+"nixie.obj"));
	public static final WaveFrontObjectVAO large_switch = getVAO(getIntegrated(basePath+"starboundswitchlol.obj"));
	public static final WaveFrontObjectVAO large_button = getVAO(getIntegrated(basePath+"starboundbuttonlol.obj"));
	public static final WaveFrontObjectVAO text_indicator = getVAO(getIntegrated(basePath+"textindicator.obj"));
	public static final WaveFrontObjectVAO meter_v = getVAO(getIntegrated(basePath+"meter_vertical.obj"));
	public static final WaveFrontObjectVAO lever = getVAO(getIntegrated(basePath+"lever.obj"));
}
