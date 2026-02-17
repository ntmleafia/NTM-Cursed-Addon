package com.leafia.dev.firestorm;

public interface IFirestormTE {
	void catchFire();
	boolean isDestroyed();
	default boolean canCatchFire() {
		return !isDestroyed();
	}
}
