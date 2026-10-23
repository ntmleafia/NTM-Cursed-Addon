// SPDX-FileCopyrightText: 2025-2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.render.util.RenderInfoSystemLegacy;
import com.hbm.render.util.RenderInfoSystemLegacy.InfoEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.*;
import java.util.Map.Entry;

@Mixin(value = RenderInfoSystemLegacy.class,remap = false)
public class MixinRenderInfoSystemLegacy {
	@Shadow
	private static Map<Integer,InfoEntry> messages;
	/// make it display on ID order
	@Redirect(method = "onOverlayRender",at = @At(value = "INVOKE", target = "Ljava/util/Collections;sort(Ljava/util/List;)V"),require = 1)
	public void onOnOverlayRender(List<InfoEntry> entries) {
		entries.clear();
		List<Entry<Integer,InfoEntry>> sortedList = new ArrayList<>(messages.entrySet());
		sortedList.sort(Entry.comparingByKey());
		for (Entry<Integer,InfoEntry> entry : sortedList)
			entries.add(entry.getValue());
	}
}
