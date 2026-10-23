// SPDX-FileCopyrightText: 2025-2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.jei;

import com.hbm.handler.jei.JEIConfig;
import com.hbm.handler.jei.JeiRecipes;
import com.hbm.inventory.recipes.CentrifugeRecipes;
import com.hbm.util.I18nUtil;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class JEICentrifuge implements IRecipeCategory<JEICentrifuge.Recipe> {
    public static class Recipe implements IRecipeWrapper {
        public static final List<Recipe> recipes = new ArrayList<>();
        private final List<List<ItemStack>> ingredientLists;
        private final List<ItemStack> outputs;
        public final List<ItemStack> inputs;

        public Recipe(ItemStack input, List<ItemStack> outputs) {
            this.inputs = null;
            this.ingredientLists = Collections.singletonList(Collections.singletonList(input));
            this.outputs = outputs;
        }

        public Recipe(List<ItemStack> inputs, List<ItemStack> outputs) {
            this.inputs = inputs;
            this.ingredientLists = Collections.singletonList(inputs);
            this.outputs = outputs;
        }

        public static List<Recipe> buildRecipes() {
            for (var entry : CentrifugeRecipes.recipes.entrySet()) {
                var alternatives = entry.getKey().getStackList();
                if (alternatives.isEmpty()) continue;
                var output = Arrays.asList(entry.getValue());
                recipes.add(alternatives.size() == 1
                        ? new Recipe(alternatives.get(0), output) : new Recipe(alternatives, output));
            }
            return recipes;
        }

        @Override
        public void getIngredients(IIngredients ingredients) {
            ingredients.setInputLists(VanillaTypes.ITEM, ingredientLists);
            ingredients.setOutputs(VanillaTypes.ITEM, outputs);
        }
    }

    public static final ResourceLocation gui_rl = new ResourceLocation("leafia", "textures/gui/jei/centrifuge.png");
    private static final int[] SLOT_X = {19, 127, 145, 127, 145, 19};
    private static final int[] SLOT_Y = {1, 1, 1, 37, 37, 37};
    protected final IDrawable background;
    protected final IDrawableStatic progressStatic, powerStatic, flameStatic;
    protected final IDrawableAnimated progressAnimated, powerAnimated, flameAnimated;

    public JEICentrifuge(IGuiHelper helper) {
        background = helper.createDrawable(gui_rl, 6, 15, 163, 55);
        progressStatic = helper.createDrawable(gui_rl, 176, 0, 54, 54);
        powerStatic = helper.createDrawable(gui_rl, 176, 54, 17, 54);
        flameStatic = helper.createDrawable(gui_rl, 194, 54, 18, 18);
        progressAnimated = helper.createAnimatedDrawable(progressStatic, 144, IDrawableAnimated.StartDirection.LEFT, false);
        powerAnimated = helper.createAnimatedDrawable(powerStatic, 480, IDrawableAnimated.StartDirection.TOP, true);
        flameAnimated = helper.createAnimatedDrawable(flameStatic, 48, IDrawableAnimated.StartDirection.TOP, true);
    }

    @Override public String getUid() { return JEIConfig.CENTRIFUGE; }
    @Override public String getTitle() { return I18nUtil.resolveKey("tile.machine_centrifuge.name"); }
    @Override public String getModName() { return "hbm"; }
    @Override public IDrawable getBackground() { return background; }

    @Override
    public void drawExtras(Minecraft minecraft) {
        progressAnimated.draw(minecraft, 55, 1);
        flameAnimated.draw(minecraft, 19, 19);
        powerAnimated.draw(minecraft, 1, 1);
    }

    @Override
    public void setRecipe(IRecipeLayout layout, Recipe recipe, IIngredients ingredients) {
        var slots = layout.getItemStacks();
        for (int slot = 0; slot < SLOT_X.length; slot++) {
            slots.init(slot, slot == 0 || slot == 5, SLOT_X[slot], SLOT_Y[slot]);
        }
        slots.set(ingredients);
        if (recipe.inputs != null) slots.set(0, recipe.inputs);
        slots.set(5, JeiRecipes.getBatteries());
    }
}
