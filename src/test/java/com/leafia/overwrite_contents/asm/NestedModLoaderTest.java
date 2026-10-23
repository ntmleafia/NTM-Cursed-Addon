// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.asm;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.IFMLSidedHandler;
import net.minecraftforge.fml.common.ModClassLoader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ContainerType;
import net.minecraftforge.fml.common.discovery.ModCandidate;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.FMLInjectionData;
import net.minecraftforge.fml.relauncher.Side;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import static org.junit.jupiter.api.Assertions.*;

class NestedModLoaderTest {
    private static Side previousSide;
    private static Object previousDelegate;

    @TempDir
    Path temporary;

    @BeforeAll
    static void setServerSide() throws Exception {
        var side = FMLLaunchHandler.class.getDeclaredField("side");
        side.setAccessible(true);
        previousSide = (Side) side.get(null);
        side.set(null, Side.SERVER);
        var delegate = FMLCommonHandler.class.getDeclaredField("sidedDelegate");
        delegate.setAccessible(true);
        previousDelegate = delegate.get(FMLCommonHandler.instance());
        delegate.set(FMLCommonHandler.instance(), Proxy.newProxyInstance(
                IFMLSidedHandler.class.getClassLoader(), new Class<?>[]{IFMLSidedHandler.class},
                (_, method, _) -> {
                    if (method.getName().equals("getSide")) return Side.SERVER;
                    throw new UnsupportedOperationException(method.getName());
                }));
    }

    @AfterAll
    static void restoreSide() throws Exception {
        var side = FMLLaunchHandler.class.getDeclaredField("side");
        side.setAccessible(true);
        side.set(null, previousSide);
        var delegate = FMLCommonHandler.class.getDeclaredField("sidedDelegate");
        delegate.setAccessible(true);
        delegate.set(FMLCommonHandler.instance(), previousDelegate);
    }

    @Test
    void discoversBundledModWithForgeAndReusesVerifiedCache() throws Exception {
        var bundle = modJar("replacement", "9.0", "bundled", Map.of());
        var table = new ASMDataTable();
        var external = discover(host("host", bundle), table);
        var result = NestedModLoader.discover(external, table, temporary);

        assertEquals(List.of("host", "replacement"), ids(result));
        var selected = result.get(1);
        assertArrayEquals(bundle, Files.readAllBytes(selected.getSource().toPath()));
        assertEquals("bundled", origin(selected));
        assertEquals("bundled", resource(selected));
        assertEquals(2, table.getAll("net.minecraftforge.fml.common.Mod").size());
        var repeated = NestedModLoader.discover(external, new ASMDataTable(), temporary);
        assertEquals(selected.getSource(), repeated.get(1).getSource());
        try (var files = Files.list(cache())) {
            assertEquals(1, files.count());
        }
    }

    @Test
    void olderExternalReplacementWinsWithoutReadingOrExtractingBundle() throws Exception {
        var bundle = modJar("replacement", "9.0", "bundled", Map.of());
        var host = modJar("host", "1.0", "host", Map.of(NestedModLoader.CATALOG, catalog("replacement", bundle)));
        var table = new ASMDataTable();
        var external = new ArrayList<>(discover(write("host.jar", host), table));
        external.addAll(discover(write("anything-at-all.jar", modJar("replacement", "0.1", "external", Map.of())), table));

        var result = NestedModLoader.discover(external, table, temporary);

        assertEquals(ids(external), ids(result));
        assertSame(external.get(1), result.get(1));
        assertEquals("0.1", result.get(1).getVersion());
        assertEquals("external", origin(result.get(1)));
        assertEquals("external", resource(result.get(1)));
        assertEquals(2, table.getAll("net.minecraftforge.fml.common.Mod").size());
        assertFalse(Files.exists(cache()));
    }

    @Test
    void rejectsCorruptBundleAndCleansTemporaryFile() throws Exception {
        var valid = modJar("replacement", "1.0", "bundled", Map.of());
        var contents = new LinkedHashMap<String, byte[]>();
        contents.put(NestedModLoader.CATALOG, catalog("replacement", valid));
        contents.put(nestedPath("replacement", valid), new byte[]{1, 2, 3});
        var table = new ASMDataTable();
        var external = discover(write("host.jar", modJar("host", "1.0", "host", contents)), table);

        var failure = assertThrows(IllegalStateException.class,
                () -> NestedModLoader.discover(external, table, temporary));

        assertTrue(failure.getCause().getMessage().contains("SHA-256 mismatch"));
        try (var files = Files.list(cache())) {
            assertEquals(0, files.count());
        }
    }

