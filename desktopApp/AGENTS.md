# DesktopApp Module Guide

## Scope

Wooheso (우회소) Compose Multiplatform desktop host application module (Windows, macOS, Linux).
Houses desktop window lifecycle and showroom preview runtime; business logic and common UI reside in `shared/`.

Report plans and results in Korean.

## Orient First

- Entrypoint: `desktopApp/src/desktopMain/kotlin/com/sumas/wooheso/desktop/Main.kt`
- Build configuration: `desktopApp/build.gradle.kts`

## Boundary & Architecture Constraints

- **Thin Host**: Keep navigation, Supabase repositories, and UI in `shared/`; `desktopApp` only hosts `App()` inside a desktop window.
- **Window Size**: `Main.kt` must maintain explicit window dimensions (`width = 420.dp, height = 860.dp`) via `WindowState` to prevent zero-size collapse.

## Change Gates

- **AI Execution Gate**: The AI agent's subprocess shell runs in an isolated virtual desktop (`exebox`), which prevents GUI windows from appearing on the user's monitor. When launching the desktop app for the user, agents **must use the Orca CLI** to open a focused terminal tab:
  ```powershell
  orca terminal create --title "Wooheso App" --command ".\gradlew.bat :desktopApp:run" --focus
  ```
- **User Execution**: Instruct the user to use Android Studio's `desktopApp` run configuration (Run ▶) or execute `.\gradlew.bat :desktopApp:run` directly in their terminal.

## Verify

```powershell
# Desktop module compile verification
.\gradlew.bat :desktopApp:compileKotlinDesktop

# Package Windows MSI installer
.\gradlew.bat :desktopApp:packageMsi

# Create distributable package
.\gradlew.bat :desktopApp:createDistributable
```
