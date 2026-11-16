# MyTerm

MyTerm is an independent Android terminal emulator and Linux development
environment. It aims to provide a reliable, accessible and extensible
terminal directly on Android devices — with support for multiple terminal
sessions, Linux shell environments and PRoot-based distributions such as
Ubuntu, Debian, Alpine and Arch Linux.

## Status

**Early development.** The project foundation is being established.
Features are added progressively — see
[docs/ROADMAP.md](docs/ROADMAP.md) for the milestone plan and
[docs/DEVELOPMENT_LOG.md](docs/DEVELOPMENT_LOG.md) for verified progress.

Currently implemented:

- Project documentation and architecture baseline (Milestone 0).
- Android application foundation: Kotlin + Jetpack Compose UI, theme system,
  main terminal screen shell with session controls and settings entry point
  (Milestone 1 — build verification pending).

Currently **not** implemented yet (honest list):

- Real shell/PTY execution — the terminal engine (Milestone 2) is not
  integrated, so the terminal screen does not yet execute commands.
- Multiple sessions, process persistence, PRoot runtime, distribution
  management, downloads/installs, SSH, file management.

## Technology stack

| Component      | Choice                                    |
|----------------|-------------------------------------------|
| Language       | Kotlin                                     |
| UI             | Jetpack Compose, Material 3, light + dark  |
| Build          | Gradle Kotlin DSL, Android Gradle Plugin   |
| Target         | Android (min SDK 26)                       |
| Planned        | PTY-based terminal engine, PRoot, tmux, SSH |

## Development setup

This project is developed **on an Android phone**:

- **Code on the Go** (AndroidIDE-based) — project editor and Gradle build
  environment. Open the project at
  `/storage/emulated/0/CodeOnTheGoProjects/MyTerm` and use its Gradle build
  to compile and install the APK.
- **Termux + PRoot (OpenCode agent)** — code inspection, Git operations and
  documentation. This shell has no Java/Gradle/SDK; Android builds are done
  in Code on the Go.
- **GitHub** — `https://github.com/emmy16-glitch/MyTerm`, branch `main`.

## Repository layout

```
app/                 Android application module (Compose UI)
docs/                Architecture, roadmap and development log
gradle/              Gradle wrapper
AGENTS.md            Durable instructions for coding sessions
```

## Licence

To be decided before the first public release.
