package com.leafia.dev.items.itembase;

import com.hbm.hazard.modifier.IHazardModifier;
import com.hbm.hazard.type.IHazardType;
import com.leafia.init.hazards.ItemRads.MultiRadContainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddonItemHazardBase extends AddonItemBase {
	public static List<AddonItemHazardBase> ALL_HAZARD_ITEMS = new ArrayList<>();
	public Map<Class<? extends IHazardType>,IHazardModifier> mods;
	public AddonItemHazardBase(String s) {
		super(s);
		ALL_HAZARD_ITEMS.add(this);
	}
	public MultiRadContainer radContainer = null;
	public double digamma;
	public int fire;
	public int cryogenic;
	public int toxic;
	public boolean blinding;
	public int asbestos;
	public int coal;
	public int alkaline;
	public double explosive;
	public double sharp;
	public AddonItemHazardBase addRad(MultiRadContainer container) { radContainer = container; return this; }
	public AddonItemHazardBase addDigamma(double value) { digamma = value; return this; }
	public AddonItemHazardBase addCryogenic(int value) { cryogenic = value; return this; }
	public AddonItemHazardBase addToxic(int value) { toxic = value; return this; }
	public AddonItemHazardBase addBlinding() { blinding = true; return this; }
	public AddonItemHazardBase addAsbestos(int value) { asbestos = value; return this; }
	public AddonItemHazardBase addCoal(int value) { coal = value; return this; }
	public AddonItemHazardBase addAlkaline(int value) { alkaline = value; return this; }
	public AddonItemHazardBase addExplosive(double value) { explosive = value; return this; }
	public AddonItemHazardBase addSharp(double value) { sharp = value; return this; }
	public AddonItemHazardBase addFire(int fire) { this.fire = fire; return this; }
	public AddonItemHazardBase addModifier(Class<? extends IHazardType> type,IHazardModifier modifier) {
		if (mods == null)
			mods = new HashMap<>();
		mods.put(type,modifier);
		return this;
	}
}
