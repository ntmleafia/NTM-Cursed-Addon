// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.asm;

import com.google.gson.JsonParser;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.client.resources.data.AnimationMetadataSectionSerializer;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLFileResourcePack;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.ModMetadata;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import net.minecraftforge.fml.common.discovery.ASMDataTable;

import static org.junit.jupiter.api.Assertions.*;

class ComponentResourcePackTest {
    private final ArrayList<FMLFileResourcePack> openedPacks = new ArrayList<>();

    @TempDir
    Path temporary;

    @AfterEach
    void closePacks() throws IOException {
        for (var pack : openedPacks) pack.close();
    }

    @Test
    void extractedResourcesResolveThroughMinecraftResourcePacks() throws Exception {
        var base = Path.of(System.getProperty("component.projectRoot"));
        var assets = base.resolve("modules/lgpl-assets/build/libs/leafia-lgpl-assets-1.0.0.jar");
        var main = base.resolve("build/libs/NTM_Cursed_Edition-CE_Addon.jar");
        var serializer = new MetadataSerializer();
        serializer.registerMetadataSectionType(new AnimationMetadataSectionSerializer(), AnimationMetadataSection.class);
        var resources = new SimpleReloadableResourceManager(serializer);
        resources.reloadResourcePack(pack("hbm", new File(System.getProperty("component.hbmJar"))));
        resources.reloadResourcePack(pack("leafia_lgpl_assets", assets.toFile()));
        resources.reloadResourcePack(pack("leafia", main.toFile()));

        try (var jar = new JarFile(assets.toFile()); var parent = new JarFile(main.toFile())) {
            var entries = jar.stream().filter(e -> !e.isDirectory() && e.getName().startsWith("assets/")).toList();
            var resourceRoot = base.resolve("modules/lgpl-assets/src/main/resources");
            try (var sourceFiles = Files.walk(resourceRoot.resolve("assets"))) {
                var expectedPaths = sourceFiles.filter(Files::isRegularFile)
                        .map(path -> resourceRoot.relativize(path).toString().replace(File.separatorChar, '/'))
                        .collect(Collectors.toSet());
                assertEquals(expectedPaths, entries.stream().map(JarEntry::getName).collect(Collectors.toSet()));
            }
            for (var entry : entries) {
                var path = entry.getName().split("/", 3);
                var id = new ResourceLocation(path[1], path[2]);
                try (var expected = jar.getInputStream(entry); var actual = resources.getResource(id)) {
                    assertArrayEquals(expected.readAllBytes(), actual.getInputStream().readAllBytes(), id.toString());
                    assertEquals("FMLFileResourcePack:leafia_lgpl_assets", actual.getResourcePackName(), id.toString());
                    var metadata = parent.getJarEntry(entry.getName() + ".mcmeta");
                    if (metadata != null) {
                        try (var input = parent.getInputStream(metadata)) {
                            var json = new JsonParser().parse(new String(input.readAllBytes(), StandardCharsets.UTF_8)).getAsJsonObject();
                            if (json.has("animation")) {
                                assertNotNull(actual.getMetadata("animation"), "Parent animation metadata: " + id);
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    void externalAssetReplacementSuppliesItsResourcesAndSkipsBothBundles() throws Exception {
        var base = Path.of(System.getProperty("component.projectRoot"));
        var assets = base.resolve("modules/lgpl-assets/build/libs/leafia-lgpl-assets-1.0.0.jar");
        var main = base.resolve("build/libs/NTM_Cursed_Edition-CE_Addon.jar");
        var code = base.resolve("modules/lgpl-code/build/libs/leafia-lgpl-code-1.0.0-dev.jar");
        var replacement = temporary.resolve("external.jar");
        var modified = "assets/leafia/lang/uk_ua.lang";
        var suffix = "\njarjar.replacement.probe=external\n";
        try (var original = new JarFile(assets.toFile()); var output = new JarOutputStream(Files.newOutputStream(replacement))) {
            for (var entry : original.stream().filter(e -> !e.isDirectory()).toList()) {
                output.putNextEntry(new JarEntry(entry.getName()));
                try (var input = original.getInputStream(entry)) {
                    var bytes = input.readAllBytes();
                    if (entry.getName().equals(modified)) {
                        output.write((new String(bytes, StandardCharsets.UTF_8) + suffix).getBytes(StandardCharsets.UTF_8));
                    } else if (entry.getName().equals("mcmod.info")) {
                        var metadata = new JsonParser().parse(new String(bytes, StandardCharsets.UTF_8)).getAsJsonArray();
                        metadata.get(0).getAsJsonObject().addProperty("version", "0.1.0");
                        output.write(metadata.toString().getBytes(StandardCharsets.UTF_8));
                    } else output.write(bytes);
                }
                output.closeEntry();
            }
        }
        var mods = new ArrayList<net.minecraftforge.fml.common.ModContainer>();
        mods.add(pack("leafia_lgpl_assets", replacement.toFile()).getFMLContainer());
        mods.add(pack("leafia_lgpl_code", code.toFile()).getFMLContainer());
        mods.add(pack("leafia", main.toFile()).getFMLContainer());
        var selected = NestedModLoader.discover(mods, new ASMDataTable(), temporary);
        assertEquals(mods, selected);
        assertFalse(Files.exists(temporary.resolve("libraries/leafia/nested-mods")));

        var resources = new SimpleReloadableResourceManager(new MetadataSerializer());
        resources.reloadResourcePack(pack("hbm", new File(System.getProperty("component.hbmJar"))));
        for (var mod : selected) resources.reloadResourcePack(pack(mod));
        try (var resource = resources.getResource(new ResourceLocation("leafia", "lang/uk_ua.lang"))) {
            assertTrue(new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8).endsWith(suffix));
        }
    }

    private FMLFileResourcePack pack(net.minecraftforge.fml.common.ModContainer container) {
        var pack = new FMLFileResourcePack(container);
        openedPacks.add(pack);
        return pack;
    }

    private FMLFileResourcePack pack(String id, File source) {
        var metadata = new ModMetadata();
        metadata.modId = id;
        metadata.name = id;
        var container = new DummyModContainer(metadata) {
            @Override
            public File getSource() {
                return source;
            }
        };
        return pack(container);
    }
}
