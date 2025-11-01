package com.leafia.dev.hazards;

public class ItemRads {

	public static MultiRad cobalt60 = new MultiRad(0,30,30,60,0).multiply(1/3f);

	public static MultiRad gold198 = new MultiRad(0,500,500,500,0).multiply(1/2f);

	public static MultiRad plutonium241 = new MultiRad(0,25f,25f,0,0).multiply(1/2f);

	public static MultiRad tritium = new MultiRad(0,0,0.5f,0,0);

	public static MultiRad waste = new MultiRad(0,125,125,50,25);
	public static MultiRad waste_v = waste.copy().multiply(1/2f);

}
