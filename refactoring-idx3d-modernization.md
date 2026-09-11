# Refactoring: idx3d modernization

## Definition

*Written and owned by the developer. Claude pre-fills from context; the developer refines before G1.*

- **Purpose (why):** idx3d III is a Java 1.1-era 3D engine shipped as Applets. It no longer
  compiles or runs on a modern JDK (uses the `enum` keyword as an identifier, the removed
  `com.sun.image.codec.jpeg` API, and the `java.applet` / AWT-1.0 event model). We want a
  maintainable, modern build and a single executable desktop application that showcases every
  original demo.
- **Scope (what should be done):**
  - Initialize the folder as its own Git repository.
  - Set up a Maven multi-module build (core library, demos, app) targeting Java 17, plus a README.
  - Reorganize the legacy source tree into Maven layout; move assets to classpath resources.
  - Migrate the engine off removed/deprecated APIs so it compiles and runs on Java 17.
  - Replace the `Applet` front-end with a Swing component framework and port every demo to it.
  - Build a modern, dark, tech-themed Swing launcher: split view, demo chooser on the left
    (demo1 selected at startup), running demo on the right.
  - Produce a runnable uber jar (Maven Shade) containing library + demos + assets.
- **Constraints:**
  - Preserve rendering behaviour of the engine and demos — this is a port, not a rewrite.
  - Keep the existing `idx3d_*` class names and package (`idx3d`) to limit risk and diff size.
  - Java 17 source/target; no external runtime dependencies beyond the JDK (Swing).
  - Keep all original demo functionality (mouse rotate, key bindings, materials, effects).
- **Non-goals (out of scope):**
  - Rewriting the renderer (software rasterizer stays as-is).
  - Renaming `idx3d_*` classes or the `idx3d` package (possible follow-up).
  - Modernizing the `idx3d.debug` inspectors beyond making them compile.
  - Porting the standalone editor apps (`idx3dMaterialLab`, `TextureLab`) into the launcher;
    they are legacy desktop tools and are parked (kept in a `legacy/` folder, not built).
  - Unit-testing the renderer (no meaningful baseline tests exist; verification is compile +
    run the app).

## Analysis

*Written by Claude in phase 1. See `analysis-and-planning.md`.*

- **Current state:**
  - Flat repo: 28 root `.java` example/applet files in the default package, plus the library under
    `idx3d/` (`idx3d`, `idx3d.implicit`, `idx3d.debug` — 56 source files). Assets in `3ds/`,
    `new3ds/`, `meshes/`, `materials/`, `textures/`; prebuilt `jars/*.jar`; legacy JBuilder `.prj`,
    Windows `.bat`, PaintShopPro `.jbf` and backup files.
  - No build system, no tests, no version control for the project itself (it sits inside a parent
    git repo rooted at `/home/piwi`).
  - Rendering: `idx3d_Scene` → `idx3d_RenderPipeline` → `idx3d_Rasterizer`/`idx3d_Screen`. Frames
    are exposed as an AWT `Image` built from a custom `idx3d_ImageProducer` via
    `Toolkit.createImage`.
  - Assets are loaded by file/URL path (`idx3d_Texture(URL,String)`, `idx3d_Material(URL,String)`,
    `idx3d_3ds_Importer.importFromURL`). No classpath/resource loading.
  - Two demos (`demo14`, `implicitTest`) use the abstract `idx3d_BaseApplet`; the rest duplicate
    applet boilerplate (`extends Applet implements Runnable`, `init/start/stop/run/repaint`,
    AWT-1.0 `mouseDown/keyDown/mouseDrag/mouseUp`).
- **Problems:**
  - **Hard compile blockers on Java 17:** `enum` used as an identifier in 9 files / 38 sites
    (`idx3d_Scene`, `idx3d_Object`, `idx3d_Vertex`, `idx3d_RenderPipeline`,
    `idx3d_ParticleCluster`, `implicit/BlobSurface`, `implicit/KFMTesselator`,
    `debug/Hashtable_Inspector`, `debug/Vector_Inspector`). `com.sun.image.codec.jpeg` removed
    since JDK 9 (`idx3d_Viewer.saveScreenshot`).
  - **Dead front-end:** `java.applet.*` deprecated for removal; AWT-1.0 handlers (`mouseDown`,
    `keyDown`, `handleEvent`, `action`) are never dispatched by modern AWT.
  - **Fragile asset IO:** `PixelGrabber` + `Toolkit.getImage` busy-wait; string-based URL base
    derivation; filesystem-only material/3DS loading.
  - **Broken asset paths** in several demos (root-relative names that only exist under
    `materials/`/`textures/`; `demo4` references a missing `textures/spectrum.jpg`).
