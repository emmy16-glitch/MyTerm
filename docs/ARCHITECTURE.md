# MyTerm Architecture

Last updated: Milestone 1 scaffold. This document describes the intended
system architecture and the technical decisions made so far. It is updated
whenever the implementation changes.

## 1. System overview

MyTerm is a single-module Android application (deliberate v1 choice — see
§5) with strictly separated internal layers:

```
┌───────────────────────────────────────────────┐
│ Presentation (Compose UI, Material 3)         │
│   ui/ — screens, components, theme            │
├───────────────────────────────────────────────┤
│ Application logic (state holders, managers)   │
│   domain/ — use cases, session/app state      │
├───────────────────────────────────────────────┤
│ Terminal engine        Process/session mgmt   │
│   terminal/            session/               │
│   (PTY, ANSI, resize)  (lifecycle, service)   │
├───────────────────────────────────────────────┤
│ Linux runtime mgmt     Distribution mgmt      │
│   runtime/ (PRoot)     distro/ (registry)     │
├───────────────────────────────────────────────┤
│ Storage/persistence    Downloads/networking   │
│   data/ (DataStore)    download/ (retry,      │
│                        checksum, resume)      │
└───────────────────────────────────────────────┘
```

Rule: arrows point downward only. The UI never executes processes or touches
the filesystem directly; it talks to state holders and managers.

## 2. Layer responsibilities

### Presentation (`ui/`)
- Compose screens: terminal, sessions, settings (settings planned).
- Reusable components: buttons, status indicators, dialogs.
- Theme system: colour, typography, spacing; light and dark.
- Holds no business logic; observes state from application logic.

### Application logic (`domain/` / managers)
- Terminal screen state holder, session manager (Milestone 3).
- Coordinates terminal engine, runtime and distro layers.

### Terminal engine (`terminal/`, Milestone 2)
- Creates and owns a PTY for each shell session.
- ANSI/VT100 escape-sequence emulation, scrollback, resize.
- Must be a pure Kotlin/library component, testable without Android UI.
- Candidate libraries under evaluation: Termux's terminal-emulator
  (GPLv3-licensed, proven on Android) vs. alternatives. Licence and
  integration implications are documented when selected.

### Process/session management (`session/`, Milestone 3)
- Session creation, switching, closing, state.
- Foreground service for running sessions; honest separation between UI
  restoration, reconnecting to live processes and restarting dead ones.
- tmux integration considered for true persistence (Milestone 7).

### Linux runtime management (`runtime/`, Milestone 4)
- PRoot-based execution. PRoot is a **userspace compatibility layer**, not a
  VM and not real root — this is stated in all user-facing features.
- Owns runtime install, verification, configuration, launch, removal and
  recovery. Environment variables, storage bindings and launch config are
  defined per distribution instance.

### Distribution management (`distro/`, Milestone 5)
- Registry-driven: each distribution (Ubuntu, Debian, Alpine, Arch…) is a
  descriptor (URL, checksums, install procedure, launch command) — no
  hardcoded assumptions that block future additions.
- Install/verify/launch/stop/remove/list operations, isolated per-distro
  directories under app-private storage.

### Storage & persistence (`data/`)
- Android app-private storage for rootfs and runtime files.
- DataStore for preferences; Room considered later if relational data
  appears (operation history).

### Downloads (`download/`, Milestone 6)
- Resumable HTTP downloads, progress reporting, retry with backoff,
  SHA-256 (or upstream-provided) integrity verification before use,
  interrupted-install recovery, storage-space checks before large writes.

## 3. Security decisions

| Decision | Rationale |
|----------|-----------|
| Minimal permissions: INTERNET + FOREGROUND_SERVICE only (as needed per feature) | Terminal app needs network and long-running sessions; nothing else |
| Rootfs in app-private storage | Shared storage is user-visible and slow; isolation prevents tampering |
| Checksum verification before executing/extracting downloads | Prevent corrupted or tampered payloads from executing |
| No secret material in the repository | Keystores, tokens and passwords stay off `main` |
| User-controlled paths validated | PRoot bind-mounts must not escape the intended sandbox |
| Destructive operations require explicit confirmation in the UI | Removal of distributions/runtime must never be silent |

## 4. Technical decisions log

| # | Decision | Reason |
|---|----------|--------|
| D1 | Kotlin + Jetpack Compose + Material 3 | Modern, maintainable, phone-first UI development |
| D2 | Single Gradle module for v1 | Avoids build overhead on a phone toolchain; layer separation enforced by package structure; revisit at Milestone 5+ |
| D3 | Gradle Kotlin DSL | Type-safe, IDE-assisted build config |
| D4 | minSdk 26, compile/target SDK 34 | Access to modern APIs (java.time, better process APIs) while covering nearly all active devices |
| D5 | Code on the Go performs builds | Only available Android build toolchain in this environment (see `AGENTS.md`) |
| D6 | PRoot from a trusted binary source with checksums | Userspace Linux environment without root; integrity matters (Milestone 4) |

## 5. Dependencies

Currently declared (Milestone 1):

- `androidx.core:core-ktx`, `androidx.lifecycle:*` — standard AndroidX.
- `androidx.activity:activity-compose` — Compose activity integration.
- Compose BOM + `material3`, `ui`, `ui-tooling-preview` — UI toolkit.
- `androidx.compose.material:material-icons-extended` — extended icon set.

Any new dependency must be justified here, including its size and licence
impact (phone-based development: avoid unnecessarily large downloads).
