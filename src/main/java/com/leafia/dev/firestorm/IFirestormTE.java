package com.leafia.dev.firestorm;

import com.hbm.inventory.RecipesCommon;
import com.hbm.inventory.RecipesCommon.AStack;
import com.hbm.tileentity.IRepairable;

import java.util.Collections;
import java.util.List;

public interface IFirestormTE extends IRepairable {
	void catchFire();
	boolean isDestroyed();
	default boolean canCatchFire() {
		return !isDestroyed();
	}
	@Override
	default boolean isDamaged() {
		return false;
	}
	@Override
	default List<AStack> getRepairMaterials() {
		return Collections.emptyList();
	}
	@Override
	default void repair() {}
}
