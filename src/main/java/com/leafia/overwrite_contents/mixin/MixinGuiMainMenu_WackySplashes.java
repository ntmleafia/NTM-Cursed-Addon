// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin;

import com.leafia.transformer.LeafiaGeneralLocal;
import net.minecraft.client.gui.GuiMainMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(GuiMainMenu.class)
public class MixinGuiMainMenu_WackySplashes {

    @Redirect(
            method = "<init>",
            at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z", ordinal = 0)
    )
    private boolean leafia$injectWackySplashes(List<String> splashes) {
        LeafiaGeneralLocal.injectWackySplashes(splashes);
        return splashes.isEmpty();
    }
}
