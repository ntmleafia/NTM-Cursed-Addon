package com.leafia.jei;

import com.hbm.blocks.ModBlocks;
import com.leafia.contents.AddonBlocks;
import com.leafia.contents.AddonBlocks.PWR;
import com.leafia.contents.control.fuel.nuclearfuel.LeafiaRodCraftingJEI;
import com.leafia.contents.control.fuel.nuclearfuel.LeafiaRodDecayJEI;
import com.leafia.contents.machines.processing.solblaster.recipes.SolBlasterJEI;
import mezz.jei.api.*;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import java.util.Objects;

@JEIPlugin
public class _AddonJEI implements IModPlugin {
	public static String GENERIC_ROD_UNCRAFTING = "leafia.generic_rod_uncrafting";
	public static String GENERIC_ROD_DECAY = "leafia.generic_rod_decay";
	public static String SOL_BLASTER = "leafia.sol_blaster";
	static IJeiRuntime runtime;
	@Override
	public void register(IModRegistry registry) {
		{
			registry.addRecipeCatalyst(
					new ItemStack(Objects.requireNonNull(Blocks.CRAFTING_TABLE)),
					GENERIC_ROD_UNCRAFTING
			);
			registry.addRecipes(LeafiaRodCraftingJEI.Recipe.buildRecipes(),GENERIC_ROD_UNCRAFTING);
		}
		{
			registry.addRecipeCatalyst(
					new ItemStack(Objects.requireNonNull(PWR.element)),
					GENERIC_ROD_DECAY
			);
			registry.addRecipeCatalyst(
					new ItemStack(Objects.requireNonNull(PWR.element_old)),
					GENERIC_ROD_DECAY
			);
			registry.addRecipeCatalyst(
					new ItemStack(Objects.requireNonNull(PWR.element_old_blank)),
					GENERIC_ROD_DECAY
			);
			registry.addRecipeCatalyst(
					new ItemStack(Objects.requireNonNull(ModBlocks.reactor_zirnox)),
					GENERIC_ROD_DECAY
			);
			registry.addRecipeCatalyst(
					new ItemStack(Objects.requireNonNull(ModBlocks.reactor_research)),
					GENERIC_ROD_DECAY
			);
			registry.addRecipeCatalyst(
					new ItemStack(Objects.requireNonNull(ModBlocks.machine_difurnace_rtg_off)),
					GENERIC_ROD_DECAY
			);
			registry.addRecipeCatalyst(
					new ItemStack(Objects.requireNonNull(AddonBlocks.heater_rt)),
					GENERIC_ROD_DECAY
			);
			registry.addRecipes(LeafiaRodDecayJEI.Recipe.buildRecipes(),GENERIC_ROD_DECAY);
		}
		{
			registry.addRecipeCatalyst(
					new ItemStack(Objects.requireNonNull(AddonBlocks.sol_blaster)),
					SOL_BLASTER
			);
			registry.addRecipes(SolBlasterJEI.Recipe.buildRecipes(),SOL_BLASTER);
		}
	}
	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		IGuiHelper help = registry.getJeiHelpers().getGuiHelper();
		registry.addRecipeCategories(
				new LeafiaRodCraftingJEI(help),
				new LeafiaRodDecayJEI(help),
				new SolBlasterJEI(help)
		);
	}
	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
		runtime = jeiRuntime;
	}
}
