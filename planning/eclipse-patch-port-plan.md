---
type: reference
tags: [repo/irl-core]
up: "[[irl-core]]"
---
# Eclipse shaderpack patch — port plan & context

**Status:** planning complete, blocked on one external input (the real Bliss `.irlights` patch).
**Decision locked:** *port the existing Bliss patch* (re-anchor it for Eclipse), not author from scratch.
**Audience:** a future session, possibly on a different machine. This doc is self-contained — it does not assume the conversation that produced it.

---

## 0. TL;DR / what this task is

Add the **Eclipse-Shader-Unstable** shaderpack to the set of packs the IRLights patcher can patch.
"Patching" = injecting a custom **per-frame dynamic light system** (an SSBO of point/spot lights at GL **binding 7**) into a shaderpack's deferred-lighting GLSL, so IRLights/IRL-editor block & entity lights show up in that pack.

Concretely, the deliverable is a **new `Eclipse.irlights` patch file** that mirrors what the existing **Bliss** patch does, with anchors re-pointed to where Eclipse's code actually lives. Eclipse is a fork ("edit") of Bliss, so most of the patch should transfer; only the regions Eclipse changed need re-anchoring.

**This repo (`irl-core`) almost certainly needs ZERO Java changes.** The patch engine is shader-agnostic. The `.irlights` file is a *resource* that ships in the consumer mods, not in irl-core. (See §6 for where it lives.)

---

## 1. Repo orientation (irl-core)

`irl-core` is a pure-Java (no Minecraft, no Fabric) library — the shared engine behind two sibling mods:
- **IRLights** (`bbs-irlights-addon`) — a BBS Mod Studio add-on
- **IRL-editor** (`irl-editor`) — a standalone ImGui light editor

It contains two subsystems:
- `org.qualet.irl.light` — the GPU light buffer (`LightBuffer`, `LightRegistry`). **This is the GLSL contract** the injected shader code must mirror.
- `org.qualet.irl.patcher` — the `.irlights` patch engine (parse / validate / apply).

### Key files (all under `src/main/java/org/qualet/irl/`)
| File | Role |
|---|---|
| `light/LightBuffer.java` | **The std430 SSBO contract.** Binding 7, `Light` struct = 6×vec4 = 96 B, header `uint irlite_lightCount + 12 B pad`. The injected GLSL MUST match this byte-for-byte. |
| `light/LightRegistry.java` | Collects lights CPU-side, uploads to the SSBO. |
| `patcher/IrlPatch.java` | Parsed patch model. Ops = `ADD_FILE / AFTER / BEFORE / REPLACE`. `CONTRACT_VERSION = 1`. |
| `patcher/IrlPatchParser.java` | The `.irlights` DSL parser (grammar below). |
| `patcher/PatchEngine.java` | Dry-run core: plays ops in memory, EOL-tolerant anchor matching, records EVERY failure. |
| `patcher/IrlPatchApplier.java` | `validate()` (no write) and `apply()` (copy pack → write dirty files → stamp marker). Works on a folder or a `.zip`. |
| `patcher/PatcherHost.java` | Platform seam. **`bundledPatches()` + `openBundledPatch()` is how the consumer mods ship `.irlights` files.** |
| `patcher/PatchLibrary.java` | Extracts bundled patches to `<gameDir>/<patchesDirName>/patches`, refreshes stale ones. |
| `patcher/Shaderpacks.java` | Lists installed shaderpacks. |

### docs/ is a red herring for this task
`docs/shadow-*.md` describe the **Java-side shadow-caster orchestration** (baking entity/block silhouettes into depth atlases) — NOT the GLSL shader patches. They do not contain any anchor strings or shader file paths. Don't waste time mining them for the Eclipse patch.

---

## 2. The `.irlights` patch DSL (everything you need to author one)

Source of truth: `IrlPatchParser.java` javadoc + body.

