package com.leafia.contents.fluids.traits;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.hbm.inventory.fluid.trait.FluidTrait;
import com.hbm.lib.Library;
import net.minecraft.client.resources.I18n;

import java.io.IOException;
import java.util.List;

public class FT_DFCFuel extends FluidTrait {
	float modifier;
	public FT_DFCFuel() { }
	public FT_DFCFuel(float modifier) {
		this.modifier = modifier;
	}
	public float getModifier() {
		return modifier;
	}
	@Override
	public void addInfo(List<String> info) {
		info.add("§5["+I18n.format("trait._hazardfluid.dfcFuel")+"]");
		float dfcEff = (modifier-1F);
		info.add(I18n.format("trait._hazardfluid.dfcFuel.desc", dfcEff >= 0 ? "+"+Library.getPercentage(dfcEff) : Library.getPercentage(dfcEff)));
	}
	@Override
	public void serializeJSON(JsonWriter writer) throws IOException {
		writer.name("dfc_modifier").value(modifier);
	}
	@Override
	public void deserializeJSON(JsonObject obj) {
		modifier = obj.get("dfc_modifier").getAsFloat();
	}
}