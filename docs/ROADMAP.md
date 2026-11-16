# MyTerm Roadmap

Milestone plan, current status and known limitations. Kept in sync with the
actual implementation — a milestone is only marked complete when its
acceptance criteria are met and verified.

## Milestone 0 — Repository and environment audit

**Status: complete**

Acceptance criteria: repository structure, Git state and development
environment inspected and documented; baseline docs created; no existing
work overwritten.

Results (2026-11-16):

- Repository was empty (no commits, no source files) — nothing to preserve.
- Remote `origin` = `https://github.com/emmy16-glitch/MyTerm`, branch `main`,
  empty on GitHub. Local branch renamed `master` → `main` before first commit.
- Development environment: OpenCode agent runs in a PRoot distro (aarch64)
  under Termux. Git 2.53.0 and PRoot available. **No Java/Gradle/Kotlin/
  Android SDK/NDK visible from the agent shell** — Android builds are done
  inside Code on the Go (AndroidIDE-based), which has Gradle sync metadata
  prepared for this project.
- Shared storage is slow; agent temp work goes to `/tmp/opencode`.
- Git "dubious ownership" resolved via global `safe.directory` entry.

## Milestone 1 — Android project foundation

**Status: in progress (scaffold complete, build verification pending)**

- [x] Gradle project structure (root + `app` module, Kotlin DSL)
- [x] Kotlin, Jetpack Compose (Material 3, light + dark themes)
- [x] Manifest, main activity, application ID `dev.myterm`
- [x] Main terminal screen shell: output region, command input area, session
      controls, status area, settings entry point
- [x] Reusable UI components and theme system
- [x] Gradle wrapper files
- [ ] **Build verified inside Code on the Go** (agent shell cannot compile)
- [ ] Navigation skeleton for settings

Acceptance criteria: project compiles and installs on the phone, terminal
screen renders, no simulated shell behaviour.

## Milestone 2 — Terminal engine

**Status: not started**

- Evaluate terminal emulator libraries (Termux terminal-emulator vs
  alternatives) including licence analysis.
- Real PTY session running the device shell; real command execution
  (`pwd`, `ls`, `echo "MyTerm"`, `whoami`).
- ANSI escape sequences, resize, keyboard/special keys, scrollback.

Acceptance criteria: commands execute for real; limitations documented.

## Milestone 3 — Process and session management

**Status: not started**

Session create/switch/close, process tracking, terminal resize, foreground
service, honest distinction between UI restoration, reconnect and restart.

## Milestone 4 — Linux runtime

**Status: not started**

PRoot integration: install, verify, configure, launch, bind mounts,
environment variables, removal, recovery. Runtime engine independent of UI.

## Milestone 5 — Distribution manager

**Status: not started**

Ubuntu, Debian, Alpine, Arch via a registry-driven descriptor design;
download, verify, install, launch, stop, remove, list.

## Milestone 6 — Reliability and recovery

**Status: not started**

Progress, retry, resume, checksums, status tracking, failure reporting,
operation history, storage and process monitoring. Integration with native
package managers (no duplicate package manager).

## Milestone 7 — Developer features

**Status: not started**

SSH client, file manager, session persistence, tmux support, developer
profiles, backup/restore, terminal preferences.

## Known limitations

- The agent shell cannot compile Android code; every build must be verified
  inside Code on the Go by running its Gradle build.
- Shared-storage performance limits heavy in-repo operations.
- Android may kill background processes; true session persistence requires
  foreground service + tmux strategy (Milestones 3 and 7). No guarantees
  will be claimed beyond what Android provides.
