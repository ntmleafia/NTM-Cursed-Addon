# Leafia LGPL Code

This is the retained legacy NTM implementation used by the addon. Its binary is
an ordinary Forge mod with ID `leafia_lgpl_code`. A supplied interface-compatible
external mod with that ID takes precedence over the nested copy, even when its
version is lower.

From the matching addon source checkout, build this component with:

    gradlew :lgpl-code:reobfJar :lgpl-code:sourcesJar


The source JAR preserves `src/main/...`, build files and notices.
After unpacking it, edit its source and build against a matching addon checkout:

    gradle -PaddonWorkspace=/path/to/NTM-Cursed-Addon build

This delegates to that checkout's wrapper with this component's Java and resource
trees as the inputs. It requires the parent checkout's documented build
dependencies and does not require changes to its main implementation. The
rebuilt component is copied to `build/libs/`. A custom component version can be
selected with `-PlgplCodeVersion=0.1.0`.

## Licensing

This component is licensed under [LGPL-3.0-only](LICENSE.LESSER), subject to the file-specific licenses
and exceptions recorded in source headers, [REUSE.toml](REUSE.toml) and [NOTICE](NOTICE). The main
addon's Shield Noncompete restriction does not apply to LGPL-covered material.

If you distribute a modified version of this component, port it to another version, or reuse its code
in a derivative work, the applicable LGPL/GPL terms require you to:

- preserve copyright and license notices, including source headers, `REUSE.toml` and applicable notices;
- state prominently that you modified the work, with a relevant date;
- license modified versions and derivative works of this component under LGPL-3.0-only, or
  GPL-3.0-only where the license permits;
- make complete corresponding source available for each build you distribute, as required by the
  license, including applicable build scripts.

An addon that only uses this component's published interfaces, without copying its implementation,
is an Application under LGPLv3 §0. Distribution of a linked Combined Work must meet LGPLv3 §4,
preserving the required rights to modify, replace and debug the LGPL component.

Modified releases of this component must be clearly identified as modified and must not be presented
as official or endorsed releases. No trademark rights to the addon's name are granted here; accurate
upstream attribution does not imply endorsement.

Violations can terminate your rights under GPLv3 §8, subject to its reinstatement provisions.
