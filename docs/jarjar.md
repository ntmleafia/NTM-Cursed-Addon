# Nested Forge mods

The `jarJar` configuration embeds independently replaceable Forge 1.12.2 mods:

```groovy
dependencies {
    jarJar files('libs/leafia-lgpl-assets.jar')
    // Or a distributable artifact from a Maven repository:
    // jarJar 'example:leafia-lgpl-support:1.0.0'
}
```

Nested dependencies are also available for compilation and development runs.
Transitive dependencies are excluded; declare every bundled mod explicitly.
Supply the distributable/reobfuscated JAR for code modules. The parent build
preserves nested bytes and does not reobfuscate or downgrade nested code.

Each JAR must contain exactly one ordinary Forge mod and an expanded
`mcmod.info` array describing its mod ID. Resource modules need a small `@Mod`
entry point so Forge creates their normal resource packs. Keep existing resource
namespaces and paths when moving artwork. Set the module's sorting dependencies
to preserve intended overrides; an asset module providing replacements for `hbm`
resources should load after `hbm` and before `leafia`.

The build writes `META-INF/leafia/nested-mods.json`, with format version `1`,
mod IDs, SHA-256 digests and paths under `META-INF/jarjar/`. These resources flow
through the normal Java 8 and Java 25 JAR pipelines. Duplicate declarations of
identical content are deduplicated; different bundled JARs declaring the same ID
fail the build.

`NestedModTransformer` adds one call at the return of Forge's
`ModDiscoverer.identifyMods`. At that point Forge has discovered external mod
containers and their real IDs, and has not constructed their mod classes.
`NestedModLoader` discovers only missing bundled modules through Forge's normal
`ModCandidate` and annotation table. Nested modules may themselves have catalogs.

An external mod with the same ID always takes precedence, including when its
version is lower than the bundle. The nested JAR is then neither extracted nor
added to discovery, the classpath or resource packs. An incompatible replacement
fails through the normal loading path; it does not reactivate the bundle.
Multiple external mods with the same ID retain Forge's duplicate-mod failure.

Selected bundles are cached by digest under the instance's
`libraries/leafia/nested-mods/` directory, outside the scanned `mods` folders.
Cached content is verified before use. A corrupt bundle or cache fails with its
mod ID in the error. The module's classes and resources become available through
normal Forge mod construction and resource-pack registration.

Coremods and tweakers require earlier bootstrap and cannot be bundled by this
loader. Bundles declaring `FMLCorePlugin`, `TweakClass` or `ContainedDeps` fail
explicitly. Keep early bootstrap classes in the parent or an independently
installed dependency. Conflicting bundled versions also fail instead of choosing
one implicitly.

Each separately licensed module must carry its applicable notices and license
texts, and its corresponding source must be available under those terms. Moving
an asset into a module does not establish permission to distribute it. The main
license must preserve the module's independent modification, debugging and
replacement rights.

The main binary and source JAR tasks include the root license, NOTICE and
LICENSES directory under `META-INF/licenses/leafia/`.

`jarJarOnly` supplies nested distributable artifacts whose compilation and
development API are provided separately. The retained implementation uses shared
compilation and exports a deobfuscated development variant; its production
artifact has an independent downgrade/reobfuscation pipeline. See the code
component's README for rebuilding and replacement instructions.
