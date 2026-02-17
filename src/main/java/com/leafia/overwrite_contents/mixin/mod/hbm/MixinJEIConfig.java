package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.handler.jei.*;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.leafia.contents.AddonItems;
import com.leafia.contents.gear.ntmfbottle.ItemNTMFBottle;
import com.leafia.jei.*;
import com.llamalad7.mixinextras.sugar.Local;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.ISubtypeRegistry.ISubtypeInterpreter;
import mezz.jei.api.ingredients.IIngredientBlacklist;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.IRecipeWrapper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = JEIConfig.class,remap = false)
public class MixinJEIConfig {
	private static List<IRecipeCategory> addon_categories = new ArrayList<>();
	@Redirect(method = "registerCategories",at = @At(value = "INVOKE", target = "Lmezz/jei/api/recipe/IRecipeCategoryRegistration;addRecipeCategories([Lmezz/jei/api/recipe/IRecipeCategory;)V"),require = 1)
	public void onRegisterCategories(IRecipeCategoryRegistration instance,IRecipeCategory[] iRecipeCategories,@Local(type = IGuiHelper.class) IGuiHelper help) {
		addon_categories.clear();
		addon_categories.add(new JEICentrifuge(help));
		addon_categories.add(new JEIChemplant(help));
		addon_categories.add(new JEIAssembler(help));
		addon_categories.add(new JEIRefinery(help));
		addon_categories.add(new JEIVacuum(help));
		addon_categories.add(new JEICracking(help));
		addon_categories.add(new JEIReformer(help));
		addon_categories.add(new JEIHydrotreater(help));
		addon_categories.add(new JEIArcWelder(help));
		addon_categories.add(new JEISoldering(help));

		for (IRecipeCategory<? extends IRecipeWrapper> category : addon_categories)
			instance.addRecipeCategories(category);

		//for (IRecipeCategory<? extends IRecipeWrapper> recipe : _AddonJEI.getRecipes(help))
		//	instance.addRecipeCategories(recipe);

		for (IRecipeCategory cat : iRecipeCategories) {
			boolean doNotAdd = false;
			for (IRecipeCategory<? extends IRecipeWrapper> addonCategory : addon_categories) {
				if (addonCategory.getUid().equals(cat.getUid())) {
					doNotAdd = true;
					break;
				}
			}
			if (!doNotAdd)
				instance.addRecipeCategories(cat); // meow
			//try {
				/*
			} catch (IllegalArgumentException iarg) {
				String message = iarg.getMessage();
				String category = message.split("\"")[1];
				boolean okay = false;
				for (IRecipeCategory<Recipe> addonCategory : addon_categories) {
					if (addonCategory.getUid().equals(category)) {
						okay = true;
						break;
					}
				}
				if (!okay)
					throw iarg;
			}*/ // this is retarded
		}
	}

	private static final ISubtypeRegistry.ISubtypeInterpreter metadataBottleInterpreter = stack -> {
		FluidType type = Fluids.fromID(stack.getMetadata());
		if (type != null && type != Fluids.NONE && ItemNTMFBottle.getFluidFromStack(stack) != Fluids.NONE) {
			return type.getTranslationKey();
		}
		return "";
	};
	@Inject(method = "registerSubtypes",at = @At(value = "TAIL"),require = 1)
	public void onRegisterSubtypes(ISubtypeRegistry subtypeRegistry,CallbackInfo ci) {
		subtypeRegistry.registerSubtypeInterpreter(AddonItems.ntmfbottle, metadataBottleInterpreter);
	}

	@Inject(method = "register",at = @At(value = "TAIL"),require = 1)
	public void onRegister(IModRegistry registry,CallbackInfo ci,@Local(type = IIngredientBlacklist.class) IIngredientBlacklist blacklist) {
		_JEIBlacklist.blacklistRecipes(blacklist);
	}

	@Redirect(method = "register",at = @At(value = "INVOKE", target = "Lcom/hbm/handler/jei/CentrifugeRecipeHandler;getRecipes()Ljava/util/List;"),require = 1)
	public List centrifuge(CentrifugeRecipeHandler instance) {
		return JEICentrifuge.Recipe.buildRecipes();
	}
	@Redirect(method = "register",at = @At(value = "INVOKE", target = "Lcom/hbm/handler/jei/ChemicalPlantRecipeHandler;getRecipes()Ljava/util/List;"),require = 1)
	public List chemplant(ChemicalPlantRecipeHandler instance) {
		return JEIChemplant.Recipe.buildRecipes();
	}
	@Redirect(method = "register",at = @At(value = "INVOKE", target = "Lcom/hbm/handler/jei/AssemblyMachineRecipeHandler;getRecipes()Ljava/util/List;"),require = 1)
	public List assembler(AssemblyMachineRecipeHandler instance) {
		return JEIAssembler.Recipe.buildRecipes();
	}
	@Redirect(method = "register",at = @At(value = "INVOKE", target = "Lcom/hbm/handler/jei/JeiRecipes;getRefineryRecipe()Ljava/util/List;"),require = 1)
	public List refinery() {
		return JEIRefinery.Recipe.buildRecipes();
	}
	@Redirect(method = "register",at = @At(value = "INVOKE", target = "Lcom/hbm/handler/jei/VacuumRecipeHandler;getRecipes()Ljava/util/List;"),require = 1)
	public List vacuum(VacuumRecipeHandler instance) {
		return JEIVacuum.Recipe.buildRecipes();
	}
	@Redirect(method = "register",at = @At(value = "INVOKE", target = "Lcom/hbm/handler/jei/CrackingHandler;getRecipes()Ljava/util/List;"),require = 1)
	public List cracking(CrackingHandler instance) {
		return JEICracking.Recipe.buildRecipes();
	}
	@Redirect(method = "register",at = @At(value = "INVOKE", target = "Lcom/hbm/handler/jei/ReformingHandler;getRecipes()Ljava/util/List;"),require = 1)
	public List reformer(ReformingHandler instance) {
		return JEIReformer.Recipe.buildRecipes();
	}
	@Redirect(method = "register",at = @At(value = "INVOKE", target = "Lcom/hbm/handler/jei/HydrotreatingHandler;getRecipes()Ljava/util/List;"),require = 1)
	public List hydro(HydrotreatingHandler instance) {
		return JEIHydrotreater.Recipe.buildRecipes();
	}
	@Redirect(method = "register",at = @At(value = "INVOKE", target = "Lcom/hbm/handler/jei/ArcWelderRecipeHandler;getRecipes()Ljava/util/List;"),require = 1)
	public List arc(ArcWelderRecipeHandler instance) {
		return JEIArcWelder.Recipe.buildRecipes();
	}
	@Redirect(method = "register",at = @At(value = "INVOKE", target = "Lcom/hbm/handler/jei/SolderingStationRecipeHandler;getRecipes()Ljava/util/List;"),require = 1)
	public List soldering(SolderingStationRecipeHandler instance) {
		return JEISoldering.Recipe.buildRecipes();
	}
}
