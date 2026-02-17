package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.render.util.RenderInfoSystemLegacy;
import com.hbm.render.util.RenderInfoSystemLegacy.InfoEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.*;
import java.util.Map.Entry;

@Mixin(value = RenderInfoSystemLegacy.class,remap = false)
public class MixinRenderInfoSystemLegacy {
	/// make it display on ID order
	@Redirect(method = "onOverlayRender",at = @At(value = "INVOKE", target = "Ljava/util/Map;values()Ljava/util/Collection;",ordinal = 2),require = 1)
	public Collection<InfoEntry> onOnOverlayRender(Map<Integer,InfoEntry> instance) {
		List<Entry<Integer,InfoEntry>> sortedList = new ArrayList<>(instance.entrySet());
		sortedList.sort(Entry.comparingByKey());
		List<InfoEntry> entries = new ArrayList<>();
		for (Entry<Integer,InfoEntry> entry : sortedList)
			entries.add(entry.getValue());
		return entries;
	}
}