```
# comment (only OUTSIDE a <<< ... >>> body)
@name        Human readable name
@target      ShaderpackName     # informational only — matched against pack names in the UI
@packversion v2.1               # informational — pack version the patch was authored against
@irlite      1                  # GLSL contract version required (optional; 0 = unpinned)
@marker      IRLITE             # stamped into the marker file

# --- add a whole new file ---
+file path/relative/to/pack/new.glsl
<<<
...whole file content...
>>>

# --- edit an existing file ---
@file path/relative/to/pack/existing.glsl
after "literal anchor"
<<<
...text inserted right AFTER the anchor...
>>>
before "literal anchor"
<<<
...text inserted right BEFORE the anchor...
>>>
replace "literal to replace"
<<<
...replacement text...
>>>
```

### Semantics (critical for re-anchoring)
- **Anchors are literal substrings**, not regex. Escapes: `\n \t \" \\`.
- An anchor must match **exactly once** in the file. **Zero matches = fail. >1 match = "ambiguous" fail.** So anchors must be long/unique enough to be singular.
- **Anchor alternatives:** `after "a" | "b" | "c"` — tries each in order, first with exactly one match wins. Use this to cover Bliss-vs-Eclipse text drift in one op.
- **Optional ops:** `after?` / `before?` / `replace?` — skipped (not failed) when no anchor matches. Use for edits that only apply to some pack variants.
- **EOL-tolerant:** a `\n` in an anchor matches `\n`, `\r\n`, or lone `\r` in the file. Inserted bodies adopt the target file's dominant EOL. (Eclipse files are **LF**.)
- **`@file` sets the "current file"** for subsequent `after/before/replace` ops until the next `@file`.
- **`+file` fails if the target already exists** in the pack (treated as "already patched").
- **`@irlite 1`** pins the contract; applier refuses if `!= CONTRACT_VERSION` (currently 1). Recommend pinning the Eclipse patch to `@irlite 1` to match the Bliss patch.
- Whole patch fails (atomic) unless EVERY non-optional op resolves. `validate()` reports all failures at once — ideal for porting.
- Output gets `irlite_patched.txt` stamped in its root; the engine refuses to re-patch a stamped pack. Always patch a CLEAN copy.

---

## 3. The GLSL contract (what the injected code must declare/use)

From `LightBuffer.java:10-43`. The injected shader code must mirror this **exactly** or the SSBO reads garbage.

```glsl
struct Light {
    vec4 posRadius;       // xyz = world position, w = radius
    vec4 colorIntensity;  // rgb = linear color, a = intensity
    vec4 dirType;         // xyz = direction (spot, normalized), w = type (0 point, 1 spot)
    vec4 cone;            // x = cos(outerAngle/2), y = cos(innerAngle/2),
                          // z = lightMask (0 all, 1 entities only, 2 blocks only),
                          // w = bulbSize (0 = use global)
    vec4 vlParams;        // x = anisotropy (HG g), y = vlDensity, z = beamStrength,
                          // w = shadowTile (-1 = none)
    vec4 cookie;          // x = gobo layer (-1 none), y = rotation(rad), z = scale,
                          // w = flags (bit0 = invert) — spot-only projected mask
};

layout(std430, binding = 7) buffer IrliteLights {
    uint irlite_lightCount;
    uint _pad0, _pad1, _pad2;
    Light irlite_lights[];
};
```

- Binding = **7** (`LightBuffer.BINDING`). Struct stride = **96 B** (6×vec4). Header = **16 B**.
- Max lights = 2048 (perf ceiling only; array is unbounded `[]`, so no regen needed for more).
- The exact names (`irlite_lightCount`, `irlite_lights`, `IrliteLights`) are whatever the **Bliss patch already uses** — match the Bliss patch verbatim so the injected sampling code is identical. The struct LAYOUT above is the hard contract.

