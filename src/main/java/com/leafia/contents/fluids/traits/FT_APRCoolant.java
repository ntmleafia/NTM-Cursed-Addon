package com.leafia.contents.fluids.traits;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.trait.FluidTrait;
import com.hbm.util.I18nUtil;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;
import java.util.List;

public class FT_APRCoolant extends FluidTrait {
	public FluidType conversion;
	public int in;
	public int out;
	public FT_APRCoolant() { }
	public FT_APRCoolant(FluidType conversion,int in,int out) {
		this.conversion = conversion;
		this.in = in;
		this.out = out;
	}
	@Override
	public void addInfoHidden(List<String> info) {
		info.add(TextFormatting.DARK_AQUA+"["+I18nUtil.resolveKey("trait._hazardfluid.aprCoolant")+"]");
	}
	@Override
	public void serializeJSON(JsonWriter writer) throws IOException {
		writer.name("conversion").value(conversion.getName());
		writer.name("in").value(in);
		writer.name("out").value(out);
	}
	@Override
	public void deserializeJSON(JsonObject obj) {
		conversion = Fluids.fromName(obj.get("conversion").getAsString());
		in = obj.get("in").getAsInt();
		out = obj.get("out").getAsInt();
	}
}
