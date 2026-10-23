// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.blocks.generic.BlockSellafield;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = BlockSellafield.class, remap = false)
public interface AccessorBlockSellafield {
    @Accessor("colors")
    static int[][] leafia$getColors() {
        throw new AssertionError();
    }
}
