# AGENTS.md — Permanent Instructions for Coding Sessions

MyTerm is an independent Android terminal emulator and Linux development
environment. This file contains durable rules for every future coding session
(human or AI agent). Read it before writing any code.

## 1. Non-negotiable constraints

1. **Development happens on an Android phone.** There is no desktop or laptop.
   The toolchain is: Code on the Go (AndroidIDE-based, performs Gradle builds
   in-app), Termux with a PRoot distro (where the OpenCode agent runs), and
   GitHub for source hosting.
2. **The agent shell cannot compile Android code.** Java, Gradle and the
   Android SDK are not visible from the Termux/PRoot shell. Build verification
   is performed inside Code on the Go. Never claim a build succeeded unless it
   was actually run and verified (in-app or via a real toolchain). Never
   invent build results.
3. **Project location:** `/storage/emulated/0/CodeOnTheGoProjects/MyTerm`
   (may be mounted as `/mnt/sdcard/CodeOnTheGoProjects/MyTerm` in the PRoot
   shell). Do not modify files outside this repository.
4. **Shared storage is slow.** Do not run heavy scans, searches or downloads
   inside the project directory; use `/tmp/opencode` for temporary work.
5. **No root assumptions.** PRoot is a userspace compatibility mechanism, not
   a virtual machine and not real root. Never design features that require
   privileged access.
6. **Git safety.** Branch `main` only. Never force push, never rewrite
   published history, never delete the remote. Never commit secrets,
   keystores, `local.properties`, build outputs or `.cg/` metadata.
7. **Git ownership:** shared storage triggers Git's "dubious ownership" check;
   `safe.directory` for this path is already configured globally in the PRoot
   shell. Re-add it if a fresh environment loses it:
   `git config --global --add safe.directory /mnt/sdcard/CodeOnTheGoProjects/MyTerm`

## 2. Architecture rules

- Layers (strictly separated):
  - **Presentation** (`ui/`): Jetpack Compose only. No business logic in
    composables.
  - **Application logic** (`domain/` or feature managers): use-case level,
    framework-light.
  - **Terminal engine** (`terminal/`): PTY handling, ANSI emulation. Must be
    UI-independent.
  - **Process/session management** (`session/`): session lifecycle, service
    integration, process tracking.
  - **Linux runtime management** (`runtime/`): PRoot integration, environment
    configuration.
  - **Distribution management** (`distro/`): install/launch/remove logic per
    distribution, registry-driven (no hardcoded distribution assumptions).
  - **Storage & persistence** (`data/`): repositories, DataStore.
  - **Networking/downloads** (`download/`): resumable downloads, checksum
    verification, retry, recovery.
- New features must plug into this layering. UI code must never exec
  processes or touch the filesystem directly.
- Distributions' files must live in isolated directories. Never mix rootfs
  contents of different distributions.

## 3. Engineering principles

- **Honesty about status.** Never report an operation as complete/successful
  unless verified. Never simulate terminal output and present it as real.
- **Reliability first:** checksums where available, resumable downloads,
  interrupted-install recovery, storage checks before big operations.
- **No duplicate package managers.** Use each distribution's native package
  manager inside the PRoot environment.
- **Security:** validate user-controlled paths; never execute downloaded
  scripts without verification; no hardcoded credentials; minimal Android
  permissions; document every security-sensitive decision.
- **Progressive development.** Follow `docs/ROADMAP.md` milestones in order.
  Do not claim a milestone complete unless its acceptance criteria are met.

## 4. Coding conventions

- Language: Kotlin. UI: Jetpack Compose, Material 3, light + dark themes.
- Build config: Gradle Kotlin DSL (`*.gradle.kts`).
- Keep dependencies minimal; justify every new dependency in
  `docs/ARCHITECTURE.md`.
- Min SDK 26, target/compile SDK 34 (adjust deliberately, with a documented
  reason).
- Every meaningful checkpoint: update `docs/DEVELOPMENT_LOG.md`, run
  available verification, commit with a descriptive message, push to
  `origin main`, and verify the push.

## 5. Current status snapshot

Keep this section in sync with `docs/ROADMAP.md`:

- Milestone 0 (audit + docs): complete — see `docs/DEVELOPMENT_LOG.md`.
- Milestone 1 (Android project foundation): scaffolded and pushed; build
  verification pending inside Code on the Go.