    @Test
    void rejectsCorruptCachedContent() throws Exception {
        var bundle = modJar("replacement", "1.0", "bundled", Map.of());
        var table = new ASMDataTable();
        var external = discover(host("host", bundle), table);
        var selected = NestedModLoader.discover(external, table, temporary).get(1);
        Files.write(selected.getSource().toPath(), new byte[]{1, 2, 3});

        var failure = assertThrows(IllegalStateException.class,
                () -> NestedModLoader.discover(external, new ASMDataTable(), temporary));

        assertTrue(failure.getCause().getMessage().contains("SHA-256 mismatch"));
    }

    @Test
    void validatesDiscoveredModIdInsteadOfTrustingCatalogOrMetadata() throws Exception {
        var bundle = modJar("different", "1.0", "bundled", Map.of());
        var contents = Map.of(NestedModLoader.CATALOG, catalog("replacement", bundle),
                nestedPath("replacement", bundle), bundle);
        var table = new ASMDataTable();
        var external = discover(write("host.jar", modJar("host", "1.0", "host", contents)), table);

        var failure = assertThrows(IllegalStateException.class,
                () -> NestedModLoader.discover(external, table, temporary));

        assertTrue(failure.getCause().getMessage().contains("must discover exactly the declared mod replacement"));
    }

    @Test
    void rejectsConflictingBundlesButKeepsExternalDuplicateIdsForForge() throws Exception {
        var first = modJar("replacement", "1.0", "one", Map.of());
        var second = modJar("replacement", "2.0", "two", Map.of());
        var table = new ASMDataTable();
        var external = new ArrayList<>(discover(host("host_one", first), table));
        external.addAll(discover(host("host_two", second), table));

        var failure = assertThrows(IllegalStateException.class,
                () -> NestedModLoader.discover(external, table, temporary));
        assertTrue(failure.getCause().getMessage().contains("Conflicting bundled JARs"));

        var standalone = new ArrayList<>(discover(write("first.jar", first), new ASMDataTable()));
        standalone.addAll(discover(write("second.jar", second), new ASMDataTable()));
        var duplicates = NestedModLoader.discover(standalone, new ASMDataTable(), temporary);
        assertEquals(List.of("replacement", "replacement"), ids(duplicates));
    }

    @Test
    void discoversRecursiveBundlesAndDeduplicatesIdenticalModules() throws Exception {
        var child = modJar("child", "1.0", "child", Map.of());
        var parent = modJar("replacement", "1.0", "parent", Map.of(
                NestedModLoader.CATALOG, catalog("child", child), nestedPath("child", child), child));
        var table = new ASMDataTable();
        var external = new ArrayList<>(discover(host("host_one", parent), table));
        external.addAll(discover(host("host_two", parent), table));

        var result = NestedModLoader.discover(external, table, temporary);

        assertEquals(List.of("host_one", "host_two", "replacement", "child"), ids(result));
        assertEquals("child", origin(result.get(3)));
    }

    @Test
    void rejectsCoremodsThatNeedEarlierBootstrap() throws Exception {
        var manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        manifest.getMainAttributes().putValue("FMLCorePlugin", "fixture.EarlyPlugin");
        var bytes = new ByteArrayOutputStream();
        manifest.write(bytes);
        var bundle = modJar("replacement", "1.0", "bundled", Map.of("META-INF/MANIFEST.MF", bytes.toByteArray()));
        var table = new ASMDataTable();

        var failure = assertThrows(IllegalStateException.class,
                () -> NestedModLoader.discover(discover(host("host", bundle), table), table, temporary));

        assertTrue(failure.getCause().getMessage().contains("requires early bootstrap via FMLCorePlugin"));
    }

