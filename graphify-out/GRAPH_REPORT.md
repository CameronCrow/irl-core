# Graph Report - irl-core  (2026-07-22)

## Corpus Check
- 23 files · ~20,469 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 261 nodes · 410 edges · 17 communities (15 shown, 2 thin omitted)
- Extraction: 90% EXTRACTED · 10% INFERRED · 0% AMBIGUOUS · INFERRED: 43 edges (avg confidence: 0.81)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `3025a903`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

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
- [[_COMMUNITY_Community 14|Community 14]]
- [[_COMMUNITY_Community 15|Community 15]]
- [[_COMMUNITY_Community 16|Community 16]]

## God Nodes (most connected - your core abstractions)
1. `LightRegistry` - 20 edges
2. `ShadowCasterSource — frozen seam contract (v2)` - 13 edges
3. `PatchEngine` - 12 edges
4. `Shadow-orchestration lockstep (Ф4)` - 12 edges
5. `ShadowCasterSource (the seam)` - 12 edges
6. `IrlPatchApplier` - 11 edges
7. `PatcherHost` - 11 edges
8. `✦ irl-core` - 11 edges
9. `Ф3 Port Plan — IRLite onto the canon seamed shadow orchestration` - 11 edges
10. `LightBuffer` - 9 edges

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

## Communities (17 total, 2 thin omitted)

### Community 0 - "Patcher Host & Shaderpacks"
Cohesion: 0.12
Nodes (7): InputStream, List, Patcher, PatcherHost, PatchLibrary, Shaderpacks, Path

### Community 1 - "Shadow Caster Seam & Invariants"
Cohesion: 0.05
Nodes (42): CasterType (neutral tag holder), emitFromBox (structural INV-5 enforcement), INV-2 Static vs dynamic split, INV-4 Exception + vertex-run isolation, INV-5 Bounding-sphere circumscription, IRLite variant (BBS Form/Film/Morph), OccluderBatch (opaque batch handle), OccluderSink (allocation-free SoA writer) (+34 more)

### Community 3 - "Patch Applier"
Cohesion: 0.36
Nodes (3): IrlPatch, IrlPatchApplier, PatchResult

### Community 4 - "Patch Engine"
Cohesion: 0.16
Nodes (7): Kind, Op, IrlPatchParser, ParseException, FileState, PatchEngine, String

### Community 5 - "Shadow Orchestration & Mod Ecosystem"
Cohesion: 0.07
Nodes (29): Architecture, `CasterType` — neutral tag holder (CHANGE 8), Erratum — INV-1 vs self-drawing BBS forms (added 2026-06-17, Ф3 audit), Frozen interfaces, How to use this spec (entering Ф2 / Ф3), INV-1 — Matrix corruption (conditional re-establish, last-before-draw, pinned ordering), INV-2 — Static vs dynamic split (decoupled from the draw-path tag), INV-3 — staticHash (avalanche-mixed, count-folded, injective over the static multiset) (+21 more)

### Community 6 - "Patch Parser"
Cohesion: 0.10
Nodes (20): 0. Pre-flight invariants (read before touching code), 1. SEAM FILES — copy VERBATIM (package line only), 2. ORCHESTRATION — bring canon ShadowBaker + ShadowRenderer into IRLite, 3. NEW FILE — `IRLiteBbsCasterSource.java`, 4. BLOCK-CAST — shared, BBS-free; sibling reconciliation, 5. BUILD WIRING — changes + green-build DoD, 6. EXECUTION ORDER (numbered, dependency-ordered), 7. IN-WORLD INVARIANT GATE (mandatory; user eyeballs) (+12 more)

### Community 7 - "Light Buffer SSBO"
Cohesion: 0.14
Nodes (12): Adding a new orchestration file to the lockstep set, Adding a new per-mod seam impl, Allowlisted divergences, Files NOT under lockstep (per-mod), Files under lockstep (14), Hand-off to Ф5 (Stonecutter), How to run, Limitations and what comes next (+4 more)

### Community 8 - "Patch Filesystem I/O"
Cohesion: 0.29
Nodes (6): Closeable, Exception, FileSystem, Override, PatchException, SourceRoot

### Community 9 - "Lockstep Verifier Script"
Cohesion: 0.36
Nodes (7): compare_pair(), main(), normalize(), Locate the two sibling repos. Honors IRL_REDACTOR_DIR / IRLITE_DIR env     vars, Return a list of human-readable diff lines for one file pair (empty     list =, Strip BOM, normalize line endings, drop package+import lines, apply     the all, resolve_repos()

### Community 10 - "Mod Icon & Branding"
Cohesion: 0.50
Nodes (4): irl-core Fabric Mod, irl-core Mod Icon, Magnifying Glass / Lens Motif, Minecraft Pixel-Art Style

### Community 11 - "Caster Static Hashing"
Cohesion: 1.00
Nodes (3): INV-3 staticHash (avalanche-mixed fold), staticHash signature, modelBlockHash (FNV avalanche per-caster content)

### Community 14 - "Community 14"
Cohesion: 0.18
Nodes (10): 1. OVERALL: **SHIP** (with mandatory in-world gate), 2. BLOCKERS and MAJORS (deduped), 3. MINORS / NITS, 4. IN-WORLD GATE CHECKLIST (ordered by likelihood of failure given the findings), MAJOR-A — Contract INV-1 claim is false for self-drawing BBS forms (doc fix only; not a seam regression), MAJOR-B — INV-5 under-bound for off-vertical (rotate.x/z) model blocks (real defect, partially-closed pre-existing class), No BLOCKERS., Pre-ship doc edit (the only thing recommended before in-world) (+2 more)

### Community 15 - "Community 15"
Cohesion: 0.50
Nodes (5): INV-1 Matrix corruption, Self-drawing BBS forms (live modelview at emit time), ShadowOccluders.drawBlockOccluders (shared block helper), MAJOR-A false INV-1 doc claim for self-drawing forms, BlockShadowCache (canon T2.5 exact-rejection)

## Knowledge Gaps
- **77 isolated node(s):** `workforce-plugin-fetch.sh script`, `What is irl-core?`, `What's inside`, `How it's consumed`, `Building` (+72 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **2 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **What connects `workforce-plugin-fetch.sh script`, `Strip BOM, normalize line endings, drop package+import lines, apply     the all`, `Locate the two sibling repos. Honors IRL_REDACTOR_DIR / IRLITE_DIR env     vars` to the rest of the system?**
  _81 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Patcher Host & Shaderpacks` be split into smaller, more focused modules?**
  _Cohesion score 0.125 - nodes in this community are weakly interconnected._
- **Should `Shadow Caster Seam & Invariants` be split into smaller, more focused modules?**
  _Cohesion score 0.052854122621564484 - nodes in this community are weakly interconnected._
- **Should `Light Registry` be split into smaller, more focused modules?**
  _Cohesion score 0.08602150537634409 - nodes in this community are weakly interconnected._
- **Should `Shadow Orchestration & Mod Ecosystem` be split into smaller, more focused modules?**
  _Cohesion score 0.06896551724137931 - nodes in this community are weakly interconnected._
- **Should `Patch Parser` be split into smaller, more focused modules?**
  _Cohesion score 0.09523809523809523 - nodes in this community are weakly interconnected._
- **Should `Light Buffer SSBO` be split into smaller, more focused modules?**
  _Cohesion score 0.14285714285714285 - nodes in this community are weakly interconnected._