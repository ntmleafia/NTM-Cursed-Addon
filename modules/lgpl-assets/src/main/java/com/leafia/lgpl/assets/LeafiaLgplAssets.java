// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LGPL-3.0-only

package com.leafia.lgpl.assets;

import net.minecraftforge.fml.common.Mod;

@Mod(
        modid = LeafiaLgplAssets.MODID,
        name = "Leafia LGPL Assets",
        useMetadata = true,
        dependencies = "required-after:hbm;before:leafia")
public final class LeafiaLgplAssets {
    public static final String MODID = "leafia_lgpl_assets";
}
