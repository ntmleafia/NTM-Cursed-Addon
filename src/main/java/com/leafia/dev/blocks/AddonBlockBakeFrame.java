// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.dev.blocks;

import com.hbm.render.block.BlockBakeFrame;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class AddonBlockBakeFrame extends BlockBakeFrame {

    @SuppressWarnings("removal")
    public AddonBlockBakeFrame(BlockForm form, @NotNull String... textures) {
        super(form, textures);
    }

    public static AddonBlockBakeFrame cubeAll(String texture) {
        return new AddonBlockBakeFrame(BlockForm.ALL, texture);
    }

    public static AddonBlockBakeFrame tintedCubeAll(String texture) {
        return new AddonBlockBakeFrame(BlockForm.ALL_TINTED, texture);
    }

    public static AddonBlockBakeFrame cross(String texture) {
        return new AddonBlockBakeFrame(BlockForm.CROSS, texture);
    }

    public static AddonBlockBakeFrame tintedCross(String texture) {
        return new AddonBlockBakeFrame(BlockForm.CROSS_TINTED, texture);
    }

    public static AddonBlockBakeFrame column(String topTexture, String sideTexture) {
        return new AddonBlockBakeFrame(BlockForm.COLUMN, topTexture, sideTexture);
    }

    public static AddonBlockBakeFrame tintedColumn(String topTexture, String sideTexture) {
        return new AddonBlockBakeFrame(BlockForm.COLUMN_TINTED, topTexture, sideTexture);
    }

    public static AddonBlockBakeFrame cubeBottomTop(String topTexture, String sideTexture, String bottomTexture) {
        return new AddonBlockBakeFrame(BlockForm.BOTTOM_TOP, topTexture, sideTexture, bottomTexture);
    }

    public static AddonBlockBakeFrame tintedCubeBottomTop(String topTexture, String sideTexture, String bottomTexture) {
        return new AddonBlockBakeFrame(BlockForm.BOTTOM_TOP_TINTED, topTexture, sideTexture, bottomTexture);
    }

    public static AddonBlockBakeFrame cube(@NotNull String... textures) {
        return new AddonBlockBakeFrame(BlockForm.CUBE, textures);
    }

    public static AddonBlockBakeFrame tintedCube(@NotNull String... textures) {
        return new AddonBlockBakeFrame(BlockForm.CUBE_TINTED, textures);
    }

    public static AddonBlockBakeFrame layer(String texture) {
        return new AddonBlockBakeFrame(BlockForm.LAYER, texture);
    }

    public static AddonBlockBakeFrame crop(String texture) {
        return new AddonBlockBakeFrame(BlockForm.CROP, texture);
    }


    @Override
	public void registerBlockTextures(TextureMap map) {
		for (String texture : new ObjectOpenHashSet<>(this.textures)) {
			ResourceLocation spriteLoc = new ResourceLocation("leafia", ROOT_PATH + texture);
			map.registerSprite(spriteLoc);
		}
	}

    public ResourceLocation getTextureLocation(int index) {
        return new ResourceLocation("leafia", ROOT_PATH + getTexturePath(index));
    }
}
