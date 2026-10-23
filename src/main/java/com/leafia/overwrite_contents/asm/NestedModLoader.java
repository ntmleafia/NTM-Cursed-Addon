// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.asm;

import com.google.gson.JsonParser;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ContainerType;
import net.minecraftforge.fml.common.discovery.ModCandidate;
import net.minecraftforge.fml.relauncher.FMLInjectionData;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;

public final class NestedModLoader {
    static final String CATALOG = "META-INF/leafia/nested-mods.json";

    private NestedModLoader() {
    }

    public static List<ModContainer> discover(List<ModContainer> externalMods, ASMDataTable table) {
        return discover(externalMods, table, ((File) FMLInjectionData.data()[6]).toPath());
    }

    static List<ModContainer> discover(List<ModContainer> externalMods, ASMDataTable table, Path gameDirectory) {
        var result = new ArrayList<>(externalMods);
        var externalIds = new HashSet<String>();
        var sources = new ArrayDeque<File>();
        var visited = new HashSet<File>();
        var selected = new HashMap<String, String>();
        for (var mod : externalMods) {
            externalIds.add(mod.getModId());
            sources.add(mod.getSource());
        }
        while (!sources.isEmpty()) {
            var source = sources.remove();
            try {
                if (!visited.add(source.getCanonicalFile())) continue;
                loadFrom(source, externalIds, selected, sources, result, table, gameDirectory);
            } catch (IOException | RuntimeException exception) {
                throw new IllegalStateException("Cannot load nested mods from " + source, exception);
            }
        }
        return result;
    }

    private static void loadFrom(File source, Set<String> externalIds, Map<String, String> selected,
                                 ArrayDeque<File> sources, List<ModContainer> result,
                                 ASMDataTable table, Path gameDirectory) throws IOException {
        if (source.isDirectory()) {
            var catalog = source.toPath().resolve(CATALOG);
            if (!Files.exists(catalog)) return;
            try (var stream = Files.newInputStream(catalog)) {
                for (var nested : readCatalog(stream)) {
                    if (!shouldLoad(nested, externalIds, selected, source)) continue;
                    var bundled = source.toPath().resolve(nested.path);
                    try (var input = Files.newInputStream(bundled)) {
                        add(nested, input, gameDirectory, table, sources, result);
                    }
                }
            }
        } else {
            try (var jar = new JarFile(source)) {
                var catalog = jar.getJarEntry(CATALOG);
                if (catalog == null) return;
                List<NestedJar> nestedJars;
                try (var stream = jar.getInputStream(catalog)) {
                    nestedJars = readCatalog(stream);
                }
                for (var nested : nestedJars) {
                    if (!shouldLoad(nested, externalIds, selected, source)) continue;
                    var entry = jar.getJarEntry(nested.path);
                    if (entry == null) throw new IllegalStateException("Missing bundled JAR " + nested.path);
                    try (var input = jar.getInputStream(entry)) {
                        add(nested, input, gameDirectory, table, sources, result);
                    }
                }
            }
        }
    }

    private static boolean shouldLoad(NestedJar nested, Set<String> externalIds,
                                      Map<String, String> selected, File source) {
        if (externalIds.contains(nested.modId)) {
            FMLLog.log.info("Using external mod {} instead of its bundle in {}", nested.modId, source);
            return false;
        }
        var previous = selected.putIfAbsent(nested.modId, nested.sha256);
        if (previous == null) return true;
        if (!previous.equals(nested.sha256)) {
            throw new IllegalStateException("Conflicting bundled JARs for mod " + nested.modId);
        }
        return false;
    }

    private static void add(NestedJar nested, InputStream input, Path gameDirectory,
                            ASMDataTable table, ArrayDeque<File> sources, List<ModContainer> result) throws IOException {
        var file = extract(nested, input, gameDirectory);
        try (var jar = new JarFile(file.toFile())) {
            var manifest = jar.getManifest();
            if (manifest != null) {
                var attributes = manifest.getMainAttributes();
                for (var unsupported : new String[]{"FMLCorePlugin", "TweakClass", "ContainedDeps"}) {
                    if (attributes.getValue(unsupported) != null) {
                        throw new IllegalStateException("Nested mod " + nested.modId
                                + " requires early bootstrap via " + unsupported);
                    }
                }
            }
        }
        var candidate = new ModCandidate(file.toFile(), file.toFile(), ContainerType.JAR);
        var discovered = candidate.explore(table);
        if (discovered.size() != 1 || !nested.modId.equals(discovered.get(0).getModId())) {
            throw new IllegalStateException("Bundled JAR " + nested.path
                    + " must discover exactly the declared mod " + nested.modId);
        }
        result.addAll(discovered);
        sources.add(file.toFile());
        FMLLog.log.info("Loaded nested mod {} from {}", nested.modId, file);
    }

    private static Path extract(NestedJar nested, InputStream input, Path gameDirectory) throws IOException {
        var directory = gameDirectory.resolve("libraries/leafia/nested-mods");
        Files.createDirectories(directory);
        var target = directory.resolve(nested.sha256 + ".jar");
        if (Files.exists(target)) {
            try (var cached = Files.newInputStream(target)) {
                verify(nested, hash(cached));
            }
            return target;
        }
        var temporary = Files.createTempFile(directory, nested.modId + "-", ".tmp");
        try {
            var digest = sha256();
            try (var hashing = new DigestInputStream(input, digest)) {
                Files.copy(hashing, temporary, StandardCopyOption.REPLACE_EXISTING);
            }
            verify(nested, hex(digest.digest()));
            Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            return target;
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    private static List<NestedJar> readCatalog(InputStream stream) {
        var catalog = new JsonParser().parse(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
        if (catalog.get("formatVersion").getAsInt() != 1) {
            throw new IllegalStateException("Unsupported nested mod catalog version");
        }
        var result = new ArrayList<NestedJar>();
        for (var element : catalog.getAsJsonArray("jars")) {
            var entry = element.getAsJsonObject();
            result.add(new NestedJar(entry.get("modid").getAsString(),
                    entry.get("path").getAsString(), entry.get("sha256").getAsString()));
        }
        return result;
    }

    private static void verify(NestedJar nested, String hash) {
        if (!nested.sha256.equals(hash)) {
            throw new IllegalStateException("SHA-256 mismatch for bundled mod " + nested.modId);
        }
    }

    private static String hash(InputStream input) throws IOException {
        var digest = sha256();
        var buffer = new byte[65536];
        int count;
        while ((count = input.read(buffer)) != -1) digest.update(buffer, 0, count);
        return hex(digest.digest());
    }

    private static MessageDigest sha256() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException exception) {
            throw new AssertionError(exception);
        }
    }

    private static String hex(byte[] bytes) {
        var result = new StringBuilder(bytes.length * 2);
        for (var value : bytes) {
            result.append(Character.forDigit((value >>> 4) & 15, 16));
            result.append(Character.forDigit(value & 15, 16));
        }
        return result.toString();
    }

    private static final class NestedJar {
        final String modId;
        final String path;
        final String sha256;

        NestedJar(String modId, String path, String sha256) {
            if (!modId.matches("[a-z][a-z0-9_]{0,63}") || !sha256.matches("[0-9a-f]{64}")
                    || !path.equals("META-INF/jarjar/" + modId + "-" + sha256 + ".jar")) {
                throw new IllegalStateException("Invalid nested mod catalog entry for " + modId);
            }
            this.modId = modId;
            this.path = path;
            this.sha256 = sha256;
        }
    }
}
