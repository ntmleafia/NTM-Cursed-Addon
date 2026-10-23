// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.dev.render;

import com.leafia.contents.building.generic.mixed.TextureAtlasSpriteHalf;
import com.leafia.contents.building.generic.mixed.TextureAtlasSpriteHalf.HalfDirection;
import com.leafia.contents.gear.ntmfbottle.TextureAtlasSpriteMask;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationFrame;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.util.ResourceLocation;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LayeredTextureAtlasSpriteTest {
    private static final ResourceLocation FIRST = new ResourceLocation("test:first");
    private static final ResourceLocation SECOND = new ResourceLocation("test:second");

    @Test
    void maskMultipliesPixelsAndClosesEveryResource() throws Exception {
        var resources = new Resources(image(2, 2, 0x80808080), image(2, 2, 0x80ff8040), null, null);
        var sprite = new TextureAtlasSpriteMask("test:mask", FIRST, SECOND);
        sprite.loadLayeredSpriteResources(resources, FIRST, SECOND, 1);
        assertArrayEquals(new int[]{0x40804020, 0x40804020, 0x40804020, 0x40804020}, sprite.getFrameTextureData(0)[0]);
        assertFalse(sprite.hasAnimationMetadata());
        resources.assertClosed(4);
    }

    @Test
    void validatesEachImagesOwnAnimationMetadata() throws Exception {
        var animation = animation(List.of());
        for (var sprite : sprites()) {
            var resources = new Resources(image(2, 4, -1), image(2, 4, -1), animation, null);
            var error = assertThrows(RuntimeException.class,
                    () -> sprite.loadLayeredSpriteResources(resources, FIRST, SECOND, 1));
            assertEquals("broken aspect ratio and not an animation", error.getMessage());
            resources.assertClosed(2);

            var inverse = new Resources(image(2, 4, -1), image(2, 4, -1), null, animation);
            assertThrows(RuntimeException.class,
                    () -> sprite.loadLayeredSpriteResources(inverse, FIRST, SECOND, 1));
            inverse.assertClosed(2);
        }
    }

    @Test
    void animatedMaskBuildsBothFrames() throws Exception {
        var resources = new Resources(image(2, 4, -1), image(2, 4, 0xff123456), animation(List.of()), animation(List.of()));
        var sprite = new TextureAtlasSpriteMask("test:mask", FIRST, SECOND);
        sprite.loadLayeredSpriteResources(resources, FIRST, SECOND, 1);
        assertTrue(sprite.hasAnimationMetadata());
        assertEquals(2, sprite.getFrameCount());
        for (var frame = 0; frame < 2; frame++)
            assertArrayEquals(new int[]{0xff123456, 0xff123456, 0xff123456, 0xff123456}, sprite.getFrameTextureData(frame)[0]);
        resources.assertClosed(4);
    }

    @Test
    void halfSpriteSplitsEachAnimationFrameInEveryDirection() throws Exception {
        var expected = new int[][]{
                {0xffff0000, 0xffff0000, 0xff00ff00, 0xff00ff00},
                {0xff00ff00, 0xffff0000, 0xff00ff00, 0xffff0000},
                {0xff00ff00, 0xff00ff00, 0xffff0000, 0xffff0000},
                {0xffff0000, 0xff00ff00, 0xffff0000, 0xff00ff00}
        };
        for (var frames : List.of(List.<AnimationFrame>of(), List.of(new AnimationFrame(1, 3), new AnimationFrame(0, 2)))) {
            for (var direction : HalfDirection.values()) {
                var resources = new Resources(image(2, 4, 0xffff0000), image(2, 4, 0xff00ff00), animation(frames), animation(frames));
                var sprite = new TextureAtlasSpriteHalf("test:half", "first", "second", direction);
                sprite.loadLayeredSpriteResources(resources, FIRST, SECOND, 1);
                assertTrue(sprite.hasAnimationMetadata());
                assertEquals(2, sprite.getFrameCount());
                for (var frame = 0; frame < 2; frame++)
                    assertArrayEquals(expected[direction.ordinal()], sprite.getFrameTextureData(frame)[0], direction + " frame " + frame);
                resources.assertClosed(4);
            }
        }
    }

    @Test
    void halfSpritePreservesSparseFramesOddSplitsAndMipmapSlots() throws Exception {
        var firstAnimation = new AnimationMetadataSection(List.of(new AnimationFrame(2, 7)), 3, 3, 2, true);
        var secondAnimation = new AnimationMetadataSection(List.of(), 3, 3, 1, false);
        var resources = new Resources(image(3, 9, 0xffff0000), image(3, 9, 0xff00ff00),
                firstAnimation, secondAnimation);
        var sprite = new TextureAtlasSpriteHalf("test:half", "first", "second", HalfDirection.A_UP_B_DOWN);
        sprite.loadLayeredSpriteResources(resources, FIRST, SECOND, 3);
        assertEquals(3, sprite.getFrameCount());
        assertNull(sprite.getFrameTextureData(0));
        assertNull(sprite.getFrameTextureData(1));
        var frame = sprite.getFrameTextureData(2);
        assertEquals(3, frame.length);
        assertArrayEquals(new int[]{0xffff0000, 0xffff0000, 0xffff0000,
                0xff00ff00, 0xff00ff00, 0xff00ff00,
                0xff00ff00, 0xff00ff00, 0xff00ff00}, frame[0]);
        assertNull(frame[1]);
        assertNull(frame[2]);
        assertTrue(sprite.hasAnimationMetadata());
        resources.assertClosed(4);
    }

    @Test
    void closesResourcesOnSizeLookupAndFrameFailures() throws Exception {
        for (var sprite : sprites()) {
            var mismatch = new Resources(image(2, 2, -1), image(4, 4, -1), null, null);
            assertThrows(IllegalArgumentException.class,
                    () -> sprite.loadLayeredSpriteResources(mismatch, FIRST, SECOND, 1));
            mismatch.assertClosed(2);
            for (var failAt : new int[]{2, 4}) {
                var lookup = new Resources(image(2, 2, -1), image(2, 2, -1), null, null);
                lookup.failAt = failAt;
                assertThrows(IOException.class,
                        () -> sprite.loadLayeredSpriteResources(lookup, FIRST, SECOND, 1));
                lookup.assertClosed(failAt - 1);
            }
            var invalidFrame = new Resources(image(2, 4, -1), image(2, 4, -1),
                    animation(List.of(new AnimationFrame(2, 1))), animation(List.of()));
            assertThrows(RuntimeException.class,
                    () -> sprite.loadLayeredSpriteResources(invalidFrame, FIRST, SECOND, 1));
            invalidFrame.assertClosed(4);
        }
    }

    private static List<LayeredTextureAtlasSprite> sprites() {
        return List.of(new TextureAtlasSpriteMask("test:mask", FIRST, SECOND),
                new TextureAtlasSpriteHalf("test:half", "first", "second", HalfDirection.A_UP_B_DOWN));
    }

    private static AnimationMetadataSection animation(List<AnimationFrame> frames) {
        return new AnimationMetadataSection(frames, 2, 2, 1, false);
    }

    private static byte[] image(int width, int height, int color) throws IOException {
        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        var pixels = new int[width * height];
        Arrays.fill(pixels, color);
        image.setRGB(0, 0, width, height, pixels, 0, width);
        var bytes = new ByteArrayOutputStream();
        ImageIO.write(image, "png", bytes);
        return bytes.toByteArray();
    }

    private static final class Resources implements IResourceManager {
        final byte[][] images;
        final AnimationMetadataSection[] metadata;
        final List<Resource> opened = new ArrayList<>();
        int lookups;
        int failAt;

        Resources(byte[] first, byte[] second, AnimationMetadataSection firstMetadata, AnimationMetadataSection secondMetadata) {
            images = new byte[][]{first, second};
            metadata = new AnimationMetadataSection[]{firstMetadata, secondMetadata};
        }

        @Override
        public Set<String> getResourceDomains() { return Set.of("test"); }

        @Override
        public IResource getResource(ResourceLocation location) throws IOException {
            if (++lookups == failAt) throw new IOException("resource lookup failed");
            var index = location.equals(FIRST) ? 0 : 1;
            var resource = new Resource(location, images[index], metadata[index]);
            opened.add(resource);
            return resource;
        }

        @Override
        public List<IResource> getAllResources(ResourceLocation location) throws IOException {
            return List.of(getResource(location));
        }

        void assertClosed(int expected) {
            assertEquals(expected, opened.size());
            for (var resource : opened) assertEquals(1, resource.closes, resource.location.toString());
        }
    }

    private static final class Resource implements IResource {
        final ResourceLocation location;
        final InputStream stream;
        final AnimationMetadataSection animation;
        int closes;

        Resource(ResourceLocation location, byte[] bytes, AnimationMetadataSection animation) {
            this.location = location;
            stream = new ByteArrayInputStream(bytes);
            this.animation = animation;
        }

        @Override public ResourceLocation getResourceLocation() { return location; }
        @Override public InputStream getInputStream() { return stream; }
        @Override public boolean hasMetadata() { return animation != null; }
        @SuppressWarnings("unchecked")
        @Override public <T extends IMetadataSection> T getMetadata(String section) { return (T) animation; }
        @Override public String getResourcePackName() { return "test"; }
        @Override public void close() throws IOException { closes++; stream.close(); }
    }
}