- **Affected code:**
  - Library: all of `idx3d/` (compile fixes in 11 files; IO/rendering modernization in
    `idx3d_Texture`, `idx3d_Screen`, `idx3d_Material`, `idx3d_ImageProducer`, `idx3d_Viewer`).
  - Examples: all 22 applet/demo files at the repo root.
  - Assets: `textures/`, `materials/`, `meshes/`, root `export.3ds`.
- **Dependencies & blast radius:**
  - Demos depend on the whole engine. The engine is self-contained (only JDK). Changing
    `idx3d_Texture`/`idx3d_Material`/`idx3d_Screen` signatures affects all demos, so the resource
    loader and modernized constructors must be introduced additively (keep old constructors where
    possible) before porting demos.
- **Test coverage:** none. There is no green baseline to protect. Mitigation: keep changes
  additive/mechanical, compile after every phase, and smoke-run the app. The prebuilt jars are not
  usable as a behavioural oracle (different JDK) but remain for reference.
- **Risks:**
  - `enum`→`enumeration` rename must not touch other identifiers (verified: only the lowercase
    word `enum` is affected; `Enumeration` is capitalised).
  - Replacing the image producer with a shared `BufferedImage` must preserve pixel layout/ARGB
    handling (rasterizer writes `int[]` in the same 0xRRGGBB layout).
  - `BufferedImage` sharing a `int[]` requires correct `DataBufferInt`/`PackedRaster` setup and
    rebuild on `resize()`/antialias toggle.
  - Asset path rewrites: materials reference textures relative to themselves (e.g.
    `materials/glass.material` → `skymap.jpg`, `materials/nemesis.material` → `stone.jpg`), so the
    resolver must handle both material-relative and asset-root-relative names.
  - 22 demos is a wide port surface; a shared `idx3d_DemoPanel` base plus mechanical conversion is
    the risk control.

## Plan

*Written by Claude in phase 2. Ordered phases, each ending in a verification step. The checkboxes are the
progress state. See `analysis-and-planning.md`.*

### Phase 1 — Bootstrap repo, Maven skeleton, move sources & assets
- [x] `git init` in project root; add `.gitignore` (target/, *.class, IDE, OS, legacy junk)
- [x] Create parent `pom.xml` (packaging `pom`, Java 17, modules) and module poms
      `idx3d-core`, `idx3d-demos`, `idx3d-app`
- [x] Move library sources into `idx3d-core/src/main/java/idx3d/{,implicit,debug}`; drop
      `idx3d_Rasterizer.java.backup` and `idx3d_BaseApplet.java` (replaced in phase 2)
- [x] Move assets (`textures/`, `materials/`, `meshes/`, `export.3ds`) into
      `idx3d-demos/src/main/resources/assets/`
- [x] Move legacy/non-built material (`3ds/`, `new3ds/`, `jars/`, `*.bat`, `*.prj`, `*.jbf`,
      `*.html`, `*.java~`, editors) into a `legacy/` folder (excluded from the build)
- [x] **Verify:** `mvn -q validate` succeeds and reports all three modules

### Phase 2 — Core library compiles & runs on Java 17
- [x] Rename `enum` identifiers to `enumeration` in the 9 affected files
- [x] Replace removed `com.sun.image.codec.jpeg` screenshot code in `idx3d_Viewer` with `ImageIO`
- [x] Modernize `idx3d_Texture` image loading (`ImageIO`/`BufferedImage`, no `PixelGrabber` busy-wait)
- [x] Modernize `idx3d_Screen` to expose a `BufferedImage` backed by the pixel buffer
      (retire `idx3d_ImageProducer` usage)
- [x] Add `idx3d_Resources` classpath resolver (`/assets/...` + relative material textures)
- [x] Add resource-aware constructors/factories to `idx3d_Material` and `idx3d_Texture`
- [x] Replace `idx3d_BaseApplet` with Swing `idx3d_DemoPanel` (JPanel + render thread +
      mouse/key handling); modernize `idx3d_Viewer` to Swing
