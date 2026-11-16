# MyTerm Development Log

Chronological record of every checkpoint: date, changes, tests, build
results, commit hashes and known issues. New entries are appended at the
bottom.

---

## Checkpoint 1 — 2026-11-16 — Milestone 0 (audit + docs) and Milestone 1 scaffold

### Environment audit findings (verified, not assumed)

- Project directory: `/storage/emulated/0/CodeOnTheGoProjects/MyTerm`
  (accessible as `/mnt/sdcard/...` from the PRoot shell). Was empty except
  for `.git/` and Code on the Go metadata (`.cg/gradle-sync/`, which
  identifies the editor as AndroidIDE-based, `com.itsaky.androidide`).
- Git: branch was `master` with **no commits**; remote `origin` =
  `https://github.com/emmy16-glitch/MyTerm` (empty repo, no branches).
  Local branch renamed to `main` before the first commit (safe — no
  history).
- PRoot shell (OpenCode agent): aarch64 PRoot distro, `git 2.53.0`,
  `proot` available. **No `java`, `javac`, `gradle`, `kotlinc`,
  `sdkmanager`, `aapt`/`aapt2`** on PATH; `ANDROID_HOME`/`JAVA_HOME`
  unset; `pkg` unusable in this root-PRoot context. Conclusion: Android
  builds are performed by Code on the Go's bundled toolchain.
- Shared storage latency is high (a `df` call on the project path timed
  out at 120 s). Agent-side heavy work moved to `/tmp/opencode`.
- Fixed: Git `safe.directory` exception added globally for this path.

### Files created

- `.gitignore` (build outputs, IDE files, `.cg/`, keystores/secrets)
- `AGENTS.md` — permanent session instructions and constraints
- `README.md` — product description, honest current status
- `docs/ARCHITECTURE.md` — layered architecture and decision log
- `docs/ROADMAP.md` — milestones with acceptance criteria and status
- `docs/DEVELOPMENT_LOG.md` — this file
- Milestone 1 scaffold:
  - `settings.gradle.kts`, root + app `build.gradle.kts`
  - `gradle/libs.versions.toml` (version catalog)
  - `gradle/wrapper/gradle-wrapper.{jar,properties}`, `gradlew`
  - `app/src/main/AndroidManifest.xml`
  - `app/src/main/res/*` (themes, strings, adaptive icon placeholders)
  - `app/src/main/java/dev/myterm/**` — `MainActivity`, theme, reusable
    components, terminal screen shell (output region, input area, session
    controls, status area, settings entry)

### Features implemented

- Milestone 0: documentation baseline.
- Milestone 1 (scaffold): Android foundation — Compose app with themed
  main terminal screen. The output region and input area are **not wired
  to a shell yet**; the status area states the engine is not connected.
  No simulated terminal behaviour.

### Tests / build results

- **Build: NOT RUN from the agent shell** — no Android toolchain available
  here. Verification must be done by running the Gradle build in Code on
  the Go. This is recorded as a pending task, not a success.

### Known issues

- Build verification pending (Code on the Go).
- Compose compiler extension version must match the Kotlin version pinned
  in the version catalog; if Code on the Go's bundled Gradle/JDK differs,
  versions may need a bump — recorded for the next checkpoint.

### Commit

- `863f1d2` — "chore: establish project docs and Android application
  foundation" (local branch `main`).
- **Push status: NOT completed.** `git push -u origin main` failed with
  `could not read Username for 'https://github.com'` — no Git credentials
  are configured in this environment (no `gh` CLI, no SSH keys, no
  credential helper). The commit is safe locally. To complete the push,
  provide one of:
  1. A GitHub personal access token exposed as `GIT_ASKPASS`/credential
     helper in the agent environment, or
  2. An SSH key registered with GitHub plus remote URL switch to SSH, or
  3. A push performed from a Termux session where the user is
     authenticated (`git push` after `git fetch` will then be in sync).
