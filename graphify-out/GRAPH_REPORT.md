# Graph Report - .  (2026-06-24)

## Corpus Check
- Corpus is ~19,962 words - fits in a single context window. You may not need a graph.

## Summary
- 177 nodes · 330 edges · 14 communities (12 shown, 2 thin omitted)
- Extraction: 87% EXTRACTED · 13% INFERRED · 0% AMBIGUOUS · INFERRED: 43 edges (avg confidence: 0.81)
- Token cost: 109,291 input · 0 output

## Community Hubs (Navigation)
- [[_COMMUNITY_Patcher Host & Shaderpacks|Patcher Host & Shaderpacks]]
- [[_COMMUNITY_Shadow Caster Seam & Invariants|Shadow Caster Seam & Invariants]]
- [[_COMMUNITY_Light Registry|Light Registry]]
- [[_COMMUNITY_Patch Applier|Patch Applier]]
- [[_COMMUNITY_Patch Engine|Patch Engine]]
- [[_COMMUNITY_Shadow Orchestration & Mod Ecosystem|Shadow Orchestration & Mod Ecosystem]]
- [[_COMMUNITY_Patch Parser|Patch Parser]]
- [[_COMMUNITY_Light Buffer SSBO|Light Buffer SSBO]]
- [[_COMMUNITY_Patch Filesystem IO|Patch Filesystem I/O]]
- [[_COMMUNITY_Lockstep Verifier Script|Lockstep Verifier Script]]
- [[_COMMUNITY_Mod Icon & Branding|Mod Icon & Branding]]
- [[_COMMUNITY_Caster Static Hashing|Caster Static Hashing]]

## God Nodes (most connected - your core abstractions)
1. `LightRegistry` - 20 edges
2. `PatchEngine` - 12 edges
3. `ShadowCasterSource (the seam)` - 12 edges
4. `IrlPatchApplier` - 11 edges
5. `PatcherHost` - 11 edges
6. `LightBuffer` - 9 edges
7. `IrlPatch` - 8 edges
8. `IrlPatchParser` - 8 edges
9. `PatchResult` - 8 edges
10. `SourceRoot` - 6 edges

## Surprising Connections (you probably didn't know these)
- `ShadowCasterSource (the seam)` --semantically_similar_to--> `PatcherHost`  [INFERRED] [semantically similar]
  docs/shadow-caster-seam-spec.md → README.md
- `IRLite variant (BBS Form/Film/Morph)` --conceptually_related_to--> `IRLights (BBS Mod Studio add-on)`  [INFERRED]
  docs/shadow-caster-seam-spec.md → README.md
- `ShadowBaker.java (bake driver)` --references--> `LightRegistry`  [EXTRACTED]
  docs/shadow-orchestration-lockstep.md → README.md
- `Shadow-orchestration lockstep (option C+)` --references--> `ShadowCasterSource (the seam)`  [EXTRACTED]
  docs/shadow-orchestration-lockstep.md → docs/shadow-caster-seam-spec.md
- `RedactorEntityCasterSource.java` --implements--> `ShadowCasterSource (the seam)`  [EXTRACTED]
  docs/shadow-orchestration-lockstep.md → docs/shadow-caster-seam-spec.md

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **The 5 load-bearing shadow invariants** — docs_shadow_caster_seam_spec_inv1, docs_shadow_caster_seam_spec_inv2, docs_shadow_caster_seam_spec_inv3, docs_shadow_caster_seam_spec_inv4, docs_shadow_caster_seam_spec_inv5 [EXTRACTED 1.00]
- **Frozen shadow seam interfaces** — docs_shadow_caster_seam_spec_shadow_caster_source, docs_shadow_caster_seam_spec_occluder_sink, docs_shadow_caster_seam_spec_occluder_batch, docs_shadow_caster_seam_spec_caster_type [EXTRACTED 1.00]
- **Three ShadowCasterSource variants** — docs_shadow_caster_seam_spec_redactor_main, docs_shadow_caster_seam_spec_redactor_port, docs_shadow_caster_seam_spec_irlite_variant [EXTRACTED 1.00]

