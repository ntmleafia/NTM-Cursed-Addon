package com.leafia.contents.machines.reactors.lftr.processing.separator.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.hbm.inventory.fluid.FluidStack;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.recipes.loader.GenericRecipes;
import com.hbm.items.ModItems;
import com.leafia.contents.AddonFluids;
import com.leafia.contents.machines.reactors.lftr.components.element.MSRElementTE.MSRFuel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

public class SaltSeparatorRecipes extends GenericRecipes<SaltSeparatorRecipe> {
	public static final SaltSeparatorRecipes INSTANCE = new SaltSeparatorRecipes();

	@Override public int inputItemLimit() { return 0; }
	@Override public int inputFluidLimit() { return 2; }
	@Override public int outputItemLimit() { return 0; }
	@Override public int outputFluidLimit() { return 3; }

	@Override public String getFileName() { return "leafiaSaltSeparator.json"; }

	@Override
	public void readExtraData(JsonElement element,SaltSeparatorRecipe recipe) {
		JsonObject obj = (JsonObject) element;
		recipe.saltType = Fluids.fromName(obj.get("saltType").getAsString());
		recipe.saltAmount = obj.get("saltAmount").getAsInt();
		recipe.mixture = new HashMap<>();
		JsonArray mix = obj.get("mixture").getAsJsonArray();
		for (JsonElement element1 : mix) {
			JsonArray content = element1.getAsJsonArray();
			recipe.mixture.put(MSRFuel.valueOf(content.get(0).getAsString()),content.get(1).getAsDouble());
		}
	}

	@Override
	public void writeExtraData(SaltSeparatorRecipe recipe,JsonWriter writer) throws IOException {
		writer.name("saltType").value(recipe.saltType.getName());
		writer.name("saltAmount").value(recipe.saltAmount);
		writer.name("mixture").beginArray();
		for (Entry<MSRFuel,Double> entry : recipe.mixture.entrySet()) {
			writer.beginArray();
			writer.setIndent("");
			writer.value(entry.getKey().name());
			writer.value(entry.getValue());
			writer.endArray();
			writer.setIndent("  ");
		}
		writer.endArray();
	}

	@Override public SaltSeparatorRecipe instantiateRecipe(String name) { return new SaltSeparatorRecipe(name); }

	@Override
	public void registerDefaults() {
		this.register(new SaltSeparatorRecipe("separator.u233").setupNamed(60, 400).setIcon(ModItems.fluid_icon,AddonFluids.UF6_233.getID())
				.setSalt(AddonFluids.FLUORIDE,1000)
				.addMixture(MSRFuel.u233,3)
				.inputFluids(new FluidStack(AddonFluids.FLUORINE, 500))
				.outputFluids(new FluidStack(AddonFluids.FLUORIDE,1000),new FluidStack(AddonFluids.UF6_233, 300))
				//.outputItems(new ItemStack(ModItems.powder_beryllium),new ItemStack(ModItems.powder_lithium))
		);
		this.register(new SaltSeparatorRecipe("separator.u235").setupNamed(60, 400).setIcon(ModItems.fluid_icon,AddonFluids.UF6_235.getID())
						.setSalt(AddonFluids.FLUORIDE,1000)
						.addMixture(MSRFuel.u235,3)
						.inputFluids(new FluidStack(AddonFluids.FLUORINE, 500))
						.outputFluids(new FluidStack(AddonFluids.FLUORIDE,1000),new FluidStack(AddonFluids.UF6_235, 300))
		);
	}
}
