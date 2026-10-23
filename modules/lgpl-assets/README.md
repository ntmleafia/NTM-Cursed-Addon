# Leafia LGPL Assets

This Forge 1.12.2 module contains retained upstream artwork, meshes, recordings
and contributor translations used by Leafia's Cursed Addon.

The addon normally embeds this module as an independently replaceable nested
JAR. To modify or replace it, put an equivalent Forge mod declaring
`leafia_lgpl_assets` in the instance's `mods` directory. That external module
takes precedence even if it has a lower version. The bundled module is then
excluded from discovery, classloading and resource packs.

From the repository root, build this component with:

    gradlew :lgpl-assets:jar :lgpl-assets:sourcesJar

## Licensing

This component is licensed under [LGPL-3.0-only](LICENSE.LESSER), subject to the file-specific licenses
and exceptions recorded in [REUSE.toml](REUSE.toml) and [NOTICE](NOTICE). The main addon's Shield
Noncompete restriction does not apply to LGPL-covered material.

If you distribute a modified version of this component, port its assets to another version, or reuse
them in a derivative work, the applicable LGPL/GPL terms require you to:

- preserve copyright and license notices and attribution, including `REUSE.toml` and applicable notices;
- state prominently that you modified the work, with a relevant date;
- license modified versions and derivative works of the LGPL-covered assets under LGPL-3.0-only, or
  GPL-3.0-only where the license permits;
- make complete corresponding source available for each build you distribute, as required by the
  license, including the preferred editable asset sources and applicable build scripts.

Distribution with another mod must preserve the modification, replacement and debugging rights
required by LGPLv3 §4. The equivalent external module described above lets users replace the bundled
component.

Modified releases of this component must be clearly identified as modified and must not be presented
as official or endorsed releases. No trademark rights to the addon's name are granted here; accurate
upstream attribution does not imply endorsement.

Violations can terminate your rights under GPLv3 §8, subject to its reinstatement provisions.