**The actual injected helper (the loop that accumulates each light's contribution per fragment) lives in the Bliss `.irlights` file, which we do not yet have.** That body is reused verbatim for Eclipse; only the anchors that place it change.

---

## 4. Eclipse shaderpack structure (verified on disk)

Location (reference only — **DO NOT EDIT**, per `CLAUDE.md`): `Eclipse-Shader-Unstable/shaders/`
Eclipse is "A Bliss Edit" of `X0nk/Bliss-Shader` (see `Eclipse-Shader-Unstable/README.md`, `CREDITS.txt`).

### Version / target
- `lib/settings.glsl:1` → `#define SHADER_VERSION_LABEL 482` (Bliss/Eclipse internal label "482").
- Programs use `#version 430 compatibility`. Iris-version guards present (`IRIS_VERSION < 11004`), `MC_VERSION` guards (`< 12101`). So this is a recent Iris/MC-1.21.x-era pack.
- Files are **UTF-8, LF** line endings.

### Directory layout — the big structural fact
```
shaders/
  world0/      122 files — MOSTLY 5-LINE STUBS that #include from dimensions/
  world1/  world-1/  world0_with_aether_flag/  world0_with_twilight_forest_flag/  — more stubs
  dimensions/  70 files — THE REAL PROGRAM CODE lives here
  lib/         45 files — shared includes (lighting, shadows, SSBOs, settings)
  photonics/   ECLIPSE-ONLY — Photonics mod GI/light integration
  template/    iProperties generator templates
  texture/  lang/  ph_lights.json  *.properties
```

**Example stub** — `world0/composite1.fsh` in full:
```glsl
#version 430 compatibility

#define OVERWORLD_SHADER

#include "/dimensions/composite1.fsh"
```
→ All real logic is in `dimensions/composite1.fsh`. **If the Bliss patch `@file`-targets `world0/…` or a top-level program file, those ops must be re-pointed to `dimensions/…` for Eclipse.** (Caveat: modern Bliss-unstable may ALSO use this `dimensions/` split — confirm against the real Bliss patch; if Bliss already targets `dimensions/`, the paths transfer unchanged.)

### SSBO bindings already in use (binding-collision check — PASSED)
Grep of `binding = N` across `shaders/`:
- `lib/SSBOs.glsl:1` → **binding 4** (`SSBO1` — Eclipse's big custom buffer: moon/eclipse shadow matrices, sky colors, exposure, water sim, etc.)
- `lib/voxel_common.glsl:5,12` → **binding 5** (LPV voxel data)
- **Binding 7 is FREE.** ✅ Our light SSBO will not collide.

---

## 5. The four edits an Eclipse light patch must make (with concrete anchor candidates)

These are the injection points the Bliss patch performs (inferred from the contract + Bliss/Eclipse structure). Each row gives the **Eclipse** target and a verified `file:line` anchor candidate. Confirm the Bliss patch does the analogous edit, then re-anchor.

| # | Edit | Eclipse target | Verified anchor candidate (file:line) |
|---|---|---|---|
| **E1** | Declare the binding-7 `IrliteLights` SSBO + `Light` struct + the per-fragment accumulation helper function. | **New file** `+file shaders/lib/irlite_lights.glsl` (preferred — keeps it out of Eclipse's heavily-customized `lib/SSBOs.glsl`). | n/a (new file) |
| **E2** | `#include` E1 into the main deferred lighting pass. | `shaders/dimensions/composite1.fsh` | `after "#include \"/lib/diffuse_lighting.glsl\""` → at **composite1.fsh:266**. (Alt: `after "#include \"/lib/SSBOs.glsl\""` at :3.) |
| **E3** | Add the dynamic-light contribution into the scene light accumulation. | `shaders/dimensions/composite1.fsh` | Block-light call site at **composite1.fsh:1592**: `vec3 blockLightColor = doBlockLightLighting(vec3(TORCH_R,TORCH_G,TORCH_B), lightmap.x, feetPlayerPos, lpvPos, viewPos, isDHrange, blueNoise(), FlatNormals, hand);` — inject `after` this line, OR fold into the final combine at **:1699** `vec3 FINAL_COLOR = (Indirect_lighting + Direct_lighting) * albedo;`. Match whatever the Bliss patch targets. |
| **E4** | *(optional, only if Bliss patch does it)* block-light helper hook inside `doBlockLightLighting`. | `shaders/lib/diffuse_lighting.glsl` | Function spans **diffuse_lighting.glsl:104** (`vec3 doBlockLightLighting(`) → **:279** (`return blockLight * TORCH_AMOUNT;`). Inject before the return. |

### Where Eclipse diverges from Bliss (the re-anchoring hotspots)
These are exactly the lighting/shadow regions Eclipse edited, so Bliss anchors landing here are the ones most likely to break:

1. **`lib/SSBOs.glsl`** — Eclipse rewrote this (binding-4 buffer full of moon-rotation / eclipse / Photonics / sky / exposure fields). **Avoid anchoring here.** This is the reason to declare the light SSBO as its own `+file` include (E1) instead of inserting into `SSBOs.glsl`.
2. **`dimensions/composite1.fsh`** — Eclipse added near the lighting path:
   - `#include "/photonics/photonics.glsl"` at **:178** (Photonics GI).
   - `#ifdef CUSTOM_MOON_ROTATION` shadow-matrix swap (uses `customShadowMatrixSSBO`) ~**:1369**.
   - Photonics-GI mixing into `Indirect_lighting` at **:1538-1548** (`Indirect_lighting = mix(Indirect_lighting, gi_color, photonicsFalloff)`).
   Keep injections AWAY from these (don't anchor on the moon/Photonics lines). The block-light call (:1592) and final combine (:1699) are downstream of them and are safer.
3. **`lib/diffuse_lighting.glsl`** — Eclipse interleaved **Photonics block-light overrides** inside `doBlockLightLighting` (guards like `!defined PHOTONICS_LIGHT_PASS || !defined PH_ENABLE_BLOCKLIGHT`, ~:124, ~:145). If the Bliss patch injects inside this function, the surrounding text differs → re-anchor on a stable line (e.g. the `return blockLight * TORCH_AMOUNT;` at :279) or use anchor alternatives.
4. **`photonics/` + `ph_lights.json` + `colorwheel.properties`** — Eclipse-only. Don't exist in Bliss; not patch targets, but be aware Photonics has its own light path that our injection should ADD to, not fight. Verify our contribution composes additively with `FINAL_COLOR`.

---

## 6. Where the `.irlights` file ships (the consumer mods)

irl-core ships **no** patches (`src/main/resources/` has only `assets/irl-core/icon.png` + `fabric.mod.json`). Patches are bundled by each consumer mod via `PatcherHost.bundledPatches()` / `openBundledPatch()`.

So the finished `Eclipse.irlights` must be added to the consumer mod(s):
- `bbs-irlights-addon` (IRLights) and/or `irl-editor` (IRL-editor)
- Drop the file in that mod's resources, add its name to the mod's `bundledPatches()` list.

**The existing Bliss `.irlights` lives in those same mods** — that is the file we need to read (user will provide path/repo). It is NOT on this machine as of writing.

---

## 7. Port procedure (do this once the Bliss patch is in hand)

1. **Read the Bliss `.irlights`.** Record: `@target`, `@packversion`, `@irlite`, every `@file` path, and for each op its kind (`after/before/replace/+file`), anchor string(s), and injected body.
2. **Capture the injected GLSL bodies verbatim** — the SSBO/struct decl (E1), the `#include` line (E2), the accumulation call/helper (E3/E4). These transfer to Eclipse UNCHANGED. Only paths + anchors change.
3. **Classify each Bliss op against Eclipse** (use the live tree under `Eclipse-Shader-Unstable/shaders/`):
   - ✅ **transfers as-is** — same path, anchor still matches exactly once.
   - ⚠️ **drifted** — path same but anchor text changed (Photonics/moon edits). Re-anchor to a nearby stable line, or add `| "eclipse alt"` alternatives.
   - ❌ **wrong file** — Bliss targets `world0/`/top-level; Eclipse's code is in `dimensions/`. Re-point the `@file`.
4. **Write `Eclipse.irlights`** with `@target Eclipse-Shader-Unstable` (or the pack's display name), `@packversion` = Eclipse label `482` (or the MC/Iris version), `@irlite 1`. Prefer the E1 `+file lib/irlite_lights.glsl` approach.
5. **Validate against the live tree** before declaring done (see §8). Every op must resolve to exactly one match.
6. **Wire into the consumer mod(s)** (§6).
7. Optionally regression-test by also validating against a clean stock Bliss copy with the ORIGINAL Bliss patch (sanity that the bodies still match the contract).

---

## 8. How to dry-run validate the Eclipse patch (no Minecraft needed)

The engine is pure Java and runs straight against the folder. Two options:

**A. Tiny Java harness** (preferred — exercises the real engine):
```java
// classpath = irl-core build output (./gradlew build → build/libs or build/classes)
import org.qualet.irl.patcher.*;
import java.nio.file.*;

String patch = Files.readString(Path.of("Eclipse.irlights"));
IrlPatch p = IrlPatchParser.parse(patch);
PatchResult r = IrlPatchApplier.validate(
    Path.of("Eclipse-Shader-Unstable"),   // folder OR a .zip; finds shaders/ root
    p);
System.out.println(r.summary);
r.log.forEach(System.out::println);   // every op + every failure, aggregated
```
`validate()` writes nothing. It reports each op that resolves and every anchor that fails / is ambiguous, all at once — exactly what you want while re-anchoring.

> Note `SourceRoot.open` expects a folder containing `shaders/`, or a `.zip`. `Eclipse-Shader-Unstable/` is a folder with `shaders/` inside, so pass the folder path directly.

**B. `apply()` to a scratch output** to eyeball the patched GLSL:
```java
IrlPatchApplier.apply(Path.of("Eclipse-Shader-Unstable"),
                      Path.of("…/scratch/Eclipse-patched"), p);
```
Then diff the patched `dimensions/composite1.fsh` etc. against the source.

Build irl-core first: `./gradlew build` (JDK 17+). Output jar: `build/libs/irl-core-1.0-obt.jar`.

---

## 9. Open questions / inputs still needed

1. **[BLOCKING] The real Bliss `.irlights` file.** User is providing a path/repo. Without it the exact anchors and injected GLSL bodies are unknown — everything in §5 is the *target side*; the *source side* comes from this file.
2. **What `@packversion`/`@target` does the Bliss patch use, and does it `@file`-target `world0/`/top-level or already `dimensions/`?** Determines whether §5 path re-pointing (❌ rows) is needed at all.
3. **Does the Bliss patch touch the shadow path (E4 / shadow tiles), or is it deferred-lighting-only?** Eclipse's shadow path is the most-diverged area (custom moon/eclipse matrices at binding 4) — if the Bliss patch injects there, expect heavy re-anchoring.
4. **Photonics interaction:** confirm the injected dynamic-light contribution composes additively with Eclipse's Photonics GI/block-light path rather than being overwritten by it (the `doBlockLightLighting` Photonics guards at `diffuse_lighting.glsl:124-199`).

---

## 10. Quick-start for the next session (different machine)

1. `cd` into the `irl-core` repo. Confirm `Eclipse-Shader-Unstable/` is present (reference pack; **never edit it** — see `CLAUDE.md`).
2. Get the Bliss `.irlights` from the user's provided path/repo. Read §2 (DSL) + §3 (contract) here.
3. Follow §7 (port procedure), using the §5 anchor table + the live Eclipse tree.
4. Validate with §8. Wire into the consumer mod per §6.
5. Eclipse facts re-verifiable any time with:
   ```bash
   B=Eclipse-Shader-Unstable/shaders
   cat "$B/world0/composite1.fsh"                 # confirm stub→dimensions indirection
   grep -rnoE "binding *= *[0-9]+" "$B"           # confirm binding 7 still free (4,5 used)
   grep -nE "doBlockLightLighting|FINAL_COLOR|#include \"/lib/diffuse_lighting" "$B/dimensions/composite1.fsh"
   grep -nE "doBlockLightLighting|return blockLight" "$B/lib/diffuse_lighting.glsl"
   grep -n SHADER_VERSION_LABEL "$B/lib/settings.glsl"
   ```

---

*Authored from a read-only investigation of irl-core + the Eclipse tree. The patch engine, GLSL contract, and Eclipse structure are verified on disk; the Bliss patch source is the one missing input.*

## Related

- [[irl-core]] — repo hub
- [[Repos/irl-core/docs/shadow-caster-seam-spec|shadow-caster-seam-spec]]
