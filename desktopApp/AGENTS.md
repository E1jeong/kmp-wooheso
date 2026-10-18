# DesktopApp Module Guide

## Scope

Wooheso (우회소) Compose Multiplatform desktop host application module (Windows, macOS, Linux).
Houses desktop window lifecycle and showroom preview runtime; business logic and common UI reside in `shared/`.

## Orient First

- Entrypoint: `desktopApp/src/desktopMain/kotlin/com/sumas/wooheso/desktop/Main.kt`
- Build configuration: `desktopApp/build.gradle.kts`

## Boundary & Architecture Constraints

- **Thin Host**: Keep navigation, Supabase repositories, and UI in `shared/`; `desktopApp` only hosts `App()` inside a desktop window.
- **Window Size**: `Main.kt` must maintain explicit window dimensions (`width = 420.dp, height = 860.dp`) via `WindowState` to prevent zero-size collapse.

## Change Gates

- **Non-Disruptive Validation**: Routine AI verification uses background compile, test, and packaging commands only. Do not launch the Desktop app, open or focus an Orca terminal, capture the visible desktop, or manipulate windows unless the user explicitly asks to see or interact with the running UI. If background screenshot capture is unavailable, report that visual verification was not performed instead of opening a visible window.
- **Explicit Desktop Run**: The AI agent's subprocess shell runs in an isolated virtual desktop (`exebox`), so GUI windows it starts never reach the user's monitor. When the user explicitly asks to run, see, or debug the Desktop app, launch it through a focused Orca terminal:
  ```powershell
  orca terminal create --worktree active --title "Wooheso App" --command ".\gradlew.bat :desktopApp:run" --focus
  ```
- **User Execution**: Only when the user will run the app themselves, instruct them to use Android Studio's `desktopApp` run configuration (Run ▶) or execute `.\gradlew.bat :desktopApp:run` directly in their terminal.

## Verify

```powershell
# Desktop module compile verification
.\gradlew.bat :desktopApp:compileKotlinDesktop

# Package Windows MSI installer
.\gradlew.bat :desktopApp:packageMsi

# Create distributable package
.\gradlew.bat :desktopApp:createDistributable
```
