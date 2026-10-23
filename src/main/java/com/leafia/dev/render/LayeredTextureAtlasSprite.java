// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.dev.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.PngSizeInfo;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;

import java.io.IOException;

/** Loads equally sized source images, validating each image's own animation metadata. */
public abstract class LayeredTextureAtlasSprite extends TextureAtlasSprite {
    protected LayeredTextureAtlasSprite(String name) {
        super(name);
    }

    @Override
    public final boolean hasCustomLoader(IResourceManager manager, ResourceLocation location) {
        return true;
    }

    protected final boolean loadLayeredSprite(IResourceManager manager, ResourceLocation first, ResourceLocation second) {
        try {
            loadLayeredSpriteResources(manager, first, second,
                    Minecraft.getMinecraft().getTextureMapBlocks().getMipmapLevels() + 1);
            return false;
        } catch (RuntimeException | IOException exception) {
            FMLClientHandler.instance().trackBrokenTexture(first, exception.getMessage());
            return true;
        }
    }

    protected final void loadLayeredSpriteResources(IResourceManager manager, ResourceLocation first,
                                                   ResourceLocation second, int mipmapLevels) throws IOException {
        try (var firstInfo = manager.getResource(first); var secondInfo = manager.getResource(second)) {
            var firstSize = loadSpriteInfo(firstInfo);
            var secondSize = loadSpriteInfo(secondInfo);
            if (firstSize.pngWidth != secondSize.pngWidth || firstSize.pngHeight != secondSize.pngHeight)
                throw new IllegalArgumentException("Layered sprite image dimensions differ");
        }
        try (var firstPixels = manager.getResource(first); var secondPixels = manager.getResource(second)) {
            loadSpriteFrames(firstPixels, secondPixels, mipmapLevels);
        }
    }

    private PngSizeInfo loadSpriteInfo(IResource resource) throws IOException {
        var animated = resource.getMetadata("animation") != null;
        var size = new PngSizeInfo(resource.getInputStream());
        loadSprite(size, animated);
        return size;
    }

    public abstract void loadSpriteFrames(IResource first, IResource second, int mipmapLevels) throws IOException;
}