- [x] Remove `java.applet` imports and other removed APIs from core
- [x] **Verify:** `mvn -q -pl idx3d-core compile` green

### Phase 3 — Demo framework & port all demos
- [x] Port numbered demos `demo1`–`demo14` to `idx3d.demos.Demo01`–`Demo14` extending
      `idx3d_DemoPanel`, using `idx3d_Resources`
- [x] Port `geometric`, `implicitTest`, `torus`, `link3d`, `MeshSmoothTest`, `tutorial1`,
      `KeyframePrototype`, `LensFlareEditor`
- [x] Add `idx3d.demos.DemoCatalog` (id, title, description, supplier)
- [x] Fix broken asset paths discovered during the port (e.g. `demo4` spectrum, `demo13` paths)
- [x] **Verify:** `mvn -q -pl idx3d-demos compile` green

### Phase 4 — Modern dark launcher (split view)
- [x] `idx3d.app.Theme` — dark palette, fonts, paddings, custom components (list cell renderer,
      header, viewport chrome)
- [x] `idx3d.app.MainFrame` — `JSplitPane`: left `JList` of demos, right viewport that
      instantiates/starts/stops the selected demo; demo1 selected at startup
- [x] `idx3d.app.Main` — Swing entry point (EDT)
- [x] **Verify:** `mvn -q -pl idx3d-app compile` green

### Phase 5 — Uber jar, README, final verification
- [x] Configure Maven Shade in `idx3d-app` (main class, merge resources, include assets)
- [x] Write `README.md` (about, build, run, module layout, controls, credits/licence note)
- [x] **Verify:** `mvn -q clean package` green; `java -jar idx3d-app/target/idx3d.jar`
      launches (smoke-test under a virtual display if headless)

## Work Log

*Appended by Claude as work happens — what changed and, for any real choice, why. Append-only. Records the
baseline, gate approvals, and each phase's result.*

### 2026-09-11 — Intake / baseline
- Project has **no build system and no tests**. `javac` on the legacy source under Java 17 fails
  (`enum` is a keyword; removed JPEG codec). A green baseline is therefore impossible; the
  contract becomes "each phase compiles, final app launches". User pre-approved execution by
  asking to "work out a plan … and then start working on it".
- Decision: **Maven multi-module** (`idx3d-core`, `idx3d-demos`, `idx3d-app`) with a shaded uber
  jar, rather than a single module, to match the library/examples/app separation.
- Decision: **keep `idx3d_*` class/package names** (behaviour preservation, smaller diff); package
  rename noted as a follow-up.
- Decision: editors `idx3dMaterialLab`/`TextureLab` are parked in `legacy/`, not ported.

### 2026-09-11 — Phase 1: Bootstrap repo, Maven skeleton, move sources & assets
- Did: `git init` (branch `main`); added `.gitignore`; parent `pom.xml` + `idx3d-core` /
  `idx3d-demos` / `idx3d-app` module poms (Java 17, compiler + shade plugin managed). Moved 36
  library sources to `idx3d-core/src/main/java/idx3d/{,implicit,debug}`; dropped
  `idx3d_BaseApplet.java` and `idx3d_Rasterizer.java.backup`. Moved `textures/`, `materials/`,
  `meshes/`, `export.3ds` to `idx3d-demos/src/main/resources/assets/`. Moved all other legacy
  material (root demos/editors `.java`, `.html`, `.bat`, `.prj`, `.jbf`, screenshots, `3ds/`,
  `new3ds/`, `jars/`, backups) into `legacy/`.
- Decisions: `3ds/`+`new3ds/` kept in `legacy/` (16 MB + 13 MB of model libraries not used by
  any working demo); only demo-used assets go on the classpath. No commit made (per session git
  rules — recommend committing this phase).
- Verify: `mvn -q validate` → exit 0, all three modules discovered. Green.