## Communities (14 total, 2 thin omitted)

### Community 0 - "Patcher Host & Shaderpacks"
Cohesion: 0.12
Nodes (7): InputStream, List, Patcher, PatcherHost, PatchLibrary, Shaderpacks, Path

### Community 1 - "Shadow Caster Seam & Invariants"
Cohesion: 0.08
Nodes (28): CasterType (neutral tag holder), emitFromBox (structural INV-5 enforcement), INV-1 Matrix corruption, INV-2 Static vs dynamic split, INV-4 Exception + vertex-run isolation, INV-5 Bounding-sphere circumscription, IRLite variant (BBS Form/Film/Morph), OccluderBatch (opaque batch handle) (+20 more)

### Community 3 - "Patch Applier"
Cohesion: 0.36
Nodes (3): IrlPatch, IrlPatchApplier, PatchResult

### Community 4 - "Patch Engine"
Cohesion: 0.22
Nodes (4): Kind, Op, FileState, PatchEngine

### Community 5 - "Shadow Orchestration & Mod Ecosystem"
Cohesion: 0.15
Nodes (13): Shadow-orchestration lockstep (option C+), Lockstep normalizer / substitution table, ShadowBaker.java (bake driver), ShadowRenderer.java (GL layer), verify-shadow-lockstep.py verifier, Gradle composite build (JiJ include), irl-core, IRL-editor (ImGui light editor) (+5 more)

### Community 6 - "Patch Parser"
Cohesion: 0.35
Nodes (4): Exception, IrlPatchParser, ParseException, String

### Community 8 - "Patch Filesystem I/O"
Cohesion: 0.33
Nodes (5): Closeable, FileSystem, Override, PatchException, SourceRoot

### Community 9 - "Lockstep Verifier Script"
Cohesion: 0.36
Nodes (7): compare_pair(), main(), normalize(), Locate the two sibling repos. Honors IRL_REDACTOR_DIR / IRLITE_DIR env     vars, Return a list of human-readable diff lines for one file pair (empty     list =, Strip BOM, normalize line endings, drop package+import lines, apply     the all, resolve_repos()

### Community 10 - "Mod Icon & Branding"
Cohesion: 0.50
Nodes (4): irl-core Fabric Mod, irl-core Mod Icon, Magnifying Glass / Lens Motif, Minecraft Pixel-Art Style

### Community 11 - "Caster Static Hashing"
Cohesion: 1.00
Nodes (3): INV-3 staticHash (avalanche-mixed fold), staticHash signature, modelBlockHash (FNV avalanche per-caster content)

## Knowledge Gaps
- **11 isolated node(s):** `CasterType (neutral tag holder)`, `ShadowBakeState.setBaking gate`, `Lockstep normalizer / substitution table`, `ShadowRenderer.java (GL layer)`, `Ф5 Stonecutter (port branch unification)` (+6 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **2 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `LightRegistry` connect `Light Registry` to `Light Buffer SSBO`?**
  _High betweenness centrality (0.135) - this node is a cross-community bridge._
- **Why does `ShadowCasterSource (the seam)` connect `Shadow Caster Seam & Invariants` to `Shadow Orchestration & Mod Ecosystem`?**
  _High betweenness centrality (0.041) - this node is a cross-community bridge._
- **What connects `Strip BOM, normalize line endings, drop package+import lines, apply     the all`, `Locate the two sibling repos. Honors IRL_REDACTOR_DIR / IRLITE_DIR env     vars`, `Return a list of human-readable diff lines for one file pair (empty     list =` to the rest of the system?**
  _15 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Patcher Host & Shaderpacks` be split into smaller, more focused modules?**
  _Cohesion score 0.125 - nodes in this community are weakly interconnected._
- **Should `Shadow Caster Seam & Invariants` be split into smaller, more focused modules?**
  _Cohesion score 0.08465608465608465 - nodes in this community are weakly interconnected._
- **Should `Light Registry` be split into smaller, more focused modules?**
  _Cohesion score 0.11695906432748537 - nodes in this community are weakly interconnected._