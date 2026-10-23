// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.bomb.missile.customnuke.container;

import com.hbm.config.BombConfig;
import com.leafia.contents.bomb.missile.customnuke.CustomNukeMissileItem.CustomNukeMissileInventory;
import com.leafia.dev.gui.MachineGaugeUI;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.util.Arrays;

public class CustomNukeMissileUI extends MachineGaugeUI {
    private static final ResourceLocation TEXTURE = new ResourceLocation("hbm:textures/gui/gunBombSchematic.png");
    private static final int[] STAGE_X = {16, 34, 52, 70, 88, 106, 142};
    private static final String[] STAGE_NAMES = {"Conventional explosive", "Nuclear", "Thermonuclear", "Balefire", "Schrabidium", "Solinium", "Ice cream"};
    private static final String[] REQUIREMENTS = {"Additional N2 effect above radius 75", "Requires conventional explosive radius 16", "Requires nuclear radius 100", "Requires nuclear radius 50", "Requires nuclear radius 50", "Requires nuclear radius 25", "Requires both schrabidium and solinium radius 1"};
    private final CustomNukeMissileInventory inventory;

    public CustomNukeMissileUI(InventoryPlayer player, CustomNukeMissileInventory inventory) {
        super(new CustomNukeMissileContainer(player, inventory), TEXTURE, 222);
        this.inventory = inventory;
    }

    @Override
    protected String inventoryTitle() {
        return inventory.box.hasDisplayName() ? inventory.box.getDisplayName() : I18n.format("item.missile_customnuke.name");
    }

    @Override
    protected void drawMachine() {
        int strongest = -1;
        for (int stage = STAGE_X.length - 1; stage >= 0; stage--) {
            if (!(inventory.stageValue(stage) > 0)) continue;
            int textureX = strongest < 0 && stage < 6 ? 194 : 176;
            drawTexturedModalRect(guiLeft + STAGE_X[stage], guiTop + 89, textureX, stage * 18, 18, 18);
            if (strongest < 0) strongest = stage;
        }
        if (inventory.dirty > 0) {
            drawTexturedModalRect(guiLeft + 53, guiTop + 83, strongest == 1 || strongest == 2 ? 201 : 176, 125, 25, 29);
        }
    }

    private float maximum(int stage) {
        return switch (stage) {
            case 0 -> BombConfig.maxCustomTNTRadius;
            case 1 -> BombConfig.maxCustomNukeRadius;
            case 2 -> BombConfig.maxCustomHydroRadius;
            case 3 -> BombConfig.maxCustomBaleRadius;
            case 4 -> BombConfig.maxCustomSchrabRadius;
            case 5 -> BombConfig.maxCustomSolRadius;
            case 6 -> BombConfig.maxCustomEuphLvl;
            default -> throw new IllegalArgumentException("Unknown blast stage: " + stage);
        };
    }

    private float adjusted(int stage) {
        return switch (stage) {
            case 1 -> inventory.getNukeAdj();
            case 2 -> inventory.getHydroAdj();
            case 3 -> inventory.getBaleAdj();
            case 4 -> inventory.getSchrabAdj();
            case 5 -> inventory.getSolAdj();
            default -> inventory.stageValue(stage);
        };
    }

    @Override
    protected void drawMachineTooltips(int mouseX, int mouseY) {
        for (int stage = 0; stage < STAGE_X.length; stage++) {
            if (!hovered(mouseX, mouseY, STAGE_X[stage], 88, 18, 18)) continue;
            String effect = switch (stage) {
                case 1 -> "Produces fallout";
                case 2 -> "Reduces the salted stage's added fallout by 75%";
                default -> "The configured radius or level limit still applies";
            };
            drawHoveringText(Arrays.asList(TextFormatting.YELLOW + STAGE_NAMES[stage] + ": " + inventory.stageValue(stage)
                    + " (adjusted " + adjusted(stage) + ") / " + maximum(stage), REQUIREMENTS[stage], effect), mouseX, mouseY);
        }
        if (hovered(mouseX, mouseY, 53, 83, 25, 5) || hovered(mouseX, mouseY, 53, 106, 25, 5)) {
            drawHoveringText(Arrays.asList(TextFormatting.YELLOW + "Salted: " + inventory.dirty + "/" + BombConfig.maxCustomDirtyRadius,
                    "Increases fallout from nuclear and thermonuclear stages"), mouseX, mouseY);
        }
    }
}