### 2026-09-11 — Phase 2: Core library compiles & runs on Java 17
- Did: renamed the lowercase `enum` identifier to `enumeration` (9 files, 38 sites). Replaced the
  removed `com.sun.image.codec.jpeg` screenshot path in `idx3d_Viewer` with `ImageIO` and rewrote
  `idx3d_Viewer` as a Swing `JPanel`. Modernized `idx3d_Texture` to load via
  `BufferedImage.getRGB` (dropped `PixelGrabber`/`Toolkit`). Rewrote `idx3d_Screen` to expose a
  `BufferedImage` backed by the shared `int[]` pixel buffer (`DataBufferInt` + packed raster) and
  deleted `idx3d_ImageProducer`. Added `idx3d_Resources` (classpath `/assets/` first, then
  filesystem, then texture/material/mesh fallback dirs) plus `idx3d_Material.load(InputStream,
  base)` and resource-aware `idx3d_Texture(String)`/`idx3d_Material(String)` constructors. Added
  `idx3d_DemoPanel` (Swing base with render thread, shared mouse/key controls, resize handling),
  replacing the deleted `idx3d_BaseApplet`. Removed all `import java.applet.*` from core.
- Decisions: keep the legacy `idx3d_Texture(URL,String)` / `idx3d_Material(URL,String)`
  constructors as thin delegates so the ported demos can be converted gradually. Image producer
  retired in favour of a shared `BufferedImage` (simpler, synchronous, headless-safe).
- Verify: `mvn -q -pl idx3d-core compile` green. Headless smoke tests pass: a rendered sphere
  produces 11521 non-black pixels; `idx3d_Resources` loads a 512x512 texture, a material with
  texture+envmap, and imports `meshes/wobble.3ds` (`Wobble2` present).

### 2026-09-11 — Phase 3: Demo framework & port all demos
- Did: ported all 22 applet/example programs to `idx3d.demos.*` extending `idx3d_DemoPanel`:
  `Demo01`–`Demo14`, `Geometric`, `Torus`, `Link3d`, `MeshSmooth`, `Tutorial1`,
  `KeyframePrototype`, `ImplicitTest`, `LensFlareEditor`. Each uses `idx3d_Resources` for assets;
  applet lifecycle/threads/old AWT events removed; animation moved to `runtime()`; demo keys moved
  to `keyEvent(char)`; `demo10`/`link3d` links open via `Desktop.browse` (stdout fallback);
  `LensFlareEditor` controls rebuilt as Swing buttons/text fields. Added `DemoCatalog` (id, title,
  description, factory) with demo1 first.
- Decisions: broken legacy paths corrected (`demo4` `spectrum.jpg`→`texture.jpg`; `demo13`
  root-relative → `materials/`/`textures/`/`meshes/`; `tutorial1`/`geometric`/`torus`/`demo14`
  material names prefixed; `MeshSmooth` missing `generic/vette.3ds` → `meshes/wobble.3ds`;
  `link3d` hardcoded HTML params). `demo13` imports `meshes/plasmacore.3ds` if present.
- Verify: `mvn -q -pl idx3d-demos -am compile` green. Headless render smoke over the whole
  catalog: every demo instantiates and renders non-blank (e.g. demo1 18818 px, demo5 16092 px,
  geometric 18347 px); `lensflare` renders once its `plugin` is applied (the app does this in
  `paintComponent`).

### 2026-09-11 — Phase 4: Modern dark launcher (split view)
- Did: added `idx3d.app.Theme` (palette + Inter/JetBrains-Mono fallback fonts), `DarkScrollBarUI`,
  `DemoCell` (custom list renderer with index, title, description, selected accent bar, hover),
  `DemoViewport` (demo title/description/id badge + host that creates/starts/stops the selected
  demo), `MainFrame` (dark header, `JSplitPane` with a 320px sidebar on the left and viewport on
  the right, status bar with control hints) and `Main` (EDT entry point). demo1 is selected on
  startup.
- Decisions: custom-painted components instead of a look-and-feel theme so the design is
  self-contained and deterministic; descriptions kept short so the fixed-height sidebar cells do
  not clip.
- Verify: `mvn -q -pl idx3d-app -am compile` green. Launched the real UI on display `:1` and
  captured screenshots: dark split view renders correctly, demo1 runs in the right pane, and
  selecting demo5 swaps the viewport and header. Screenshot saved to `docs/screenshot.png`.

### 2026-09-11 — Phase 5: Uber jar, README, final verification
- Did: configured Maven Shade in `idx3d-app` (main class `idx3d.app.Main`, signature-file
  exclusions, output `idx3d.jar`); wrote `README.md` (about, build/run, controls, module layout,
  modernization notes, credits); added `docs/screenshot.png`.
