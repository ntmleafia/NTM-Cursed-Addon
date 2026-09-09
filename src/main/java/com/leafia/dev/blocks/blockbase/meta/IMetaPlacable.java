package com.leafia.dev.blocks.blockbase.meta;

public interface IMetaPlacable {
	String getTranslationKey(int meta);
	default void registerCustomMRL() {}
}
