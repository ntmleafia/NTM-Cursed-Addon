// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.misc.modular_turbine;

public enum MTPacketId {
	PORT_IDENTIFIER(0),
	PORT_DECOMPRESSION(1),

	CORE_ASSEMBLY_SYNC(0),
	CORE_STEAM_SYNC(1),
	CORE_TURBULENCE_REASONS(2),
	CORE_WEIGHT(3),
	CORE_TURBULENCE(4),
	CORE_GENERATION(5),
	CORE_GLOBAL_GEAR(6),
	CORE_RPS(7),
	CORE_OVERDRIVE(8),
	;
	public final int id;
	MTPacketId(int id) {
		this.id = id;
	}
}
