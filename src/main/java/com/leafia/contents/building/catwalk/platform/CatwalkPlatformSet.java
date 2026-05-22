package com.leafia.contents.building.catwalk.platform;

public class CatwalkPlatformSet {
	public final CatwalkPlatformBase normal;
	public final CatwalkPlatformBase north;
	public final CatwalkPlatformBase south;
	public final CatwalkPlatformBase west;
	public final CatwalkPlatformBase east;
	public CatwalkPlatformSet(CatwalkPlatformBase sample) {
		normal = sample;
		north = sample.copy(sample,"_north");
		south = sample.copy(sample,"_south");
		west = sample.copy(sample,"_west");
		east = sample.copy(sample,"_east");
	}
}
