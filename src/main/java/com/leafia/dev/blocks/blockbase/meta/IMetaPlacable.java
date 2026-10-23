// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.dev.blocks.blockbase.meta;

public interface IMetaPlacable {
	String getTranslationKey(int meta);
	default void registerCustomMRL() {}
}
