// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.building.generic.mixed;

import com.leafia.dev.render.LayeredTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationFrame;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;

public class TextureAtlasSpriteHalf extends LayeredTextureAtlasSprite {
    public final String a;
    public final String b;
    public final HalfDirection dir;

    public enum HalfDirection { A_UP_B_DOWN, A_RIGHT_B_LEFT, A_DOWN_B_UP, A_LEFT_B_RIGHT }

    public TextureAtlasSpriteHalf(String spriteName, String a, String b, HalfDirection dir) {
        super(spriteName);
        this.a = a;
        this.b = b;
        this.dir = dir;
    }

    @Override
    public boolean load(IResourceManager manager, ResourceLocation location,
                        Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
        return loadLayeredSprite(manager,
                new ResourceLocation(location.getNamespace(), "textures/" + a + ".png"),
                new ResourceLocation(location.getNamespace(), "textures/" + b + ".png"));
    }

    @Override
    public void loadSpriteFrames(IResource first, IResource second, int mipmapLevels) throws IOException {
        // Decode on the CPU: TextureAtlasSprite's loader initializes TextureUtil's GL texture.
        var image = ImageIO.read(first.getInputStream());
        var metadata = first.<AnimationMetadataSection>getMetadata("animation");
        var frameWidth = image.getWidth();
        var frameHeight = metadata == null ? image.getHeight() : frameWidth;
        var count = image.getHeight() / frameHeight;
        var selected = new boolean[count];
        var slots = count;

        if (metadata != null && metadata.getFrameCount() != 0) {
            slots = 0;
            for (var index : metadata.getFrameIndexSet()) {
                if (index < 0 || index >= count)
                    throw new IllegalArgumentException("Invalid sprite frame " + index);
                selected[index] = true;
                slots = Math.max(slots, index + 1);
            }
        } else {
            Arrays.fill(selected, true);
            if (metadata != null) {
                var sequence = new ArrayList<AnimationFrame>(count);
                for (var index = 0; index < count; index++) sequence.add(new AnimationFrame(index, -1));
                metadata = new AnimationMetadataSection(sequence, width, height,
                        metadata.getFrameTime(), metadata.isInterpolate());
            }
        }

        var overlay = second == null ? null : ImageIO.read(second.getInputStream());
        var split = frameWidth / 2;
        var left = dir == HalfDirection.A_LEFT_B_RIGHT ? split : 0;
        var top = dir == HalfDirection.A_UP_B_DOWN ? split : 0;
        var columns = switch (dir) {
            case A_LEFT_B_RIGHT -> frameWidth - split;
            case A_RIGHT_B_LEFT -> split;
            default -> frameWidth;
        };
        var rows = switch (dir) {
            case A_UP_B_DOWN -> frameHeight - split;
            case A_DOWN_B_UP -> split;
            default -> frameHeight;
        };

        var decoded = new ArrayList<int[][]>(slots);
        for (var frame = 0; frame < slots; frame++) {
            if (!selected[frame]) {
                decoded.add(null);
                continue;
            }
            var pixels = new int[mipmapLevels][];
            pixels[0] = image.getRGB(0, frame * frameHeight, frameWidth, frameHeight, null, 0, frameWidth);
            if (overlay != null && columns != 0 && rows != 0)
                overlay.getRGB(left, frame * frameHeight + top, columns, rows,
                        pixels[0], top * frameWidth + left, frameWidth);
            decoded.add(pixels);
        }
        setFramesTextureData(decoded);
        animationMetadata = metadata;
    }
}