- Verify: `mvn -q clean package` green → `idx3d-app/target/idx3d.jar` (4.2 MB, 100 classes) with
  `Main-Class: idx3d.app.Main` and assets bundled under `assets/` (`export.3ds`,
  `textures/texture.jpg`, `materials/glass.material`, `meshes/wobble.3ds`). Ran
  `java -jar idx3d-app/target/idx3d.jar` on display `:1`; it launched with no exceptions and
  rendered the launcher.
- Note: no automated test suite exists (the legacy project had none); verification is
  compile + headless render smoke over all 22 demos + a live GUI launch.

### 2026-09-11 — Completion
- All five phases complete and green. Result: a Java 17 Maven multi-module project that builds a
  single executable `idx3d.jar` containing the engine, all 22 ported demos and their assets, and a
  modern dark Swing launcher with a split demo chooser/runner.
- Out of scope / follow-ups deliberately left: renaming `idx3d_*` classes and the `idx3d` package
  to modern names; modernizing the `idx3d.debug` inspectors beyond compiling; porting the
  standalone editors (`idx3dMaterialLab`, `TextureLab`) which remain in `legacy/`; adding a test
  suite; converting the software rasterizer to Java2D/GPU.
- Git: repository initialized on branch `main`; nothing committed (session rule: commit only when
  asked). Recommended first commit of the completed port.

### 2026-09-11 — Post-completion: slow animations 10x
- Did: in `idx3d_DemoPanel`, `runtime()` is now called only every `animationDivisor` rendered
  frames (default 10), so all demos animate at 1/10 speed while the frame rate is unchanged.
  The factor is configurable with the system property `idx3d.animationDivisor` (set to 1 for the
  original speed).
- Verify: `mvn` green; headless demo smoke green; a counting test confirms 100 paints produce 10
  `runtime()` calls.

### 2026-09-11 — Post-completion: smooth slow motion (replaces frame-skipping)
- Did: replaced the frame-skipping divisor with true slow motion. Added `idx3d_Animation` (global
  delta scale, default 1) and multiply transform deltas by it inside `idx3d_CoreObject`
  (`shift`/`rotate`/`rotateSelf`) and `idx3d_Camera` (`roll`/`shift`/`rotate`). `idx3d_DemoPanel`
  now calls `runtime()` every rendered frame, wrapping it in
  `idx3d_Animation.setScale(animationScale)` (default 0.1) and resetting to 1 before render. Added
  a virtual `animationTime()` clock advancing at `animationScale`× wall time; the 10 time-based
  demos (`Demo02/03/05/06/07/10/11/12`, `ImplicitTest`, `Torus`, `Link3d`) now use it instead of
  `System.currentTimeMillis()`. The combination `θ(t) = θ_original(scale·t)` yields same-amplitude,
  smoothly slowed motion. Configurable via `-Didx3d.animationScale` (1 = original speed).
- Verify: `mvn` green; headless demo smoke green. A probe test shows 100 paints → 100 `runtime()`
  calls with scale 0.1 during runtime and 1.0 after; the virtual clock measured 0.103s over 1.03s
  real (ratio 0.100).

### 2026-09-11 — Post-completion: prune demo catalog to 12
- Did: kept only the requested demos and removed the rest from `idx3d-demos`: deleted `Demo05`,
  `Demo09`, `Demo14`, `Geometric`, `Torus`, `Link3d`, `Tutorial1`, `KeyframePrototype`,
  `ImplicitTest` and `LensFlareEditor`. Renamed demo 03 from "Wobble" to **"Stones"** (id
  `stones`, class `Demo03`). `DemoCatalog` now lists 12 entries; the launcher header count is
  derived from the catalog. README updated.
- Kept: `Demo01`, `Demo02`, `Demo03` (Stones), `Demo04`, `Demo06`, `Demo07`, `Demo08`, `Demo10`,
  `Demo11`, `Demo12`, `Demo13`, `MeshSmooth`.
- Verify: `mvn` green; headless render smoke over the 12 remaining demos passes; `Stones` renders.

### 2026-09-11 — Post-completion: prune to 10 (remove Plasma Core + Mesh Smooth)
- Did: removed `Demo13` (Plasma Core) and `MeshSmooth` from `idx3d-demos` and from
  `DemoCatalog`. README updated. Remaining catalog (sidebar order): Torus & Wineglass, Terrain,
  Stones, Torus Knot, Lens Flare, Venus, Mech, 3D Links, Demon, Torus Knot II.
- Verify: `mvn` green; headless render smoke over the 10 remaining demos passes.