    @Test
    void transformedForgeDiscovererActuallyFindsTheNestedMod() throws Exception {
        var bundle = modJar("replacement", "1.0", "bundled", Map.of());
        var host = host("host", bundle).toFile();
        var target = "net.minecraftforge.fml.common.discovery.ModDiscoverer";
        byte[] original;
        try (var input = getClass().getResourceAsStream("/" + target.replace('.', '/') + ".class")) {
            original = input.readAllBytes();
        }
        var transformed = new NestedModTransformer().transform(target, target, original);
        var loader = new ClassLoader(getClass().getClassLoader()) {
            @Override
            protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
                if (!name.equals(target)) return super.loadClass(name, resolve);
                var existing = findLoadedClass(name);
                return existing != null ? existing : defineClass(name, transformed, 0, transformed.length);
            }
        };
        var discovererClass = loader.loadClass(target);
        var discoverer = discovererClass.getConstructor().newInstance();
        var classpath = new ModClassLoader(null) {
            @Override
            public File[] getParentSources() {
                return new File[]{host};
            }
        };
        discovererClass.getMethod("findClasspathMods", ModClassLoader.class).invoke(discoverer, classpath);
        var gameDirectory = FMLInjectionData.class.getDeclaredField("minecraftHome");
        gameDirectory.setAccessible(true);
        var previousHome = gameDirectory.get(null);
        try {
            gameDirectory.set(null, temporary.toFile());
            @SuppressWarnings("unchecked")
            var result = (List<ModContainer>) discovererClass.getMethod("identifyMods").invoke(discoverer);
            assertEquals(List.of("host", "replacement"), ids(result));
            assertEquals("bundled", origin(result.get(1)));
        } finally {
            gameDirectory.set(null, previousHome);
        }
    }

    private Path host(String id, byte[] bundle) throws Exception {
        return write(id + ".jar", modJar(id, "1.0", "host", Map.of(
                NestedModLoader.CATALOG, catalog("replacement", bundle), nestedPath("replacement", bundle), bundle)));
    }

    private Path write(String name, byte[] bytes) throws Exception {
        return Files.write(temporary.resolve(name), bytes);
    }

    private Path cache() {
        return temporary.resolve("libraries/leafia/nested-mods");
    }

    private static List<ModContainer> discover(Path source, ASMDataTable table) {
        var mods = new ModCandidate(source.toFile(), source.toFile(), ContainerType.JAR).explore(table);
        assertEquals(1, mods.size(), "Fixture must be discovered by Forge: " + source);
        return mods;
    }

    private static List<String> ids(List<ModContainer> mods) {
        return mods.stream().map(ModContainer::getModId).toList();
    }

    private static String origin(ModContainer mod) throws Exception {
        try (var loader = new URLClassLoader(new URL[]{mod.getSource().toURI().toURL()}, NestedModLoaderTest.class.getClassLoader())) {
            return (String) loader.loadClass("fixture." + mod.getModId()).getMethod("origin").invoke(null);
        }
    }

    private static String resource(ModContainer mod) throws Exception {
        try (var loader = new URLClassLoader(new URL[]{mod.getSource().toURI().toURL()}, NestedModLoaderTest.class.getClassLoader());
             var input = loader.getResourceAsStream("assets/replacement/origin.txt")) {
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static byte[] catalog(String id, byte[] jar) throws Exception {
        var entry = new JsonObject();
        entry.addProperty("modid", id);
        entry.addProperty("sha256", hash(jar));
        entry.addProperty("path", nestedPath(id, jar));
        var jars = new JsonArray();
        jars.add(entry);
        var catalog = new JsonObject();
        catalog.addProperty("formatVersion", 1);
        catalog.add("jars", jars);
        return catalog.toString().getBytes(StandardCharsets.UTF_8);
    }

    private static String nestedPath(String id, byte[] jar) throws Exception {
        return "META-INF/jarjar/" + id + "-" + hash(jar) + ".jar";
    }

    private static String hash(byte[] bytes) throws Exception {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
    }

    private static byte[] modJar(String id, String version, String origin, Map<String, byte[]> extras) throws Exception {
        var internalName = "fixture/" + id;
        var writer = new ClassWriter(0);
        writer.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, internalName, null, "java/lang/Object", null);
        var annotation = writer.visitAnnotation("Lnet/minecraftforge/fml/common/Mod;", true);
        annotation.visit("modid", id);
        annotation.visit("version", version);
        annotation.visit("useMetadata", true);
        annotation.visit("acceptedMinecraftVersions", "[1.12.2]");
        annotation.visitEnd();
        var method = writer.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "origin", "()Ljava/lang/String;", null, null);
        method.visitCode();
        method.visitLdcInsn(origin);
        method.visitInsn(Opcodes.ARETURN);
        method.visitMaxs(1, 0);
        method.visitEnd();
        writer.visitEnd();
        var entries = new LinkedHashMap<String, byte[]>();
        entries.put(internalName + ".class", writer.toByteArray());
        entries.put("mcmod.info", ("[{\"modid\":\"" + id + "\",\"name\":\"" + id
                + "\",\"version\":\"" + version + "\",\"useDependencyInformation\":true}]").getBytes(StandardCharsets.UTF_8));
        entries.put("assets/replacement/origin.txt", origin.getBytes(StandardCharsets.UTF_8));
        entries.putAll(extras);
        var bytes = new ByteArrayOutputStream();
        try (var jar = new JarOutputStream(bytes)) {
            for (var entry : entries.entrySet()) {
                var zipEntry = new JarEntry(entry.getKey());
                zipEntry.setTime(0);
                jar.putNextEntry(zipEntry);
                jar.write(entry.getValue());
                jar.closeEntry();
            }
        }
        return bytes.toByteArray();
    }
}
